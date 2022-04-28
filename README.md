[![](https://img.shields.io/badge/Lifecycle-Proof%20of%20Concept-blueviolet)](https://github.com/Camunda-Community-Hub/community/blob/main/extension-lifecycle.md#proof-of-concept-)
[![Community extension badge](https://img.shields.io/badge/Community%20Extension-An%20open%20source%20community%20maintained%20project-FF4700)](https://github.com/camunda-community-hub/community)

# Zeebe Keycloak Interceptor

This project's purpose is to enable authentication on Zeebe Gateways.

## Usage

### Quick start
* Obtain jar with dependencies of this project
  * Maven Coordinates (groupId: org.camunda.community.zeebe.keycloak.interceptor, artifactId: zeebe-keycloak-interceptor)
  * Build by yourself in this project
    * Prerequisites:
      * JDK 11
      * Maven
      * Docker
    * Run: `mvn clean verify`
* Copy this jar to the Zeebe Gateway (or Zeebe Broker, if you are using embedded gateways) container
* Create a JSON configuration file
  * All available configurations (this is the default configuration. The file, that you have created can only override some properties. Also, this project uses the [HOCON configuration](https://github.com/lightbend/config)):
```json
{
  "zeebeKeycloak": {
    "serverUrl": "http://localhost:8080",
    "realm": "test-realm",
    "grantType": "client_credentials",
    "clientId": "test-client-id",
    "clientSecret": "test-client-secret",
    "username": "test-username",
    "password": "test-password",
    "expectedAudience": "test-expected-audience",
    "expectedIssuedFor": "test-expected-issued-for",
    "allowedIssuers": [],
    "minTimeBetweenJwksRequests": "12 s",
    "publicKeyCacheTtl": "12 d"
  }
}
```
* Put this file into the Zeebe Gateway container of container path: `/tmp/zeebe-keycloak.json`
* [Configure the Zeebe Gateway's interceptors](https://docs.camunda.io/docs/self-managed/zeebe-deployment/interceptors/#loading-an-interceptor-into-a-gateway) (you should point to the jar that we have copied to the container):
  * `className` should be `org.camunda.community.zeebe.keycloak.interceptor.ZeebeKeycloakInterceptor`

### Configure the configuration file location
The algorithm for resolving the location of the config file:
1. Looking at the Java property of `zeebe.gateway.security.keycloak.config.path`
2. If the previous is missed look at the environment variable: `ZEEBE_GATEWAY_SECURITY_KEYCLOAK_CONFIG_PATH`
3. If the previous is missed get this path: `/tmp/zeebe-keycloak.json`

## Versioning

This is still in progress and could be changed in the future. Because we have to build this library against each version of Zeebe (to verify compatibility on class level and so) so the versions will be stick to the Zeebe version with suffix:
```regexp
^(\d+\.\d+\.\d+)(-\d+)?$
```
Where this part `(\d+\.\d+\.\d+)` describing Zeebe version (i.e. 1.0.1, 1.2.11, etc.) and there is optional part that describing patch version of this interceptor: `(-\d+)`, i.e `-1, -3, -4`. So there is the next valid versions of this library:
`1.2.11`, `8.0.0-4`. In case of chosing version I will recomend to update to the version with suffix, i.e. `1.2.11-5` is preferable than `1.2.11`.
