package io.github.aniketdeshkar.agenttestkit;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import io.github.aniketdeshkar.agenttestkit.junit.AgentTest;
import org.junit.jupiter.api.Test;

@AgentTest
class AgentTestExtensionTest {
  @Test
  void injectsAnIsolatedContext(AgentTestContext context) {
    assertNotNull(context.model());
    assertNotNull(context.tools());
    assertNotNull(context.transcript());
  }
}
