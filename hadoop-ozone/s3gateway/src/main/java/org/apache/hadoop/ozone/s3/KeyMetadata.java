package org.apache.hadoop.ozone.s3;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.time.Instant;

@XmlAccessorType(XmlAccessType.FIELD)
public class KeyMetadata {

  @XmlElement(name = "Key")
  private String key; // or the Object Name

  @XmlJavaTypeAdapter(IsoDateAdapter.class)
  @XmlElement(name = "LastModified")
  private Instant lastModified;

  @XmlElement(name = "ETag")
  private String eTag;

  @XmlElement(name = "Size")
  private long size;

  @XmlElement(name = "StorageClass")
  private String storageClass;

  public String getKey() {
    return key;
  }

  public void setKey(String key) {
    this.key = key;
  }

  public Instant getLastModified() {
    return lastModified;
  }

  public void setLastModified(Instant lastModified) {
    this.lastModified = lastModified;
  }

  public String geteTag() {
    return eTag;
  }

  public void seteTag(String eTag) {
    this.eTag = eTag;
  }

  public long getSize() {
    return size;
  }

  public void setSize(long size) {
    this.size = size;
  }

  public String getStorageClass() {
    return storageClass;
  }

  public void setStorageClass(String storageClass) {
    this.storageClass = storageClass;
  }
}
