package org.camunda.community.zeebe.keycloak.interceptor;

import static org.assertj.core.api.Assertions.assertThat;
import static org.camunda.community.zeebe.keycloak.interceptor.ConfigFileLocationProvider.KEYCLOAK_CONFIG_DEFAULT_PATH;

import com.fasterxml.jackson.databind.ObjectMapper;
import dasniko.testcontainers.keycloak.KeycloakContainer;
import io.camunda.zeebe.client.ZeebeClient;
import io.camunda.zeebe.client.api.response.Topology;
import io.camunda.zeebe.client.impl.oauth.OAuthCredentialsProvider;
import io.camunda.zeebe.client.impl.oauth.OAuthCredentialsProviderBuilder;
import io.zeebe.containers.ZeebeTopologyWaitStrategy;
import io.zeebe.containers.cluster.ZeebeCluster;
import io.zeebe.containers.cluster.ZeebeClusterBuilder;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.testcontainers.containers.BindMode;
import org.testcontainers.containers.Network;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

@Tag("integration")
@Testcontainers
class ZeebeKeycloakInterceptorIT {

  private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
  private final Network network = Network.builder().build();

  private final String keycloakContainerName = String.format("keycloak_%s", UUID.randomUUID());

  @Container
  private final KeycloakContainer keycloak =
      new KeycloakContainer()
          .withRealmImportFile("/keycloak/zeebe-realm.json")
          .withCreateContainerCmdModifier(
              createContainerCmd -> createContainerCmd.withName(keycloakContainerName))
          .withNetwork(network);

  @Test
  void shouldRunWithKeycloak(@TempDir Path tempDir) throws IOException {
    // given
    final ZeebeKeycloakConfiguration configuration;
    try (final InputStream configurationTemplate =
        ZeebeKeycloakInterceptorIT.class.getResourceAsStream(
            "/keycloak/zeebe-keycloak-configuration-integration-template.json")) {
      configuration =
          OBJECT_MAPPER.readValue(configurationTemplate, ZeebeKeycloakConfiguration.class);
    }
    final ZeebeKeycloakConfiguration.ZeebeKeycloak innerConfiguration =
        configuration.getZeebeKeycloak();
    innerConfiguration.setServerUrl(String.format("http://%s:%s", keycloakContainerName, 8080));
    final String authServerUrl =
        keycloak.getAuthServerUrl().substring(0, keycloak.getAuthServerUrl().length() - 1);
    innerConfiguration.setAllowedIssuers(List.of(authServerUrl));
    final Path configFilePath = Files.createFile(tempDir.resolve("zeebe-keycloak-config.json"));
    OBJECT_MAPPER.writeValue(configFilePath.toFile(), configuration);
    final String zeebeVersion = "1.2.12";
    ZeebeClusterBuilder builder =
        ZeebeCluster.builder()
            .withEmbeddedGateway(false)
            .withBrokersCount(1)
            .withGatewaysCount(1)
            .withPartitionsCount(1)
            .withReplicationFactor(1)
            .withNetwork(network)
            .withGatewayConfig(
                zeebeGatewayNode -> {
                  final String keyCloakInterceptorJarPath = "/tmp/keycloak-interceptor-all.jar";
                  zeebeGatewayNode
                      .withTopologyCheck(
                          new ZeebeTopologyWaitStrategy()
                              .forBuilder(
                                  () ->
                                      ZeebeClient.newClientBuilder()
                                          .credentialsProvider(
                                              getOauthClientProvider(authServerUrl))
                                          .usePlaintext()))
                      .withStartupTimeout(Duration.ofMinutes(5))
                      .withFileSystemBind(
                          "target/zeebe-keycloak-interceptor-0.0.1-SNAPSHOT-jar-with-dependencies.jar",
                          keyCloakInterceptorJarPath,
                          BindMode.READ_ONLY)
                      .withFileSystemBind(
                          configFilePath.toAbsolutePath().toString(),
                          KEYCLOAK_CONFIG_DEFAULT_PATH,
                          BindMode.READ_ONLY)
                      .withEnv(
                          Map.of(
                              "ZEEBE_GATEWAY_INTERCEPTORS_0_ID",
                              "keycloak-interceptor",
                              "ZEEBE_GATEWAY_INTERCEPTORS_0_JARPATH",
                              keyCloakInterceptorJarPath,
                              "ZEEBE_GATEWAY_INTERCEPTORS_0_CLASSNAME",
                              ZeebeKeycloakInterceptor.class.getName()));
                })
            .withImage(DockerImageName.parse("camunda/zeebe").withTag(zeebeVersion));
    try (final ZeebeCluster zeebeCluster = builder.build()) {
      zeebeCluster.start();
      try (final ZeebeClient zeebeClient =
          zeebeCluster
              .newClientBuilder()
              .credentialsProvider(getOauthClientProvider(authServerUrl))
              .build()) {
        // when
        final Topology topology =
            zeebeClient
                .newTopologyRequest()
                .requestTimeout(Duration.ofSeconds(5))
                .send()
                .join(5, TimeUnit.SECONDS);
        assertThat(topology.getGatewayVersion()).isEqualTo(zeebeVersion);
      }
      assertThat(zeebeCluster).isNotNull();
    }
  }

  @NotNull
  private OAuthCredentialsProvider getOauthClientProvider(String authServerUrl) {
    return new OAuthCredentialsProviderBuilder()
        .clientId("test-service")
        .clientSecret("pxInf8INhK1bKHhPqDRnvPAnY59ol2YC")
        .audience("zeebe-client-in-test")
        .authorizationServerUrl(
            String.format("%s/realms/test/protocol/openid-connect/token", authServerUrl))
        .build();
  }
}
