package org.apache.hadoop.ozone.ksm;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;
import java.util.List;

import org.apache.hadoop.hdds.client.BlockID;
import org.apache.hadoop.hdds.conf.OzoneConfiguration;
import org.apache.hadoop.hdds.protocol.proto.HddsProtos.ReplicationFactor;
import org.apache.hadoop.hdds.protocol.proto.HddsProtos.ReplicationType;
import org.apache.hadoop.hdds.scm.ScmInfo;
import org.apache.hadoop.hdds.scm.container.common.helpers.AllocatedBlock;
import org.apache.hadoop.hdds.scm.protocol.ScmBlockLocationProtocol;
import org.apache.hadoop.ozone.common.BlockGroup;
import org.apache.hadoop.ozone.common.DeleteBlockGroupResult;
import org.apache.hadoop.ozone.ksm.helpers.KsmBucketInfo;
import org.apache.hadoop.ozone.ksm.helpers.KsmKeyArgs;
import org.apache.hadoop.ozone.ksm.helpers.KsmKeyArgs.Builder;
import org.apache.hadoop.ozone.ksm.helpers.KsmKeyInfo;
import org.apache.hadoop.ozone.ksm.helpers.KsmKeyLocationInfo;
import org.apache.hadoop.ozone.ksm.helpers.KsmKeyLocationInfoGroup;
import org.apache.hadoop.ozone.ksm.helpers.KsmVolumeArgs;
import org.apache.hadoop.ozone.ksm.helpers.OpenKeySession;

import static org.apache.hadoop.ozone.OzoneConfigKeys.OZONE_METADATA_DIRS;

/**
 * Utility class to generate test data.
 */
public class TestDataGenerator implements ScmBlockLocationProtocol {

  private Path workingDir;

  private AllocatedBlock blockAllocation;
  private KSMMetadataManagerImpl metadataManager;
  private VolumeManager volumeManager;
  private BucketManager bucketManager;
  private KeyManager keyManager;
  private long dataSize;

  public static void main(String[] args) throws IOException {
    TestDataGenerator testDataGenerator = new TestDataGenerator();
    if (args.length > 0) {
      testDataGenerator.workingDir = Paths.get(args[0]);
    } else {
      testDataGenerator.workingDir = Paths.get("/tmp");
    }
    testDataGenerator.generateData();
  }

  public void generateData() throws IOException {
    init(workingDir);
    findExistingContainer();
    generate();
  }

  private void findExistingContainer() throws IOException {

    String volume = selectAVolume();
    System.out.println(volume);

    String bucket = selectABucket(volume);
    System.out.println(bucket);

    List<KsmKeyInfo> ksmKeyInfos =
        keyManager.listKeys(volume, bucket, "", "", 10);
    for (KsmKeyInfo key : ksmKeyInfos) {
      List<KsmKeyLocationInfoGroup> location = key.getKeyLocationVersions();
      List<KsmKeyLocationInfo> locationList =
          location.get(location.size() - 1).getLocationList();
      for (KsmKeyLocationInfo loc : locationList) {
        System.out.println("Found key to use as a template:");
        System.out.println("Volume: " + volume);
        System.out.println("Bucket: " + bucket);
        System.out.println("Key: " + key.getKeyName());
        System.out.println("ContainerId: " + loc.getContainerID());
        System.out.println("LocalId:" + loc.getLocalID());
        dataSize = key.getDataSize();
        blockAllocation = new AllocatedBlock.Builder()
            .setBlockID(new BlockID(loc.getContainerID(), loc.getLocalID()))
            .setShouldCreateContainer(false).build();

        return;
      }
    }
    throw new AssertionError("Key doesn't exist to use it as a template.");
  }

  private void generate() throws IOException {
    int counter = 0;
    long start = System.currentTimeMillis();
    for (int v = 0; v < 1000; v++) {
      String volumeName = "vol" + v;
      KsmVolumeArgs args =
          KsmVolumeArgs.newBuilder().setAdminName("root").setOwnerName("root")
              .setVolume(volumeName).build();
      volumeManager.createVolume(args);

      for (int b = 0; b < 1000; b++) {
        String bucketName = "bucket" + b;
        KsmBucketInfo bucket =
            KsmBucketInfo.newBuilder().setBucketName(bucketName)
                .setVolumeName(volumeName).build();
        bucketManager.createBucket(bucket);

        for (int k = 0; k < 10000; k++) {
          String keyName = "key" + k;
          KsmKeyArgs key =
              new Builder().setBucketName(bucketName).setVolumeName(volumeName)
                  .setKeyName(keyName).setDataSize(dataSize).build();

          OpenKeySession openKeySession = keyManager.openKey(key);
          keyManager.commitKey(key, openKeySession.getId());
          if (++counter % 100000 == 0) {
            float speed =
                (float) counter / (System.currentTimeMillis() - start) * 1000;
            System.out.println(counter + " " + speed + " key/s " + new Date());
          }
        }
      }
    }
    System.out.println(System.currentTimeMillis() - start);
  }

  private void init(Path metadataDir) throws IOException {
    OzoneConfiguration configuration = new OzoneConfiguration();

    configuration.set(OZONE_METADATA_DIRS, metadataDir.toString());
    KSMStorage ksmStorage = new KSMStorage(configuration);

    metadataManager = new KSMMetadataManagerImpl(configuration);
    volumeManager = new VolumeManagerImpl(metadataManager, configuration);
    bucketManager = new BucketManagerImpl(metadataManager);

    keyManager = new KeyManagerImpl(this, metadataManager, configuration,
        ksmStorage.getKsmId());
  }

  @Override
  public AllocatedBlock allocateBlock(long size, ReplicationType type,
      ReplicationFactor factor, String owner) throws IOException {
    return blockAllocation;
  }

  @Override
  public List<DeleteBlockGroupResult> deleteKeyBlocks(
      List<BlockGroup> keyBlocksInfoList) throws IOException {
    return null;
  }

  @Override
  public ScmInfo getScmInfo() throws IOException {
    return null;
  }

  private String selectAVolume() throws IOException {
    List<KsmVolumeArgs> ksmVolumeArgs =
        volumeManager.listVolumes("root", "", "", 10);
    if (ksmVolumeArgs.size() == 0) {
      throw new AssertionError("No existing volumes");
    }
    return ksmVolumeArgs.get(0).getVolume();
  }

  private String selectABucket(String volume) throws IOException {

    List<KsmBucketInfo> buckets = bucketManager.listBuckets(volume, "", "", 10);
    if (buckets.size() == 0) {
      throw new AssertionError("No existing buckets on volume " + volume);
    }
    return buckets.get(0).getBucketName();
  }

}