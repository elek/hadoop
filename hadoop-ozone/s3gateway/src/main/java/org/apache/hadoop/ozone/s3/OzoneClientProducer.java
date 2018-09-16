package org.apache.hadoop.ozone.s3;

import javax.enterprise.inject.Produces;
import java.io.IOException;

import org.apache.hadoop.hdds.conf.OzoneConfiguration;
import org.apache.hadoop.ozone.client.OzoneClient;
import org.apache.hadoop.ozone.client.OzoneClientFactory;


public class OzoneClientProducer {

  @Produces
  public OzoneClient createClient() throws IOException {
    return OzoneClientFactory.getClient(new OzoneConfiguration());
  }
}
