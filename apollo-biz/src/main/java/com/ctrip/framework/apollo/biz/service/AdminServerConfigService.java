package com.ctrip.framework.apollo.biz.service;

import com.ctrip.framework.apollo.biz.entity.ServerConfig;
import com.ctrip.framework.apollo.biz.repository.ServerConfigRepository;
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

    public AdminServerConfigService(ServerConfigRepository serverConfigRepository) {
        this.serverConfigRepository = serverConfigRepository;
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
}
