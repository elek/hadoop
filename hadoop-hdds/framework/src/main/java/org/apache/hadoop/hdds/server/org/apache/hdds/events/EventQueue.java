package org.apache.hadoop.hdds.server.org.apache.hdds.events;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

import org.apache.hadoop.util.Time;

import com.google.common.annotations.VisibleForTesting;

public class EventQueue implements EventPublisher {

  private Map<Class<? extends Event>, List<EventExecutor>> executors =
      new HashMap<>();

  private AtomicLong queuedCount = new AtomicLong(0);
  private AtomicLong eventCount = new AtomicLong(0);

  public <PAYLOAD, EVENT_TYPE extends Event<PAYLOAD>> void addHandler(
      EVENT_TYPE event, EventHandler<PAYLOAD> handler) {

    executors.putIfAbsent(event.getClass(), new ArrayList<>());
    executors.get(event.getClass())
        .add(new SingleThreadExecutor<PAYLOAD>(handler));
  }

  public void addHandlerFromAnnotations(Object o) {
    for (Method method : o.getClass().getDeclaredMethods()) {
      if (method.isAnnotationPresent(Subscribe.class)) {
        Subscribe subscribe = method.getAnnotation(Subscribe.class);
        String locationDebugStr = String
            .format("Class: %s, Method: %s", o.getClass(), method.getName());

        if (subscribe.event() == null) {

          throw new IllegalArgumentException(
              "event parameter is missing from Subscribe annotation."
                  + locationDebugStr);
        }

        SingleThreadExecutor executor =
            new SingleThreadExecutor((payload, publisher) -> {
              try {
                method.invoke(o, payload, publisher);
              } catch (IllegalAccessException e) {
                throw new AssertionError(
                    "Subscriber method can't be call. " + locationDebugStr, e);
              } catch (InvocationTargetException e) {
                throw new AssertionError(
                    "Subscriber method can't be call. " + locationDebugStr, e);
              }
            });

        executors.putIfAbsent(subscribe.event(), new ArrayList<>());
        executors.get(subscribe.event()).add(executor);
      }
    }
  }

  public <PAYLOAD, EVENT_TYPE extends Event<PAYLOAD>> void fireEvent(
      EVENT_TYPE event, PAYLOAD payload) {

    List<EventExecutor> handlers = executors.get(event.getClass());

    if (handlers != null) {
      eventCount.incrementAndGet();
      for (EventExecutor handler : handlers) {
        queuedCount.incrementAndGet();
        handler.onMessage(payload, this);
      }

    } else {
      throw new IllegalArgumentException(
          "No event handler registered for event " + event);
    }

  }

  /**
   * This is just for unit testing, don't use it from production code.
   * <p>
   * It wait's for all messages to be processed. If one event handler invokes an
   * other one, the later one also should be finished.
   * <p>
   * Long counter overflow is not handler, therefore it's safe only for unit
   * testing.
   *
   * @param timeout
   */
  @VisibleForTesting
  public void processAll(long timeout) {
    long currentTime = Time.now();
    while (true) {

      long processed = 0;
      for (List<EventExecutor> executorList : executors.values()) {
        for (EventExecutor executor : executorList) {
          processed += executor.processedEvents();
        }
      }
      if (processed == queuedCount.get()) {
        return;
      }

      try {
        Thread.sleep(100);
      } catch (InterruptedException e) {
        e.printStackTrace();
      }

      if (Time.now() > currentTime + timeout) {
        throw new AssertionError(
            "Messages are not processed in the given timeframe. Queued: "
                + queuedCount.get() + " Processed: " + processed);
      }
    }
  }
}
