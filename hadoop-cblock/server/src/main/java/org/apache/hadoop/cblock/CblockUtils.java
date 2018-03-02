package org.apache.hadoop.cblock;

import java.net.InetSocketAddress;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.net.NetUtils;

import com.google.common.base.Optional;
import static org.apache.hadoop.cblock.CBlockConfigKeys
    .DFS_CBLOCK_JSCSIRPC_ADDRESS_KEY;
import static org.apache.hadoop.cblock.CBlockConfigKeys
    .DFS_CBLOCK_JSCSI_PORT_DEFAULT;
import static org.apache.hadoop.cblock.CBlockConfigKeys
    .DFS_CBLOCK_SERVICERPC_ADDRESS_KEY;
import static org.apache.hadoop.cblock.CBlockConfigKeys
    .DFS_CBLOCK_SERVICERPC_HOSTNAME_DEFAULT;
import static org.apache.hadoop.cblock.CBlockConfigKeys
    .DFS_CBLOCK_SERVICERPC_PORT_DEFAULT;
import static org.apache.hadoop.hdls.HdlsUtils.getHostNameFromConfigKeys;
import static org.apache.hadoop.hdls.HdlsUtils.getPortNumberFromConfigKeys;

public class CblockUtils {

  /**
   * Retrieve the socket address that is used by CBlock Service.
   *
   * @param conf
   * @return Target InetSocketAddress for the CBlock Service endpoint.
   */
  public static InetSocketAddress getCblockServiceRpcAddr(Configuration conf) {
    final Optional<String> host =
        getHostNameFromConfigKeys(conf, DFS_CBLOCK_SERVICERPC_ADDRESS_KEY);

    // If no port number is specified then we'll just try the defaultBindPort.
    final Optional<Integer> port =
        getPortNumberFromConfigKeys(conf, DFS_CBLOCK_SERVICERPC_ADDRESS_KEY);

    return NetUtils.createSocketAddr(
        host.or(DFS_CBLOCK_SERVICERPC_HOSTNAME_DEFAULT) + ":" + port
            .or(DFS_CBLOCK_SERVICERPC_PORT_DEFAULT));
  }

  /**
   * Retrieve the socket address that is used by CBlock Server.
   *
   * @param conf
   * @return Target InetSocketAddress for the CBlock Server endpoint.
   */
  public static InetSocketAddress getCblockServerRpcAddr(Configuration conf) {
    final Optional<String> host =
        getHostNameFromConfigKeys(conf, DFS_CBLOCK_JSCSIRPC_ADDRESS_KEY);

    // If no port number is specified then we'll just try the defaultBindPort.
    final Optional<Integer> port =
        getPortNumberFromConfigKeys(conf, DFS_CBLOCK_JSCSIRPC_ADDRESS_KEY);

    return NetUtils.createSocketAddr(
        host.or(DFS_CBLOCK_SERVICERPC_HOSTNAME_DEFAULT) + ":" + port
            .or(DFS_CBLOCK_JSCSI_PORT_DEFAULT));
  }

}