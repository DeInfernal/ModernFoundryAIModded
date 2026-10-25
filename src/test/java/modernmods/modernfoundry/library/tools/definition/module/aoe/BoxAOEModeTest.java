package modernmods.modernfoundry.library.tools.definition.module.aoe;

import modernmods.modernfoundry.TConstruct;
import modernmods.modernfoundry.library.tools.item.IModifiable;
import modernmods.modernfoundry.library.tools.nbt.ModDataNBT;
import modernmods.modernfoundry.library.tools.nbt.ToolDataNBT;
import modernmods.modernfoundry.library.tools.nbt.ToolStack;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class BoxAOEModeTest {
  @Test
  void modesPersistPerToolAndTrackUpgrades() {
    var box = BoxAOEIterator.builder(1, 1, 0).addWidth(1).addHeight(1).build();
    var tool = mock(ToolStack.class);
    var other = mock(ToolStack.class);
    var upgrades = new ModDataNBT();
    var data = new ToolDataNBT();
    when(tool.getPersistentData()).thenReturn(data);
    when(other.getPersistentData()).thenReturn(new ToolDataNBT());
    when(tool.getVolatileData()).thenReturn(upgrades);
    when(other.getVolatileData()).thenReturn(upgrades);
    assertThat(box.getSelectedMode(tool).label()).isEqualTo("3×3");
    box.cycleMode(tool);
    assertThat(box.getSelectedMode(tool).label()).isEqualTo("1×1");
    assertThat(box.getSelectedMode(other).label()).isEqualTo("3×3");
    var restored = ToolDataNBT.readFromNBT(data.getCopy());
    when(tool.getPersistentData()).thenReturn(restored);
    assertThat(box.getSelectedMode(tool).label()).isEqualTo("1×1");
    upgrades.putInt(IModifiable.EXPANDED, 2);
    assertThat(box.getModes(tool)).contains(new BoxAOEIterator.Mode(3, 3, 3));
    restored.putString(TConstruct.getResource("aoe_mode"), "3x3x3");
    assertThat(box.getSelectedMode(tool).depth()).isEqualTo(3);
    upgrades.putInt(IModifiable.EXPANDED, 0);
    assertThat(box.getSelectedMode(tool).depth()).isEqualTo(1);
  }

  @Test
  void rectangleHonorsPlaneAndDepthAndSkipsOrigin() {
    var line = new BoxAOEIterator.RectangleIterator(BlockPos.ZERO, Direction.EAST, 0,
      Direction.UP, 1, false, Direction.NORTH, 0, pos -> true);
    assertThat(line.next().immutable()).isEqualTo(BlockPos.ZERO.above());
    assertThat(line.hasNext()).isFalse();
    var cube = new BoxAOEIterator.RectangleIterator(BlockPos.ZERO, Direction.EAST, 1,
      Direction.UP, 1, true, Direction.NORTH, 2, pos -> true);
    var blocks = new java.util.HashSet<BlockPos>();
    cube.forEachRemaining(pos -> blocks.add(pos.immutable()));
    assertThat(blocks).hasSize(26).doesNotContain(BlockPos.ZERO);
  }
}
