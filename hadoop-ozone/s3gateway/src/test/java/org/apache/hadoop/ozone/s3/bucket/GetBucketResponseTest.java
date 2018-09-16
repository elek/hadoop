package org.apache.hadoop.ozone.s3.bucket;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;

import org.apache.hadoop.ozone.s3.bucket.GetBucketResponse;

import org.junit.Test;

public class GetBucketResponseTest {

  @Test
  public void serialize() throws JAXBException {

    JAXBContext context = JAXBContext.newInstance(GetBucketResponse.class);
    context.createMarshaller().marshal(new GetBucketResponse(), System.out);

  }

}