package modernmods.modernfoundry.library.recipe.material;

import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import net.minecraft.core.HolderLookup;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import modernmods.hilt.data.loadable.field.LoadableField;
import modernmods.hilt.recipe.helper.LoggingRecipeSerializer;
import modernmods.modernfoundry.library.materials.definition.MaterialVariantId;
import modernmods.modernfoundry.library.modifiers.ModifierEntry;
import modernmods.modernfoundry.library.tools.SlotType.SlotCount;
import modernmods.modernfoundry.library.tools.item.IModifiable;
import modernmods.modernfoundry.library.tools.nbt.MaterialNBT;
import modernmods.modernfoundry.library.tools.nbt.ToolStack;
import modernmods.modernfoundry.tables.TinkerTables;
import java.util.List;
import java.util.Optional;

/** Fixed material alternatives to vanilla recipes, loaded only by the Polymorph condition. */
public class FixedMaterialToolRecipe extends ShapedRecipe {
  private final ItemStack output;
  private final List<MaterialVariantId> materials;
  private final List<ModifierEntry> modifiers;
  private final SlotCount slots;

  public FixedMaterialToolRecipe(ShapedRecipe recipe, List<MaterialVariantId> materials) {
    this(recipe, materials, List.of(), null);
  }

  public FixedMaterialToolRecipe(ShapedRecipe recipe, List<MaterialVariantId> materials, List<ModifierEntry> modifiers, SlotCount slots) {
    super(recipe.getGroup(), recipe.category(),
      new ShapedRecipePattern(recipe.getWidth(), recipe.getHeight(), recipe.getIngredients(), Optional.empty()),
      recipe.getResultItem(null), recipe.showNotification());
    this.output = recipe.getResultItem(null);
    this.materials = List.copyOf(materials);
    this.modifiers = List.copyOf(modifiers);
    this.slots = slots;
    if (!(output.getItem() instanceof IModifiable)) {
      throw new JsonSyntaxException("Fixed-material recipe result must be a modifiable tool");
    }
  }

  private ItemStack buildResult() {
    var definition = ((IModifiable)output.getItem()).getToolDefinition();
    var tool = ToolStack.createTool(output.getItem(), definition, MaterialNBT.builder().add(materials).build());
    for (ModifierEntry modifier : modifiers) { tool.addModifier(modifier.getId(), modifier.getLevel()); }
    if (slots != null) { tool.getPersistentData().addSlots(slots.type(), -slots.count()); }
    return tool.createStack(output.getCount());
  }

  @Override
  public ItemStack assemble(CraftingInput input, HolderLookup.Provider registries) { return buildResult(); }

  @Override
  public ItemStack getResultItem(HolderLookup.Provider registries) { return buildResult(); }

  @Override
  public RecipeSerializer<?> getSerializer() { return TinkerTables.fixedMaterialToolRecipeSerializer.get(); }

  public static class Serializer implements LoggingRecipeSerializer<FixedMaterialToolRecipe> {
    private static final LoadableField<List<MaterialVariantId>,FixedMaterialToolRecipe> MATERIALS =
      MaterialVariantId.LOADABLE.list(1).requiredField("materials", recipe -> recipe.materials);

    private static final LoadableField<List<ModifierEntry>,FixedMaterialToolRecipe> MODIFIERS =
      ModifierEntry.LOADABLE.list(0).defaultField("modifiers", List.of(), recipe -> recipe.modifiers);
    private static final LoadableField<SlotCount,FixedMaterialToolRecipe> SLOTS =
      SlotCount.LOADABLE.nullableField("slots", recipe -> recipe.slots);

    @Override
    public FixedMaterialToolRecipe fromJson(ResourceLocation id, JsonObject json) {
      return new FixedMaterialToolRecipe(SHAPED_RECIPE.fromJson(id, json), MATERIALS.get(json), MODIFIERS.get(json), SLOTS.get(json));
    }

    @Override
    public FixedMaterialToolRecipe fromNetworkSafe(ResourceLocation id, FriendlyByteBuf buffer) {
      ShapedRecipe recipe = SHAPED_RECIPE.fromNetwork(id, buffer);
      var materials = MATERIALS.decode(buffer);
      var modifiers = MODIFIERS.decode(buffer);
      var slots = SLOTS.decode(buffer);
      return recipe == null ? null : new FixedMaterialToolRecipe(recipe, materials, modifiers, slots);
    }

    @Override
    public void toNetworkSafe(FriendlyByteBuf buffer, FixedMaterialToolRecipe recipe) {
      SHAPED_RECIPE.toNetwork(buffer, recipe);
      MATERIALS.encode(buffer, recipe);
      MODIFIERS.encode(buffer, recipe);
      SLOTS.encode(buffer, recipe);
    }
  }
}
