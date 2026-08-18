package io.github.aniketdeshkar.agenttestkit;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Objects;

/** Entry points for behavioral transcript and structured response assertions. */
public final class AgentAssertions {
  private static final ObjectMapper DEFAULT_MAPPER = new ObjectMapper();

  private AgentAssertions() {}

  public static AgentTranscriptAssert assertThat(AgentTranscript transcript) {
    return new AgentTranscriptAssert(
        Objects.requireNonNull(transcript, "transcript"), DEFAULT_MAPPER);
  }

  public static AgentResponseAssert assertThatResponse(String response) {
    return new AgentResponseAssert(response, DEFAULT_MAPPER);
  }
}
