package org.apache.hadoop.ozone.s3.bucket;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.hadoop.ozone.client.ObjectStore;
import org.apache.hadoop.ozone.client.OzoneBucket;
import org.apache.hadoop.ozone.client.OzoneClient;
import org.apache.hadoop.ozone.client.OzoneKey;
import org.apache.hadoop.ozone.client.OzoneVolume;
import org.apache.hadoop.ozone.s3.bucket.GetBucket;
import org.apache.hadoop.ozone.s3.bucket.GetBucketResponse;

import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;
import static org.mockito.Mockito.*;

public class GetBucketTest {

  @Test
  public void listRoot() throws IOException {

    GetBucket getBucket = new GetBucket();

    OzoneClient ozoneClient = Mockito.mock(OzoneClient.class);
    ObjectStore objectStore = Mockito.mock(ObjectStore.class);
    OzoneVolume volume = Mockito.mock(OzoneVolume.class);
    OzoneBucket bucket = Mockito.mock(OzoneBucket.class);

    when(ozoneClient.getObjectStore()).thenReturn(objectStore);
    when(objectStore.getVolume("vol1")).thenReturn(volume);
    when(volume.getBucket("b1")).thenReturn(bucket);

    getBucket.setClient(ozoneClient);
    List<OzoneKey> keys = new ArrayList();

    keys.add(
        new OzoneKey("vol1", "b1", "file1", 10,
            System.currentTimeMillis(),
            System.currentTimeMillis()));
    keys.add(
        new OzoneKey("vol1", "b1", "dir1/file2", 10,
            System.currentTimeMillis(),
            System.currentTimeMillis()));

    when(bucket.listKeys(anyString())).thenReturn(keys.iterator());

    GetBucketResponse getBucketResponse =
        getBucket.get("vol1", "b1", "/", null, null, 100, "", null);

    Assert.assertEquals(1, getBucketResponse.getCommonPrefixes().size());
    Assert.assertEquals("dir1/",
        getBucketResponse.getCommonPrefixes().get(0).getPrefix());

    Assert.assertEquals(1, getBucketResponse.getContents().size());
    Assert.assertEquals("file1",
        getBucketResponse.getContents().get(0).getKey());

  }

  @Test
  public void listDir() throws IOException {

    GetBucket getBucket = new GetBucket();

    OzoneClient ozoneClient = Mockito.mock(OzoneClient.class);
    ObjectStore objectStore = Mockito.mock(ObjectStore.class);
    OzoneVolume volume = Mockito.mock(OzoneVolume.class);
    OzoneBucket bucket = Mockito.mock(OzoneBucket.class);

    when(ozoneClient.getObjectStore()).thenReturn(objectStore);
    when(objectStore.getVolume("vol1")).thenReturn(volume);
    when(volume.getBucket("b1")).thenReturn(bucket);

    getBucket.setClient(ozoneClient);
    List<OzoneKey> keys = new ArrayList();

    keys.add(
        new OzoneKey("vol1", "b1", "dir1/file2", 10,
            System.currentTimeMillis(),
            System.currentTimeMillis()));

    keys.add(
        new OzoneKey("vol1", "b1", "dir1/dir2/file2", 10,
            System.currentTimeMillis(),
            System.currentTimeMillis()));


    when(bucket.listKeys(anyString())).thenReturn(keys.iterator());

    GetBucketResponse getBucketResponse =
        getBucket.get("vol1", "b1", "/", null, null, 100, "dir1", null);

    Assert.assertEquals(1, getBucketResponse.getCommonPrefixes().size());
    Assert.assertEquals("dir1/",
        getBucketResponse.getCommonPrefixes().get(0).getPrefix());

    Assert.assertEquals(0, getBucketResponse.getContents().size());


  }

  @Test
  public void listSubDir() throws IOException {

    GetBucket getBucket = new GetBucket();

    OzoneClient ozoneClient = Mockito.mock(OzoneClient.class);
    ObjectStore objectStore = Mockito.mock(ObjectStore.class);
    OzoneVolume volume = Mockito.mock(OzoneVolume.class);
    OzoneBucket bucket = Mockito.mock(OzoneBucket.class);

    when(ozoneClient.getObjectStore()).thenReturn(objectStore);
    when(objectStore.getVolume("vol1")).thenReturn(volume);
    when(volume.getBucket("b1")).thenReturn(bucket);

    getBucket.setClient(ozoneClient);
    List<OzoneKey> keys = new ArrayList();

    keys.add(
        new OzoneKey("vol1", "b1", "dir1/file2", 10,
            System.currentTimeMillis(),
            System.currentTimeMillis()));

    keys.add(
        new OzoneKey("vol1", "b1", "dir1/dir2/file2", 10,
            System.currentTimeMillis(),
            System.currentTimeMillis()));


    when(bucket.listKeys(anyString())).thenReturn(keys.iterator());

    GetBucketResponse getBucketResponse =
        getBucket.get("vol1", "b1", "/", null, null, 100, "dir1/", null);

    Assert.assertEquals(1, getBucketResponse.getCommonPrefixes().size());
    Assert.assertEquals("dir1/dir2/",
        getBucketResponse.getCommonPrefixes().get(0).getPrefix());

    Assert.assertEquals(1, getBucketResponse.getContents().size());
    Assert.assertEquals("dir1/file2",
        getBucketResponse.getContents().get(0).getKey());

  }
}