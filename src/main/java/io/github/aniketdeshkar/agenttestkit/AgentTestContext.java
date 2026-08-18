package io.github.aniketdeshkar.agenttestkit;

/** Per-test container for the scripted model, fake tools, and captured transcript. */
public final class AgentTestContext {
  private final AgentTranscript transcript = new AgentTranscript();
  private final ScriptedChatModel model = new ScriptedChatModel(transcript);
  private final FakeToolRegistry tools = new FakeToolRegistry(transcript);

  public AgentTranscript transcript() {
    return transcript;
  }

  public ScriptedChatModel model() {
    return model;
  }

  public FakeToolRegistry tools() {
    return tools;
  }

  public AgentTranscriptAssert assertThatTranscript() {
    return AgentAssertions.assertThat(transcript);
  }
}
