package io.github.aniketdeshkar.agenttestkit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

class FakeToolRegistryTest {
  @Test
  void recordsInputAndOutput() {
    var transcript = new AgentTranscript();
    var registry =
        new FakeToolRegistry(transcript).register("weather", "Find weather", ignored -> "sunny");

    assertEquals("sunny", registry.callback("weather").call("{\"city\":\"Pune\"}"));
    var call = transcript.toolInvocations().getFirst();
    assertEquals("weather", call.name());
    assertEquals("{\"city\":\"Pune\"}", call.arguments());
    assertEquals("sunny", call.result());
  }

  @Test
  void rejectsDuplicateAndUnknownTools() {
    var registry =
        new FakeToolRegistry(new AgentTranscript())
            .register("weather", "Find weather", ignored -> "sunny");

    assertThrows(
        IllegalArgumentException.class,
        () -> registry.register("weather", "Duplicate", ignored -> "rain"));
    assertThrows(IllegalArgumentException.class, () -> registry.callback("missing"));
  }
}
