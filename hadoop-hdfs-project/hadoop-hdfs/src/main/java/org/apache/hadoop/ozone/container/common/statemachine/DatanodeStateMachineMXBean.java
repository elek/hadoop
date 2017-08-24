package org.apache.hadoop.ozone.container.common.statemachine;

import java.util.List;

/**
 * JMX information about the Ozone part of Datanode.
 */
public interface DatanodeStateMachineMXBean {
  List<String> getNodeReport();
}
