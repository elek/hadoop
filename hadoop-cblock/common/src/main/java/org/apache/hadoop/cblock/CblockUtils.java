
/**
 * Retrieve the socket address that is used by CBlock Service.
 * @param conf
 * @return Target InetSocketAddress for the CBlock Service endpoint.
 */
public static InetSocketAddress getCblockServiceRpcAddr(
    Configuration conf) {
final Optional<String> host = getHostNameFromConfigKeys(conf,
    DFS_CBLOCK_SERVICERPC_ADDRESS_KEY);

// If no port number is specified then we'll just try the defaultBindPort.
final Optional<Integer> port = getPortNumberFromConfigKeys(conf,
    DFS_CBLOCK_SERVICERPC_ADDRESS_KEY);

    return NetUtils.createSocketAddr(
    host.or(DFS_CBLOCK_SERVICERPC_HOSTNAME_DEFAULT) + ":" +
    port.or(DFS_CBLOCK_SERVICERPC_PORT_DEFAULT));
    }

/**
 * Retrieve the socket address that is used by CBlock Server.
 * @param conf
 * @return Target InetSocketAddress for the CBlock Server endpoint.
 */
public static InetSocketAddress getCblockServerRpcAddr(
    Configuration conf) {
final Optional<String> host = getHostNameFromConfigKeys(conf,
    DFS_CBLOCK_JSCSIRPC_ADDRESS_KEY);

// If no port number is specified then we'll just try the defaultBindPort.
final Optional<Integer> port = getPortNumberFromConfigKeys(conf,
    DFS_CBLOCK_JSCSIRPC_ADDRESS_KEY);

    return NetUtils.createSocketAddr(
    host.or(DFS_CBLOCK_SERVICERPC_HOSTNAME_DEFAULT) + ":" +
    port.or(DFS_CBLOCK_JSCSI_PORT_DEFAULT));
    }
