package com.ctrip.framework.apollo.core;

public interface ConfigConsts {
  String NAMESPACE_APPLICATION = "application";
  String CLUSTER_NAME_DEFAULT = "default";
  String CLUSTER_NAMESPACE_SEPARATOR = "+";
  String APOLLO_CLUSTER_KEY = "apollo.cluster";
  String APOLLO_META_KEY = "apollo.meta";
  String CONFIG_FILE_CONTENT_KEY = "content";
  String NO_APPID_PLACEHOLDER = "ApolloNoAppIdPlaceHolder";
  long NOTIFICATION_ID_PLACEHOLDER = -1;
  //自定义
  String APOLLO_CONFIG_SERVICE_KEY = "apollo.configService";
}
