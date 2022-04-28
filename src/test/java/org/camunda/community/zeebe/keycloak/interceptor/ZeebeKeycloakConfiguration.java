package org.camunda.community.zeebe.keycloak.interceptor;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ZeebeKeycloakConfiguration {
  private ZeebeKeycloak zeebeKeycloak;

  public ZeebeKeycloak getZeebeKeycloak() {
    return zeebeKeycloak;
  }

  public void setZeebeKeycloak(ZeebeKeycloak zeebeKeycloak) {
    this.zeebeKeycloak = zeebeKeycloak;
  }

  @JsonIgnoreProperties(ignoreUnknown = true)
  public static class ZeebeKeycloak {
    private String serverUrl;
    private String realm;
    private String grantType;
    private String clientId;
    private String clientSecret;
    private String username;
    private String password;
    private String expectedAudience;
    private String expectedIssuedFor;
    private List<String> allowedIssuers;
    private String minTimeBetweenJwksRequests;
    private String publicKeyCacheTtl;

    public void setServerUrl(String serverUrl) {
      this.serverUrl = serverUrl;
    }

    public void setRealm(String realm) {
      this.realm = realm;
    }

    public void setGrantType(String grantType) {
      this.grantType = grantType;
    }

    public void setClientId(String clientId) {
      this.clientId = clientId;
    }

    public void setClientSecret(String clientSecret) {
      this.clientSecret = clientSecret;
    }

    public void setUsername(String username) {
      this.username = username;
    }

    public void setPassword(String password) {
      this.password = password;
    }

    public void setExpectedAudience(String expectedAudience) {
      this.expectedAudience = expectedAudience;
    }

    public void setExpectedIssuedFor(String expectedIssuedFor) {
      this.expectedIssuedFor = expectedIssuedFor;
    }

    public void setAllowedIssuers(List<String> allowedIssuers) {
      this.allowedIssuers = allowedIssuers;
    }

    public void setMinTimeBetweenJwksRequests(String minTimeBetweenJwksRequests) {
      this.minTimeBetweenJwksRequests = minTimeBetweenJwksRequests;
    }

    public void setPublicKeyCacheTtl(String publicKeyCacheTtl) {
      this.publicKeyCacheTtl = publicKeyCacheTtl;
    }

    public String getServerUrl() {
      return serverUrl;
    }

    public String getRealm() {
      return realm;
    }

    public String getGrantType() {
      return grantType;
    }

    public String getClientId() {
      return clientId;
    }

    public String getClientSecret() {
      return clientSecret;
    }

    public String getUsername() {
      return username;
    }

    public String getPassword() {
      return password;
    }

    public String getExpectedAudience() {
      return expectedAudience;
    }

    public String getExpectedIssuedFor() {
      return expectedIssuedFor;
    }

    public List<String> getAllowedIssuers() {
      return allowedIssuers;
    }

    public String getMinTimeBetweenJwksRequests() {
      return minTimeBetweenJwksRequests;
    }

    public String getPublicKeyCacheTtl() {
      return publicKeyCacheTtl;
    }
  }
}
