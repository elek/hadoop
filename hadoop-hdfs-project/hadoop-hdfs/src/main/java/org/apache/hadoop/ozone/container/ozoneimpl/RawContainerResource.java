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

import javax.inject.Singleton;
import javax.servlet.ServletContext;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;

/**
 * Rest API to download raw container data for replication.
 */
@Path("/container")
@Singleton
public class RawContainerResource {

  public static final String OZONE_REPLICATION_SOURCE =
      "ozone.replication.source";

  @Context
  private ServletContext context;

  @GET()
  @Path("/{name}")
  public Response streamContainer(@PathParam("name") String containerName)
      throws Exception {

    ContainerReplicationSource replicationSource =
        (ContainerReplicationSource) context
            .getAttribute(OZONE_REPLICATION_SOURCE);

    return Response
        .ok(new ContainerStreamingOutput(containerName, replicationSource))
        .build();
  }

}
