package org.apache.hadoop.ozone.insight;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.apache.hadoop.hdds.cli.HddsVersionProvider;
import org.apache.hadoop.hdds.conf.OzoneConfiguration;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;
import picocli.CommandLine;

/**
 * Subcommand to display log.
 */
@CommandLine.Command(
    name = "log",
    description = "Show log4j events related to the insight point",
    mixinStandardHelpOptions = true,
    versionProvider = HddsVersionProvider.class)
public class LogSubcommand extends BaseInsightSubcommand
    implements Callable<Void> {

  @CommandLine.Parameters(description = "Name of the insight point (use list "
      + "to check the available options)")
  private String component;

  @CommandLine.Option(names = "-v", description = "Enable verbose mode to "
      + "show more information / detailed message")
  private boolean verbose;

  @CommandLine.Parameters(defaultValue = "")
  private String selection;

  @Override
  public Void call() throws Exception {
    OzoneConfiguration conf =
        getInsightCommand().createOzoneConfiguration();
    InsightPoint insight =
        getInsight(conf, selection);

    List<LoggerSource> loggers = insight.getRelatedLoggers(verbose);
    for (LoggerSource logger : loggers) {
      setLogLevel(conf, logger);
    }

    Set<Component> sources = loggers.stream().map(LoggerSource::getComponent)
        .collect(Collectors.toSet());
    streamLog(conf, sources, loggers);
    return null;
  }

  private void streamLog(OzoneConfiguration conf, Set<Component> sources,
      List<LoggerSource> relatedLoggers) {
    List<Thread> loggers = new ArrayList<>();
    for (Component component : sources) {
      loggers.add(new Thread(new Runnable() {
        @Override
        public void run() {
          streamLog(conf, component, relatedLoggers);
        }
      }));
    }
    for (Thread thread : loggers) {
      thread.start();
    }
    for (Thread thread : loggers) {
      try {
        thread.join();
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
    }
  }

  private void streamLog(OzoneConfiguration conf, Component component,
      List<LoggerSource> loggers) {
    HttpClient client = HttpClientBuilder.create().build();

    HttpGet get = new HttpGet(getHost(conf, component) + "/logstream");
    try {
      HttpResponse execute = client.execute(get);
      try (BufferedReader bufferedReader = new BufferedReader(
          new InputStreamReader(execute.getEntity().getContent(),
              StandardCharsets.UTF_8))) {
        bufferedReader.lines()
            .filter(line -> {
              for (LoggerSource logger : loggers) {
                if (line.contains(logger.getLoggerName())) {
                  return true;
                }
              }
              return false;
            })
            .map(this::processLogLine)
            .map(l -> "[" + component.prefix() + "] " + l)
            .forEach(System.out::println);
      }
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  public String processLogLine(String line) {
    Pattern p = Pattern.compile("<json>(.*)</json>");
    Matcher m = p.matcher(line);
    StringBuffer sb = new StringBuffer();
    while (m.find()) {
      m.appendReplacement(sb, "\n" + m.group(1).replaceAll("\\\\n", "\n"));
    }
    m.appendTail(sb);
    return sb.toString();
  }

  private void setLogLevel(OzoneConfiguration conf, LoggerSource logger) {
    HttpClient client = HttpClientBuilder.create().build();

    String request = String
        .format("/logLevel?log=%s&level=%s", logger.getLoggerName(),
            logger.getLevel());
    String hostName = getHost(conf, logger.getComponent());
    HttpGet get = new HttpGet(hostName + request);
    try {
      HttpResponse execute = client.execute(get);
      if (execute.getStatusLine().getStatusCode() != 200) {
        throw new RuntimeException(
            "Can't set the log level: " + hostName + " -> HTTP " + execute
                .getStatusLine().getStatusCode());
      }
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }
}
