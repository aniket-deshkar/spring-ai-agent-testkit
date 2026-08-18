package io.github.aniketdeshkar.agenttestkit.fixture;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import io.github.aniketdeshkar.agenttestkit.AgentTestContext;
import io.github.aniketdeshkar.agenttestkit.ScriptedResponse;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;
import org.springframework.ai.chat.messages.AssistantMessage;

/** Reads, writes, and installs recorded fixtures without invoking a live model. */
public final class FixtureCodec {
  private final ObjectMapper mapper;

  public FixtureCodec() {
    this(new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT));
  }

  public FixtureCodec(ObjectMapper mapper) {
    this.mapper = Objects.requireNonNull(mapper, "mapper");
  }

  public RecordedFixture read(Path path) {
    try {
      return mapper.readValue(path.toFile(), RecordedFixture.class);
    } catch (IOException exception) {
      throw new FixtureException("Cannot read agent fixture " + path, exception);
    }
  }

  public void write(Path path, RecordedFixture fixture) {
    try {
      Path parent = path.toAbsolutePath().getParent();
      if (parent != null) {
        Files.createDirectories(parent);
      }
      mapper.writeValue(path.toFile(), fixture);
    } catch (IOException exception) {
      throw new FixtureException("Cannot write agent fixture " + path, exception);
    }
  }

  public void install(AgentTestContext context, RecordedFixture fixture) {
    Objects.requireNonNull(context, "context");
    fixture.modelReplies().forEach(reply -> context.model().enqueue(toScriptedResponse(reply)));
    fixture
        .toolResults()
        .forEach(
            (name, result) ->
                context.tools().register(name, "Recorded fixture tool " + name, ignored -> result));
  }

  private ScriptedResponse toScriptedResponse(RecordedFixture.ModelReply reply) {
    if (!reply.toolRequests().isEmpty()) {
      var calls =
          reply.toolRequests().stream()
              .map(
                  request ->
                      new AssistantMessage.ToolCall(
                          request.id(), "function", request.name(), request.arguments()))
              .toList();
      return ScriptedResponse.toolCalls(calls);
    }
    return ScriptedResponse.text(reply.text());
  }

  public static final class FixtureException extends IllegalStateException {
    public FixtureException(String message, Throwable cause) {
      super(message, cause);
    }
  }
}
