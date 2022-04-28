package org.camunda.community.zeebe.keycloak.interceptor;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.only;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import io.grpc.Metadata;
import io.grpc.ServerCall;
import io.grpc.ServerCallHandler;
import io.grpc.ServerInterceptor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ZeebeKeycloakInterceptorTest {
  private ServerInterceptor innerInterceptor;
  private ZeebeKeycloakInterceptor zeebeKeycloakInterceptor;

  @BeforeEach
  void setUp() {
    innerInterceptor = mock(ServerInterceptor.class);
    zeebeKeycloakInterceptor = new ZeebeKeycloakInterceptor(innerInterceptor);
  }

  @Test
  void shouldInterceptCall() {
    // given
    final ServerCall mockServerCall = mock(ServerCall.class);
    final Metadata mockMetadata = new Metadata();
    final ServerCallHandler mockServerCallHandler = mock(ServerCallHandler.class);
    final ServerCall.Listener mockListener = mock(ServerCall.Listener.class);
    when(innerInterceptor.interceptCall(mockServerCall, mockMetadata, mockServerCallHandler))
        .thenReturn(mockListener);
    // when
    final ServerCall.Listener listener =
        zeebeKeycloakInterceptor.interceptCall(mockServerCall, mockMetadata, mockServerCallHandler);
    // then
    assertThat(listener).isEqualTo(mockListener);
    verify(innerInterceptor, only())
        .interceptCall(mockServerCall, mockMetadata, mockServerCallHandler);
    verifyNoMoreInteractions(innerInterceptor);
  }
}
