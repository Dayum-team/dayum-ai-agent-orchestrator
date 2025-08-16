package dayum.aiagent.orchestrator.application;

import dayum.aiagent.orchestrator.application.context.dto.ConversationContext;
import dayum.aiagent.orchestrator.application.context.dto.ShortTermContext;
import dayum.aiagent.orchestrator.application.conversation.ConversationService;
import dayum.aiagent.orchestrator.application.tools.RecipeGenerateTool;
import dayum.aiagent.orchestrator.application.tools.ToolRegistry;
import dayum.aiagent.orchestrator.application.tools.model.request.RecipeGenerateRequest;
import dayum.aiagent.orchestrator.common.vo.Ingredient;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@RestController
@RequiredArgsConstructor
public class TestController {

  private final ConversationService conversationService;
  private final RecipeGenerateTool recipeGenerateTool;
  private final ToolRegistry toolRegistry;
  private final ObjectMapper objectMapper;

  @PostMapping("/chat/test")
  public String testChat(@RequestBody TestMessage message) {
    return conversationService.chat(
        message.getMemberId(), message.getSession(), message.getUserMessage());
  }

  @GetMapping("/test/tool-schema")
  public String toolSchemas() throws JsonProcessingException {
    return objectMapper.writeValueAsString(toolRegistry.getToolSchemaList());
  }

  @GetMapping("/test")
  public String test() {
    return recipeGenerateTool.execute(
        new ConversationContext(
            0,
            "0",
            List.of(
                new ShortTermContext(
                    "나는 매운게 싫어!",
                    "알겠어, 그럼 음식 추천할 때 매운 건 빼고 부드럽고 담백한 쪽으로 골라줄게.\n"
                        + "혹시 완전 무매운 모드로 할까요, 아니면 살짝 칼칼한 건 괜찮아요?")),
            "매운게 싫다고 한다..."),
        new RecipeGenerateRequest(
            List.of(
                new Ingredient("달걀", "2개"),
                new Ingredient("두부", "반모"),
                new Ingredient("토마토 소스"),
                new Ingredient("스리라차 소스"),
                new Ingredient("간장", "100g"))));
  }

  @Data
  @NoArgsConstructor
  @AllArgsConstructor
  public static class TestMessage {
    private long memberId;
    private String session;
    private String userMessage;
  }
}
