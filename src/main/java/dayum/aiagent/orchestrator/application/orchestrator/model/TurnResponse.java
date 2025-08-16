package dayum.aiagent.orchestrator.application.orchestrator.model;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public record TurnResponse(List<PlaybookResponse> steps) {

  public String toSingleString() {
    if (steps == null || steps.isEmpty()) return "";

    return IntStream.range(0, steps.size())
        .mapToObj(
            i -> {
              PlaybookResponse step = steps.get(i);
              if (step == null) return "";

              String title = step.title();
              String body = step.message();

              StringBuilder sb = new StringBuilder();
              String numberedTitle =
                  (title == null || title.isBlank())
                      ? String.format("%d) ", i + 1)
                      : String.format("%d) %s", i + 1, title.trim());

              sb.append(numberedTitle).append("\n\n");
              if (body != null && !body.isBlank()) {
                sb.append(body.trim());
              }

              return sb.toString().trim();
            })
        .filter(s -> !s.isBlank())
        .collect(Collectors.joining("\n\n"));
  }
}
