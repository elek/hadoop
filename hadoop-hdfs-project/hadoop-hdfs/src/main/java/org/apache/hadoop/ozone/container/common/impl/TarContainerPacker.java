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
package org.apache.hadoop.ozone.container.common.impl;

import org.apache.commons.compress.archivers.ArchiveEntry;
import org.apache.commons.compress.archivers.ArchiveOutputStream;
import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
import org.apache.commons.compress.archivers.tar.TarArchiveOutputStream;
import org.apache.commons.compress.compressors.CompressorException;
import org.apache.commons.compress.compressors.CompressorInputStream;
import org.apache.commons.compress.compressors.CompressorOutputStream;
import org.apache.commons.compress.compressors.CompressorStreamFactory;
import org.apache.commons.io.IOUtils;

import org.apache.hadoop.ozone.container.common.helpers.ContainerData;
import org.apache.hadoop.ozone.container.common.helpers.ContainerUtils;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Collectors;

/**
 * Create a binary byte array from a container and vica versa.
 */
public class TarContainerPacker implements
    org.apache.hadoop.ozone.container.common.interfaces.ContainerPacker {

  public static final String DB_DIR_NAME = "db";

  public static final String CONTAINER_DATA_NAME = "container.data";

  public static final String DATA_DIR_NAME = "data";

  /**
   * Given an input stream (tar file) extract the data to the specified
   * directories.
   *
   * @param containerData container data which defined the destination dirs.
   * @param inputStream   conains the compressed tar file
   * @throws IOException
   */
  @Override
  public void unpack(ContainerData containerData, InputStream inputStream)
      throws IOException {

    try {
      CompressorInputStream compressorInputStream =
          new CompressorStreamFactory()
              .createCompressorInputStream(CompressorStreamFactory.GZIP,
                  inputStream);

      TarArchiveInputStream tarInput =
          new TarArchiveInputStream(compressorInputStream);

      TarArchiveEntry entry = tarInput.getNextTarEntry();
      while (entry != null) {
        String name = entry.getName();
        if (name.startsWith(DB_DIR_NAME + "/")) {
          Path destinationPath = Paths.get(containerData.getDBPath())
              .resolve(name.substring(DB_DIR_NAME.length() + 1));
          extractEntry(tarInput, entry.getSize(), destinationPath);
        } else if (name.startsWith(DATA_DIR_NAME + "/")) {
          Path destinationPath = Paths.get(containerData.getDBPath())
              .resolve(name.substring(DATA_DIR_NAME.length() + 1));
          extractEntry(tarInput, entry.getSize(), destinationPath);
        } else if (name.equals(CONTAINER_DATA_NAME)) {
          Path destinationPath = Paths.get(containerData.getContainerPath());
          extractEntry(tarInput, entry.getSize(), destinationPath);
        } else {
          throw new IllegalArgumentException(
              "Unknown entry in the tar file: " + "" + name);
        }
        entry = tarInput.getNextTarEntry();
      }

    } catch (CompressorException e) {
      throw new IOException(
          "Can't uncompress the given container: " + containerData.getName(),
          e);
    }
  }

  private void extractEntry(TarArchiveInputStream tarInput, long size,
      Path path) throws IOException {
    try (BufferedOutputStream bos = new BufferedOutputStream(
        new FileOutputStream(path.toAbsolutePath().toString()))) {
      int bufferSize = 1024;
      byte[] buffer = new byte[bufferSize + 1];
      long remaining = size;
      int offset = 0;
      while (remaining > 0) {
        int read =
            tarInput.read(buffer, 0, (int) Math.min(remaining, bufferSize));
        offset += read;
        remaining -= read;
        bos.write(buffer, 0, read);
      }
    }

  }

  /**
   * Given a containerData include all the required container data/metadata
   * in a tar file.
   *
   * @param containerData Container data, defined the dirs where the
   *                      data/metadata are stored.
   * @param destination   Destination tar file/stream.
   * @throws IOException
   */
  @Override
  public void pack(ContainerData containerData, OutputStream destination)
      throws IOException {

    try {
      CompressorOutputStream gzippedOut = new CompressorStreamFactory()
          .createCompressorOutputStream(CompressorStreamFactory.GZIP,
              destination);

      ArchiveOutputStream archiveOutputStream =
          new TarArchiveOutputStream(gzippedOut);

      includeFile(new File(containerData.getContainerPath()),
          CONTAINER_DATA_NAME, archiveOutputStream);

      includePath(containerData.getDBPath(), DB_DIR_NAME, archiveOutputStream);

      includePath(ContainerUtils.getDataDirectory(containerData).toString(),
          DATA_DIR_NAME, archiveOutputStream);

      archiveOutputStream.close();
      gzippedOut.close();
    } catch (CompressorException e) {
      throw new IOException(
          "Can't compress the container: " + containerData.getContainerName(),
          e);
    }

  }

  private void includePath(String containerPath, String subdir,
      ArchiveOutputStream archiveOutputStream) throws IOException {

    for (Path path : Files.list(Paths.get(containerPath))
        .collect(Collectors.toList())) {

      includeFile(path.toFile(), subdir + "/" + path.getFileName().toString(),
          archiveOutputStream);
    }
  }

  private void includeFile(File file, String entryName,
      ArchiveOutputStream archiveOutputStream) throws IOException {
    ArchiveEntry archiveEntry =
        archiveOutputStream.createArchiveEntry(file, entryName);
    archiveOutputStream.putArchiveEntry(archiveEntry);
    try (FileInputStream fis = new FileInputStream(file)) {
      IOUtils.copy(fis, archiveOutputStream);
    }
    archiveOutputStream.closeArchiveEntry();
  }
}
