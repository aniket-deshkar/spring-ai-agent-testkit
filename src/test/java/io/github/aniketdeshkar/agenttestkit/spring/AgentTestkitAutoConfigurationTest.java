package io.github.aniketdeshkar.agenttestkit.spring;

import static org.junit.jupiter.api.Assertions.assertSame;

import io.github.aniketdeshkar.agenttestkit.AgentTestContext;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;

class AgentTestkitAutoConfigurationTest {
  private final ApplicationContextRunner runner =
      new ApplicationContextRunner()
          .withConfiguration(AutoConfigurations.of(AgentTestkitAutoConfiguration.class));

  @Test
  void providesOneSharedTestContext() {
    runner.run(
        context -> {
          var testContext = context.getBean(AgentTestContext.class);
          assertSame(testContext.model(), testContext.model());
          assertSame(testContext.transcript(), testContext.transcript());
        });
  }
}
