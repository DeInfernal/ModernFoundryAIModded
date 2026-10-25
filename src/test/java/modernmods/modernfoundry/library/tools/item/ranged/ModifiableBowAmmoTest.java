package modernmods.modernfoundry.library.tools.item.ranged;

import modernmods.modernfoundry.library.tools.definition.ToolDefinition;
import modernmods.modernfoundry.library.tools.item.ModifiableArrowItem;
import modernmods.modernfoundry.test.BaseMcTest;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class ModifiableBowAmmoTest extends BaseMcTest {
  @Test
  void acceptsCraftedArrowEvenWithoutVanillaTag() {
    var bow = new ModifiableBowItem(new Item.Properties(), ToolDefinition.EMPTY);
    var stack = mock(ItemStack.class);
    when(stack.getItem()).thenReturn(new ModifiableArrowItem(new Item.Properties(), ToolDefinition.EMPTY));
    assertThat(bow.getAllSupportedProjectiles().test(stack)).isTrue();
    when(stack.getItem()).thenReturn(new Item(new Item.Properties()));
    assertThat(bow.getAllSupportedProjectiles().test(stack)).isFalse();
  }
}
