package org.apache.hadoop.ozone.insight.scm;

import org.apache.hadoop.hdds.scm.node.SCMNodeManager;
import org.apache.hadoop.hdds.server.events.EventQueue;
import org.apache.hadoop.ozone.insight.BaseInsightPoint;
import org.apache.hadoop.ozone.insight.Component.Type;
import org.apache.hadoop.ozone.insight.LoggerSource;
import org.apache.hadoop.ozone.insight.MetricDisplay;
import org.apache.hadoop.ozone.insight.MetricGroupDisplay;

import java.util.ArrayList;
import java.util.List;

/**
 * Insight definition to check node manager / node report events.
 */
public class NodeManagerInsight extends BaseInsightPoint {

  @Override
  public List<LoggerSource> getRelatedLoggers(boolean verbose) {
    List<LoggerSource> loggers = new ArrayList<>();
    loggers.add(
        new LoggerSource(Type.SCM, SCMNodeManager.class, defaultLevel(verbose)));
    return loggers;
  }

  @Override
  public List<MetricGroupDisplay> getMetrics() {
    List<MetricGroupDisplay> display = new ArrayList<>();

    MetricGroupDisplay nodes = new MetricGroupDisplay(Type.SCM, "Node counters");
    nodes.addMetrics(
        new MetricDisplay("Healthy Nodes", "scm_node_manager_healthy_nodes"));
    nodes.addMetrics(
        new MetricDisplay("Dead Nodes", "scm_node_manager_dead_nodes"));

    display.add(nodes);

    MetricGroupDisplay hb =
        new MetricGroupDisplay(Type.SCM, "HB processing stats");
    hb.addMetrics(
        new MetricDisplay("HB processed", "scm_node_manager_num_hb_processed"));
    hb.addMetrics(new MetricDisplay("HB processing failed",
        "scm_node_manager_num_hb_processing_failed"));
    display.add(hb);

    return display;
  }

  @Override
  public String getDescription() {
    return "SCM Datanode management related information.";
  }

}
