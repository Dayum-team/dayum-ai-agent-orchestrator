package dayum.aiagent.orchestrator.client.dayum;

import dayum.aiagent.orchestrator.client.dayum.dto.RecommendContentsRequest;
import dayum.aiagent.orchestrator.client.dayum.dto.RecommendContentsResponse;
import dayum.aiagent.orchestrator.common.vo.Ingredient;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
@RequiredArgsConstructor
public class DayumApiClient {

  private final DayumServerProperties dayumProperties;
  private final RestClient restClient;

  public List<RecommendContentsResponse> recommendContentsBy(List<Ingredient> ingredients) {
    var response =
        restClient
            .post()
            .uri(dayumProperties.getBaseUrl() + "/internal/api/contents/recommended")
            .header("INTERNAL-API-KEY", dayumProperties.getApiKey())
            .body(new RecommendContentsRequest(ingredients))
            .retrieve()
            .body(
                new ParameterizedTypeReference<
                    DayumApiResponse<List<RecommendContentsResponse>>>() {});
    if (response == null || !response.success()) {
      throw new RuntimeException();
    }
    return response.data();
  }

  private record DayumApiResponse<T>(boolean success, T data) {}
}
