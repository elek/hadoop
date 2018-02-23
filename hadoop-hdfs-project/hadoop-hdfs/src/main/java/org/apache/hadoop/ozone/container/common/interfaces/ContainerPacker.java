package org.apache.hadoop.ozone.container.common.interfaces;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.apache.hadoop.ozone.container.common.helpers.ContainerData;

/**
 * Service to pack/unpack container data to/from a single byte stream.
 */
public interface ContainerPacker {

  void unpack(ContainerData containerData, InputStream inputStream)
      throws IOException;

  void pack(ContainerData containerData, OutputStream destination)
          throws IOException;
}
