package org.apache.hadoop.ozone.s3.object;

import javax.inject.Inject;
import javax.ws.rs.DELETE;
import javax.ws.rs.HEAD;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.io.InputStream;

import org.apache.hadoop.ozone.client.OzoneBucket;
import org.apache.hadoop.ozone.client.OzoneClient;
import org.apache.hadoop.ozone.client.OzoneKeyDetails;

@Path("/{volume}/{bucket}/{path:.+}")
public class DeleteObject {

  @Inject
  OzoneClient client;

  @DELETE
  @Produces(MediaType.APPLICATION_XML)
  public Response head(
      @PathParam("volume") String volumeName,
      @PathParam("bucket") String bucketName,
      @PathParam("path") String keyPath,
      @HeaderParam("Content-Length") long length,
      InputStream body) throws IOException {

    OzoneBucket bucket =
        client.getObjectStore().getVolume(volumeName).getBucket(bucketName);

    bucket.deleteKey(keyPath);
    return Response.
        ok().build();

  }
}
