package io.github.aniketdeshkar.agenttestkit.example;

import io.github.aniketdeshkar.agenttestkit.AgentAssertions;
import io.github.aniketdeshkar.agenttestkit.AgentTestContext;
import io.github.aniketdeshkar.agenttestkit.ScriptedResponse;
import io.github.aniketdeshkar.agenttestkit.spring.AgentTestkitAutoConfiguration;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;

class WeatherAgentExampleTest {
  private final ApplicationContextRunner runner =
      new ApplicationContextRunner()
          .withConfiguration(AutoConfigurations.of(AgentTestkitAutoConfiguration.class))
          .withUserConfiguration(WeatherAgentTestApplication.class);

  @Test
  void testsARealChatClientToolLoopWithoutNetwork() {
    runner.run(
        application -> {
          var context = application.getBean(AgentTestContext.class);
          context
              .model()
              .enqueue(ScriptedResponse.toolCall("call-1", "weather", "{\"city\":\"Pune\"}"))
              .enqueue(ScriptedResponse.text("{\"city\":\"Pune\",\"summary\":\"31 C and sunny\"}"));

          String response =
              application.getBean(WeatherAgent.class).answer("What is the weather in Pune?");

          context
              .assertThatTranscript()
              .modelCallCount(2)
              .requiredTool("weather")
              .forbiddenTool("delete_account")
              .toolArgumentEquals("weather", "/city", "Pune")
              .stepsAtMost(3);
          AgentAssertions.assertThatResponse(response).jsonPathEquals("/city", "Pune");
        });
  }
}
