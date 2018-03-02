package org.apache.hadoop.ozone.scm;

import java.net.InetSocketAddress;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.net.NetUtils;
import org.apache.hadoop.ozone.OzoneConfigKeys;
import org.apache.hadoop.scm.ScmConfigKeys;

import com.google.common.base.Optional;
import static org.apache.hadoop.hdls.HdlsUtils.OZONE_SCM_SERVICE_ID;
import static org.apache.hadoop.hdls.HdlsUtils.OZONE_SCM_SERVICE_INSTANCE_ID;
import static org.apache.hadoop.hdls.HdlsUtils.getHostName;
import static org.apache.hadoop.hdls.HdlsUtils.getHostNameFromConfigKeys;
import static org.apache.hadoop.hdls.HdlsUtils.getHostPort;
import static org.apache.hadoop.hdls.HdlsUtils.getPortNumberFromConfigKeys;
import static org.apache.hadoop.ozone.web.util.ServerUtils.sanitizeUserArgs;
import static org.apache.hadoop.scm.ScmConfigKeys.OZONE_SCM_DEADNODE_INTERVAL;
import static org.apache.hadoop.scm.ScmConfigKeys
    .OZONE_SCM_DEADNODE_INTERVAL_DEFAULT;
import static org.apache.hadoop.scm.ScmConfigKeys.OZONE_SCM_HEARTBEAT_INTERVAL;
import static org.apache.hadoop.scm.ScmConfigKeys
    .OZONE_SCM_HEARTBEAT_LOG_WARN_DEFAULT;
import static org.apache.hadoop.scm.ScmConfigKeys
    .OZONE_SCM_HEARTBEAT_LOG_WARN_INTERVAL_COUNT;
import static org.apache.hadoop.scm.ScmConfigKeys
    .OZONE_SCM_HEARTBEAT_PROCESS_INTERVAL;
import static org.apache.hadoop.scm.ScmConfigKeys
    .OZONE_SCM_HEARTBEAT_RPC_TIMEOUT;
import static org.apache.hadoop.scm.ScmConfigKeys
    .OZONE_SCM_HEARTBEAT_RPC_TIMEOUT_DEFAULT;
import static org.apache.hadoop.scm.ScmConfigKeys.OZONE_SCM_STALENODE_INTERVAL;
import static org.apache.hadoop.scm.ScmConfigKeys
    .OZONE_SCM_STALENODE_INTERVAL_DEFAULT;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HdlsServerUtil {

  private static final Logger LOG = LoggerFactory.getLogger(
      HdlsServerUtil.class);

  /**
   * Retrieve the socket addresses of all storage container managers.
   *
   * @param conf
   * @return A collection of SCM addresses
   * @throws IllegalArgumentException If the configuration is invalid
   */
  public static Collection<InetSocketAddress> getSCMAddresses(
      Configuration conf) throws IllegalArgumentException {
    Collection<InetSocketAddress> addresses =
        new HashSet<InetSocketAddress>();
    Collection<String> names =
        conf.getTrimmedStringCollection(ScmConfigKeys.OZONE_SCM_NAMES);
    if (names == null || names.isEmpty()) {
      throw new IllegalArgumentException(ScmConfigKeys.OZONE_SCM_NAMES
          + " need to be a set of valid DNS names or IP addresses."
          + " Null or empty address list found.");
    }

    final com.google.common.base.Optional<Integer>
        defaultPort =  com.google.common.base.Optional.of(ScmConfigKeys
        .OZONE_SCM_DEFAULT_PORT);
    for (String address : names) {
      com.google.common.base.Optional<String> hostname =
          getHostName(address);
      if (!hostname.isPresent()) {
        throw new IllegalArgumentException("Invalid hostname for SCM: "
            + hostname);
      }
      com.google.common.base.Optional<Integer> port =
          getHostPort(address);
      InetSocketAddress addr = NetUtils.createSocketAddr(hostname.get(),
          port.or(defaultPort.get()));
      addresses.add(addr);
    }
    return addresses;
  }



  /**
   * Retrieve the socket address that should be used by DataNodes to connect
   * to the SCM.
   *
   * @param conf
   * @return Target InetSocketAddress for the SCM service endpoint.
   */
  public static InetSocketAddress getScmAddressForDataNodes(
      Configuration conf) {
    // We try the following settings in decreasing priority to retrieve the
    // target host.
    // - OZONE_SCM_DATANODE_ADDRESS_KEY
    // - OZONE_SCM_CLIENT_ADDRESS_KEY
    //
    final Optional<String> host = getHostNameFromConfigKeys(conf,
        ScmConfigKeys.OZONE_SCM_DATANODE_ADDRESS_KEY,
        ScmConfigKeys.OZONE_SCM_CLIENT_ADDRESS_KEY);

    if (!host.isPresent()) {
      throw new IllegalArgumentException(
          ScmConfigKeys.OZONE_SCM_CLIENT_ADDRESS_KEY +
              " must be defined. See" +
              " https://wiki.apache.org/hadoop/Ozone#Configuration for details" +
              " on configuring Ozone.");
    }

    // If no port number is specified then we'll just try the defaultBindPort.
    final Optional<Integer> port = getPortNumberFromConfigKeys(conf,
        ScmConfigKeys.OZONE_SCM_DATANODE_ADDRESS_KEY);

    InetSocketAddress addr = NetUtils.createSocketAddr(host.get() + ":" +
        port.or(ScmConfigKeys.OZONE_SCM_DATANODE_PORT_DEFAULT));

    return addr;
  }

  /**
   * Retrieve the socket address that should be used by clients to connect
   * to the SCM.
   *
   * @param conf
   * @return Target InetSocketAddress for the SCM client endpoint.
   */
  public static InetSocketAddress getScmClientBindAddress(
      Configuration conf) {
    final Optional<String> host = getHostNameFromConfigKeys(conf,
        ScmConfigKeys.OZONE_SCM_CLIENT_BIND_HOST_KEY);

    final Optional<Integer> port = getPortNumberFromConfigKeys(conf,
        ScmConfigKeys.OZONE_SCM_CLIENT_ADDRESS_KEY);

    return NetUtils.createSocketAddr(
        host.or(ScmConfigKeys.OZONE_SCM_CLIENT_BIND_HOST_DEFAULT) + ":" +
            port.or(ScmConfigKeys.OZONE_SCM_CLIENT_PORT_DEFAULT));
  }

  /**
   * Retrieve the socket address that should be used by clients to connect
   * to the SCM Block service.
   *
   * @param conf
   * @return Target InetSocketAddress for the SCM block client endpoint.
   */
  public static InetSocketAddress getScmBlockClientBindAddress(
      Configuration conf) {
    final Optional<String> host = getHostNameFromConfigKeys(conf,
        ScmConfigKeys.OZONE_SCM_BLOCK_CLIENT_BIND_HOST_KEY);

    final Optional<Integer> port = getPortNumberFromConfigKeys(conf,
        ScmConfigKeys.OZONE_SCM_BLOCK_CLIENT_ADDRESS_KEY);

    return NetUtils.createSocketAddr(
        host.or(ScmConfigKeys.OZONE_SCM_BLOCK_CLIENT_BIND_HOST_DEFAULT) +
            ":" + port.or(ScmConfigKeys.OZONE_SCM_BLOCK_CLIENT_PORT_DEFAULT));
  }

  /**
   * Retrieve the socket address that should be used by DataNodes to connect
   * to the SCM.
   *
   * @param conf
   * @return Target InetSocketAddress for the SCM service endpoint.
   */
  public static InetSocketAddress getScmDataNodeBindAddress(
      Configuration conf) {
    final Optional<String> host = getHostNameFromConfigKeys(conf,
        ScmConfigKeys.OZONE_SCM_DATANODE_BIND_HOST_KEY);

    // If no port number is specified then we'll just try the defaultBindPort.
    final Optional<Integer> port = getPortNumberFromConfigKeys(conf,
        ScmConfigKeys.OZONE_SCM_DATANODE_ADDRESS_KEY);

    return NetUtils.createSocketAddr(
        host.or(ScmConfigKeys.OZONE_SCM_DATANODE_BIND_HOST_DEFAULT) + ":" +
            port.or(ScmConfigKeys.OZONE_SCM_DATANODE_PORT_DEFAULT));
  }


  /**
   * Returns the interval in which the heartbeat processor thread runs.
   *
   * @param conf - Configuration
   * @return long in Milliseconds.
   */
  public static long getScmheartbeatCheckerInterval(Configuration conf) {
    return conf.getTimeDuration(OZONE_SCM_HEARTBEAT_PROCESS_INTERVAL,
        ScmConfigKeys.OZONE_SCM_HEARTBEAT_PROCESS_INTERVAL_DEFAULT,
        TimeUnit.MILLISECONDS);
  }

  /**
   * Heartbeat Interval - Defines the heartbeat frequency from a datanode to
   * SCM.
   *
   * @param conf - Ozone Config
   * @return - HB interval in seconds.
   */
  public static long getScmHeartbeatInterval(Configuration conf) {
    return conf.getTimeDuration(OZONE_SCM_HEARTBEAT_INTERVAL,
        ScmConfigKeys.OZONE_SCM_HEARBEAT_INTERVAL_DEFAULT,
        TimeUnit.SECONDS);
  }

  /**
   * Get the Stale Node interval, which is used by SCM to flag a datanode as
   * stale, if the heartbeat from that node has been missing for this duration.
   *
   * @param conf - Configuration.
   * @return - Long, Milliseconds to wait before flagging a node as stale.
   */
  public static long getStaleNodeInterval(Configuration conf) {

    long staleNodeIntervalMs =
        conf.getTimeDuration(OZONE_SCM_STALENODE_INTERVAL,
            OZONE_SCM_STALENODE_INTERVAL_DEFAULT, TimeUnit.MILLISECONDS);

    long heartbeatThreadFrequencyMs = getScmheartbeatCheckerInterval(conf);

    long heartbeatIntervalMs = getScmHeartbeatInterval(conf) * 1000;


    // Make sure that StaleNodeInterval is configured way above the frequency
    // at which we run the heartbeat thread.
    //
    // Here we check that staleNodeInterval is at least five times more than the
    // frequency at which the accounting thread is going to run.
    try {
      sanitizeUserArgs(staleNodeIntervalMs, heartbeatThreadFrequencyMs,
          5, 1000);
    } catch (IllegalArgumentException ex) {
      LOG.error("Stale Node Interval is cannot be honored due to " +
              "mis-configured {}. ex:  {}",
          OZONE_SCM_HEARTBEAT_PROCESS_INTERVAL, ex);
      throw ex;
    }

    // Make sure that stale node value is greater than configured value that
    // datanodes are going to send HBs.
    try {
      sanitizeUserArgs(staleNodeIntervalMs, heartbeatIntervalMs, 3, 1000);
    } catch (IllegalArgumentException ex) {
      LOG.error("Stale Node Interval MS is cannot be honored due to " +
          "mis-configured {}. ex:  {}", OZONE_SCM_HEARTBEAT_INTERVAL, ex);
      throw ex;
    }
    return staleNodeIntervalMs;
  }

  /**
   * Gets the interval for dead node flagging. This has to be a value that is
   * greater than stale node value,  and by transitive relation we also know
   * that this value is greater than heartbeat interval and heartbeatProcess
   * Interval.
   *
   * @param conf - Configuration.
   * @return - the interval for dead node flagging.
   */
  public static long getDeadNodeInterval(Configuration conf) {
    long staleNodeIntervalMs = getStaleNodeInterval(conf);
    long deadNodeIntervalMs = conf.getTimeDuration(OZONE_SCM_DEADNODE_INTERVAL,
        OZONE_SCM_DEADNODE_INTERVAL_DEFAULT,
        TimeUnit.MILLISECONDS);

    try {
      // Make sure that dead nodes Ms is at least twice the time for staleNodes
      // with a max of 1000 times the staleNodes.
      sanitizeUserArgs(deadNodeIntervalMs, staleNodeIntervalMs, 2, 1000);
    } catch (IllegalArgumentException ex) {
      LOG.error("Dead Node Interval MS is cannot be honored due to " +
          "mis-configured {}. ex:  {}", OZONE_SCM_STALENODE_INTERVAL, ex);
      throw ex;
    }
    return deadNodeIntervalMs;
  }

  /**
   * Returns the maximum number of heartbeat to process per loop of the process
   * thread.
   * @param conf Configuration
   * @return - int -- Number of HBs to process
   */
  public static int getMaxHBToProcessPerLoop(Configuration conf) {
    return conf.getInt(ScmConfigKeys.OZONE_SCM_MAX_HB_COUNT_TO_PROCESS,
        ScmConfigKeys.OZONE_SCM_MAX_HB_COUNT_TO_PROCESS_DEFAULT);
  }

  /**
   * Timeout value for the RPC from Datanode to SCM, primarily used for
   * Heartbeats and container reports.
   *
   * @param conf - Ozone Config
   * @return - Rpc timeout in Milliseconds.
   */
  public static long getScmRpcTimeOutInMilliseconds(Configuration conf) {
    return conf.getTimeDuration(OZONE_SCM_HEARTBEAT_RPC_TIMEOUT,
        OZONE_SCM_HEARTBEAT_RPC_TIMEOUT_DEFAULT, TimeUnit.MILLISECONDS);
  }

  /**
   * Log Warn interval.
   *
   * @param conf - Ozone Config
   * @return - Log warn interval.
   */
  public static int getLogWarnInterval(Configuration conf) {
    return conf.getInt(OZONE_SCM_HEARTBEAT_LOG_WARN_INTERVAL_COUNT,
        OZONE_SCM_HEARTBEAT_LOG_WARN_DEFAULT);
  }

  /**
   * returns the Container port.
   * @param conf - Conf
   * @return port number.
   */
  public static int getContainerPort(Configuration conf) {
    return conf.getInt(OzoneConfigKeys.DFS_CONTAINER_IPC_PORT,
        OzoneConfigKeys.DFS_CONTAINER_IPC_PORT_DEFAULT);
  }


  /**
   * Return the list of service addresses for the Ozone SCM. This method is used
   * by the DataNodes to determine the service instances to connect to.
   *
   * @param conf
   * @return list of SCM service addresses.
   */
  public static Map<String, ? extends Map<String, InetSocketAddress>>
  getScmServiceRpcAddresses(Configuration conf) {
    final Map<String, InetSocketAddress> serviceInstances = new HashMap<>();
    serviceInstances.put(OZONE_SCM_SERVICE_INSTANCE_ID,
        getScmAddressForDataNodes(conf));

    final Map<String, Map<String, InetSocketAddress>> services =
        new HashMap<>();
    services.put(OZONE_SCM_SERVICE_ID, serviceInstances);
    return services;
  }
}
