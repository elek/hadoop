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
package org.apache.hadoop.hdds.server.org.apache.hdds.events;

import org.junit.Assert;
import org.junit.Test;

public class TestEventQueue {

  public static final Event<Long> EVENT1 = new TypedEvent<Long>(Long.class);

  private long result;

  @Test
  public void simpleEvent() {
    EventQueue queue = new EventQueue();

    queue.addHandler(EVENT1, (payload, publisher) -> result = payload);

    queue.fireEvent(EVENT1, new Long(11));
    queue.processAll(1000);
    Assert.assertEquals(11, result);

  }

  @Test
  public void multipleSubscriber() {
    EventQueue queue = new EventQueue();
    final long[] result = new long[2];
    queue.addHandler(EVENT1, (payload, publisher) -> result[0] = payload);

    queue.addHandler(EVENT1, (payload, publisher) -> result[1] = payload);

    queue.fireEvent(EVENT1, new Long(23));
    queue.processAll(1000);
    Assert.assertEquals(23, result[0]);
    Assert.assertEquals(23, result[1]);

  }

}