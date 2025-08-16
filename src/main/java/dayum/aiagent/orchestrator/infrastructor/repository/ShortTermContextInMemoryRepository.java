package dayum.aiagent.orchestrator.infrastructor.repository;

import dayum.aiagent.orchestrator.application.context.dto.ShortTermContext;
import dayum.aiagent.orchestrator.application.context.port.ShortTermContextRepository;
import java.util.Deque;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.atomic.AtomicReference;
import org.springframework.stereotype.Repository;

@Repository
public class ShortTermContextInMemoryRepository implements ShortTermContextRepository {

  private static final int MAX_SIZE_CONTEXT = 3;
  private final AtomicReference<Map<String, Deque<ShortTermContext>>> contexts =
      new AtomicReference<>();

  @Override
  public List<ShortTermContext> fetchBy(String sessionId) {
    return contexts.get().getOrDefault(sessionId, new ConcurrentLinkedDeque<>()).stream().toList();
  }

  @Override
  public void append(String sessionId, ShortTermContext newContext) {
    Deque<ShortTermContext> context =
        contexts.get().computeIfAbsent(sessionId, key -> new ConcurrentLinkedDeque<>());
    context.addLast(newContext);
    if (context.size() > MAX_SIZE_CONTEXT) {
      context.pollFirst();
    }
  }
}
