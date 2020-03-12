package com.ctrip.framework.apollo.portal.controller;


import com.ctrip.framework.apollo.common.dto.ServerConfigDTO;
import com.ctrip.framework.apollo.common.utils.BeanUtils;
import com.ctrip.framework.apollo.core.enums.Env;
import com.ctrip.framework.apollo.portal.component.PortalSettings;
import com.ctrip.framework.apollo.portal.entity.po.ServerConfig;
import com.ctrip.framework.apollo.portal.repository.ServerConfigRepository;
import com.ctrip.framework.apollo.portal.service.ServerConfigService;
import com.ctrip.framework.apollo.portal.spi.UserInfoHolder;

import java.util.List;
import java.util.Objects;
import javax.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * 配置中心本身需要一些配置,这些配置放在数据库里面
 */
@RestController
public class ServerConfigController {

  private final ServerConfigRepository serverConfigRepository;
  private final UserInfoHolder userInfoHolder;
  private final ServerConfigService serverConfigService;
  private final PortalSettings portalSettings;

  public ServerConfigController(final ServerConfigRepository serverConfigRepository,
                                final UserInfoHolder userInfoHolder,
                                final ServerConfigService serverConfigService,
                                final PortalSettings portalSettings) {
    this.serverConfigRepository = serverConfigRepository;
    this.userInfoHolder = userInfoHolder;
    this.serverConfigService = serverConfigService;
    this.portalSettings = portalSettings;
  }

  @PreAuthorize(value = "@permissionValidator.isSuperAdmin()")
  @PostMapping("/server/config")
  public ServerConfig createOrUpdate(@Valid @RequestBody ServerConfig serverConfig) {
    String modifiedBy = userInfoHolder.getUser().getUserId();

    ServerConfig storedConfig = serverConfigRepository.findByKey(serverConfig.getKey());

    if (Objects.isNull(storedConfig)) {//create
      serverConfig.setDataChangeCreatedBy(modifiedBy);
      serverConfig.setDataChangeLastModifiedBy(modifiedBy);
      serverConfig.setId(0L);//为空，设置ID 为0，jpa执行新增操作
      ServerConfig saveResult = serverConfigRepository.save(serverConfig);
      if (saveResult != null) {
        List<Env> envs = portalSettings.getActiveEnvs();
        for (Env env : envs) {
          ServerConfigDTO serverConfigDTO = new ServerConfigDTO();
          BeanUtils.copyEntityProperties(saveResult, serverConfigDTO);
          serverConfigService.createAdminServierConfig(env, serverConfigDTO);
        }
      }
      return saveResult;
    } else {//update
      BeanUtils.copyEntityProperties(serverConfig, storedConfig);
      storedConfig.setDataChangeLastModifiedBy(modifiedBy);
      ServerConfig saveResult = serverConfigRepository.save(storedConfig);
      if (saveResult != null) {
        List<Env> envs = portalSettings.getActiveEnvs();
        for (Env env : envs) {
          ServerConfigDTO serverConfigDTO = new ServerConfigDTO();
          BeanUtils.copyEntityProperties(saveResult, serverConfigDTO);
          serverConfigService.createAdminServierConfig(env, serverConfigDTO);
        }
      }
      return saveResult;
    }
  }

  @PreAuthorize(value = "@permissionValidator.isSuperAdmin()")
  @GetMapping("/server/config/{key:.+}")
  public ServerConfig loadServerConfig(@PathVariable String key) {
    return serverConfigRepository.findByKey(key);
  }

}
