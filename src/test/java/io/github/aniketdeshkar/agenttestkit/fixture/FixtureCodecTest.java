package io.github.aniketdeshkar.agenttestkit.fixture;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import io.github.aniketdeshkar.agenttestkit.AgentTestContext;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class FixtureCodecTest {
  @TempDir Path directory;

  @Test
  void roundTripsAndInstallsFixture() {
    var fixture =
        new RecordedFixture(
            1,
            List.of(
                new RecordedFixture.ModelReply(
                    null,
                    List.of(
                        new RecordedFixture.ToolRequest(
                            "call-1", "weather", "{\"city\":\"Pune\"}"))),
                new RecordedFixture.ModelReply("sunny", List.of())),
            Map.of("weather", "31 C"));
    var path = directory.resolve("weather.json");
    var codec = new FixtureCodec();

    codec.write(path, fixture);
    var loaded = codec.read(path);
    var context = new AgentTestContext();
    codec.install(context, loaded);

    assertEquals(fixture, loaded);
    assertEquals(2, context.model().remainingResponses());
    assertEquals("31 C", context.tools().callback("weather").call("{}"));
  }

  @Test
  void rejectsUnknownSchemaVersions() {
    assertThrows(IllegalArgumentException.class, () -> new RecordedFixture(2, List.of(), Map.of()));
  }
}
