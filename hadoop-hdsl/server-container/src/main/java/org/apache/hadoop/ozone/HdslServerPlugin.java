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
package org.apache.hadoop.ozone;

import java.io.IOException;

import org.apache.hadoop.classification.InterfaceAudience;
import org.apache.hadoop.hdfs.protocol.DatanodeID;
import org.apache.hadoop.hdfs.server.datanode.DataNode;
import org.apache.hadoop.hdfs.server.datanode.DataNodeServicePlugin;
import org.apache.hadoop.hdfs.server.protocol.DatanodeRegistration;
import org.apache.hadoop.hdsl.conf.OzoneConfiguration;
import org.apache.hadoop.ozone.container.common.statemachine
    .DatanodeStateMachine;
import org.apache.hadoop.ozone.web.utils.OzoneUtils;

public class HdslServerPlugin implements DataNodeServicePlugin {

  private DatanodeStateMachine datanodeStateMachine;
  private DataNode dataNode;

  public HdslServerPlugin() {
    OzoneConfiguration.activate();
  }

  @Override
  public void start(Object service) {
    dataNode = (DataNode) service;
  }

  @Override
  public void onDatanodeSuccessfulNamenodeRegisration(
      DatanodeRegistration dataNodeId) {
    if (OzoneUtils.isOzoneEnabled(dataNode.getConf())) {
      try {
        datanodeStateMachine =
            new DatanodeStateMachine(dataNodeId,
                dataNode.getConf());
        datanodeStateMachine.startDaemon();
      } catch (IOException e) {
        throw new RuntimeException("Can't start the HDSL server plugin", e);
      }

    }
  }

  @Override
  public void stop() {
    if (datanodeStateMachine != null) {
      datanodeStateMachine.stopDaemon();
    }
  }

  @Override
  public void close() throws IOException {
  }

  @InterfaceAudience.Private
  public DatanodeStateMachine getDatanodeStateMachine() {
    return datanodeStateMachine;
  }
}
