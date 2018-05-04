/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.hadoop.hdds.server.org.apache.hdds.events;

import org.junit.Test;

public class TestEventQueueChain {

  @Test
  public void simpleEvent() {
    EventQueue queue = new EventQueue();

    queue.addHandler(EventType.DECOMMISSION, new PipelineManager());
    queue.addHandler(EventType.DECOMMISSION_START, new NodeWatcher());

    queue.fireEvent(EventType.DECOMMISSION, new FailedNode("node1"));

    queue.processAll(5000);
  }

  public enum EventType implements Event<FailedNode> {
    DECOMMISSION(FailedNode.class), DECOMMISSION_START(FailedNode.class);

    Class payload;

    <T> EventType(Class<T> payload) {
      this.payload = payload;
    }

    public Class getPayloadType() {
      return payload;
    }
  }

  public static class FailedNode {
    private String nodeId;

    public FailedNode(String nodeId) {
      this.nodeId = nodeId;
    }

    public String getNodeId() {
      return nodeId;
    }
  }

  private static class PipelineManager implements EventHandler<FailedNode> {

    @Override
    public void onMessage(FailedNode message, EventPublisher publisher) {

      System.out.println(
          "Closing pipelines for all pipelines including node: " + message
              .getNodeId());

      publisher.fireEvent(EventType.DECOMMISSION_START, message);
    }

  }

  private static class NodeWatcher implements EventHandler<FailedNode> {

    @Override
    public void onMessage(FailedNode message, EventPublisher publisher) {
      System.out.println("Clear timer");
    }
  }
}