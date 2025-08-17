package dayum.aiagent.orchestrator.client.chat.dto;

import java.util.List;

import dayum.aiagent.orchestrator.common.vo.Ingredient;

public record ExtractIngredientsResponse(List<Ingredient> ingredients) {}
