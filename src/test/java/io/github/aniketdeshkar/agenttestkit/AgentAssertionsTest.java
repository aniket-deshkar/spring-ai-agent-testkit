package io.github.aniketdeshkar.agenttestkit;

import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;
import org.springframework.ai.chat.prompt.Prompt;

class AgentAssertionsTest {
  @Test
  void assertsBehaviorWithoutNaturalLanguageEquality() {
    var transcript = new AgentTranscript();
    transcript.recordModel(new Prompt("weather"));
    transcript.recordTool("weather", "{\"city\":\"Pune\"}", "sunny");

    AgentAssertions.assertThat(transcript)
        .requiredTool("weather")
        .forbiddenTool("delete_account")
        .toolCallCount("weather", 1)
        .toolArgumentEquals("weather", "/city", "Pune")
        .modelCallCount(1)
        .modelCallsAtMost(2)
        .stepsAtMost(2);
  }

  @Test
  void reportsActionableAssertionFailures() {
    var transcript = new AgentTranscript();

    assertThrows(
        AssertionError.class, () -> AgentAssertions.assertThat(transcript).requiredTool("weather"));
  }

  @Test
  void checksStructuredResponses() {
    AgentAssertions.assertThatResponse("{\"city\":\"Pune\",\"temperature\":31}")
        .isValidJson()
        .jsonPathEquals("/city", "Pune")
        .satisfies(
            Weather.class,
            weather -> {
              if (weather.temperature() < 30) {
                throw new AssertionError("expected a warm temperature");
              }
            });
  }

  private record Weather(String city, int temperature) {}
}
