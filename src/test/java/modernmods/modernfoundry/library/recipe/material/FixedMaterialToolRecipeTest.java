package modernmods.modernfoundry.library.recipe.material;

import modernmods.modernfoundry.library.tools.item.ToolItemTest;
import modernmods.modernfoundry.fixture.MaterialFixture;
import modernmods.modernfoundry.library.materials.definition.MaterialVariantId;
import modernmods.modernfoundry.library.tools.nbt.ToolStack;
import net.minecraft.core.NonNullList;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.*;
import org.junit.jupiter.api.Test;
import java.util.List;
import java.util.Optional;
import static org.assertj.core.api.Assertions.assertThat;

class FixedMaterialToolRecipeTest extends ToolItemTest {
  @Test void preAppliedUpgradeAndSlotCostSurviveRecipeNetworkRoundTrip() {
    var inputs = NonNullList.withSize(1,Ingredient.of(Items.DIAMOND));
    var vanilla = new ShapedRecipe("",CraftingBookCategory.EQUIPMENT,new ShapedRecipePattern(1,1,inputs,Optional.empty()),new ItemStack(tool));
    List<MaterialVariantId> materials = List.of(MaterialFixture.MATERIAL_WITH_HEAD.getIdentifier(), MaterialFixture.MATERIAL_WITH_HANDLE.getIdentifier(), MaterialFixture.MATERIAL_WITH_EXTRA.getIdentifier());
    var id = modernmods.modernfoundry.library.modifiers.ModifierFixture.TEST_1;
    var recipe = new FixedMaterialToolRecipe(vanilla, materials,
      List.of(new modernmods.modernfoundry.library.modifiers.ModifierEntry(id,1)),
      new modernmods.modernfoundry.library.tools.SlotType.SlotCount(modernmods.modernfoundry.library.tools.SlotType.UPGRADE,1));
    var input = CraftingInput.of(1,1,List.of(new ItemStack(Items.DIAMOND)));
    var result = ToolStack.from(recipe.assemble(input,null));
    assertThat(result.getUpgrades().getLevel(id)).isEqualTo(1);
    assertThat(result.getPersistentData().getSlots(modernmods.modernfoundry.library.tools.SlotType.UPGRADE)).isEqualTo(-1);
    var buffer = new net.minecraft.network.RegistryFriendlyByteBuf(io.netty.buffer.Unpooled.buffer(),
      net.minecraft.core.RegistryAccess.fromRegistryOfRegistries(net.minecraft.core.registries.BuiltInRegistries.REGISTRY));
    try {
      var serializer = new FixedMaterialToolRecipe.Serializer();
      serializer.toNetworkSafe(buffer,recipe);
      var decoded = serializer.fromNetworkSafe(modernmods.modernfoundry.TConstruct.getResource("test_diamond_alternative"),buffer);
      for (int i=0;i<2;i++) {
        var rebuilt=ToolStack.from(decoded.assemble(input,null));
        assertThat(rebuilt.getUpgrades().getLevel(id)).isEqualTo(1);
        assertThat(rebuilt.getMaterials()).isEqualTo(result.getMaterials());
        assertThat(rebuilt.getPersistentData().getSlots(modernmods.modernfoundry.library.tools.SlotType.UPGRADE)).isEqualTo(-1);
      }
    } finally { buffer.release(); }
  }

  @Test void vanillaPatternBuildsFreshToolWithFixedHeadHandleBinding() {
    var inputs = NonNullList.withSize(9,Ingredient.EMPTY);
    for(int i=0;i<3;i++)inputs.set(i,Ingredient.of(Items.COBBLESTONE));
    inputs.set(4,Ingredient.of(Items.STICK));inputs.set(7,Ingredient.of(Items.STICK));
    var vanilla = new ShapedRecipe("",CraftingBookCategory.EQUIPMENT,new ShapedRecipePattern(3,3,inputs,Optional.empty()),new ItemStack(tool));
    List<MaterialVariantId> materials = List.of(MaterialFixture.MATERIAL_WITH_HEAD.getIdentifier(), MaterialFixture.MATERIAL_WITH_HANDLE.getIdentifier(), MaterialFixture.MATERIAL_WITH_EXTRA.getIdentifier());
    var recipe = new FixedMaterialToolRecipe(vanilla,materials);
    var input = CraftingInput.of(3,3,List.of(new ItemStack(Items.COBBLESTONE),new ItemStack(Items.COBBLESTONE),new ItemStack(Items.COBBLESTONE),ItemStack.EMPTY,new ItemStack(Items.STICK),ItemStack.EMPTY,ItemStack.EMPTY,new ItemStack(Items.STICK),ItemStack.EMPTY));
    var buffer = new net.minecraft.network.RegistryFriendlyByteBuf(io.netty.buffer.Unpooled.buffer(),
      net.minecraft.core.RegistryAccess.fromRegistryOfRegistries(net.minecraft.core.registries.BuiltInRegistries.REGISTRY));
    try {
      var serializer = new FixedMaterialToolRecipe.Serializer();
      serializer.toNetworkSafe(buffer, recipe);
      var decoded = serializer.fromNetworkSafe(modernmods.modernfoundry.TConstruct.getResource("test_vanilla_alternative"), buffer);
      assertThat(decoded.matches(input,null)).isTrue();
      assertThat(ToolStack.from(decoded.assemble(input,null)).getMaterials()).isEqualTo(ToolStack.from(testItemStack).getMaterials());
    } finally { buffer.release(); }
    assertThat(recipe.matches(input,null)).isTrue();
    var result = recipe.assemble(input,null);
    var assembled = ToolStack.from(result);
    assertThat(assembled.getMaterials()).isEqualTo(ToolStack.from(testItemStack).getMaterials());
    assertThat(assembled.getDamage()).isZero();
    assertThat(assembled.isBroken()).isFalse();
    result.setDamageValue(10);
    assertThat(ToolStack.from(recipe.assemble(input,null)).getDamage()).isZero();
  }
}
