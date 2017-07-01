package org.apache.hadoop.ozone.scm;

import org.apache.hadoop.ozone.scm.container.placement.metrics.SCMNodeStat;
import org.apache.hadoop.ozone.scm.node.SCMNodeManager;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by elek on 6/28/17.
 */
public class SCMMXBeanAdapter implements SCMMXBean {

  private final StorageContainerManager storageContainerManager;

  public SCMMXBeanAdapter(StorageContainerManager storageContainerManager) {
    this.storageContainerManager = storageContainerManager;
  }

  @Override
  public Map<String, Integer> getNodeCount() {
    Map<String, Integer> countMap = new HashMap<String, Integer>();
    for (SCMNodeManager.NODESTATE state : SCMNodeManager.NODESTATE.values()) {
      countMap.put(state.toString(),
          storageContainerManager.getScmNodeManager().getNodeCount(state));
    }
    return countMap;
  }

  @Override
  public String getDatanodeRpcPort() {
    return storageContainerManager.getDatanodeRpcPort();
  }

  @Override
  public String getClientRpcPort() {
    return storageContainerManager.getClientRpcPort();
  }

  @Override
  public Map<String, SCMNodeStat> getNodeStats() {
    return storageContainerManager.getScmNodeManager().getNodeStats();
  }

  @Override
  public SCMNodeStat getStats() {
    return storageContainerManager.getScmNodeManager().getStats();
  }
}
