package org.camunda.community.zeebe.keycloak.interceptor;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import com.typesafe.config.ConfigParseOptions;
import com.typesafe.config.ConfigSyntax;
import java.io.File;
import java.util.function.Supplier;

public class ConfigurationProvider implements Supplier<Config> {

  public static final String ZEEBE_KEYCLOAK_CONFIG_NAME = "zeebeKeycloak";

  private final Supplier<String> configFileLocationProvider;

  public ConfigurationProvider() {
    this(new ConfigFileLocationProvider());
  }

  ConfigurationProvider(Supplier<String> configFileLocationProvider) {
    this.configFileLocationProvider = configFileLocationProvider;
  }

  private final Config defaultConfig =
      ConfigFactory.parseResources(
          ZeebeKeycloakInterceptor.class.getClassLoader(), "keycloak-reference.conf");

  @Override
  public Config get() {
    return ConfigFactory.parseFile(
            getConfigFile(),
            ConfigParseOptions.defaults()
                // this file should be JSON
                .setSyntax(ConfigSyntax.JSON))
        .withFallback(defaultConfig)
        .getConfig(ZEEBE_KEYCLOAK_CONFIG_NAME);
  }

  private File getConfigFile() {
    return new File(configFileLocationProvider.get());
  }
}
