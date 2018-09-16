package org.apache.hadoop.ozone.s3;

import org.glassfish.jersey.server.ResourceConfig;

public class GatewayApplication extends ResourceConfig {
  public GatewayApplication() {
    packages("org.apache.hadoop.ozone.s3");
  }
}
