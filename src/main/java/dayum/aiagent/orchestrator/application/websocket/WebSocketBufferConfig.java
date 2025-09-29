package dayum.aiagent.orchestrator.application.websocket;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.server.standard.ServletServerContainerFactoryBean;
@Configuration
public class WebSocketBufferConfig {
  @Bean
  public ServletServerContainerFactoryBean createWebSocketContainer() {
    var c = new ServletServerContainerFactoryBean();
    c.setMaxTextMessageBufferSize(2 * 1024 * 1024);   // 2MB
    c.setMaxBinaryMessageBufferSize(2 * 1024 * 1024); // 2MB
    // c.setMaxSessionIdleTimeout(10_000L); // 선택
    return c;
  }
}
