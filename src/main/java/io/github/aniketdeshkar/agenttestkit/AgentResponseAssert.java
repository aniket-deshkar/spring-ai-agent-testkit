package io.github.aniketdeshkar.agenttestkit;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Objects;
import java.util.function.Consumer;

/** Structural JSON assertions that avoid brittle natural-language equality checks. */
public final class AgentResponseAssert {
  private final String response;
  private final ObjectMapper mapper;
  private JsonNode parsed;

  AgentResponseAssert(String response, ObjectMapper mapper) {
    this.response = Objects.requireNonNull(response, "response");
    this.mapper = mapper;
  }

  public AgentResponseAssert isValidJson() {
    json();
    return this;
  }

  public AgentResponseAssert jsonPathEquals(String jsonPointer, Object expected) {
    JsonNode actual = json().at(jsonPointer);
    JsonNode expectedNode = mapper.valueToTree(expected);
    if (actual.isMissingNode() || !actual.equals(expectedNode)) {
      throw new AssertionError(
          "Expected response path "
              + jsonPointer
              + " to be "
              + expectedNode
              + ", but was "
              + actual);
    }
    return this;
  }

  public <T> AgentResponseAssert satisfies(Class<T> type, Consumer<T> assertion) {
    try {
      assertion.accept(mapper.treeToValue(json(), type));
    } catch (JsonProcessingException exception) {
      throw new AssertionError("Response cannot be deserialized as " + type.getName(), exception);
    }
    return this;
  }

  private JsonNode json() {
    if (parsed == null) {
      try {
        parsed = mapper.readTree(response);
      } catch (JsonProcessingException exception) {
        throw new AssertionError("Response is not valid JSON", exception);
      }
    }
    return parsed;
  }
}
