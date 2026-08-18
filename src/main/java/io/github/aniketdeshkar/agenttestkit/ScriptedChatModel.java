package io.github.aniketdeshkar.agenttestkit;

import java.util.ArrayDeque;
import java.util.Collection;
import java.util.Deque;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.locks.ReentrantLock;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.model.Generation;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.model.tool.ToolCallingChatOptions;

/** A deterministic, thread-safe Spring AI {@link ChatModel} backed by an ordered script. */
public final class ScriptedChatModel implements ChatModel {
  private static final ToolCallingChatOptions DEFAULT_OPTIONS =
      ToolCallingChatOptions.builder().build();
  private final AgentTranscript transcript;
  private final Deque<ScriptedResponse> responses = new ArrayDeque<>();
  private final ReentrantLock lock = new ReentrantLock();

  public ScriptedChatModel(AgentTranscript transcript) {
    this.transcript = Objects.requireNonNull(transcript, "transcript");
  }

  public ScriptedChatModel enqueue(ScriptedResponse response) {
    lock.lock();
    try {
      responses.addLast(Objects.requireNonNull(response, "response"));
      return this;
    } finally {
      lock.unlock();
    }
  }

  public ScriptedChatModel enqueueAll(Collection<ScriptedResponse> scriptedResponses) {
    Objects.requireNonNull(scriptedResponses, "scriptedResponses").forEach(this::enqueue);
    return this;
  }

  public int remainingResponses() {
    lock.lock();
    try {
      return responses.size();
    } finally {
      lock.unlock();
    }
  }

  @Override
  public ChatResponse call(Prompt prompt) {
    Objects.requireNonNull(prompt, "prompt");
    ScriptedResponse response;
    lock.lock();
    try {
      transcript.recordModel(prompt);
      response = responses.pollFirst();
    } finally {
      lock.unlock();
    }
    if (response == null) {
      throw new UnscriptedModelCallException(transcript.modelInvocations().size());
    }
    if (response.failure() != null) {
      throw response.failure();
    }
    AssistantMessage message =
        AssistantMessage.builder().content(response.text()).toolCalls(response.toolCalls()).build();
    return new ChatResponse(List.of(new Generation(message)));
  }

  @Override
  public ToolCallingChatOptions getOptions() {
    return DEFAULT_OPTIONS;
  }

  public static final class UnscriptedModelCallException extends IllegalStateException {
    public UnscriptedModelCallException(int callNumber) {
      super("No scripted response remains for model call " + callNumber);
    }
  }
}
