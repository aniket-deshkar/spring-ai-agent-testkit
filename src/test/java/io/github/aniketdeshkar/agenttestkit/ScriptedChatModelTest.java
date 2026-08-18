package io.github.aniketdeshkar.agenttestkit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.Test;
import org.springframework.ai.chat.prompt.Prompt;

class ScriptedChatModelTest {
  @Test
  void consumesResponsesInOrderAndRecordsPrompts() {
    var transcript = new AgentTranscript();
    var model =
        new ScriptedChatModel(transcript)
            .enqueue(ScriptedResponse.text("first"))
            .enqueue(ScriptedResponse.text("second"));

    assertEquals("first", model.call("one"));
    assertEquals("second", model.call("two"));
    assertEquals(2, transcript.modelInvocations().size());
    assertEquals(0, model.remainingResponses());
  }

  @Test
  void failsFastWhenScriptIsExhausted() {
    var model = new ScriptedChatModel(new AgentTranscript());

    var error =
        assertThrows(
            ScriptedChatModel.UnscriptedModelCallException.class,
            () -> model.call(new Prompt("unexpected")));

    assertEquals("No scripted response remains for model call 1", error.getMessage());
  }

  @Test
  void preservesScriptedFailureInstance() {
    var expected = new IllegalStateException("provider unavailable");
    var model =
        new ScriptedChatModel(new AgentTranscript()).enqueue(ScriptedResponse.failure(expected));

    assertSame(expected, assertThrows(IllegalStateException.class, () -> model.call("hello")));
  }

  @Test
  void emitsSpringAiToolCallResponses() {
    var model =
        new ScriptedChatModel(new AgentTranscript())
            .enqueue(ScriptedResponse.toolCall("call-1", "weather", "{\"city\":\"Pune\"}"));

    assertTrue(model.call(new Prompt("weather")).hasToolCalls());
  }

  @Test
  void serializesConcurrentScriptConsumption() throws Exception {
    int calls = 20;
    var model = new ScriptedChatModel(new AgentTranscript());
    for (int index = 0; index < calls; index++) {
      model.enqueue(ScriptedResponse.text("response-" + index));
    }
    var start = new CountDownLatch(1);
    try (var executor = Executors.newFixedThreadPool(8)) {
      var futures =
          java.util.stream.IntStream.range(0, calls)
              .mapToObj(
                  index ->
                      executor.submit(
                          () -> {
                            start.await(5, TimeUnit.SECONDS);
                            return model.call("request-" + index);
                          }))
              .toList();
      start.countDown();
      for (var future : futures) {
        future.get(5, TimeUnit.SECONDS);
      }
    }
    assertEquals(0, model.remainingResponses());
  }
}
