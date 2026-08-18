package io.github.aniketdeshkar.agenttestkit.example;

import io.github.aniketdeshkar.agenttestkit.AgentTestContext;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;

@SpringBootConfiguration
@EnableAutoConfiguration
class WeatherAgentTestApplication {
  @Bean
  ChatClient chatClient(AgentTestContext context) {
    context.tools().register("weather", "Current weather by city", ignored -> "31 C and sunny");
    return ChatClient.builder(context.model())
        .defaultTools(context.tools().callback("weather"))
        .build();
  }

  @Bean
  WeatherAgent weatherAgent(ChatClient chatClient) {
    return new WeatherAgent(chatClient);
  }
}
