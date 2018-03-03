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
package org.apache.hadoop.scm;

import org.apache.hadoop.classification.InterfaceAudience;
import org.apache.hadoop.classification.InterfaceStability;

/**
 * This class contains constants for configuration keys used in SCM.
 */
@InterfaceAudience.Public
@InterfaceStability.Unstable
public final class ScmConfigKeys {

  public static final String SCM_CONTAINER_CLIENT_STALE_THRESHOLD_KEY =
      "scm.container.client.idle.threshold";
  public static final String SCM_CONTAINER_CLIENT_STALE_THRESHOLD_DEFAULT =
      "10s";

  public static final String SCM_CONTAINER_CLIENT_MAX_SIZE_KEY =
      "scm.container.client.max.size";
  public static final int SCM_CONTAINER_CLIENT_MAX_SIZE_DEFAULT =
      256;

  public static final String SCM_CONTAINER_CLIENT_MAX_OUTSTANDING_REQUESTS =
      "scm.container.client.max.outstanding.requests";
  public static final int SCM_CONTAINER_CLIENT_MAX_OUTSTANDING_REQUESTS_DEFAULT
      = 100;

  public static final String DFS_CONTAINER_RATIS_ENABLED_KEY
      = "dfs.container.ratis.enabled";
  public static final boolean DFS_CONTAINER_RATIS_ENABLED_DEFAULT
      = false;
  public static final String DFS_CONTAINER_RATIS_RPC_TYPE_KEY
      = "dfs.container.ratis.rpc.type";
  public static final String DFS_CONTAINER_RATIS_RPC_TYPE_DEFAULT
      = "GRPC";
  public static final String DFS_CONTAINER_RATIS_NUM_WRITE_CHUNK_THREADS_KEY
      = "dfs.container.ratis.num.write.chunk.threads";
  public static final int DFS_CONTAINER_RATIS_NUM_WRITE_CHUNK_THREADS_DEFAULT
      = 60;
  public static final String DFS_CONTAINER_RATIS_SEGMENT_SIZE_KEY =
      "dfs.container.ratis.segment.size";
  public static final int DFS_CONTAINER_RATIS_SEGMENT_SIZE_DEFAULT =
      1 * 1024 * 1024 * 1024;
  public static final String DFS_CONTAINER_RATIS_SEGMENT_PREALLOCATED_SIZE_KEY =
      "dfs.container.ratis.segment.preallocated.size";
  public static final int
      DFS_CONTAINER_RATIS_SEGMENT_PREALLOCATED_SIZE_DEFAULT = 128 * 1024 * 1024;

  // TODO : this is copied from OzoneConsts, may need to move to a better place
  public static final String OZONE_SCM_CHUNK_SIZE_KEY = "ozone.scm.chunk.size";
  // 16 MB by default
  public static final int OZONE_SCM_CHUNK_SIZE_DEFAULT = 16 * 1024 * 1024;
  public static final int OZONE_SCM_CHUNK_MAX_SIZE = 32 * 1024 * 1024;

  public static final String OZONE_SCM_CLIENT_PORT_KEY =
      "ozone.scm.client.port";
  public static final int OZONE_SCM_CLIENT_PORT_DEFAULT = 9860;

  public static final String OZONE_SCM_DATANODE_PORT_KEY =
      "ozone.scm.datanode.port";
  public static final int OZONE_SCM_DATANODE_PORT_DEFAULT = 9861;

  // OZONE_KSM_PORT_DEFAULT = 9862
  public static final String OZONE_SCM_BLOCK_CLIENT_PORT_KEY =
      "ozone.scm.block.client.port";
  public static final int OZONE_SCM_BLOCK_CLIENT_PORT_DEFAULT = 9863;

  // Container service client
  public static final String OZONE_SCM_CLIENT_ADDRESS_KEY =
      "ozone.scm.client.address";
  public static final String OZONE_SCM_CLIENT_BIND_HOST_KEY =
      "ozone.scm.client.bind.host";
  public static final String OZONE_SCM_CLIENT_BIND_HOST_DEFAULT =
      "0.0.0.0";

  // Block service client
  public static final String OZONE_SCM_BLOCK_CLIENT_ADDRESS_KEY =
      "ozone.scm.block.client.address";
  public static final String OZONE_SCM_BLOCK_CLIENT_BIND_HOST_KEY =
      "ozone.scm.block.client.bind.host";
  public static final String OZONE_SCM_BLOCK_CLIENT_BIND_HOST_DEFAULT =
      "0.0.0.0";

  public static final String OZONE_SCM_DATANODE_ADDRESS_KEY =
      "ozone.scm.datanode.address";
  public static final String OZONE_SCM_DATANODE_BIND_HOST_KEY =
      "ozone.scm.datanode.bind.host";
  public static final String OZONE_SCM_DATANODE_BIND_HOST_DEFAULT =
      "0.0.0.0";

  public static final String OZONE_SCM_HTTP_ENABLED_KEY =
      "ozone.scm.http.enabled";
  public static final String OZONE_SCM_HTTP_BIND_HOST_KEY =
      "ozone.scm.http-bind-host";
  public static final String OZONE_SCM_HTTPS_BIND_HOST_KEY =
      "ozone.scm.https-bind-host";
  public static final String OZONE_SCM_HTTP_ADDRESS_KEY =
      "ozone.scm.http-address";
  public static final String OZONE_SCM_HTTPS_ADDRESS_KEY =
      "ozone.scm.https-address";
  public static final String OZONE_SCM_KEYTAB_FILE =
      "ozone.scm.keytab.file";
  public static final String OZONE_SCM_HTTP_BIND_HOST_DEFAULT = "0.0.0.0";
  public static final int OZONE_SCM_HTTP_BIND_PORT_DEFAULT = 9876;
  public static final int OZONE_SCM_HTTPS_BIND_PORT_DEFAULT = 9877;


  public static final String OZONE_HDLS_HTTP_ENABLED_KEY =
      "ozone.hdls.http.enabled";
  public static final String OZONE_HDLS_HTTP_BIND_HOST_KEY =
      "ozone.hdls.http-bind-host";
  public static final String OZONE_HDLS_HTTPS_BIND_HOST_KEY =
      "ozone.hdls.https-bind-host";
  public static final String OZONE_HDLS_HTTP_ADDRESS_KEY =
      "ozone.hdls.http-address";
  public static final String OZONE_HDLS_HTTPS_ADDRESS_KEY =
      "ozone.hdls.https-address";
  public static final String OZONE_HDLS_KEYTAB_FILE =
      "ozone.hdls.keytab.file";
  public static final String OZONE_HDLS_HTTP_BIND_HOST_DEFAULT = "0.0.0.0";
  public static final int OZONE_HDLS_HTTP_BIND_PORT_DEFAULT = 9880;
  public static final int OZONE_HDLS_HTTPS_BIND_PORT_DEFAULT = 9881;

  
  public static final String OZONE_SCM_HANDLER_COUNT_KEY =
      "ozone.scm.handler.count.key";
  public static final int OZONE_SCM_HANDLER_COUNT_DEFAULT = 10;

  public static final String OZONE_SCM_HEARTBEAT_INTERVAL =
      "ozone.scm.heartbeat.interval";
  public static final String OZONE_SCM_HEARBEAT_INTERVAL_DEFAULT =
      "30s";

  public static final String OZONE_SCM_DEADNODE_INTERVAL =
      "ozone.scm.dead.node.interval";
  public static final String OZONE_SCM_DEADNODE_INTERVAL_DEFAULT =
      "10m";

  public static final String OZONE_SCM_MAX_HB_COUNT_TO_PROCESS =
      "ozone.scm.max.hb.count.to.process";
  public static final int OZONE_SCM_MAX_HB_COUNT_TO_PROCESS_DEFAULT = 5000;

  public static final String OZONE_SCM_HEARTBEAT_PROCESS_INTERVAL =
      "ozone.scm.heartbeat.thread.interval";
  public static final String OZONE_SCM_HEARTBEAT_PROCESS_INTERVAL_DEFAULT =
      "3s";

  public static final String OZONE_SCM_STALENODE_INTERVAL =
      "ozone.scm.stale.node.interval";
  public static final String OZONE_SCM_STALENODE_INTERVAL_DEFAULT =
      "90s";

  public static final String OZONE_SCM_HEARTBEAT_RPC_TIMEOUT =
      "ozone.scm.heartbeat.rpc-timeout";
  public static final long OZONE_SCM_HEARTBEAT_RPC_TIMEOUT_DEFAULT =
      1000;

  /**
   * Defines how frequently we will log the missing of heartbeat to a specific
   * SCM. In the default case we will write a warning message for each 10
   * sequential heart beats that we miss to a specific SCM. This is to avoid
   * overrunning the log with lots of HB missed Log statements.
   */
  public static final String OZONE_SCM_HEARTBEAT_LOG_WARN_INTERVAL_COUNT =
      "ozone.scm.heartbeat.log.warn.interval.count";
  public static final int OZONE_SCM_HEARTBEAT_LOG_WARN_DEFAULT =
      10;

  // ozone.scm.names key is a set of DNS | DNS:PORT | IP Address | IP:PORT.
  // Written as a comma separated string. e.g. scm1, scm2:8020, 7.7.7.7:7777
  //
  // If this key is not specified datanodes will not be able to find
  // SCM. The SCM membership can be dynamic, so this key should contain
  // all possible SCM names. Once the SCM leader is discovered datanodes will
  // get the right list of SCMs to heartbeat to from the leader.
  // While it is good for the datanodes to know the names of all SCM nodes,
  // it is sufficient to actually know the name of on working SCM. That SCM
  // will be able to return the information about other SCMs that are part of
  // the SCM replicated Log.
  //
  //In case of a membership change, any one of the SCM machines will be
  // able to send back a new list to the datanodes.
  public static final String OZONE_SCM_NAMES = "ozone.scm.names";

  public static final int OZONE_SCM_DEFAULT_PORT =
      OZONE_SCM_DATANODE_PORT_DEFAULT;
  // File Name and path where datanode ID is to written to.
  // if this value is not set then container startup will fail.
  public static final String OZONE_SCM_DATANODE_ID = "ozone.scm.datanode.id";

  public static final String OZONE_SCM_DATANODE_ID_PATH_DEFAULT = "datanode.id";

  public static final String OZONE_SCM_DB_CACHE_SIZE_MB =
      "ozone.scm.db.cache.size.mb";
  public static final int OZONE_SCM_DB_CACHE_SIZE_DEFAULT = 128;

  public static final String OZONE_SCM_CONTAINER_SIZE_GB =
      "ozone.scm.container.size.gb";
  public static final int OZONE_SCM_CONTAINER_SIZE_DEFAULT = 5;

  public static final String OZONE_SCM_CONTAINER_PLACEMENT_IMPL_KEY =
      "ozone.scm.container.placement.impl";

  public static final String OZONE_SCM_CONTAINER_PROVISION_BATCH_SIZE =
      "ozone.scm.container.provision_batch_size";
  public static final int OZONE_SCM_CONTAINER_PROVISION_BATCH_SIZE_DEFAULT = 20;

  public static final String OZONE_SCM_CONTAINER_DELETION_CHOOSING_POLICY =
      "ozone.scm.container.deletion-choosing.policy";

  public static final String OZONE_SCM_CONTAINER_CREATION_LEASE_TIMEOUT =
      "ozone.scm.container.creation.lease.timeout";

  public static final String
      OZONE_SCM_CONTAINER_CREATION_LEASE_TIMEOUT_DEFAULT = "60s";

  /**
   * Don't start processing a pool if we have not had a minimum number of
   * seconds from the last processing.
   */
  public static final String OZONE_SCM_CONTAINER_REPORT_PROCESSING_INTERVAL =
      "ozone.scm.container.report.processing.interval";
  public static final String
      OZONE_SCM_CONTAINER_REPORT_PROCESSING_INTERVAL_DEFAULT = "60s";

  /**
   * This determines the total number of pools to be processed in parallel.
   */
  public static final String OZONE_SCM_MAX_NODEPOOL_PROCESSING_THREADS =
      "ozone.scm.max.nodepool.processing.threads";
  public static final int OZONE_SCM_MAX_NODEPOOL_PROCESSING_THREADS_DEFAULT = 1;
  /**
   * These 2 settings control the number of threads in executor pool and time
   * outs for thw container reports from all nodes.
   */
  public static final String OZONE_SCM_MAX_CONTAINER_REPORT_THREADS =
      "ozone.scm.max.container.report.threads";
  public static final int OZONE_SCM_MAX_CONTAINER_REPORT_THREADS_DEFAULT = 100;
  public static final String OZONE_SCM_CONTAINER_REPORTS_WAIT_TIMEOUT =
      "ozone.scm.container.reports.wait.timeout";
  public static final String OZONE_SCM_CONTAINER_REPORTS_WAIT_TIMEOUT_DEFAULT =
      "5m";

  public static final String OZONE_SCM_BLOCK_DELETION_MAX_RETRY =
      "ozone.scm.block.deletion.max.retry";
  public static final int OZONE_SCM_BLOCK_DELETION_MAX_RETRY_DEFAULT = 4096;

  // Once a container usage crosses this threshold, it is eligible for
  // closing.
  public static final String OZONE_SCM_CONTAINER_CLOSE_THRESHOLD =
      "ozone.scm.container.close.threshold";
  public static final float OZONE_SCM_CONTAINER_CLOSE_THRESHOLD_DEFAULT = 0.9f;

  /**
   * Never constructed.
   */
  private ScmConfigKeys() {

  }
}
