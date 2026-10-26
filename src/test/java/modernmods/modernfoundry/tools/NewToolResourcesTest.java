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
  @Test void shovelAssemblyLayoutAndDefinitionUseTheSameThreeParts() throws Exception {
    var definition = json("/data/modernfoundry/tinkering/tool_definitions/shovel.json");
    var layout = json("/data/modernfoundry/tinkering/station_layouts/shovel.json");
    String[] parts = {"modernfoundry:adze_head", "modernfoundry:tool_handle", "modernfoundry:tool_binding"};
    var slots = layout.getAsJsonArray("input_slots");
    assertThat(slots).hasSize(3);
    for (var element : definition.getAsJsonArray("modules")) {
      var module = element.getAsJsonObject();
      switch (module.get("type").getAsString()) {
        case "modernfoundry:part_stats" -> {
          assertThat(module.getAsJsonArray("parts")).hasSize(3);
          for (int index = 0; index < parts.length; index++) {
            assertThat(module.getAsJsonArray("parts").get(index).getAsString()).isEqualTo(parts[index]);
            assertThat(slots.get(index).getAsJsonObject().getAsJsonObject("filter").get("item").getAsString()).isEqualTo(parts[index]);
          }
        }
        case "modernfoundry:tool_actions" -> assertThat(module.getAsJsonArray("tool_actions").toString()).isEqualTo("[\"shovel_dig\"]");
        case "modernfoundry:is_effective" -> assertThat(module.get("tag").getAsString()).isEqualTo("minecraft:mineable/shovel");
        case "modernfoundry:traits" -> assertThat(module.getAsJsonArray("traits").toString()).contains("modernfoundry:pathing").doesNotContain("pierce");
      }
    }
    assertThat(json("/data/modernfoundry/recipe/tools/building/shovel.json").get("result").getAsString()).isEqualTo("modernfoundry:shovel");
    for (String tag : new String[]{"small", "multipart", "durability", "bonus_slots", "harvest/primary", "aoe", "interactable/right"}) {
      assertThat(json("/data/modernfoundry/tags/item/modifiable/" + tag + ".json").getAsJsonArray("values").toString()).contains("modernfoundry:shovel");
    }
    assertThat(json("/data/minecraft/tags/item/shovels.json").getAsJsonArray("values").toString()).contains("modernfoundry:shovel");
  }

  @Test void customToolModelsResolveTheirTexturesAndKeepSavedMaterialIndices() throws Exception {
    for (String tool : new String[]{"paxel", "shovel"}) {
      var normal = json("/assets/modernfoundry/models/item/" + tool + ".json");
      var broken = json("/assets/modernfoundry/models/item/tool/" + tool + "/broken.json");
      assertThat(normal.getAsJsonArray("overrides").get(0).getAsJsonObject().get("model").getAsString()).isEqualTo("modernfoundry:item/tool/" + tool + "/broken");
      var indices = tool.equals("paxel") ? java.util.Map.of("pick", 0, "axe", 1, "shovel", 2, "handle", 3)
                                           : java.util.Map.of("shovel", 0, "handle", 1, "binding", 2);
      for (var model : java.util.List.of(normal, broken)) {
        assertThat(model.getAsJsonArray("parts")).hasSize(indices.size());
        for (var partElement : model.getAsJsonArray("parts")) {
          var part = partElement.getAsJsonObject();
          assertThat(part.get("index").getAsInt()).isEqualTo(indices.get(part.get("name").getAsString()));
        }
        for (var texture : model.getAsJsonObject("textures").entrySet()) {
          assertThat(getClass().getResource("/assets/modernfoundry/textures/" + texture.getValue().getAsString().split(":")[1] + ".png")).as(texture.getKey()).isNotNull();
        }
        assertThat(model.getAsJsonArray("modifier_maps").get(0).getAsString()).isEqualTo("modernfoundry:" + tool);
      }
      assertModifierTexturesExist(json("/assets/modernfoundry/tinkering/modifier_models/" + tool + ".json"));
    }
  }

  private void assertModifierTexturesExist(JsonElement node) {
    if (node.isJsonObject()) {
      for (var entry : node.getAsJsonObject().entrySet()) assertModifierTexturesExist(entry.getValue());
    } else if (node.isJsonArray()) {
      for (var entry : node.getAsJsonArray()) assertModifierTexturesExist(entry);
    } else if (node.isJsonPrimitive() && node.getAsJsonPrimitive().isString() && node.getAsString().startsWith("modernfoundry:item/")) {
      assertThat(getClass().getResource("/assets/modernfoundry/textures/" + node.getAsString().split(":")[1] + ".png")).as(node.getAsString()).isNotNull();
    }
  }
}
