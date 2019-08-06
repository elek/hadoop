package org.apache.hadoop.ozone.insight;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

import org.apache.hadoop.hdds.HddsUtils;
import org.apache.hadoop.hdds.conf.OzoneConfiguration;
import org.apache.hadoop.hdds.scm.ScmConfigKeys;
import org.apache.hadoop.ozone.insight.Component.Type;
import org.apache.hadoop.ozone.insight.datanode.RatisInsight;
import org.apache.hadoop.ozone.insight.om.KeyManagerInsight;
import org.apache.hadoop.ozone.insight.om.OmProtocolInsight;
import org.apache.hadoop.ozone.insight.scm.EventQueueInsight;
import org.apache.hadoop.ozone.insight.scm.NodeManagerInsight;
import org.apache.hadoop.ozone.insight.scm.ReplicaManagerInsight;
import org.apache.hadoop.ozone.insight.scm.ScmProtocolBlockLocationInsight;
import org.apache.hadoop.ozone.om.OMConfigKeys;

import picocli.CommandLine;

public class BaseInsightSubcommand {

  @CommandLine.ParentCommand
  private Insight insightCommand;

  public InsightPoint getInsight(OzoneConfiguration configuration,
      String selection) {
    Map<String, InsightPoint> insights = createInsightPoints(configuration);

    if (!insights.containsKey(selection)) {
      throw new RuntimeException(String
          .format("No such component; %s. Available components: %s", selection,
              insights.keySet()));
    }
    return insights.get(selection);
  }

  /**
   * Utility to get the host base on a component.
   */
  public String getHost(OzoneConfiguration conf, Component component) {
    if (component.getHostname() != null) {
      return "http://" + component.getHostname() + ":" + component.getPort();
    } else if (component.getName() == Type.SCM) {
      Optional<String> scmHost =
          HddsUtils.getHostNameFromConfigKeys(conf,
              ScmConfigKeys.OZONE_SCM_BLOCK_CLIENT_ADDRESS_KEY,
              ScmConfigKeys.OZONE_SCM_CLIENT_ADDRESS_KEY);

      return "http://" + scmHost.get() + ":9876";
    } else if (component.getName() == Type.OM) {
      Optional<String> omHost =
          HddsUtils.getHostNameFromConfigKeys(conf,
              OMConfigKeys.OZONE_OM_ADDRESS_KEY);
      return "http://" + omHost.get() + ":9874";
    } else {
      throw new IllegalArgumentException(
          "Component type is not supported: " + component.getName());
    }

  }

  public Map<String, InsightPoint> createInsightPoints(
      OzoneConfiguration configuration) {
    Map<String, InsightPoint> insights = new LinkedHashMap<>();
    insights.put("scm.node-manager", new NodeManagerInsight());
    insights.put("scm.replica-manager", new ReplicaManagerInsight());
    insights.put("scm.event-queue", new EventQueueInsight());
    insights.put("scm.protocol.block-location",
        new ScmProtocolBlockLocationInsight());
    insights.put("scm.protocol.container-location", new StubInsightPoint());
    insights.put("scm.protocol.datanode", new StubInsightPoint());
    insights.put("scm.protocol.security", new StubInsightPoint());
    insights.put("scm.http", new StubInsightPoint());

    insights.put("om.key-manager", new KeyManagerInsight());
    insights.put("om.protocol.client", new OmProtocolInsight());
    insights.put("om.http", new StubInsightPoint());

    insights.put("datanode.pipeline[id]", new RatisInsight(configuration));
    insights.put("datanode.rocksdb", new RatisInsight(configuration));

    insights.put("s3g.http", new StubInsightPoint());

    return insights;
  }

  public Insight getInsightCommand() {
    return insightCommand;
  }
}
