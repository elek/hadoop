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
package org.apache.hadoop.ozone.common.conf;

import java.util.List;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TestOzoneConfUtil {
  private static final Logger LOGGER = LoggerFactory.getLogger(TestOzoneConfUtil.class);

  @Test
  public void testRequiredProperties() throws Exception {
    OzoneConfiguration conf = new OzoneConfiguration();
    conf.addResource(this.getClass().getClassLoader().getResourceAsStream
        ("ozone-default.xml"));
    conf.reloadConfiguration();
    // Validate default properties
    List<String> invalidProperties = OzoneConfUtil.validateConfiguration(conf);

    Assert.assertFalse(invalidProperties.contains("dfs.cblock.disk.cache"
        + ".path"));
    Assert.assertTrue(invalidProperties.contains("ozone.scm.client.address"));
    Assert.assertTrue(invalidProperties.contains("ozone.metadata.dirs"));
    Assert.assertFalse(invalidProperties.contains("ozone.enabled"));
    Assert.assertFalse(invalidProperties.contains("ozone.scm.chunk.size"));

    // Validate modified properties
    conf.set("ozone.metadata.dirs", "\u0000");
    conf.set("dfs.cblock.disk.cache.path", "");
    conf.set("ozone.metadata.dirs", "");
    conf.set("ozone.scm.client.address", "");
    conf.set("ozone.enabled", "");
    conf.set("ozone.scm.container.size.gb","abc");
    conf.set("ozone.scm.keytab.file","nonexistentfile.keytab");
    invalidProperties = OzoneConfUtil.validateConfiguration(conf);

    Assert.assertTrue(invalidProperties.contains("dfs.cblock.disk.cache.path"));
    Assert.assertTrue(invalidProperties.contains("ozone.scm.client.address"));
    Assert.assertTrue(invalidProperties.contains("ozone.metadata.dirs"));
    Assert.assertTrue(invalidProperties.contains("ozone.enabled"));
    Assert.assertTrue(invalidProperties.contains("ozone.ksm.address"));
    Assert.assertTrue(invalidProperties.contains("dfs.cblock.servicerpc-address"));
    Assert.assertTrue(invalidProperties.contains("ozone.scm.container.size.gb"));
  }

}
