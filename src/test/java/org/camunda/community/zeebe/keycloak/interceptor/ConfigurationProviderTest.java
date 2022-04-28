package org.camunda.community.zeebe.keycloak.interceptor;

import static org.assertj.core.api.Assertions.assertThat;

import com.typesafe.config.Config;
import org.junit.jupiter.api.Test;

class ConfigurationProviderTest {

  @Test
  void shouldLoadFullConfigFromFile() {
    // given
    final ConfigurationProvider provider =
        new ConfigurationProvider(
            () -> "src/test/resources/test-files/full-zeebe-interceptor-config.json");

    // when
    final Config config = provider.get();

    // thenÎ
    assertThat(config.getString("serverUrl")).isEqualTo("http://localhost");
    assertThat(config.getString("realm")).isEqualTo("test-realm");
    assertThat(config.getString("grantType")).isEqualTo("client_credentials");
    assertThat(config.getString("clientId")).isEqualTo("test-client-id");
    assertThat(config.getString("clientSecret")).isEqualTo("test-client-secret");
    assertThat(config.getString("username")).isEqualTo("test-username");
    assertThat(config.getString("password")).isEqualTo("test-password");
    assertThat(config.getString("expectedAudience")).isEqualTo("test-expected-audience");
    assertThat(config.getString("expectedIssuedFor")).isEqualTo("test-expected-issued-for");
    assertThat(config.getAnyRefList("allowedIssuers")).isEmpty();
    assertThat(config.getString("minTimeBetweenJwksRequests")).isEqualTo("12 s");
    assertThat(config.getString("publicKeyCacheTtl")).isEqualTo("12 d");
  }

  @Test
  void shouldWorkWithDefaultsAndPartialConfig() {
    // given
    final ConfigurationProvider provider =
        new ConfigurationProvider(
            () -> "src/test/resources/test-files/partial-zeebe-interceptor-config.json");

    // when
    final Config config = provider.get();

    // then
    assertThat(config.getString("serverUrl")).isEqualTo("http://localhost:8080");
    assertThat(config.getString("realm")).isEqualTo("another-test-realm");
    assertThat(config.getString("grantType")).isEqualTo("client_credentials");
    assertThat(config.getString("clientId")).isEqualTo("");
    assertThat(config.getString("clientSecret")).isEqualTo("");
    assertThat(config.getString("username")).isEqualTo("");
    assertThat(config.getString("password")).isEqualTo("");
    assertThat(config.getString("expectedAudience")).isEqualTo("");
    assertThat(config.getString("expectedIssuedFor")).isEqualTo("");
    assertThat(config.getAnyRefList("allowedIssuers")).isEmpty();
    assertThat(config.getString("minTimeBetweenJwksRequests")).isEqualTo("10 seconds");
    assertThat(config.getString("publicKeyCacheTtl")).isEqualTo("1 day");
  }
}
