package com.ctrip.framework.apollo.adminservice.controller;

import com.ctrip.framework.apollo.biz.entity.ServerConfig;
import com.ctrip.framework.apollo.biz.service.AdminServerConfigService;
import com.ctrip.framework.apollo.common.dto.ServerConfigDTO;
import com.ctrip.framework.apollo.common.utils.BeanUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

/**
 * @author no_f
 * @since 2020-03-13
 */
@RestController
public class AdminServerConfigController {

    private final AdminServerConfigService adminServerConfigService;

    public AdminServerConfigController(AdminServerConfigService adminServerConfigService) {
        this.adminServerConfigService = adminServerConfigService;
    }

    @PostMapping("/Syn/PotalServerConfig/To/AdminServerConfig")
    public void synServerConfig(@Valid @RequestBody ServerConfigDTO dto) {
        ServerConfig entity = BeanUtils.transform(ServerConfig.class, dto);
        adminServerConfigService.createAdminServerConfig(entity);
    }

}
