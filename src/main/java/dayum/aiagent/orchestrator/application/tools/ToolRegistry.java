package dayum.aiagent.orchestrator.application.tools;

import com.fasterxml.jackson.databind.ObjectMapper;
import dayum.aiagent.orchestrator.application.context.dto.ConversationContext;
import dayum.aiagent.orchestrator.application.tools.model.request.ToolRequest;
import dayum.aiagent.orchestrator.client.chat.dto.ToolSignatureSchema;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class ToolRegistry {

  private final List<Tool<ToolRequest>> tools;
  private final ObjectMapper objectMapper;

  public List<ToolSignatureSchema.ToolSchema> getToolSchemaList() {
    return tools.stream()
        .map(
            tool ->
                new ToolSignatureSchema.ToolSchema(
                    tool.getName(), tool.getDescription(), tool.getSchema()))
        .toList();
  }

  public String execute(String toolName, String argumentsJsonString, ConversationContext context) {
    var selectedTool =
        tools.stream().filter(tool -> tool.getName().equals(toolName)).findFirst().get();

    try {
      var request = objectMapper.readValue(argumentsJsonString, selectedTool.getRequestType());
      var response = selectedTool.execute(context, request);
      return objectMapper.writeValueAsString(response);
    } catch (Exception e) {
      log.error("Invalid format tool request & response.", e);
      return "";
    }
  }
}
