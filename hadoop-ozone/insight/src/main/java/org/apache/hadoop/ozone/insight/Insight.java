package org.apache.hadoop.ozone.insight;

import org.apache.hadoop.hdds.cli.GenericCli;
import org.apache.hadoop.hdds.cli.HddsVersionProvider;

import picocli.CommandLine;

@CommandLine.Command(name = "ozone insight",
        hidden = true, description = "Show debug information about a selected Ozone component",
        versionProvider = HddsVersionProvider.class,
        subcommands = {List.class, LogSubcommand.class, MetricsSubCommand.class, Configuration.class},
        mixinStandardHelpOptions = true)
public class Insight extends GenericCli {

    public static void main(String[] args) throws Exception {
        new Insight().run(args);
    }

}
