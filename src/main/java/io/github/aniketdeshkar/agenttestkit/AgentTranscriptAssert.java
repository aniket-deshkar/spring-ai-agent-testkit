package io.github.aniketdeshkar.agenttestkit;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;

/** Fluent assertions over observed model and tool behavior. */
public final class AgentTranscriptAssert {
  private final AgentTranscript transcript;
  private final ObjectMapper mapper;

  AgentTranscriptAssert(AgentTranscript transcript, ObjectMapper mapper) {
    this.transcript = transcript;
    this.mapper = mapper;
  }

  public AgentTranscriptAssert requiredTool(String name) {
    if (calls(name).isEmpty()) {
      fail("Expected tool '" + name + "' to be called, but observed " + observedTools());
    }
    return this;
  }

  public AgentTranscriptAssert forbiddenTool(String name) {
    if (!calls(name).isEmpty()) {
      fail(
          "Expected tool '"
              + name
              + "' not to be called, but it was called "
              + calls(name).size()
              + " time(s)");
    }
    return this;
  }

  public AgentTranscriptAssert toolCallCount(String name, int expected) {
    int actual = calls(name).size();
    if (actual != expected) {
      fail("Expected tool '" + name + "' to be called " + expected + " time(s), but was " + actual);
    }
    return this;
  }

  public AgentTranscriptAssert toolArgumentEquals(
      String name, String jsonPointer, Object expected) {
    var matchingCalls = calls(name);
    if (matchingCalls.isEmpty()) {
      fail("Cannot assert arguments because tool '" + name + "' was not called");
    }
    JsonNode arguments;
    try {
      arguments = mapper.readTree(matchingCalls.getFirst().arguments());
    } catch (JsonProcessingException exception) {
      throw new AssertionError("Tool '" + name + "' arguments are not valid JSON", exception);
    }
    JsonNode actual = arguments.at(jsonPointer);
    JsonNode expectedNode = mapper.valueToTree(expected);
    if (actual.isMissingNode() || !actual.equals(expectedNode)) {
      fail(
          "Expected tool '"
              + name
              + "' argument "
              + jsonPointer
              + " to be "
              + expectedNode
              + ", but was "
              + actual);
    }
    return this;
  }

  public AgentTranscriptAssert modelCallsAtMost(int maximum) {
    int actual = transcript.modelInvocations().size();
    if (actual > maximum) {
      fail("Expected at most " + maximum + " model call(s), but observed " + actual);
    }
    return this;
  }

  public AgentTranscriptAssert modelCallCount(int expected) {
    int actual = transcript.modelInvocations().size();
    if (actual != expected) {
      fail("Expected " + expected + " model call(s), but observed " + actual);
    }
    return this;
  }

  public AgentTranscriptAssert stepsAtMost(int maximum) {
    int actual = transcript.stepCount();
    if (actual > maximum) {
      fail("Expected at most " + maximum + " step(s), but observed " + actual);
    }
    return this;
  }

  private List<AgentTranscript.ToolInvocation> calls(String name) {
    return transcript.toolInvocations().stream().filter(call -> call.name().equals(name)).toList();
  }

  private List<String> observedTools() {
    return transcript.toolInvocations().stream().map(AgentTranscript.ToolInvocation::name).toList();
  }

  private static void fail(String message) {
    throw new AssertionError(message);
  }
}
