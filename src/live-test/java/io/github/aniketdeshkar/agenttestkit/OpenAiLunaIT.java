package io.github.aniketdeshkar.agenttestkit;

import static org.junit.jupiter.api.Assertions.assertFalse;

import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.Test;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.ai.openai.OpenAiChatOptions;

class OpenAiLunaIT {
  @Test
  void invokesConfiguredLunaModelOnlyWhenExplicitlyEnabled() {
    String apiKey = System.getenv("OPENAI_API_KEY");
    Assumptions.assumeTrue(apiKey != null && !apiKey.isBlank(), "OPENAI_API_KEY is not set");

    String modelName = System.getProperty("openai.model", "gpt-5.6-luna");
    var model =
        OpenAiChatModel.builder()
            .options(OpenAiChatOptions.builder().apiKey(apiKey).model(modelName).build())
            .build();

    String response = model.call("Reply with the single word: ready");
    assertFalse(response == null || response.isBlank());
  }
}
