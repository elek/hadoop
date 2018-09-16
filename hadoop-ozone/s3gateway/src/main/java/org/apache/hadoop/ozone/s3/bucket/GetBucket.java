package org.apache.hadoop.ozone.s3.bucket;

import javax.inject.Inject;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import java.io.IOException;
import java.time.Instant;
import java.util.Iterator;

import org.apache.hadoop.ozone.client.OzoneClient;
import org.apache.hadoop.ozone.client.OzoneKey;
import org.apache.hadoop.ozone.s3.KeyMetadata;

import com.google.common.annotations.VisibleForTesting;
import org.apache.commons.lang3.StringUtils;

@Path("/{volume}/{bucket}")
public class GetBucket {

  @Inject
  OzoneClient client;

  @GET
  @Produces(MediaType.APPLICATION_XML)
  public GetBucketResponse get(
      @PathParam("volume") String volume,
      @PathParam("bucket") String bucket,
      @QueryParam("delimiter") String delimiter,
      @QueryParam("encoding-type") String encodingType,
      @QueryParam("marker") String marker,
      @DefaultValue("1000") @QueryParam("max-keys") int maxKeys,
      @QueryParam("prefix") String prefix,
      @Context HttpHeaders hh) throws IOException {

    if (delimiter == null) {
      delimiter = "/";
    }
    if (prefix == null) {
      prefix = "";
    }
    Iterator<OzoneKey> ozoneKeyIterator = client.getObjectStore()
        .getVolume(volume)
        .getBucket(bucket)
        .listKeys(prefix);

    GetBucketResponse response = new GetBucketResponse();
    response.setDelimiter(delimiter);
    response.setName(bucket);
    response.setPrefix(prefix);
    response.setMarker("");
    response.setMaxKeys(1000);
    response.setEncodingType("url");
    response.setTruncated(false);

    String prevDir = null;
    while (ozoneKeyIterator.hasNext()) {
      OzoneKey next = ozoneKeyIterator.next();
      String relativeKeyName = next.getName().substring(prefix.length());
      int depth =
          StringUtils.countMatches(relativeKeyName, response.getDelimiter());

      if (prefix.length() > 0 && !prefix.endsWith(response.getDelimiter())
          && relativeKeyName.length() > 0) {
        response.addPrefix(prefix + "/");
        break;
      }
      if (depth > 0) {
        String dirName = relativeKeyName
            .substring(0, relativeKeyName.indexOf(response.getDelimiter()));
        if (!dirName.equals(prevDir)) {
          response.addPrefix(
              prefix + dirName + response
                  .getDelimiter());
          prevDir = dirName;
        }
      } else if (relativeKeyName.endsWith(response.getDelimiter())) {
        response.addPrefix(relativeKeyName);
      } else if (relativeKeyName.length() > 0) {
        KeyMetadata keyMetadata = new KeyMetadata();
        keyMetadata.setKey(next.getName());
        keyMetadata.setSize(next.getDataSize());
        keyMetadata.seteTag("" + next.getModificationTime());
        keyMetadata.setStorageClass("STANDARD");
        keyMetadata
            .setLastModified(Instant.ofEpochMilli(next.getModificationTime()));
        response.addKey(keyMetadata);
      }
    }
    return response;
  }

  @VisibleForTesting
  public void setClient(OzoneClient ozoneClient) {
    this.client = ozoneClient;
  }
}
