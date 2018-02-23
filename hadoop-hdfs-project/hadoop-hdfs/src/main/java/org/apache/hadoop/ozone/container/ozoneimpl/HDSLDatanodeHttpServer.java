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
package org.apache.hadoop.ozone.container.ozoneimpl;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.ozone.OzoneConfigKeys;
import org.apache.hadoop.ozone.web.OzoneHttpServer;

import java.io.IOException;

/**
 * This Http server publish all the method which are required by the HDSL
 * functionality on datanode.
 */
public class HDSLDatanodeHttpServer extends OzoneHttpServer {

  public HDSLDatanodeHttpServer(Configuration conf) throws IOException {
    super(conf, "hdsl");
    getHttpServer().addJerseyResourcePackage(
        RawContainerResource.class.getPackage().getName(), "/api/*");

  }

  @Override
  protected String getHttpAddressKey() {
    return OzoneConfigKeys.HDLS_DATANODE_HTTP_ADDRESS_KEY;
  }

  @Override
  protected String getHttpBindHostKey() {
    return OzoneConfigKeys.HDLS_DATANODE_HTTP_BIND_HOST_KEY;
  }

  @Override
  protected String getHttpsAddressKey() {
    return OzoneConfigKeys.HDLS_DATANODE_HTTPS_ADDRESS_KEY;
  }

  @Override
  protected String getHttpsBindHostKey() {
    return OzoneConfigKeys.HDLS_DATANODE_HTTPS_BIND_HOST_KEY;
  }

  @Override
  protected String getBindHostDefault() {
    return OzoneConfigKeys.HDLS_DATANODE_HTTP_BIND_HOST_DEFAULT;
  }

  @Override
  protected int getHttpBindPortDefault() {
    return OzoneConfigKeys.HDLS_DATANODE_HTTP_BIND_PORT_DEFAULT;
  }

  @Override
  protected int getHttpsBindPortDefault() {
    return OzoneConfigKeys.HDLS_DATANODE_HTTPS_BIND_PORT_DEFAULT;
  }

  @Override
  protected String getKeytabFile() {
    return OzoneConfigKeys.HDLS_DATANODE_KEYTAB_FILE;
  }

  @Override
  protected String getSpnegoPrincipal() {
    return "";
  }

  @Override
  protected String getEnabledKey() {
    return OzoneConfigKeys.HDLS_DATANODE_HTTP_ENABLED_KEY;
  }

}
