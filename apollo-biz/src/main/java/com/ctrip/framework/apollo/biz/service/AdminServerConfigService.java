package com.ctrip.framework.apollo.biz.service;

import com.ctrip.framework.apollo.biz.entity.Commit;
import com.ctrip.framework.apollo.biz.entity.ServerConfig;
import com.ctrip.framework.apollo.biz.repository.CommitRepository;
import com.ctrip.framework.apollo.biz.repository.ServerConfigRepository;
import com.ctrip.framework.apollo.common.utils.RSAEncryptUtil;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author bbb
 * @since 2020-03-13
 */
@Service
public class AdminServerConfigService {
    private final static Logger logger = LoggerFactory.getLogger(AdminServerConfigService.class);

    private final ServerConfigRepository serverConfigRepository;
    private final CommitRepository commitRepository;
    private final ItemService itemService;

    public AdminServerConfigService(ServerConfigRepository serverConfigRepository,
                                    CommitRepository commitRepository,
                                    ItemService itemService) {
        this.serverConfigRepository = serverConfigRepository;
        this.commitRepository = commitRepository;
        this.itemService= itemService;
    }

    @Transactional
    public void createAdminServerConfig(ServerConfig serverConfig) {
        if (serverConfig.getValue() == null || serverConfig.getValue().length() == 0) {
            return;
        }
        serverConfig.setCluster("default");
        serverConfig.setDataChangeCreatedBy("default");
        ServerConfig oldServerConfig = serverConfigRepository.findByKey("publickey");
        if (whetherTosave(oldServerConfig, serverConfig)) {
            if (oldServerConfig == null) {
                serverConfig.setId(0L);
                serverConfigRepository.save(serverConfig);
            } else {
                serverConfig.setId(oldServerConfig.getId());
                serverConfig.setDataChangeCreatedTime(oldServerConfig.getDataChangeCreatedTime());
                serverConfigRepository.save(serverConfig);
                //更新数据库中所有的加密的配置重新加密
                updateAllEncryptData();
            }
        }
    }

    /**
     *  判断是否更新密钥
     * @param oldServerConfig 老密钥
     * @param newServerConfig 新密钥
     * @return
     */
    private Boolean whetherTosave(ServerConfig oldServerConfig, ServerConfig newServerConfig) {
        if (oldServerConfig == null) {
            return true;
        }
        if (oldServerConfig != null && !newServerConfig.getValue().equals(oldServerConfig.getValue())) {
            return true;
        }
        return false;
    }

    /**
     * 公钥修改后，更新所有加密过的数据
     *  不建议这么操作。。。先支持先这功能
     */
    private void updateAllEncryptData() {
        Iterable<Commit> commits =  commitRepository.findAll();
        if (commits == null) {
            return;
        }
        ServerConfig serverConfig = itemService.findByKey();
        if (serverConfig.getValue() == null) {
            return;
        }
        for (Commit commit : commits) {
            JsonParser jsonParser = new JsonParser();
            JsonObject jsonObject = jsonParser.parse(commit.getChangeSets()).getAsJsonObject();
            JsonArray jsonArray = jsonParser.parse(jsonObject.get("createItems").toString()).getAsJsonArray();
            if (jsonArray.size() == 0) {
                continue;
            }
            for (int i = 0; i < jsonArray.size(); i++) {
                JsonObject jsonObject1 = jsonArray.get(i).getAsJsonObject();
                String vaule = jsonObject1.get("value").getAsString();
                if (!RSAEncryptUtil.isEncryptedValue(vaule)) {
                    continue;
                }
                String NewENcrypData = getNewENcrypt(vaule, serverConfig.getValue());
                if (NewENcrypData == null) {
                    continue;
                }
                jsonObject1.addProperty("value", NewENcrypData);
                jsonArray.add(jsonObject1);
            }
            commit.setChangeSets(jsonArray.toString());
            commitRepository.save(commit);
        }
    }

    /**
     * 获取新加密的密码
     * @param oldENcryptData 密钥更换前的密码
     * @return 新加密的密码
     */
    private String getNewENcrypt(String oldENcryptData, String publicKey) {
        //老的密码解密获取明文  在用新的公钥加密
        String data = RSAEncryptUtil.decrypt(oldENcryptData, RSAEncryptUtil.getPriKeyString());
        return data == null ? null : RSAEncryptUtil.encrypt(data, publicKey);
    }
}
