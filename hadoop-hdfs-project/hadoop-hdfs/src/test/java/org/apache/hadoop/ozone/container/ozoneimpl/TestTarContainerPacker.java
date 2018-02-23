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

package org.apache.hadoop.ozone.container.ozoneimpl;

import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
import org.apache.commons.compress.compressors.CompressorException;
import org.apache.commons.compress.compressors.CompressorInputStream;
import org.apache.commons.compress.compressors.CompressorStreamFactory;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.hadoop.conf.OzoneConfiguration;
import org.apache.hadoop.ozone.container.common.helpers.ContainerData;
import org.apache.hadoop.ozone.container.common.impl.TarContainerPacker;
import org.apache.hadoop.ozone.container.common.interfaces.ContainerPacker;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Test the tar/untar for a given container.
 */
public class TestTarContainerPacker {

  public static final String TEST_DB_FILE_CONTENT = "test1";

  public static final String TEST_DB_FILE_NAME = "test1";

  private ContainerPacker packer = new TarContainerPacker();

  private static final Path SOURCE_CONTAINER_ROOT =
      Paths.get("target/test/data/packer-source-dir");

  private static final Path DEST_CONTAINER_ROOT =
      Paths.get("target/test/data/packer-dest-dir");

  @BeforeClass
  public static void init() throws IOException {
    initDir(SOURCE_CONTAINER_ROOT);
    initDir(DEST_CONTAINER_ROOT);
  }

  private static void initDir(Path path) throws IOException {
    if (path.toFile().exists()) {
      FileUtils.deleteDirectory(path.toFile());
    }
    path.toFile().mkdirs();
  }

  private ContainerData createContainer(String name, Path dir,
      OzoneConfiguration conf)
      throws IOException {
    Path containerDescriptor = dir.resolve("container");
    Path dbDir = dir.resolve("db");
    Path dataDir = dir.getParent().resolve("data");
    Files.createDirectories(dbDir);
    Files.createDirectories(dataDir);

    ContainerData containerData = new ContainerData(name, conf);
    containerData.setDBPath(dbDir.toString());
    containerData.setContainerPath(containerDescriptor.toString());
    return containerData;
  }

  @Test
  public void pack() throws IOException, CompressorException {
    OzoneConfiguration conf = new OzoneConfiguration();

    ContainerData sourceContainer = createContainer("source-container",
        SOURCE_CONTAINER_ROOT, conf);

    try (FileWriter writer = new FileWriter(
        Paths.get(sourceContainer.getDBPath()).resolve(TEST_DB_FILE_NAME)
            .toFile())) {
      IOUtils.write(TEST_DB_FILE_CONTENT, writer);
    }

    try (FileWriter writer = new FileWriter(
        Paths.get(sourceContainer.getContainerPath()).toFile())) {
      IOUtils.write("descriptor", writer);
    }

    Path targetFile = SOURCE_CONTAINER_ROOT.resolve("container.tar.gz");

    //pack it
    try (FileOutputStream output = new FileOutputStream(targetFile.toFile())) {
      packer.pack(sourceContainer, output);
    }

    //check the result
    try (FileInputStream input = new FileInputStream(targetFile.toFile())) {
      CompressorInputStream uncompressed = new CompressorStreamFactory()
          .createCompressorInputStream(CompressorStreamFactory.GZIP, input);
      TarArchiveInputStream tarStream = new TarArchiveInputStream(uncompressed);

      TarArchiveEntry entry;
      Map<String, TarArchiveEntry> entries = new HashMap<>();
      while ((entry = tarStream.getNextTarEntry()) != null) {
        entries.put(entry.getName(), entry);
      }

      Assert.assertTrue(entries.containsKey("container.data"));

    }

    ContainerData destinationContainer = createContainer(
        "destination-container", DEST_CONTAINER_ROOT, conf);

    //unpack
    try (FileInputStream input = new FileInputStream(targetFile.toFile())) {
      packer.unpack(destinationContainer, input);
    }

    Path metadataPath = Paths.get(destinationContainer.getContainerPath());
    Path dbDirPath = Paths.get(destinationContainer.getDBPath());

    //check the unpack
    Assert.assertTrue("Metadata file is missing after pack/unpack: " +
        metadataPath, Files.exists(metadataPath));

    //check the unpack
    Assert.assertTrue("DB dir is missing after pack/unpack: " +
        dbDirPath, Files.exists(dbDirPath));

    Path dbFile = dbDirPath.resolve(TEST_DB_FILE_NAME);

    Assert.assertTrue("test1 DB file is missing after pack/unpack: " +
        dbFile, Files.exists(dbFile));
    System.out.println(destinationContainer);

    try (FileInputStream testFile = new FileInputStream(dbFile.toFile())) {
      List<String> strings = IOUtils.readLines(testFile);
      Assert.assertEquals(1, strings.size());
      Assert.assertEquals(TEST_DB_FILE_CONTENT, strings.get(0));
    }

  }

}