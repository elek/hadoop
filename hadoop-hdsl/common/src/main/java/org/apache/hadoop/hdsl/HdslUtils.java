/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.hadoop.hdsl;

import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Map;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.conf.OzoneConfiguration;
import org.apache.hadoop.net.NetUtils;
import org.apache.hadoop.scm.ScmConfigKeys;

import com.google.common.base.Optional;
import com.google.common.net.HostAndPort;

public class HdslUtils {

  /**
   * The service ID of the solitary Ozone SCM service.
   */
  public static final String OZONE_SCM_SERVICE_ID = "OzoneScmService";
  public static final String OZONE_SCM_SERVICE_INSTANCE_ID =
      "OzoneScmServiceInstance";

  private static final int NO_PORT = -1;

  /**
   * Retrieve the socket address that should be used by clients to connect
   * to the SCM.
   *
   * @param conf
   * @return Target InetSocketAddress for the SCM client endpoint.
   */
  public static InetSocketAddress getScmAddressForClients(Configuration conf) {
    final Optional<String> host = getHostNameFromConfigKeys(conf,
        ScmConfigKeys.OZONE_SCM_CLIENT_ADDRESS_KEY);

    if (!host.isPresent()) {
      throw new IllegalArgumentException(
          ScmConfigKeys.OZONE_SCM_CLIENT_ADDRESS_KEY + " must be defined. See"
              + " https://wiki.apache.org/hadoop/Ozone#Configuration for "
              + "details"
              + " on configuring Ozone.");
    }

    final Optional<Integer> port = getPortNumberFromConfigKeys(conf,
        ScmConfigKeys.OZONE_SCM_CLIENT_ADDRESS_KEY);

    return NetUtils.createSocketAddr(host.get() + ":" + port
        .or(ScmConfigKeys.OZONE_SCM_CLIENT_PORT_DEFAULT));
  }

  /**
   * Retrieve the socket address that should be used by clients to connect
   * to the SCM for block service. If
   * {@link ScmConfigKeys#OZONE_SCM_BLOCK_CLIENT_ADDRESS_KEY} is not defined
   * then {@link ScmConfigKeys#OZONE_SCM_CLIENT_ADDRESS_KEY} is used.
   *
   * @param conf
   * @return Target InetSocketAddress for the SCM block client endpoint.
   * @throws IllegalArgumentException if configuration is not defined.
   */
  public static InetSocketAddress getScmAddressForBlockClients(
      Configuration conf) {
    Optional<String> host = getHostNameFromConfigKeys(conf,
        ScmConfigKeys.OZONE_SCM_BLOCK_CLIENT_ADDRESS_KEY);

    if (!host.isPresent()) {
      host = getHostNameFromConfigKeys(conf,
          ScmConfigKeys.OZONE_SCM_CLIENT_ADDRESS_KEY);
      if (!host.isPresent()) {
        throw new IllegalArgumentException(
            ScmConfigKeys.OZONE_SCM_BLOCK_CLIENT_ADDRESS_KEY
                + " must be defined. See"
                + " https://wiki.apache.org/hadoop/Ozone#Configuration"
                + " for details on configuring Ozone.");
      }
    }

    final Optional<Integer> port = getPortNumberFromConfigKeys(conf,
        ScmConfigKeys.OZONE_SCM_BLOCK_CLIENT_ADDRESS_KEY);

    return NetUtils.createSocketAddr(host.get() + ":" + port
        .or(ScmConfigKeys.OZONE_SCM_BLOCK_CLIENT_PORT_DEFAULT));
  }

  /**
   * Retrieve the hostname, trying the supplied config keys in order.
   * Each config value may be absent, or if present in the format
   * host:port (the :port part is optional).
   *
   * @param conf  - Conf
   * @param keys a list of configuration key names.
   *
   * @return first hostname component found from the given keys, or absent.
   * @throws IllegalArgumentException if any values are not in the 'host'
   *             or host:port format.
   */
  public static Optional<String> getHostNameFromConfigKeys(Configuration conf,
      String... keys) {
    for (final String key : keys) {
      final String value = conf.getTrimmed(key);
      final Optional<String> hostName = getHostName(value);
      if (hostName.isPresent()) {
        return hostName;
      }
    }
    return Optional.absent();
  }

  /**
   * Gets the hostname or Indicates that it is absent.
   * @param value host or host:port
   * @return hostname
   */
  public static Optional<String> getHostName(String value) {
    if ((value == null) || value.isEmpty()) {
      return Optional.absent();
    }
    return Optional.of(HostAndPort.fromString(value).getHostText());
  }

  /**
   * Gets the port if there is one, throws otherwise.
   * @param value  String in host:port format.
   * @return Port
   */
  public static Optional<Integer> getHostPort(String value) {
    if ((value == null) || value.isEmpty()) {
      return Optional.absent();
    }
    int port = HostAndPort.fromString(value).getPortOrDefault(NO_PORT);
    if (port == NO_PORT) {
      return Optional.absent();
    } else {
      return Optional.of(port);
    }
  }

  /**
   * Retrieve the port number, trying the supplied config keys in order.
   * Each config value may be absent, or if present in the format
   * host:port (the :port part is optional).
   *
   * @param conf Conf
   * @param keys a list of configuration key names.
   *
   * @return first port number component found from the given keys, or absent.
   * @throws IllegalArgumentException if any values are not in the 'host'
   *             or host:port format.
   */
  public static Optional<Integer> getPortNumberFromConfigKeys(
      Configuration conf, String... keys) {
    for (final String key : keys) {
      final String value = conf.getTrimmed(key);
      final Optional<Integer> hostPort = getHostPort(value);
      if (hostPort.isPresent()) {
        return hostPort;
      }
    }
    return Optional.absent();
  }

  public static boolean isOzoneEnabled(OzoneConfiguration conf) {
    return true;
  }
}
