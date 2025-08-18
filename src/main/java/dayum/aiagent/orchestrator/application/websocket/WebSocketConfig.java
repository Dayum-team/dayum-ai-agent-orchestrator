package dayum.aiagent.orchestrator.application.websocket;

import dayum.aiagent.orchestrator.application.security.JwtProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;
import org.springframework.web.socket.server.HandshakeInterceptor;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Map;

@Configuration
@EnableWebSocket
@RequiredArgsConstructor
public class WebSocketConfig implements WebSocketConfigurer {

  private final AiChatHandler aiChatHandler;
  private final JwtProvider jwtProvider;

  @Override
  public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
    registry.addHandler(aiChatHandler, "/ws-ai")
        .addInterceptors(new HandshakeInterceptor() {
          @Override
          public boolean beforeHandshake(ServerHttpRequest req, ServerHttpResponse res,
              WebSocketHandler h, Map<String, Object> attrs) {
            var q = UriComponentsBuilder.fromUri(req.getURI()).build().getQueryParams();
            var token = q.getFirst("token");
            if (!StringUtils.hasText(token)) return false;

            token = stripBearer(token);
            if (!jwtProvider.validate(token)) return false;

            Long memberId = jwtProvider.getMemberId(token);
            if (memberId == null) return false;

            attrs.put("memberId", memberId);
            return true;
          }

          @Override
          public void afterHandshake(ServerHttpRequest a, ServerHttpResponse b, WebSocketHandler c, Exception d) {}

          private String stripBearer(String t) {
            var trimmed = t.trim();
            return trimmed.startsWith("Bearer ") ? trimmed.substring(7) : trimmed;
          }
        })
        .setAllowedOriginPatterns("*");
  }
}
