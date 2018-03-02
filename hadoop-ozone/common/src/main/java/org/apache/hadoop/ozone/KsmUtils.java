package org.apache.hadoop.ozone;

import java.net.InetSocketAddress;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.net.NetUtils;

import com.google.common.base.Optional;
import static org.apache.hadoop.hdsl.HdslUtils.getHostNameFromConfigKeys;
import static org.apache.hadoop.hdsl.HdslUtils.getPortNumberFromConfigKeys;
import static org.apache.hadoop.ozone.ksm.KSMConfigKeys.OZONE_KSM_ADDRESS_KEY;
import static org.apache.hadoop.ozone.ksm.KSMConfigKeys
    .OZONE_KSM_BIND_HOST_DEFAULT;
import static org.apache.hadoop.ozone.ksm.KSMConfigKeys.OZONE_KSM_PORT_DEFAULT;

public class KsmUtils {


  /**
   * Retrieve the socket address that is used by KSM.
   * @param conf
   * @return Target InetSocketAddress for the SCM service endpoint.
   */
  public static InetSocketAddress getKsmAddress(
      Configuration conf) {
    final Optional<String> host = getHostNameFromConfigKeys(conf,
        OZONE_KSM_ADDRESS_KEY);

    // If no port number is specified then we'll just try the defaultBindPort.
    final Optional<Integer> port = getPortNumberFromConfigKeys(conf,
        OZONE_KSM_ADDRESS_KEY);

    return NetUtils.createSocketAddr(
        host.or(OZONE_KSM_BIND_HOST_DEFAULT) + ":" +
            port.or(OZONE_KSM_PORT_DEFAULT));
  }

  /**
   * Retrieve the socket address that should be used by clients to connect
   * to KSM.
   * @param conf
   * @return Target InetSocketAddress for the KSM service endpoint.
   */
  public static InetSocketAddress getKsmAddressForClients(
      Configuration conf) {
    final Optional<String> host = getHostNameFromConfigKeys(conf,
        OZONE_KSM_ADDRESS_KEY);

    if (!host.isPresent()) {
      throw new IllegalArgumentException(
          OZONE_KSM_ADDRESS_KEY + " must be defined. See" +
              " https://wiki.apache.org/hadoop/Ozone#Configuration for" +
              " details on configuring Ozone.");
    }

    // If no port number is specified then we'll just try the defaultBindPort.
    final Optional<Integer> port = getPortNumberFromConfigKeys(conf,
        OZONE_KSM_ADDRESS_KEY);

    return NetUtils.createSocketAddr(
        host.get() + ":" + port.or(OZONE_KSM_PORT_DEFAULT));
  }

}
