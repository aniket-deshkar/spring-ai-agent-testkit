package io.github.aniketdeshkar.agenttestkit.example;

import org.springframework.ai.chat.client.ChatClient;

final class WeatherAgent {
  private final ChatClient chatClient;

  WeatherAgent(ChatClient chatClient) {
    this.chatClient = chatClient;
  }

  String answer(String question) {
    return chatClient.prompt(question).call().content();
  }
}
