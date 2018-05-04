package org.apache.hadoop.hdds.server.org.apache.hdds.events;

import java.util.Objects;

/**
 * Basic event implementation to implement custom events.
 *
 * @param <T>
 */
public class TypedEvent<T> implements Event<T> {

  private Class<T> payloadType;

  public TypedEvent(Class<T> payloadType) {
    this.payloadType = payloadType;
  }

  @Override
  public Class<T> getPayloadType() {
    return payloadType;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    TypedEvent<?> that = (TypedEvent<?>) o;
    return Objects.equals(payloadType, that.payloadType);
  }

  @Override
  public int hashCode() {
    return Objects.hash(payloadType);
  }
}
