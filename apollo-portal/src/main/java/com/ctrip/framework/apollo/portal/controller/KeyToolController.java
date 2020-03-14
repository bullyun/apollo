package com.ctrip.framework.apollo.portal.controller;

import com.ctrip.framework.apollo.portal.service.KeyToolService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 非对称密钥对下载
 */
@RestController
public class KeyToolController {

  private final KeyToolService keyToolService;

  public KeyToolController(final KeyToolService keyToolService) {
    this.keyToolService = keyToolService;
  }

  @PreAuthorize(value = "@permissionValidator.isSuperAdmin()")
  @PostMapping("/keyTool/create")
  public void createKeys(HttpServletRequest request, HttpServletResponse response) throws Exception {
    keyToolService.createKeys(request, response);
  }
}
