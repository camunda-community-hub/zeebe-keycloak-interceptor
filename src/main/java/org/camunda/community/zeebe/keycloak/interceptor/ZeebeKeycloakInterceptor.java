package org.camunda.community.zeebe.keycloak.interceptor;

import com.avast.grpc.jwt.keycloak.server.KeycloakJwtServerInterceptor;
import com.typesafe.config.Config;
import io.grpc.Metadata;
import io.grpc.ServerCall;
import io.grpc.ServerCallHandler;
import io.grpc.ServerInterceptor;
import java.util.function.Supplier;

public class ZeebeKeycloakInterceptor implements ServerInterceptor {

  public ZeebeKeycloakInterceptor() {
    this(new ConfigurationProvider());
  }

  ZeebeKeycloakInterceptor(Supplier<Config> configSupplier) {
    this.innerInterceptor =
        KeycloakJwtServerInterceptor.fromConfig(
            configSupplier.get(), ZeebeKeycloakInterceptor.class.getClassLoader());
  }

  ZeebeKeycloakInterceptor(ServerInterceptor innerInterceptor) {
    this.innerInterceptor = innerInterceptor;
  }

  private final ServerInterceptor innerInterceptor;

  @Override
  public <ReqT, RespT> ServerCall.Listener<ReqT> interceptCall(
      ServerCall<ReqT, RespT> call, Metadata headers, ServerCallHandler<ReqT, RespT> next) {
    return innerInterceptor.interceptCall(call, headers, next);
  }
}
