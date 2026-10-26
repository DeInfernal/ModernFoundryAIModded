package modernmods.modernfoundry.tools;

import com.google.gson.*;
import org.junit.jupiter.api.Test;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import static org.assertj.core.api.Assertions.assertThat;

class NewToolResourcesTest {
  private JsonObject json(String path) throws Exception {
    try(var stream=getClass().getResourceAsStream(path)) {
      assertThat(stream).as(path).isNotNull();
      return JsonParser.parseReader(new InputStreamReader(stream,StandardCharsets.UTF_8)).getAsJsonObject();
    }
  }
  @Test void alternativesForFourCraftableTiersRequirePolymorph() throws Exception {
    for(String tier:new String[]{"wooden","stone","iron","diamond"}) {
      for(String tool:new String[]{"pickaxe","axe","shovel","hoe","sword"}) {
        var recipe=json("/data/modernfoundry/recipe/compat/polymorph/"+tier+"_"+tool+".json");
        var condition=recipe.getAsJsonArray("neoforge:conditions").get(0).getAsJsonObject();
        assertThat(condition.get("type").getAsString()).isEqualTo("neoforge:mod_loaded");
        assertThat(condition.get("modid").getAsString()).isEqualTo("polymorph");
        assertThat(recipe.getAsJsonArray("materials")).hasSize(3);
      }
    }
  }
  @Test void diamondRecipesUseIronAndDiamondUpgradeAndGoldenAlternativesAreAbsent() throws Exception {
    for(String kind:new String[]{"pickaxe","axe","shovel","hoe","sword"}) {
      var recipe=json("/data/modernfoundry/recipe/compat/polymorph/diamond_"+kind+".json");
      assertThat(recipe.getAsJsonArray("materials").get(0).getAsString()).isEqualTo("modernfoundry:iron");
      assertThat(recipe.getAsJsonArray("materials").toString()).doesNotContain("modernfoundry:diamond");
      var modifier=recipe.getAsJsonArray("modifiers").get(0).getAsJsonObject();
      assertThat(modifier.get("name").getAsString()).isEqualTo("modernfoundry:diamond");
      assertThat(modifier.get("level").getAsInt()).isEqualTo(1);
      assertThat(recipe.getAsJsonObject("slots").get("upgrades").getAsInt()).isEqualTo(1);
      assertThat(getClass().getResource("/data/modernfoundry/recipe/compat/polymorph/golden_"+kind+".json")).isNull();
    }
    assertThat(getClass().getResource("/data/modernfoundry/tinkering/materials/definition/diamond.json")).isNull();
  }

  @Test void broadAxeUsesOnlyTreeFellingAndNoBoxFallback() throws Exception {
    var modules=json("/data/modernfoundry/tinkering/tool_definitions/broad_axe.json").getAsJsonArray("modules");
    assertThat(modules.toString()).contains("modernfoundry:tree_aoe").doesNotContain("conditional_aoe","box_aoe");
  }

  @Test void paxelNeedsFourPartsAndHasOnlyDigActions() throws Exception {
    var modules=json("/data/modernfoundry/tinkering/tool_definitions/paxel.json").getAsJsonArray("modules");
    for(var element:modules) {
      var module=element.getAsJsonObject();
      if(module.get("type").getAsString().equals("modernfoundry:part_stats")) {
        assertThat(module.getAsJsonArray("parts")).hasSize(4);
      }
      if(module.get("type").getAsString().equals("modernfoundry:tool_actions")) {
        var actions=module.getAsJsonArray("tool_actions");
        assertThat(actions.toString()).contains("pickaxe_dig","axe_dig","shovel_dig").doesNotContain("strip","flatten");
      }
      assertThat(module.get("type").getAsString()).isNotEqualTo("modernfoundry:traits");
    }
    assertThat(json("/data/modernfoundry/tinkering/station_layouts/paxel.json").getAsJsonArray("input_slots")).hasSize(4);
    assertThat(json("/data/modernfoundry/recipe/tools/building/paxel.json").get("type").getAsString()).isEqualTo("modernfoundry:tool_building");
  }
}
