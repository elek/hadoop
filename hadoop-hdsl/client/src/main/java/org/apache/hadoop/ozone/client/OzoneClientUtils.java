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

package org.apache.hadoop.ozone.client;

import java.text.ParseException;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.TimeUnit;

import org.apache.hadoop.classification.InterfaceAudience;
import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hdsl.conf.OzoneConfiguration;
import org.apache.hadoop.ozone.OzoneConfigKeys;
import org.apache.hadoop.ozone.OzoneConsts;
import org.apache.hadoop.scm.ScmConfigKeys;

import com.google.common.base.Preconditions;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Utility methods for Ozone and Container Clients.
 *
 * The methods to retrieve SCM service endpoints assume there is a single
 * SCM service instance. This will change when we switch to replicated service
 * instances for redundancy.
 */
@InterfaceAudience.Public
@InterfaceStability.Unstable
public final class OzoneClientUtils {
  private static final Logger LOG = LoggerFactory.getLogger(
      OzoneClientUtils.class);
  private static final int NO_PORT = -1;

  /**
   * Date format that used in ozone. Here the format is thread safe to use.
   */
  private static final ThreadLocal<DateTimeFormatter> DATE_FORMAT =
      ThreadLocal.withInitial(() -> {
        DateTimeFormatter format =
            DateTimeFormatter.ofPattern(OzoneConsts.OZONE_DATE_FORMAT);
        return format.withZone(ZoneId.of(OzoneConsts.OZONE_TIME_ZONE));
      });




  /**
   * Returns the cache value to be used for list calls.
   * @param conf Configuration object
   * @return list cache size
   */
  public static int getListCacheSize(Configuration conf) {
    return conf.getInt(OzoneConfigKeys.OZONE_CLIENT_LIST_CACHE_SIZE,
        OzoneConfigKeys.OZONE_CLIENT_LIST_CACHE_SIZE_DEFAULT);
  }




  /**
   * @return a default instance of {@link CloseableHttpClient}.
   */
  public static CloseableHttpClient newHttpClient() {
    return OzoneClientUtils.newHttpClient(new OzoneConfiguration());
  }

  /**
   * Returns a {@link CloseableHttpClient} configured by given configuration.
   * If conf is null, returns a default instance.
   *
   * @param conf configuration
   * @return a {@link CloseableHttpClient} instance.
   */
  public static CloseableHttpClient newHttpClient(Configuration conf) {
    long socketTimeout = OzoneConfigKeys
        .OZONE_CLIENT_SOCKET_TIMEOUT_DEFAULT;
    long connectionTimeout = OzoneConfigKeys
        .OZONE_CLIENT_CONNECTION_TIMEOUT_DEFAULT;
    if (conf != null) {
      socketTimeout = conf.getTimeDuration(
          OzoneConfigKeys.OZONE_CLIENT_SOCKET_TIMEOUT,
          OzoneConfigKeys.OZONE_CLIENT_SOCKET_TIMEOUT_DEFAULT,
          TimeUnit.MILLISECONDS);
      connectionTimeout = conf.getTimeDuration(
          OzoneConfigKeys.OZONE_CLIENT_CONNECTION_TIMEOUT,
          OzoneConfigKeys.OZONE_CLIENT_CONNECTION_TIMEOUT_DEFAULT,
          TimeUnit.MILLISECONDS);
    }

    CloseableHttpClient client = HttpClients.custom()
        .setDefaultRequestConfig(
            RequestConfig.custom()
                .setSocketTimeout(Math.toIntExact(socketTimeout))
                .setConnectTimeout(Math.toIntExact(connectionTimeout))
                .build())
        .build();
    return client;
  }

  /**
   * verifies that bucket name / volume name is a valid DNS name.
   *
   * @param resName Bucket or volume Name to be validated
   *
   * @throws IllegalArgumentException
   */
  public static void verifyResourceName(String resName)
      throws IllegalArgumentException {
    OzoneClientUtils.verifyResourceName(resName);
  }

  /**
   * Convert time in millisecond to a human readable format required in ozone.
   * @return a human readable string for the input time
   */
  public static String formatDateTime(long millis) {
    ZonedDateTime dateTime = ZonedDateTime.ofInstant(
        Instant.ofEpochSecond(millis), DATE_FORMAT.get().getZone());
    return  DATE_FORMAT.get().format(dateTime);
  }

  /**
   * Convert time in ozone date format to millisecond.
   * @return time in milliseconds
   */
  public static long formatDateTime(String date) throws ParseException {
    Preconditions.checkNotNull(date, "Date string should not be null.");
    return ZonedDateTime.parse(date, DATE_FORMAT.get())
        .toInstant().getEpochSecond();
  }

  /**
   * Returns the maximum no of outstanding async requests to be handled by
   * Standalone and Ratis client.
   */
  public static int getMaxOutstandingRequests(Configuration config) {
    return config
        .getInt(ScmConfigKeys.SCM_CONTAINER_CLIENT_MAX_OUTSTANDING_REQUESTS,
            ScmConfigKeys.SCM_CONTAINER_CLIENT_MAX_OUTSTANDING_REQUESTS_DEFAULT);
  }
}