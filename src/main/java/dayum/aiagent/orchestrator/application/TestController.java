package dayum.aiagent.orchestrator.application;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import dayum.aiagent.orchestrator.application.context.model.ContextType;
import dayum.aiagent.orchestrator.application.context.model.ConversationContext;
import dayum.aiagent.orchestrator.application.context.model.PantryContext;
import dayum.aiagent.orchestrator.application.orchestrator.PlaybookPlanner;
import dayum.aiagent.orchestrator.application.orchestrator.model.PlaybookPlanResult;
import dayum.aiagent.orchestrator.application.orchestrator.model.PlaybookResult;
import dayum.aiagent.orchestrator.application.orchestrator.playbook.generateDietRecipe.GenerateDietRecipePlaybook;
import dayum.aiagent.orchestrator.application.tools.ToolRegistry;
import dayum.aiagent.orchestrator.common.vo.Ingredient;
import dayum.aiagent.orchestrator.common.vo.UserMessage;
import java.util.List;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class TestController {

  private final ToolRegistry toolRegistry;
  private final ObjectMapper objectMapper;
  private final PlaybookPlanner planner;
  private final GenerateDietRecipePlaybook playbook;

  @PostMapping("/test/plan")
  public List<PlaybookPlanResult> planning(@RequestBody TestMessage message) {
    return planner.planning(
        new ConversationContext(
            message.memberId,
            message.getSession(),
            Map.of(
                ContextType.PANTRY,
                new PantryContext(
                    List.of(
                        new Ingredient("계란"),
                        new Ingredient("호떡"),
                        new Ingredient("두부"),
                        new Ingredient("간장"),
                        new Ingredient("토마토소스")))),
            List.of(),
            "사용자가 제공한 계란, 호떡, 두부, 간장, 토마토소스를 재료로 기억합니다."),
        message.userMessage);
  }

  @PostMapping("/test/generate")
  public PlaybookResult generate(@RequestBody TestMessage message) {
    return playbook.play(
        new ConversationContext(
            message.memberId,
            message.getSession(),
            Map.of(
                ContextType.PANTRY,
                new PantryContext(
                    List.of(
                        new Ingredient("계란"),
                        new Ingredient("호떡"),
                        new Ingredient("두부"),
                        new Ingredient("간장"),
                        new Ingredient("토마토소스")))),
            List.of(),
            "사용자가 제공한 계란, 호떡, 두부, 간장, 토마토소스를 재료로 기억합니다."),
        message.userMessage);
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
