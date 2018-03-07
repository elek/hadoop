/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with this
 * work for additional information regarding copyright ownership.  The ASF
 * licenses this file to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.apache.hadoop.ozone.web;

import java.io.Closeable;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.ozone.OzoneConfigKeys;
import org.apache.hadoop.ozone.client.rest.OzoneExceptionMapper;
import org.apache.hadoop.ozone.web.handlers.BucketHandler;
import org.apache.hadoop.ozone.web.handlers.KeyHandler;
import org.apache.hadoop.ozone.web.handlers.VolumeHandler;
import org.apache.hadoop.ozone.web.interfaces.StorageHandler;
import org.apache.hadoop.ozone.web.messages.LengthInputStreamMessageBodyWriter;
import org.apache.hadoop.ozone.web.messages.StringMessageBodyWriter;
import org.apache.hadoop.scm.ScmConfigKeys;

public class ObjectStoreRestServer extends OzoneHttpServer
    implements Closeable {

  public ObjectStoreRestServer(Configuration conf, StorageHandler handler)
      throws IOException {
    super(conf, "ozone", builder -> {
      builder.withoutDefaultApps();
      builder.withoutDefaultServlet();
      builder.withoutCommonServlets();
    });
    List<String> classes = new ArrayList<>();
    classes.add(VolumeHandler.class.getCanonicalName());
    classes.add(BucketHandler.class.getCanonicalName());
    classes.add(KeyHandler.class.getCanonicalName());
    classes.add(OzoneExceptionMapper.class.getCanonicalName());
    classes.add(LengthInputStreamMessageBodyWriter.class.getCanonicalName());
    classes.add(StringMessageBodyWriter.class.getCanonicalName());
    getHttpServer().addJerseyResourceClasses(classes
        , "/*");

    getHttpServer().setAttribute(StorageHandler.class.toString(), handler);
    getHttpServer().addGlobalFilter("storageHandler",
        StorageHandlerFilter.class.getCanonicalName().toString(),
        new HashMap<>());
  }

  @Override
  protected String getHttpAddressKey() {
    return ScmConfigKeys.OZONE_hdsl_HTTP_ADDRESS_KEY;
  }

  @Override
  protected String getHttpBindHostKey() {
    return ScmConfigKeys.OZONE_hdsl_HTTP_BIND_HOST_KEY;
  }

  @Override
  protected String getHttpsAddressKey() {
    return ScmConfigKeys.OZONE_hdsl_HTTPS_ADDRESS_KEY;
  }

  @Override
  protected String getHttpsBindHostKey() {
    return ScmConfigKeys.OZONE_hdsl_HTTPS_BIND_HOST_KEY;
  }

  @Override
  protected String getBindHostDefault() {
    return ScmConfigKeys.OZONE_hdsl_HTTP_BIND_HOST_DEFAULT;
  }

  @Override
  protected int getHttpBindPortDefault() {
    return ScmConfigKeys.OZONE_hdsl_HTTP_BIND_PORT_DEFAULT;
  }

  @Override
  protected int getHttpsBindPortDefault() {
    return ScmConfigKeys.OZONE_hdsl_HTTPS_BIND_PORT_DEFAULT;
  }

  @Override
  protected String getKeytabFile() {
    return ScmConfigKeys.OZONE_hdsl_KEYTAB_FILE;
  }

  @Override
  protected String getSpnegoPrincipal() {
    return OzoneConfigKeys.OZONE_SCM_WEB_AUTHENTICATION_KERBEROS_PRINCIPAL;
  }

  @Override
  protected String getEnabledKey() {
    return ScmConfigKeys.OZONE_hdsl_HTTP_ENABLED_KEY;
  }

  @Override
  public void close() throws IOException {
    try {
      getHttpServer().stop();
    } catch (Exception e) {
      throw new IOException("Can stop the object store rest http server", e);
    }
  }
}
