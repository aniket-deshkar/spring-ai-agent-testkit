package io.github.aniketdeshkar.agenttestkit;

import java.time.Instant;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import org.springframework.ai.chat.prompt.Prompt;

/** Thread-safe record of model and tool interactions observed during one test. */
public final class AgentTranscript {
  private final CopyOnWriteArrayList<ModelInvocation> modelInvocations =
      new CopyOnWriteArrayList<>();
  private final CopyOnWriteArrayList<ToolInvocation> toolInvocations = new CopyOnWriteArrayList<>();

  public List<ModelInvocation> modelInvocations() {
    return List.copyOf(modelInvocations);
  }

  public List<ToolInvocation> toolInvocations() {
    return List.copyOf(toolInvocations);
  }

  public int stepCount() {
    return modelInvocations.size() + toolInvocations.size();
  }

  public void clear() {
    modelInvocations.clear();
    toolInvocations.clear();
  }

  void recordModel(Prompt prompt) {
    modelInvocations.add(new ModelInvocation(modelInvocations.size() + 1, prompt, Instant.now()));
  }

  void recordTool(String name, String arguments, String result) {
    toolInvocations.add(
        new ToolInvocation(toolInvocations.size() + 1, name, arguments, result, Instant.now()));
  }

  public record ModelInvocation(int sequence, Prompt prompt, Instant recordedAt) {}

  public record ToolInvocation(
      int sequence, String name, String arguments, String result, Instant recordedAt) {}
}
