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

class BoxAOEModeTest extends modernmods.modernfoundry.test.BaseMcTest {
  @Test
  void exactExpandedProgressionForSmallAndBroadTools() {
    var small = BoxAOEIterator.builder(0, 0, 0).build();
    var broad = BoxAOEIterator.builder(1, 1, 0).build();
    var tool = mock(ToolStack.class);
    var upgrades = new ModDataNBT();
    when(tool.getVolatileData()).thenReturn(upgrades);
    when(tool.getPersistentData()).thenReturn(new ToolDataNBT());
    String[] smallCaps = {"1×1", "1×3+3×1", "5×3", "5×5×5"};
    String[] broadCaps = {"3×3", "5×5", "7×7", "9×9"};
    for (int level = 0; level <= 3; level++) {
      upgrades.putInt(IModifiable.EXPANDED, level);
      assertThat(small.getModes(tool).getLast().label()).isEqualTo(smallCaps[level]);
      assertThat(broad.getModes(tool).getLast().label()).isEqualTo(broadCaps[level]);
    }
  }

  @Test
  void plusAndCubeGeometryWorksForEveryHitFace() {
    for (Direction face : Direction.values()) {
      var dirs = IBoxExpansion.SIDE_HIT.getDirections(null, face);
      var plus = new java.util.HashSet<BlockPos>();
      BoxAOEIterator.blocksForMode(new BoxAOEIterator.Mode(3, 3, 1, true), BlockPos.ZERO, dirs, p -> true)
        .forEach(p -> plus.add(p.immutable()));
      assertThat(plus).hasSize(4).contains(BlockPos.ZERO.relative(dirs.width()), BlockPos.ZERO.relative(dirs.width().getOpposite()),
        BlockPos.ZERO.relative(dirs.height()), BlockPos.ZERO.relative(dirs.height().getOpposite()));
      var cube = new java.util.HashSet<BlockPos>();
      BoxAOEIterator.blocksForMode(new BoxAOEIterator.Mode(5, 5, 5), BlockPos.ZERO, dirs, p -> true)
        .forEach(p -> cube.add(p.immutable()));
      assertThat(cube).hasSize(124).doesNotContain(BlockPos.ZERO);
    }
  }

  @Test
  void modesUseOneCommaSeparatedRowWithIndependentColors() {
    var box = BoxAOEIterator.builder(1, 1, 0).build();
    var tool = mock(ToolStack.class);
    when(tool.getPersistentData()).thenReturn(new ToolDataNBT());
    when(tool.getVolatileData()).thenReturn(new ModDataNBT());
    var row = box.getModeTooltip(tool);
    assertThat(row.getString()).isEqualTo("1×1, 1×2, 1×3+3×1, 3×3");
    assertThat(row.getSiblings()).hasSize(7);
    assertThat(row.getSiblings().get(0).getStyle().getColor().getValue()).isEqualTo(0xAAAAAA);
    assertThat(row.getSiblings().get(6).getStyle().getColor().getValue()).isEqualTo(0x55FF55);
  }

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
