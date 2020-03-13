package com.ctrip.framework.apollo.biz.service;

import com.ctrip.framework.apollo.biz.AbstractIntegrationTest;
import com.ctrip.framework.apollo.biz.entity.Audit;
import com.ctrip.framework.apollo.biz.entity.Cluster;
import com.ctrip.framework.apollo.biz.entity.Namespace;
import com.ctrip.framework.apollo.biz.repository.AppRepository;
import com.ctrip.framework.apollo.common.entity.App;
import com.ctrip.framework.apollo.common.exception.ServiceException;
import com.ctrip.framework.apollo.common.utils.RSAEncryptUtil;
import com.ctrip.framework.apollo.core.ConfigConsts;
import java.util.Date;
import java.util.List;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class AdminServiceTest extends AbstractIntegrationTest {

  @Autowired
  private AdminService adminService;

  @Autowired
  private AuditService auditService;

  @Autowired
  private AppRepository appRepository;

  @Autowired
  private ClusterService clusterService;

  @Autowired
  private NamespaceService namespaceService;

  @Autowired
  private AppNamespaceService appNamespaceService;

  @Test
  public void testCreateNewApp() {
    String appId = "someAppId";
    App app = new App();
    app.setAppId(appId);
    app.setName("someAppName");
    String owner = "someOwnerName";
    app.setOwnerName(owner);
    app.setOwnerEmail("someOwnerName@ctrip.com");
    app.setDataChangeCreatedBy(owner);
    app.setDataChangeLastModifiedBy(owner);
    app.setDataChangeCreatedTime(new Date());

    app = adminService.createNewApp(app);
    Assert.assertEquals(appId, app.getAppId());

    List<Cluster> clusters = clusterService.findParentClusters(app.getAppId());
    Assert.assertEquals(1, clusters.size());
    Assert.assertEquals(ConfigConsts.CLUSTER_NAME_DEFAULT, clusters.get(0).getName());

    List<Namespace> namespaces = namespaceService.findNamespaces(appId, clusters.get(0).getName());
    Assert.assertEquals(1, namespaces.size());
    Assert.assertEquals(ConfigConsts.NAMESPACE_APPLICATION, namespaces.get(0).getNamespaceName());

    List<Audit> audits = auditService.findByOwner(owner);
    Assert.assertEquals(4, audits.size());
  }

  @Test(expected = ServiceException.class)
  public void testCreateDuplicateApp() {
    String appId = "someAppId";
    App app = new App();
    app.setAppId(appId);
    app.setName("someAppName");
    String owner = "someOwnerName";
    app.setOwnerName(owner);
    app.setOwnerEmail("someOwnerName@ctrip.com");
    app.setDataChangeCreatedBy(owner);
    app.setDataChangeLastModifiedBy(owner);
    app.setDataChangeCreatedTime(new Date());

    appRepository.save(app);

    adminService.createNewApp(app);
  }

  @Test
  public void testDeleteApp() {
    String appId = "someAppId";
    App app = new App();
    app.setAppId(appId);
    app.setName("someAppName");
    String owner = "someOwnerName";
    app.setOwnerName(owner);
    app.setOwnerEmail("someOwnerName@ctrip.com");
    app.setDataChangeCreatedBy(owner);
    app.setDataChangeLastModifiedBy(owner);
    app.setDataChangeCreatedTime(new Date());

    app = adminService.createNewApp(app);

    Assert.assertEquals(appId, app.getAppId());

    Assert.assertEquals(1, appNamespaceService.findByAppId(appId).size());

    Assert.assertEquals(1, clusterService.findClusters(appId).size());

    Assert.assertEquals(1, namespaceService.findNamespaces(appId, ConfigConsts.CLUSTER_NAME_DEFAULT).size());

    adminService.deleteApp(app, owner);

    Assert.assertEquals(0, appNamespaceService.findByAppId(appId).size());

    Assert.assertEquals(0, clusterService.findClusters(appId).size());

    Assert
        .assertEquals(0, namespaceService.findByAppIdAndNamespaceName(appId, ConfigConsts.CLUSTER_NAME_DEFAULT).size());
  }

  @Test
  public void updateAllEncryptData() {
    String changeSet = "{\"createItems\":[{\"namespaceId\":2,\"key\":\"system.fc\",\"value\":\"ENC(OgiWCKcUcaNJzmquXpfLSAamLM+IHh89etmsrcAfDvyFkq"
            + "6iwCyjsscMmoFl5YjKGINuzo3J5QWxeONGO4dinA\\u003d\\u003d)\",\"lineNum\":4,\"id\":16,\"isDeleted\":false,"
            + "\"dataChangeCreatedBy\":\"apollo\",\"dataChangeCreatedTime\":\"2020-03-11 14:55:45\","
            + "\"dataChangeLastModifiedBy\":\"apollo\",\"dataChangeLastModifiedTime\":\"2020-03-11 14:55:45\"},"
            + "{\"namespaceId\":2,\"key\":\"system.fc\",\"value\":\"xxxxxxxx\",\"lineNum\":4,\"id\":16,\"isDeleted\":false,"
            + "\"dataChangeCreatedBy\":\"apollo\",\"dataChangeCreatedTime\":\"2020-03-11 14:55:45\",\"dataChangeLastModifiedBy\":\"apollo\","
            + "\"dataChangeLastModifiedTime\":\"2020-03-11 14:55:45\"}],\"updateItems\":[],\"deleteItems\":[]}";
    JsonParser jsonParser = new JsonParser();
    JsonObject jsonObject = jsonParser.parse(changeSet).getAsJsonObject();
    JsonArray jsonArray = jsonParser.parse(jsonObject.get("createItems").toString()).getAsJsonArray();
    if (jsonArray.size() == 0) {
      return;
    }
    for (int i = 0; i < jsonArray.size(); i++) {
      JsonObject jsonObject1 = jsonArray.get(i).getAsJsonObject();
      String vaule = jsonObject1.get("value").getAsString();
      if (!RSAEncryptUtil.isEncryptedValue(vaule)) {
        continue;
      }
      jsonObject1.addProperty("value", "test11111111");
      jsonArray.add(jsonObject1);
    }
    System.out.println(jsonArray.toString());

  }

}
