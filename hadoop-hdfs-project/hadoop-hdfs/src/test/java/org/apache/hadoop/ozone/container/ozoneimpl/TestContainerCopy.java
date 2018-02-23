/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 *  with the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package org.apache.hadoop.ozone.container.ozoneimpl;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;

import org.apache.hadoop.conf.OzoneConfiguration;
import org.apache.hadoop.hdfs.ozone.protocol.proto.ContainerProtos;
import org.apache.hadoop.hdfs.ozone.protocol.proto.ContainerProtos
    .ContainerCommandRequestProto;
import org.apache.hadoop.hdfs.ozone.protocol.proto.ContainerProtos
    .ContainerCommandResponseProto;
import org.apache.hadoop.hdfs.protocol.DatanodeID;
import org.apache.hadoop.hdfs.server.datanode.DataNode;
import org.apache.hadoop.ozone.MiniOzoneClassicCluster;
import org.apache.hadoop.ozone.OzoneConsts;
import org.apache.hadoop.ozone.container.ContainerTestHelper;
import org.apache.hadoop.ozone.container.common.helpers.KeyData;
import org.apache.hadoop.ozone.protocol.commands.CopyContainerCommand;
import org.apache.hadoop.scm.XceiverClient;
import org.apache.hadoop.scm.container.common.helpers.Pipeline;

import static org.apache.hadoop.ozone.container.ozoneimpl.TestOzoneContainer
    .writeChunkForContainer;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.Timeout;

/**
 * Tests ozone containers replication.
 */
public class TestContainerCopy {
  /**
   * Set the timeout for every test.
   */
  @Rule
  public Timeout testTimeout = new Timeout(300000);

  @Test
  public void testContainerReplication() throws Exception {
    OzoneConfiguration conf = newOzoneConfiguration();

    String containerName = "testcontainer";

    conf.setSocketAddr("hdls.datanode.http-address",
        new InetSocketAddress("0.0.0.0", 0));

    try (MiniOzoneClassicCluster cluster = new MiniOzoneClassicCluster.Builder(
        conf).numDataNodes(4).setRandomContainerPort(true)
        .setHandlerType(OzoneConsts.OZONE_HANDLER_DISTRIBUTED).build()) {
      cluster.waitOzoneReady();

      DataNode firstDatanode = cluster.getDataNodes().get(0);

      List<DatanodeID> piplineDatanodes = new ArrayList<>();
      piplineDatanodes.add(firstDatanode.getDatanodeId());

      Pipeline pipeline =
          ContainerTestHelper.createPipeline(containerName, piplineDatanodes);

      // This client talks to ozone container via destinationDatanode.
      XceiverClient client = new XceiverClient(pipeline, conf);
      client.connect();

      TestOzoneContainer.createContainerForTesting(client, containerName);

      ContainerCommandRequestProto writeChunkRequest =
          writeChunkForContainer(client, containerName, 1024);

      String keyName = writeChunkRequest.getWriteChunk().getKeyName();
      // Put Key
      ContainerCommandRequestProto putKeyRequest = ContainerTestHelper
          .getPutKeyRequest(writeChunkRequest.getWriteChunk());

      ContainerCommandResponseProto response =
          client.sendCommand(putKeyRequest);

      Assert.assertNotNull(response);
      Assert.assertEquals(ContainerProtos.Result.SUCCESS, response.getResult());
      Assert
          .assertTrue(putKeyRequest.getTraceID().equals(response.getTraceID()));

      DataNode destinationDatanode =
          chooseDatanodeWithoutContainer(pipeline, cluster.getDataNodes());

      //send the order to close the container
      cluster.getStorageContainerManager().getScmNodeManager()
          .addDatanodeCommand(destinationDatanode.getDatanodeId(),
              new CopyContainerCommand(containerName, pipeline));

      Thread.sleep(3000);

      KeyData key =
          destinationDatanode.getOzoneContainerManager().getContainerManager()
              .getKeyManager().getKey(new KeyData(containerName, keyName));

      Assert.assertNotNull(key);
      Assert.assertEquals(1, key.getChunks().size());
      Assert.assertEquals(writeChunkRequest.getWriteChunk().getChunkData(),
          key.getChunks().get(0));

    } catch (Exception ex) {
      ex.printStackTrace();
    }
  }

  private DataNode chooseDatanodeWithoutContainer(Pipeline pipeline,
      ArrayList<DataNode> dataNodes) {
    for (DataNode datanode : dataNodes) {
      if (!pipeline.getMachines().contains(datanode.getDatanodeId())) {
        return datanode;
      }
    }
    throw new AssertionError("No datanode outside of the pipeline");
  }

  static OzoneConfiguration newOzoneConfiguration() {
    final OzoneConfiguration conf = new OzoneConfiguration();
    return conf;
  }

}
