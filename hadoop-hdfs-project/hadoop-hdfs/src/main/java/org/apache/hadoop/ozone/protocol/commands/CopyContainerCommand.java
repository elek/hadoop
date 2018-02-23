/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.hadoop.ozone.protocol.commands;

import org.apache.hadoop.ozone.protocol.proto
    .StorageContainerDatanodeProtocolProtos;
import org.apache.hadoop.ozone.protocol.proto
    .StorageContainerDatanodeProtocolProtos.SCMCloseContainerCmdResponseProto;
import org.apache.hadoop.ozone.protocol.proto
    .StorageContainerDatanodeProtocolProtos.Type;
import org.apache.hadoop.scm.container.common.helpers.Pipeline;

import com.google.common.base.Preconditions;

import static org.apache.hadoop.ozone.protocol.proto
    .StorageContainerDatanodeProtocolProtos.Type.copyContainerCommand;

/**
 * Asks datanode to copy a container from other datanodes.
 */
public class CopyContainerCommand
    extends SCMCommand<SCMCloseContainerCmdResponseProto> {

  private String containerName;

  private Pipeline source;

  public CopyContainerCommand(String containerName,
      Pipeline source) {
    this.containerName = containerName;
    this.source = source;
  }

  /**
   * Returns the type of this command.
   *
   * @return Type
   */
  @Override
  public Type getType() {
    return copyContainerCommand;
  }

  /**
   * Gets the protobuf message of this object.
   *
   * @return A protobuf message.
   */
  @Override
  public byte[] getProtoBufMessage() {
    return getProto().toByteArray();
  }

  public StorageContainerDatanodeProtocolProtos
      .SCMCopyContainerCmdResponseProto getProto() {
    return StorageContainerDatanodeProtocolProtos
        .SCMCopyContainerCmdResponseProto.newBuilder()
        .setPipeline(source.getProtobufMessage())
        .setContainerName(containerName)
        .build();
  }

  public static CopyContainerCommand getFromProtobuf(
      StorageContainerDatanodeProtocolProtos.SCMCopyContainerCmdResponseProto
          copyContainerCmdResponseProto) {
    Preconditions.checkNotNull(copyContainerCmdResponseProto);
    return new CopyContainerCommand(copyContainerCmdResponseProto
        .getContainerName(), Pipeline.getFromProtoBuf(
            copyContainerCmdResponseProto.getPipeline()));

  }

  public String getContainerName() {
    return containerName;
  }

  public Pipeline getSource() {
    return source;
  }
}
