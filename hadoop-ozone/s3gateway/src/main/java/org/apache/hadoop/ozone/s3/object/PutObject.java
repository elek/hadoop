package org.apache.hadoop.ozone.s3.object;

import javax.inject.Inject;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.io.IOException;
import java.io.InputStream;

import org.apache.hadoop.hdds.client.ReplicationFactor;
import org.apache.hadoop.hdds.client.ReplicationType;
import org.apache.hadoop.ozone.client.OzoneBucket;
import org.apache.hadoop.ozone.client.OzoneClient;
import org.apache.hadoop.ozone.client.io.OzoneOutputStream;

@Path("/{volume}/{bucket}/{path:.+}")
public class PutObject {

  @Inject
  OzoneClient client;

  @PUT
  @Produces(MediaType.APPLICATION_XML)
  public void put(
      @PathParam("volume") String volumeName,
      @PathParam("bucket") String bucketName,
      @PathParam("path") String keyPath,
      @HeaderParam("Content-Length") long length,
      InputStream body) throws IOException {

    OzoneBucket bucket =
        client.getObjectStore().getVolume(volumeName).getBucket(bucketName);

    OzoneOutputStream output = bucket
        .createKey(keyPath, length, ReplicationType.STAND_ALONE,
            ReplicationFactor.ONE);

    byte[] buffer = new byte[1024];
    int read = 0;
    while ((read = body.read()) > 0) {
      output.write(buffer, 0, read);
    }

    output.close();
  }
}
