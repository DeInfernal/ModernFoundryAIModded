package modernmods.modernfoundry.smeltery.block;

import modernmods.modernfoundry.smeltery.block.entity.CastingBlockEntity;
import modernmods.modernfoundry.test.BaseMcTest;
import net.minecraft.core.BlockPos;
import net.minecraft.world.Containers;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import org.junit.jupiter.api.Test;
import static org.mockito.Mockito.*;

class CastingInventoryDropTest extends BaseMcTest {
  @Test
  void removalDropsFullContainerEvenWithoutAutomationCapability() {
    var block = mock(AbstractCastingBlock.class, CALLS_REAL_METHODS);
    var level = mock(Level.class);
    var casting = mock(CastingBlockEntity.class);
    var state = mock(BlockState.class);
    when(level.getBlockEntity(BlockPos.ZERO)).thenReturn(casting);
    try (var drops = mockStatic(Containers.class)) {
      block.onRemove(state, level, BlockPos.ZERO, Blocks.AIR.defaultBlockState(), false);
      drops.verify(() -> Containers.dropContents(level, BlockPos.ZERO, casting), times(1));
      verify(casting).clearContent();
    }
  }

  @Test
  void stateChangeKeepsCastAndDoesNotDropInventory() {
    var block = mock(AbstractCastingBlock.class, CALLS_REAL_METHODS);
    var level = mock(Level.class);
    var state = mock(BlockState.class);
    when(state.getBlock()).thenReturn(block);
    when(state.is(block)).thenReturn(true);
    try (var drops = mockStatic(Containers.class)) {
      block.onRemove(state, level, BlockPos.ZERO, state, false);
      drops.verifyNoInteractions();
    }
    verify(level, never()).getBlockEntity(any());
  }
}
