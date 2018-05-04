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

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Simple EventExecutor to call all the event handler one-by-one.
 *
 * @param <T>
 */
public class SingleThreadExecutor<T> implements EventExecutor<T> {

  private static final Logger LOG =
      LoggerFactory.getLogger(SingleThreadExecutor.class);

  private ThreadPoolExecutor executor;

  private EventHandler<T> handler;

  private final LinkedBlockingQueue<Runnable> workQueue;

  private AtomicLong processedCount = new AtomicLong(0);

  public SingleThreadExecutor(EventHandler<T> handler) {
    this.handler = handler;

    workQueue = new LinkedBlockingQueue<>();
    executor =
        new ThreadPoolExecutor(1, 1, 0L, TimeUnit.MILLISECONDS, workQueue);

  }

  @Override
  public void onMessage(T message, EventPublisher publisher) {
    executor.execute(() -> {
      try {
        handler.onMessage(message, publisher);
      } catch (Exception ex) {
        LOG.error("Error on execution message {} ", message, ex);
      } finally {
        processedCount.incrementAndGet();
      }
    });
  }

  @Override
  public int todoItems() {
    return workQueue.size();
  }

  @Override
  public long processedEvents() {
    return processedCount.get();

  }

}
