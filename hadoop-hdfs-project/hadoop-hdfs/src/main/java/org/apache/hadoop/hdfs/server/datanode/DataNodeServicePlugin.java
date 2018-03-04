package org.apache.hadoop.hdfs.server.datanode;

import org.apache.hadoop.hdfs.protocol.DatanodeID;
import org.apache.hadoop.util.ServicePlugin;

/**
 * Datanode specific service plugin with additional hooks.
 */
public interface DataNodeServicePlugin extends ServicePlugin{

  /**
   * Extension point to modify the datanode id.
   *
   * @param dataNodeId
   */
  default void onDatanodeIdCreation(DatanodeID dataNodeId) {
    //NOOP
  }
}
