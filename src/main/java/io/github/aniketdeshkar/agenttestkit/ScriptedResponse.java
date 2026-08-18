package io.github.aniketdeshkar.agenttestkit;

import java.util.List;
import java.util.Objects;
import org.springframework.ai.chat.messages.AssistantMessage;

/** One deterministic response or failure consumed by {@link ScriptedChatModel}. */
public final class ScriptedResponse {
  private final String text;
  private final List<AssistantMessage.ToolCall> toolCalls;
  private final RuntimeException failure;

  private ScriptedResponse(
      String text, List<AssistantMessage.ToolCall> toolCalls, RuntimeException failure) {
    this.text = text;
    this.toolCalls = List.copyOf(toolCalls);
    this.failure = failure;
  }

  public static ScriptedResponse text(String text) {
    return new ScriptedResponse(Objects.requireNonNull(text, "text"), List.of(), null);
  }

  public static ScriptedResponse toolCall(String id, String name, String jsonArguments) {
    var call =
        new AssistantMessage.ToolCall(
            Objects.requireNonNull(id, "id"),
            "function",
            Objects.requireNonNull(name, "name"),
            Objects.requireNonNull(jsonArguments, "jsonArguments"));
    return new ScriptedResponse(null, List.of(call), null);
  }

  public static ScriptedResponse toolCalls(List<AssistantMessage.ToolCall> calls) {
    if (calls.isEmpty()) {
      throw new IllegalArgumentException("calls must not be empty");
    }
    return new ScriptedResponse(null, calls, null);
  }

  public static ScriptedResponse failure(RuntimeException failure) {
    return new ScriptedResponse(null, List.of(), Objects.requireNonNull(failure, "failure"));
  }

  String text() {
    return text;
  }

  List<AssistantMessage.ToolCall> toolCalls() {
    return toolCalls;
  }

  RuntimeException failure() {
    return failure;
  }
}
