package org.camunda.community.zeebe.keycloak.interceptor;

import java.util.Map;
import java.util.Optional;
import java.util.Properties;
import java.util.function.Supplier;

public class ConfigFileLocationProvider implements Supplier<String> {

  public static final String KEYCLOAK_CONFIG_PATH_PROPERTY =
      "zeebe.gateway.security.keycloak.config.path";
  public static final String KEYCLOAK_CONFIG_PATH_ENV =
      "ZEEBE_GATEWAY_SECURITY_KEYCLOAK_CONFIG_PATH";
  public static final String KEYCLOAK_CONFIG_DEFAULT_PATH = "/tmp/zeebe-keycloak.json";
  private final Properties systemProperties;
  private final Map<String, String> systemEnvMap;

  ConfigFileLocationProvider(
      final Properties systemProperties, final Map<String, String> systemEnvMap) {
    this.systemProperties = systemProperties;
    this.systemEnvMap = systemEnvMap;
  }

  public ConfigFileLocationProvider() {
    this(System.getProperties(), System.getenv());
  }

  @Override
  public String get() {
    return Optional.ofNullable(systemProperties.getProperty(KEYCLOAK_CONFIG_PATH_PROPERTY))
        .orElse(
            Optional.ofNullable(systemEnvMap.get(KEYCLOAK_CONFIG_PATH_ENV))
                .orElse(KEYCLOAK_CONFIG_DEFAULT_PATH));
  }
}
