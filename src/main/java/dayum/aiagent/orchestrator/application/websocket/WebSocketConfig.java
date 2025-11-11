package dayum.aiagent.orchestrator.application.websocket;

import dayum.aiagent.orchestrator.application.security.JwtProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.util.StringUtils;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;
import org.springframework.web.socket.server.support.HttpSessionHandshakeInterceptor;
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
        .addInterceptors(new HttpSessionHandshakeInterceptor() {
          @Override
          public boolean beforeHandshake(ServerHttpRequest req, ServerHttpResponse res,
              WebSocketHandler wsHandler, Map<String, Object> attrs) {
            var q = UriComponentsBuilder.fromUri(req.getURI()).build().getQueryParams();
            String token = q.getFirst("token");
            String sessionId = q.getFirst("sessionId");

            if (!StringUtils.hasText(token)) return false;
            token = token.replace("Bearer ", "");
            if (!jwtProvider.validate(token)) return false;

            Long memberId = jwtProvider.getMemberId(token);
            if (memberId == null) return false;

            attrs.put("memberId", memberId);
            if (StringUtils.hasText(sessionId)) {
              attrs.put("sessionId", sessionId);
            }
            return true;
          }
        })
        .setAllowedOrigins("*");
  }
}
