package modernmods.modernfoundry.common;

import com.google.gson.JsonParser;
import org.junit.jupiter.api.Test;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import static org.assertj.core.api.Assertions.assertThat;

class GlobalLootRegistrationTest {
  @Test
  void neoForgeLoadsSeveringHookAndWitherBoneReplacement() throws Exception {
    try (var stream = getClass().getResourceAsStream("/data/neoforge/loot_modifiers/global_loot_modifiers.json")) {
      assertThat(stream).as("NeoForge global loot modifier entrypoint").isNotNull();
      var list = JsonParser.parseReader(new InputStreamReader(stream, StandardCharsets.UTF_8)).getAsJsonObject();
      assertThat(list.get("replace").getAsBoolean()).isFalse();
      var entries = new java.util.ArrayList<String>();
      list.getAsJsonArray("entries").forEach(e -> entries.add(e.getAsString()));
      assertThat(entries).contains("modernfoundry:modifier_hook", "modernfoundry:wither_bone").doesNotHaveDuplicates();
      for (String id : entries) {
        String path = "/data/" + id.replace(":", "/loot_modifiers/") + ".json";
        try (var modifier = getClass().getResourceAsStream(path)) {
          assertThat(modifier).as(path).isNotNull();
          var data = JsonParser.parseReader(new InputStreamReader(modifier, StandardCharsets.UTF_8)).getAsJsonObject();
          assertThat(data.has("type")).isTrue();
        }
      }
    }
  }
}
