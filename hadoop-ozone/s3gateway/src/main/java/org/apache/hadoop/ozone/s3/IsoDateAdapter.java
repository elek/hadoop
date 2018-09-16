package org.apache.hadoop.ozone.s3;

import javax.xml.bind.annotation.adapters.XmlAdapter;
import java.time.Instant;

public class IsoDateAdapter extends XmlAdapter<String, Instant> {

  @Override
  public Instant unmarshal(String v) throws Exception {
    return null;
  }

  @Override
  public String marshal(Instant v) throws Exception {
    return "2016-12-12T13:54:59.000Z";
  }
}
