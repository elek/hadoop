package org.apache.hadoop.cblock.kubernetes;

import com.google.gson.reflect.TypeToken;
import com.squareup.okhttp.RequestBody;
import io.kubernetes.client.ApiClient;
import io.kubernetes.client.ApiException;
import io.kubernetes.client.Configuration;
import io.kubernetes.client.apis.CoreV1Api;
import io.kubernetes.client.models.V1ISCSIVolumeSource;
import io.kubernetes.client.models.V1ObjectMeta;
import io.kubernetes.client.models.V1ObjectReference;
import io.kubernetes.client.models.V1PersistentVolume;
import io.kubernetes.client.models.V1PersistentVolumeClaim;
import io.kubernetes.client.models.V1PersistentVolumeSpec;
import io.kubernetes.client.util.Config;
import io.kubernetes.client.util.Watch;
import okio.Buffer;
import static org.apache.hadoop.cblock.CBlockConfigKeys
    .DFS_CBLOCK_ISCSI_ADVERTISED_IP;
import static org.apache.hadoop.cblock.CBlockConfigKeys
    .DFS_CBLOCK_ISCSI_ADVERTISED_PORT;
import static org.apache.hadoop.cblock.CBlockConfigKeys
    .DFS_CBLOCK_ISCSI_ADVERTISED_PORT_DEFAULT;
import static org.apache.hadoop.cblock.CBlockConfigKeys
    .DFS_CBLOCK_JSCSI_SERVER_ADDRESS_DEFAULT;
import static org.apache.hadoop.cblock.CBlockConfigKeys
    .DFS_CBLOCK_JSCSI_SERVER_ADDRESS_KEY;
import static org.apache.hadoop.cblock.CBlockConfigKeys
    .DFS_CBLOCK_KUBERNETES_CBLOCK_USER;
import static org.apache.hadoop.cblock.CBlockConfigKeys
    .DFS_CBLOCK_KUBERNETES_CBLOCK_USER_DEFAULT;
import static org.apache.hadoop.cblock.CBlockConfigKeys
    .DFS_CBLOCK_KUBERNETES_CONFIG_FILE_KEY;
import org.apache.hadoop.cblock.exception.CBlockException;
import org.apache.hadoop.cblock.proto.MountVolumeResponse;
import org.apache.hadoop.cblock.storage.StorageManager;
import org.apache.hadoop.conf.OzoneConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

/**
 * Kubernetes Dynamic Persistent Volume provisioner.
 * <p>
 * Listens on the kubernetes feed and creates the appropriate cblock AND
 * kubernetes PersistentVolume according to the created PersistentVolumeClaims.
 */
public class DynamicProvisioner {

  protected static final Logger LOGGER =
      LoggerFactory.getLogger(DynamicProvisioner.class);

  private static final String STORAGE_CLASS = "cblock";

  private static final String PROVISIONER_ID = "hadoop.apache.org/cblock";

  private static boolean running = true;

  private final StorageManager storageManager;

  private String kubernetesConfigFile;

  private String externalIp;

  private int externalPort;

  private String cblockUser;

  private CoreV1Api api;

  private ApiClient client;

  public DynamicProvisioner(OzoneConfiguration ozoneConf,
      StorageManager storageManager) throws IOException {
    this.storageManager = storageManager;

    kubernetesConfigFile = ozoneConf
        .getTrimmed(DFS_CBLOCK_KUBERNETES_CONFIG_FILE_KEY);

    String jscsiServerAddress = ozoneConf
        .get(DFS_CBLOCK_JSCSI_SERVER_ADDRESS_KEY,
            DFS_CBLOCK_JSCSI_SERVER_ADDRESS_DEFAULT);

    externalIp = ozoneConf.
        getTrimmed(DFS_CBLOCK_ISCSI_ADVERTISED_IP, jscsiServerAddress);

    externalPort = ozoneConf.
        getInt(DFS_CBLOCK_ISCSI_ADVERTISED_PORT,
            DFS_CBLOCK_ISCSI_ADVERTISED_PORT_DEFAULT);

    cblockUser = ozoneConf.getTrimmed(DFS_CBLOCK_KUBERNETES_CBLOCK_USER,
        DFS_CBLOCK_KUBERNETES_CBLOCK_USER_DEFAULT);
    init();
  }

  public void init() throws IOException {
    if (kubernetesConfigFile != null) {
      client = Config.fromConfig(kubernetesConfigFile);
    } else {
      client = Config.fromCluster();
    }
    client.getHttpClient().setReadTimeout(60, TimeUnit.SECONDS);
    Configuration.setDefaultApiClient(client);
    api = new CoreV1Api();
  }

  public void start() {
    LOGGER.info("Starting kubernetes dynamic provisioner.");
    while (running) {
      String resourceVersion = null;
      try {
        Watch<V1PersistentVolumeClaim> watch = Watch.createWatch(client,
            api.listPersistentVolumeClaimForAllNamespacesCall(null, null, false,
                null, null, null, resourceVersion, null, true, null, null),
            new TypeToken<Watch.Response<V1PersistentVolumeClaim>>() {
            }.getType());

        for (Watch.Response<V1PersistentVolumeClaim> item : watch) {

          V1PersistentVolumeClaim claim = item.object;
          if (claim.getStatus().getPhase().equals("Pending") && STORAGE_CLASS
              .equals(claim.getSpec().getStorageClassName())) {
            LOGGER.info("Provisioning volumes for PVC {}/{}",
                claim.getMetadata().getNamespace(),
                claim.getMetadata().getName());

            String volumeName =
                claim.getMetadata().getName() + "-" + claim.getMetadata()
                    .getUid();
            createCblock(volumeName);
            createPersistentVolumeFromPVC(api, item.object, volumeName);
            resourceVersion = item.object.getMetadata().getResourceVersion();
          }
        }
      } catch (Exception ex) {
        LOGGER.error("Error on provisioning persistent volumes.", ex);
        try {
          Thread.sleep(1000);
        } catch (InterruptedException e) {
          e.printStackTrace();
        }
      }
    }
  }

  public void stop() {
    running = false;
    //TODO force watcher to stop;
  }

  private void createCblock(String volumeName) throws CBlockException {
    MountVolumeResponse mountVolumeResponse =
        storageManager.isVolumeValid(cblockUser, volumeName);
    if (!mountVolumeResponse.getIsValid()) {
      storageManager
          .createVolume(cblockUser, volumeName, 1 * 1024 * 1024 * 1024,
              4 * 1024);
    }
  }

  private void createPersistentVolumeFromPVC(CoreV1Api api,
      V1PersistentVolumeClaim claim, String volumeName)
      throws ApiException, IOException {
    V1PersistentVolume v1PersistentVolume = new V1PersistentVolume();
    v1PersistentVolume.setKind("PersistentVolume");
    v1PersistentVolume.setApiVersion("v1");

    V1ObjectMeta metadata = new V1ObjectMeta();
    metadata.setName(volumeName);
    metadata.setNamespace(claim.getMetadata().getNamespace());
    metadata.setAnnotations(new HashMap<>());
    metadata.getAnnotations()
        .put("pv.kubernetes.io/provisioned-by", PROVISIONER_ID);
    metadata.getAnnotations().put("volume.beta.kubernetes.io/storage-class", STORAGE_CLASS);
    v1PersistentVolume.setMetadata(metadata);

    V1PersistentVolumeSpec spec = new V1PersistentVolumeSpec();

    spec.setCapacity(new HashMap<>());
    spec.getCapacity().put("storage",
        claim.getSpec().getResources().getRequests().get("storage"));

    spec.setAccessModes(new ArrayList<>());
    spec.getAccessModes().add("ReadWriteOnce");

    V1ObjectReference claimRef = new V1ObjectReference();
    claimRef.setName(claim.getMetadata().getName());
    claimRef.setNamespace(claim.getMetadata().getNamespace());
    claimRef.setKind(claim.getKind());
    claimRef.setApiVersion(claim.getApiVersion());
    claimRef.setUid(claim.getMetadata().getUid());
    spec.setClaimRef(claimRef);

    spec.persistentVolumeReclaimPolicy("Delete");

    V1ISCSIVolumeSource iscsi = new V1ISCSIVolumeSource();
    iscsi.setIqn(cblockUser + ":" + volumeName);
    iscsi.setLun(0);
    iscsi.setFsType("ext4");
    String portal = externalIp + ":" + externalPort;
    iscsi.setTargetPortal(portal);
    iscsi.setPortals(new ArrayList<>());
    iscsi.getPortals().add(portal);

    spec.iscsi(iscsi);
    v1PersistentVolume.setSpec(spec);

    if (LOGGER.isDebugEnabled()) {
      RequestBody request =
          api.getApiClient().serialize(v1PersistentVolume, "application/json");
      final Buffer buffer = new Buffer();
      request.writeTo(buffer);
      LOGGER.debug("Creating new PVC: " + buffer.readUtf8());
    }
    api.createPersistentVolume(v1PersistentVolume, null);
  }
}
