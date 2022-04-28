package org.camunda.community.zeebe.keycloak.interceptor;

import static org.assertj.core.api.Assertions.assertThat;
import static org.camunda.community.zeebe.keycloak.interceptor.ConfigFileLocationProvider.KEYCLOAK_CONFIG_DEFAULT_PATH;
import static org.camunda.community.zeebe.keycloak.interceptor.ConfigFileLocationProvider.KEYCLOAK_CONFIG_PATH_ENV;
import static org.camunda.community.zeebe.keycloak.interceptor.ConfigFileLocationProvider.KEYCLOAK_CONFIG_PATH_PROPERTY;

import java.util.Map;
import java.util.Properties;
import org.junit.jupiter.api.Test;

class ConfigFileLocationProviderTest {

  @Test
  void shouldTakeLocationFromPropertyFirst() {
    // given
    final Properties systemProperties = new Properties();
    final String location = "test-config.json";
    systemProperties.put(KEYCLOAK_CONFIG_PATH_PROPERTY, location);
    // when
    final ConfigFileLocationProvider configFileLocationProvider =
        new ConfigFileLocationProvider(systemProperties, Map.of());
    // then
    assertThat(configFileLocationProvider.get()).isEqualTo(location);
  }

  @Test
  void shouldTakeLocationFromEnvironmentVariablesIfNoPropertiesPassed() {
    // given
    final String location = "test-config-from-env.json";
    // when
    final ConfigFileLocationProvider configFileLocationProvider =
        new ConfigFileLocationProvider(
            new Properties(), Map.of(KEYCLOAK_CONFIG_PATH_ENV, location));
    // then
    assertThat(configFileLocationProvider.get()).isEqualTo(location);
  }

  @Test
  void shouldFallbackToConstant() {
    // given
    // when
    final ConfigFileLocationProvider configFileLocationProvider =
        new ConfigFileLocationProvider(new Properties(), Map.of());
    // then
    assertThat(configFileLocationProvider.get()).isEqualTo(KEYCLOAK_CONFIG_DEFAULT_PATH);
  }
}
