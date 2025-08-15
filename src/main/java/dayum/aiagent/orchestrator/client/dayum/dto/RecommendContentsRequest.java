package dayum.aiagent.orchestrator.client.dayum.dto;

import dayum.aiagent.orchestrator.common.vo.Ingredient;
import java.util.List;

public record RecommendContentsRequest(List<Ingredient> ingredients) {}
