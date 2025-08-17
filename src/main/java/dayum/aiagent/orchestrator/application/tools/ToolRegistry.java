package dayum.aiagent.orchestrator.application.tools;

import com.fasterxml.jackson.databind.ObjectMapper;
import dayum.aiagent.orchestrator.application.context.model.ConversationContext;
import dayum.aiagent.orchestrator.client.chat.schema.JsonSchemaGenerator;
import dayum.aiagent.orchestrator.client.chat.schema.SchemaFactory;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class ToolRegistry {

  private final List<Tool<? extends ToolRequest>> tools;
  private final ObjectMapper objectMapper;

  public List<SchemaFactory.ToolSchema> getToolSchemaList() {
    return tools.stream()
        .map(
            tool ->
                new SchemaFactory.ToolSchema(
                    tool.getName(),
                    tool.getDescription(),
                    JsonSchemaGenerator.generate(tool.getClass())))
        .toList();
  }

  public String execute(String toolName, String argumentsJsonString, ConversationContext context) {
    var selectedTool =
        tools.stream().filter(tool -> tool.getName().equals(toolName)).findFirst().get();
    return this.execute(selectedTool, argumentsJsonString, context);
  }

  private <T extends ToolRequest> String execute(
      Tool<T> tool, String argumentsJsonString, ConversationContext context) {
    try {
      T request = objectMapper.readValue(argumentsJsonString, tool.getRequestType());
      var response = tool.execute(context, request);
      return objectMapper.writeValueAsString(response);
    } catch (Exception e) {
      log.error("Invalid format tool request & response.", e);
      return "";
    }
  }
}
