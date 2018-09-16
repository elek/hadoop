package org.apache.hadoop.ozone.s3.object;

import javax.inject.Inject;
import javax.ws.rs.HEAD;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.io.InputStream;

import org.apache.hadoop.hdds.client.ReplicationFactor;
import org.apache.hadoop.hdds.client.ReplicationType;
import org.apache.hadoop.ozone.client.OzoneBucket;
import org.apache.hadoop.ozone.client.OzoneClient;
import org.apache.hadoop.ozone.client.OzoneKeyDetails;
import org.apache.hadoop.ozone.client.io.OzoneOutputStream;

@Path("/{volume}/{bucket}/{path:.+}")
public class HeadObject {

  @Inject
  OzoneClient client;

  @HEAD
  @Produces(MediaType.APPLICATION_XML)
  public Response head(
      @PathParam("volume") String volumeName,
      @PathParam("bucket") String bucketName,
      @PathParam("path") String keyPath,
      @HeaderParam("Content-Length") long length,
      InputStream body) throws IOException {

    OzoneBucket bucket =
        client.getObjectStore().getVolume(volumeName).getBucket(bucketName);

    OzoneKeyDetails key = bucket.getKey(keyPath);

    return Response.
        ok()
        .header("Contet-Length", key.getDataSize())
        .build();

  }
}
