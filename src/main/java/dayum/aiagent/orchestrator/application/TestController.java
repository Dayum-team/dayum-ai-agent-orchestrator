package dayum.aiagent.orchestrator.application;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import dayum.aiagent.orchestrator.application.context.model.ConversationContext;
import dayum.aiagent.orchestrator.application.orchestrator.Planner;
import dayum.aiagent.orchestrator.application.orchestrator.playbook.Playbook;
import dayum.aiagent.orchestrator.application.orchestrator.playbook.PlaybookType;
import dayum.aiagent.orchestrator.application.tools.ToolRegistry;
import dayum.aiagent.orchestrator.common.vo.UserMessage;
import java.util.List;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

import org.springframework.data.util.Pair;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class TestController {

  private final ToolRegistry toolRegistry;
  private final ObjectMapper objectMapper;
  private final Planner planner;

  @PostMapping("/test/plan")
  public List<Pair<PlaybookType, String>> planning(@RequestBody TestMessage message) {
    return planner
        .planning(
            new ConversationContext(
                message.memberId, message.getSession(), Map.of(), List.of(), ""),
            message.userMessage)
        .stream()
        .map(p -> Pair.of(p.getFirst().getType(), p.getSecond()))
        .toList();
  }

  @GetMapping("/test/tool-schema")
  public String toolSchemas() throws JsonProcessingException {
    return objectMapper.writeValueAsString(toolRegistry.getToolSchemaList());
  }

  @Data
  @NoArgsConstructor
  @AllArgsConstructor
  public static class TestMessage {
    private long memberId;
    private String session;
    private UserMessage userMessage;
  }
}
