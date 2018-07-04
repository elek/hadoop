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
import org.apache.hadoop.ozone.ksm.exceptions.KSMException;
import org.apache.hadoop.ozone.ksm.exceptions.KSMException.ResultCodes;
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

  private static final String VOL_PREFIX = "tstvol";
  private static final String USER = "root";
  private static final String BUCKET_PREFIX = "bucket";
  private static final String KEY_PREFIX = "key";
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
    try {
      setUpBlockAllocationTemplate();
    } catch (Exception ex) {
      ex.printStackTrace(System.err);
      System.err
          .println("Can't find existing containerid/localid. Using 1l/1l.");
      blockAllocation =
          new AllocatedBlock.Builder().setBlockID(new BlockID(1l, 1l))
              .setShouldCreateContainer(false).build();
    }
    generate();
  }

  private void setUpBlockAllocationTemplate() throws IOException {

    String volume = selectAVolume();
    String bucket = selectABucket(volume);


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
    throw new RuntimeException("Key doesn't exist to use it as a template.");
  }

  private void generate() throws IOException {
    int maxVol = 1000;
    int maxBucket = 1000;
    int maxKey = 10000;

    int startVol = -1;
    int startBucket = -1;
    int startKey = -1;

    List<KsmVolumeArgs> volumes =
        volumeManager.listVolumes(USER, VOL_PREFIX, "", maxVol);
    for (KsmVolumeArgs volume : volumes) {
      startVol =
          Integer.parseInt(volume.getVolume().substring(VOL_PREFIX.length()));
    }

    if (startVol > -1) {
      List<KsmBucketInfo> buckets = bucketManager
          .listBuckets(VOL_PREFIX + startVol, "", BUCKET_PREFIX, maxBucket);
      for (KsmBucketInfo bucket : buckets) {
        startBucket = Integer
            .parseInt(bucket.getBucketName().substring(BUCKET_PREFIX.length()));
      }
    }

    if (startBucket > -1) {
      List<KsmKeyInfo> keys = keyManager
          .listKeys(VOL_PREFIX + startVol, BUCKET_PREFIX + startBucket, "", "",
              maxKey);

      for (KsmKeyInfo key : keys) {
        startKey =
            Integer.parseInt(key.getKeyName().substring(KEY_PREFIX.length()));
      }
    }

    startVol = Math.max(startVol, 0);
    startBucket = Math.max(startBucket, 0);
    startKey = Math.max(startKey, 0);

    System.out.println(String
        .format("Starting from vol: %d, bucket: %d, key: %d", startVol,
            startBucket, startKey));

    int counter =
        startVol * maxBucket * maxKey + startBucket * maxKey + startKey;
    long start = System.currentTimeMillis();
    for (int v = startVol; v < maxVol; v++) {
      String volumeName = VOL_PREFIX + v;

      try {
        KsmVolumeArgs args =
            KsmVolumeArgs.newBuilder().setAdminName(USER).setOwnerName(USER)
                .setVolume(volumeName).build();
        volumeManager.createVolume(args);
      } catch (KSMException ex) {
        if (ex.getResult() != ResultCodes.FAILED_VOLUME_ALREADY_EXISTS) {
          throw ex;
        }
      }
      for (int b = startBucket; b < maxBucket; b++) {
        String bucketName = BUCKET_PREFIX + b;
        KsmBucketInfo bucket =
            KsmBucketInfo.newBuilder().setBucketName(bucketName)
                .setVolumeName(volumeName).build();
        try {
          bucketManager.createBucket(bucket);
        } catch (KSMException ex) {
          if (ex.getResult() != ResultCodes.FAILED_BUCKET_ALREADY_EXISTS) {
            throw ex;
          }
        }
        for (int k = startKey; k < maxKey; k++) {
          try {
            String keyName = KEY_PREFIX + k;
            KsmKeyArgs key = new Builder().setBucketName(bucketName)
                .setVolumeName(volumeName).setKeyName(keyName)
                .setDataSize(dataSize).build();

            OpenKeySession openKeySession = keyManager.openKey(key);
            keyManager.commitKey(key, openKeySession.getId());
          } catch (KSMException ex) {
            if (ex.getResult() != ResultCodes.FAILED_KEY_ALREADY_EXISTS) {
              throw ex;
            }
          }
          if (++counter % 100000 == 0) {
            float speed =
                (float) counter / (System.currentTimeMillis() - start) * 1000;
            System.out.println(counter + " " + speed + " key/s " + new Date());
          }
          }
        startKey = 0;
      }
      startBucket = 0;
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
        volumeManager.listVolumes(USER, "", "", 10);
    if (ksmVolumeArgs.size() == 0) {
      throw new RuntimeException("No existing volumes");
    }
    return ksmVolumeArgs.get(0).getVolume();
  }

  private String selectABucket(String volume) throws IOException {

    List<KsmBucketInfo> buckets = bucketManager.listBuckets(volume, "", "", 10);
    if (buckets.size() == 0) {
      throw new RuntimeException("No existing buckets on volume " + volume);
    }
    return buckets.get(0).getBucketName();
  }

}