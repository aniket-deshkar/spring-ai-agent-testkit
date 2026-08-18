package io.github.aniketdeshkar.agenttestkit.spring;

import io.github.aniketdeshkar.agenttestkit.AgentTestContext;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.Bean;

/** Explicit test auto-configuration imported through {@link AutoConfigureAgentTestkit}. */
@AutoConfiguration
public class AgentTestkitAutoConfiguration {
  @Bean
  AgentTestContext agentTestContext() {
    return new AgentTestContext();
  }
}
