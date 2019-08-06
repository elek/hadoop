package org.apache.hadoop.ozone.insight;

import org.apache.hadoop.hdds.cli.HddsVersionProvider;
import org.apache.hadoop.hdds.conf.Config;
import org.apache.hadoop.hdds.conf.ConfigGroup;
import org.apache.hadoop.hdds.conf.OzoneConfiguration;

import picocli.CommandLine;

import java.lang.reflect.Method;
import java.util.concurrent.Callable;

@CommandLine.Command(
    name = "config",
    description = "Show configuration for a specific subcomponents",
    mixinStandardHelpOptions = true,
    versionProvider = HddsVersionProvider.class)
public class Configuration extends BaseInsightSubcommand
    implements Callable<Void> {

  @CommandLine.Parameters(defaultValue = "")
  private String selection;

  @Override
  public Void call() throws Exception {
    InsightPoint insight =
        getInsight(getInsightCommand().createOzoneConfiguration(), selection);
    System.out.println(
        "Configuration for `" + selection + "` (" + insight.getDescription()
            + ")");
    System.out.println();
    for (Class clazz : insight.getConfigurationClasses()) {
      showConfig(clazz);

    }
    return null;
  }

  private void showConfig(Class clazz) {
    OzoneConfiguration conf = new OzoneConfiguration();
    conf.addResource("http://localhost:9876/conf");
    ConfigGroup configGroup =
        (ConfigGroup) clazz.getAnnotation(ConfigGroup.class);
    if (configGroup == null) {
      return;
    }

    String prefix = configGroup.prefix();

    for (Method method : clazz.getMethods()) {
      if (method.isAnnotationPresent(Config.class)) {
        Config config = method.getAnnotation(Config.class);
        String key = prefix + "." + config.key();
        System.out.println(">>> " + key);
        System.out.println("       default: " + config.defaultValue());
        System.out.println("       current: " + conf.get(key));
        System.out.println();
        System.out.println(config.description());
        System.out.println();
        System.out.println();

      }
    }

  }

}
