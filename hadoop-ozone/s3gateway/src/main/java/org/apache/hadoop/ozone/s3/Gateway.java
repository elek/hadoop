package org.apache.hadoop.ozone.s3;

import java.net.URI;
import java.net.URL;
import java.security.ProtectionDomain;

import org.apache.hadoop.hdds.cli.GenericCli;
import org.apache.hadoop.hdds.cli.HddsVersionProvider;
import org.apache.hadoop.hdds.scm.cli.container.CloseSubcommand;
import org.apache.hadoop.hdds.scm.cli.container.CreateSubcommand;
import org.apache.hadoop.hdds.scm.cli.container.DeleteSubcommand;
import org.apache.hadoop.hdds.scm.cli.container.InfoSubcommand;
import org.apache.hadoop.hdds.scm.cli.container.ListSubcommand;
import org.apache.hadoop.http.HttpServer2;

import org.eclipse.jetty.webapp.WebAppContext;
import picocli.CommandLine.Command;

@Command(name = "ozone s3g",
    hidden = true, description = "S3 compatible rest server.",
    versionProvider = HddsVersionProvider.class,
    mixinStandardHelpOptions = true)
public class Gateway extends GenericCli {

  public static void main(String[] args) throws Exception {
    new Gateway().run(args);
  }

  @Override
  public Void call() throws Exception {

    ProtectionDomain domain = Gateway.class.getProtectionDomain();
    URL location = domain.getCodeSource().getLocation();
    HttpServer2 server = new HttpServer2.Builder()
        .setName("s3gateway")
        .addEndpoint(new URI("http://localhost:1234"))
        .build();

    server.start();
    return null;
  }
}
