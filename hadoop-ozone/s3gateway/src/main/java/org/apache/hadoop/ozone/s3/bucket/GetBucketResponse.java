
package org.apache.hadoop.ozone.s3.bucket;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;
import java.util.List;

import org.apache.hadoop.ozone.s3.CommonPrefix;
import org.apache.hadoop.ozone.s3.KeyMetadata;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "ListBucketResult", namespace = "http://s3.amazonaws"
    + ".com/doc/2006-03-01/")
public class GetBucketResponse {

  @XmlElement(name = "Name")
  private String name;

  @XmlElement(name = "Prefix")
  private String prefix;

  @XmlElement(name = "Marker")
  private String marker;

  @XmlElement(name = "MaxKeys")
  private int maxKeys;

  @XmlElement(name = "Delimiter")
  private String delimiter = "/";

  @XmlElement(name = "EncodingType")
  private String encodingType = "url";

  @XmlElement(name = "IsTruncated")
  private boolean isTruncated;

  @XmlElement(name = "Contents")
  private List<KeyMetadata> contents = new ArrayList<>();

  @XmlElement(name = "CommonPrefixes")
  private List<CommonPrefix> commonPrefixes = new ArrayList<>();

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getPrefix() {
    return prefix;
  }

  public void setPrefix(String prefix) {
    this.prefix = prefix;
  }

  public String getMarker() {
    return marker;
  }

  public void setMarker(String marker) {
    this.marker = marker;
  }

  public int getMaxKeys() {
    return maxKeys;
  }

  public void setMaxKeys(int maxKeys) {
    this.maxKeys = maxKeys;
  }

  public String getDelimiter() {
    return delimiter;
  }

  public void setDelimiter(String delimiter) {
    this.delimiter = delimiter;
  }

  public String getEncodingType() {
    return encodingType;
  }

  public void setEncodingType(String encodingType) {
    this.encodingType = encodingType;
  }

  public boolean isTruncated() {
    return isTruncated;
  }

  public void setTruncated(boolean truncated) {
    isTruncated = truncated;
  }

  public List<KeyMetadata> getContents() {
    return contents;
  }

  public void setContents(
      List<KeyMetadata> contents) {
    this.contents = contents;
  }

  public List<CommonPrefix> getCommonPrefixes() {
    return commonPrefixes;
  }

  public void setCommonPrefixes(
      List<CommonPrefix> commonPrefixes) {
    this.commonPrefixes = commonPrefixes;
  }

  public void addKey(KeyMetadata keyMetadata) {
    contents.add(keyMetadata);
  }

  public void addPrefix(String relativeKeyName) {
    commonPrefixes.add(new CommonPrefix(relativeKeyName));
  }
}
