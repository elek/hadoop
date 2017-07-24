/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.hadoop.jmx;

import org.apache.hadoop.util.VersionInfo;

/**
 * Helper base class to report the standard version and runtime information.
 */
public class ServiceRuntimeInfo implements ServiceRuntimeInfoMBean {

  private long startedTimeInMillis;

  @Override
  public String getVersion() {
    return VersionInfo.getVersion() + ", r" + VersionInfo.getRevision();
  }

  @Override
  public String getSoftwareVersion() {
    return VersionInfo.getVersion();
  }

  @Override
  public String getCompileInfo() {
    return VersionInfo.getDate() + " by " + VersionInfo.getUser() + " from "
        + VersionInfo.getBranch();
  }

  @Override
  public long getStartedTimeInMillis() {
    return System.currentTimeMillis();
  }

  public void setStartTime() {
    startedTimeInMillis = System.currentTimeMillis();
  }

}
