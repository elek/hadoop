package org.apache.hadoop.utils;

import com.google.common.primitives.UnsignedBytes;
import org.apache.commons.lang3.tuple.ImmutablePair;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class InMemoryStore implements MetadataStore {

  private Map<byte[], byte[]> values =
      new TreeMap<>(UnsignedBytes.lexicographicalComparator());

  @Override
  public void put(byte[] key, byte[] value) throws IOException {
    values.put(key, value);
  }

  @Override
  public boolean isEmpty() throws IOException {
    return values.isEmpty();
  }

  @Override
  public byte[] get(byte[] key) throws IOException {
    return values.get(key);
  }

  @Override
  public void delete(byte[] key) throws IOException {
    values.remove(key);
  }

  @Override
  public List<Map.Entry<byte[], byte[]>> getRangeKVs(byte[] startKey, int count,
      MetadataKeyFilters.MetadataKeyFilter... filters)
      throws IOException, IllegalArgumentException {
    Stream<Map.Entry<byte[], byte[]>> stream =
        values.entrySet().stream().sequential();

    if (startKey != null) {
      stream = stream.filter(new FromStartKey(startKey));
    }

    if (count > -1) {
      stream = stream.limit(count);
    }
    for (MetadataKeyFilters.MetadataKeyFilter filter : filters) {
      stream = stream.filter(new MetadataStreamFilter(filter));
    }
    return stream.collect(Collectors.toList());
  }

  @Override
  public List<Map.Entry<byte[], byte[]>> getSequentialRangeKVs(byte[] startKey,
      int count, MetadataKeyFilters.MetadataKeyFilter... filters)
      throws IOException, IllegalArgumentException {
    return null;
  }

  @Override
  public void writeBatch(BatchOperation operation) throws IOException {

  }

  @Override
  public void compactDB() throws IOException {

  }

  @Override
  public void destroy() throws IOException {
    values.clear();
  }

  @Override
  public ImmutablePair<byte[], byte[]> peekAround(int offset, byte[] from)
      throws IOException, IllegalArgumentException {
    Stream<Map.Entry<byte[], byte[]>> stream =
        values.entrySet().stream().sequential();
    if (offset == 0) {
      return stream.filter(new FromStartKey(from)).findFirst().get();
    }
  }

  @Override
  public void iterate(byte[] from, EntryConsumer consumer) throws IOException {
    Stream<Map.Entry<byte[], byte[]>> stream =
        values.entrySet().stream().sequential();
    if (from != null) {
      stream = stream.filter(new FromStartKey(from));
    }
    stream.forEach(new Consumer<Map.Entry<byte[], byte[]>>() {
      @Override
      public void accept(Map.Entry<byte[], byte[]> entry) {
        try {
          consumer.consume(entry.getKey(), entry.getValue());
        } catch (IOException e) {
          e.printStackTrace();
        }
      }
    });
  }

  @Override
  public void close() throws IOException {

  }

  private static class FromStartKey
      implements Predicate<Map.Entry<byte[], byte[]>> {

    private byte[] startKey;

    boolean started = false;

    public FromStartKey(byte[] startKey) {
      this.startKey = startKey;
    }

    @Override
    public boolean test(Map.Entry<byte[], byte[]> entry) {
      if (started) {
        return true;
      } else if (Arrays.equals(startKey, entry.getKey())) {
        started = true;
        return true;
      } else {
        return false;
      }
    }
  }

  private class MetadataStreamFilter
      implements Predicate<Map.Entry<byte[], byte[]>> {

    private MetadataKeyFilters.MetadataKeyFilter filter;

    public MetadataStreamFilter(
        MetadataKeyFilters.MetadataKeyFilter filter) {
      this.filter = filter;
    }

    @Override
    public boolean test(Map.Entry<byte[], byte[]> entry) {
      return false;
    }
  }
}
