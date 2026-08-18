package io.github.aniketdeshkar.agenttestkit.fixture;

import java.util.List;
import java.util.Map;

/** Portable, versioned script of model responses and deterministic tool results. */
public record RecordedFixture(
    int schemaVersion, List<ModelReply> modelReplies, Map<String, String> toolResults) {
  public static final int CURRENT_SCHEMA_VERSION = 1;

  public RecordedFixture {
    modelReplies = List.copyOf(modelReplies);
    toolResults = Map.copyOf(toolResults);
    if (schemaVersion != CURRENT_SCHEMA_VERSION) {
      throw new IllegalArgumentException("Unsupported fixture schema version: " + schemaVersion);
    }
  }

  public record ModelReply(String text, List<ToolRequest> toolRequests) {
    public ModelReply {
      toolRequests = List.copyOf(toolRequests);
      if (text == null && toolRequests.isEmpty()) {
        throw new IllegalArgumentException("A model reply needs text or at least one tool request");
      }
    }
  }

  public record ToolRequest(String id, String name, String arguments) {}
}
