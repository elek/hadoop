package org.apache.hadoop.ozone.s3;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

@XmlAccessorType(XmlAccessType.FIELD)
public class CommonPrefix {

  @XmlElement(name = "Prefix")
  private String prefix;

  public CommonPrefix(String prefix) {
    this.prefix = prefix;
  }

  public CommonPrefix() {
  }

  public String getPrefix() {
    return prefix;
  }

  public void setPrefix(String prefix) {
    this.prefix = prefix;
  }
}
