package io.github.aniketdeshkar.agenttestkit;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.ai.tool.definition.ToolDefinition;

/** Registry of deterministic tool callbacks that records every invocation. */
public final class FakeToolRegistry {
  public static final String OBJECT_SCHEMA = "{\"type\":\"object\",\"additionalProperties\":true}";

  private final AgentTranscript transcript;
  private final Map<String, ToolCallback> callbacks = new LinkedHashMap<>();

  public FakeToolRegistry(AgentTranscript transcript) {
    this.transcript = Objects.requireNonNull(transcript, "transcript");
  }

  public synchronized FakeToolRegistry register(
      String name, String description, Function<String, String> handler) {
    return register(name, description, OBJECT_SCHEMA, handler);
  }

  public synchronized FakeToolRegistry register(
      String name, String description, String inputSchema, Function<String, String> handler) {
    Objects.requireNonNull(name, "name");
    if (callbacks.containsKey(name)) {
      throw new IllegalArgumentException("Tool already registered: " + name);
    }
    ToolDefinition definition =
        ToolDefinition.builder()
            .name(name)
            .description(Objects.requireNonNull(description, "description"))
            .inputSchema(Objects.requireNonNull(inputSchema, "inputSchema"))
            .build();
    Function<String, String> checkedHandler = Objects.requireNonNull(handler, "handler");
    callbacks.put(
        name,
        new ToolCallback() {
          @Override
          public ToolDefinition getToolDefinition() {
            return definition;
          }

          @Override
          public String call(String input) {
            String result = checkedHandler.apply(input);
            transcript.recordTool(name, input, result);
            return result;
          }
        });
    return this;
  }

  public synchronized ToolCallback[] callbacks() {
    return callbacks.values().toArray(ToolCallback[]::new);
  }

  public synchronized ToolCallback callback(String name) {
    ToolCallback callback = callbacks.get(name);
    if (callback == null) {
      throw new IllegalArgumentException("Unknown fake tool: " + name);
    }
    return callback;
  }
}
