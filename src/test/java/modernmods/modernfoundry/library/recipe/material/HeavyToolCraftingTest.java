package modernmods.modernfoundry.library.recipe.material;

import com.google.gson.*;
import modernmods.modernfoundry.library.tools.item.ToolItemTest;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.CraftingInput;
import org.junit.jupiter.api.Test;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.*;
import static org.assertj.core.api.Assertions.assertThat;

class HeavyToolCraftingTest extends ToolItemTest {
  private JsonObject json(String name) throws Exception {
    try(var stream=getClass().getResourceAsStream("/data/modernfoundry/recipe/compat/polymorph/"+name+".json")) {
      assertThat(stream).as(name).isNotNull();
      return JsonParser.parseReader(new InputStreamReader(stream,StandardCharsets.UTF_8)).getAsJsonObject();
    }
  }
  private void condition(JsonObject recipe) {
    var gate=recipe.getAsJsonArray("neoforge:conditions").get(0).getAsJsonObject();
    assertThat(gate.get("type").getAsString()).isEqualTo("neoforge:mod_loaded");
    assertThat(gate.get("modid").getAsString()).isEqualTo("polymorph");
  }
  // Decode the actual recipe pattern/ingredients using a registered fixture output.
  private FixedMaterialToolRecipe decode(JsonObject data) {
    var copy=data.deepCopy();
    copy.getAsJsonObject("result").addProperty("id",BuiltInRegistries.ITEM.getKey(tool).toString());
    // The fixture tool is unstackable; this helper checks matching, not output count.
    // The original arrow recipe count is asserted separately.
    copy.getAsJsonObject("result").addProperty("count",1);
    return new FixedMaterialToolRecipe.Serializer().fromJson(ResourceLocation.fromNamespaceAndPath("test","crafting_pattern"),copy);
  }
  @Test void broadToolPatternsAndPartsMatchEveryTier() throws Exception {
    var patterns=Map.of("sledge_hammer",List.of("XXX","XSX"," S "),"excavator",List.of("XSX","XXX"," S "),"broad_axe",List.of("XXX","XS ","XS "));
    for(var tier:new String[]{"wooden","stone","iron","diamond"}) {
      for(var entry:patterns.entrySet()) {
        var data=json(tier+"_"+entry.getKey());condition(data);
        assertThat(data.getAsJsonArray("pattern").toString()).isEqualTo(new Gson().toJsonTree(entry.getValue()).toString());
        assertThat(data.getAsJsonObject("result").get("id").getAsString()).isEqualTo("modernfoundry:"+entry.getKey());
        assertThat(data.getAsJsonArray("materials")).hasSize(4);
        if(tier.equals("wooden")) assertThat(data.getAsJsonObject("key").getAsJsonObject("X").get("tag").getAsString()).isEqualTo("minecraft:logs");
        if(tier.equals("stone")) assertThat(data.getAsJsonObject("key").getAsJsonObject("X").get("item").getAsString()).isEqualTo("minecraft:smooth_stone");
        if(tier.equals("diamond")) {
          assertThat(data.getAsJsonArray("materials").get(0).getAsString()).isEqualTo("modernfoundry:iron");
          assertThat(data.getAsJsonArray("modifiers").get(0).getAsJsonObject().get("name").getAsString()).isEqualTo("modernfoundry:diamond");
          assertThat(data.getAsJsonObject("slots").get("upgrades").getAsInt()).isEqualTo(1);
        }
        // Ingredient tests use vanilla smooth stone/ingots/diamonds. Log tag data is checked above.
        if(!tier.equals("wooden")) {
          var item=tier.equals("stone")?Items.SMOOTH_STONE:tier.equals("iron")?Items.IRON_INGOT:Items.DIAMOND;
          var stacks=new ArrayList<ItemStack>();
          for(String row:entry.getValue()) for(char c:row.toCharArray()) stacks.add(c=='X'?new ItemStack(item):c=='S'?new ItemStack(Items.STICK):ItemStack.EMPTY);
          var input=CraftingInput.of(3,3,stacks);
          var recipe=decode(data);
          assertThat(recipe.matches(input,null)).isTrue();
          stacks.set(0,new ItemStack(Items.DIRT));
          assertThat(recipe.matches(CraftingInput.of(3,3,stacks),null)).isFalse();
        }
      }
    }
  }
  @Test void vanillaBowAndArrowPatternsHaveCorrectMaterialsAndCounts() throws Exception {
    var bow=json("bow");condition(bow);
    assertThat(bow.getAsJsonArray("materials").toString()).isEqualTo("[\"modernfoundry:wood\",\"modernfoundry:wood\",\"modernfoundry:wood\",\"modernfoundry:string\"]");
    var bowInput=CraftingInput.of(3,3,List.of(ItemStack.EMPTY,new ItemStack(Items.STICK),new ItemStack(Items.STRING),new ItemStack(Items.STICK),ItemStack.EMPTY,new ItemStack(Items.STRING),ItemStack.EMPTY,new ItemStack(Items.STICK),new ItemStack(Items.STRING)));
    assertThat(decode(bow).matches(bowInput,null)).isTrue();
    assertThat(bow.getAsJsonObject("result").get("id").getAsString()).isEqualTo("modernfoundry:longbow");
    var arrows=json("arrow");condition(arrows);
    assertThat(arrows.getAsJsonObject("result").get("count").getAsInt()).isEqualTo(4);
    assertThat(arrows.getAsJsonArray("materials").toString()).isEqualTo("[\"modernfoundry:flint\",\"modernfoundry:wood\",\"modernfoundry:feather\"]");
    assertThat(decode(arrows).matches(CraftingInput.of(1,3,List.of(new ItemStack(Items.FLINT),new ItemStack(Items.STICK),new ItemStack(Items.FEATHER))),null)).isTrue();
  }
}
