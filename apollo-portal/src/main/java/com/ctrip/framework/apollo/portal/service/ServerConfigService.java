package com.ctrip.framework.apollo.portal.service;

import com.ctrip.framework.apollo.common.dto.ServerConfigDTO;
import com.ctrip.framework.apollo.core.enums.Env;
import com.ctrip.framework.apollo.portal.api.AdminServiceAPI;
import org.springframework.stereotype.Service;

/**
 * @author bbb
 * @since 2020-03-13
 */
@Service
public class ServerConfigService {
    private final AdminServiceAPI.AdminServiceConfigAPI adminServiceConfigAPI;

    public ServerConfigService(AdminServiceAPI.AdminServiceConfigAPI adminServiceConfigAPI) {
        this.adminServiceConfigAPI = adminServiceConfigAPI;
    }

    public ServerConfigDTO createAdminServierConfig(Env env, ServerConfigDTO serverConfig) {
        ServerConfigDTO serverConfigDTO = adminServiceConfigAPI.createAdminServiceConfig(env, serverConfig);
        return serverConfigDTO;
    }
}
