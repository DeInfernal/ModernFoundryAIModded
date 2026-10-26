package modernmods.modernfoundry.library.tools.definition.module.aoe;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.item.context.UseOnContext;
import modernmods.modernfoundry.test.BaseMcTest;
import modernmods.modernfoundry.library.tools.nbt.ToolStack;
import modernmods.modernfoundry.library.tools.nbt.ModDataNBT;
import modernmods.modernfoundry.library.tools.item.IModifiable;
import org.junit.jupiter.api.Test;
import java.util.*;
import static org.mockito.Mockito.*;
import static org.assertj.core.api.Assertions.assertThat;

class TreeAndVeinTest extends BaseMcTest {
  private Level world(Map<BlockPos,BlockState> blocks) {
    var level = mock(Level.class);
    when(level.hasChunkAt(any())).thenReturn(true);
    when(level.getBlockState(any())).thenAnswer(call -> blocks.getOrDefault(call.getArgument(0), Blocks.AIR.defaultBlockState()));
    return level;
  }
  private Map<BlockPos,BlockState> trunk() {
    var blocks = new HashMap<BlockPos,BlockState>();
    for (int y = 0; y < 5; y++) for (int x = 0; x < 2; x++) for (int z = 0; z < 2; z++)
      blocks.put(new BlockPos(x,y,z), Blocks.OAK_LOG.defaultBlockState());
    blocks.put(new BlockPos(2,4,0), Blocks.OAK_LOG.defaultBlockState());
    return blocks;
  }
  private Set<BlockPos> fell(Level level) {
    var result = new HashSet<BlockPos>();
    TreeAOEIterator.calculate(level,null,BlockPos.ZERO,Direction.NORTH,Blocks.OAK_LOG.defaultBlockState(),0,0)
      .forEach(p -> result.add(p.immutable()));
    return result;
  }
  @Test void treeFellingHasNoSelectableModesOrClientAreaRendering() {
    var iterator = new TreeAOEIterator(0,0);
    assertThat(iterator).isNotInstanceOf(SelectableAOEIterator.class);
    assertThat(iterator).isInstanceOf(HiddenAOEIterator.class);
    assertThat(BoxAOEIterator.builder(0,0,0).build()).isNotInstanceOf(HiddenAOEIterator.class);
  }
  @Test void nonLogsDoNotScanOrHarvestNeighboringBlocks() {
    var context = mock(UseOnContext.class);
    var iterator = new TreeAOEIterator(0,0);
    assertThat(iterator.getBlocks(mock(ToolStack.class),context,Blocks.OAK_PLANKS.defaultBlockState(),
      AreaOfEffectIterator.AOEMatchType.BREAKING)).isEmpty();
    verifyNoInteractions(context);
  }
  @Test void allColumnsAndBranchesWithNaturalLeaves() {
    var blocks = trunk();
    blocks.put(new BlockPos(2,5,0),Blocks.OAK_LEAVES.defaultBlockState().setValue(LeavesBlock.PERSISTENT,false));
    assertThat(fell(world(blocks))).hasSize(20).contains(new BlockPos(1,0,1),new BlockPos(2,4,0)).doesNotContain(BlockPos.ZERO);
  }
  @Test void bareOrPlayerLeafColumnsStaySingleBlock() {
    var blocks = trunk();
    assertThat(fell(world(blocks))).isEmpty();
    blocks.put(new BlockPos(2,5,0),Blocks.OAK_LEAVES.defaultBlockState().setValue(LeavesBlock.PERSISTENT,true));
    assertThat(fell(world(blocks))).isEmpty();
  }
  @Test void unloadedBoundaryDoesNotCausePartialFellingOrChunkLookup() {
    var blocks = trunk();
    blocks.put(new BlockPos(2,5,0),Blocks.OAK_LEAVES.defaultBlockState().setValue(LeavesBlock.PERSISTENT,false));
    var level = world(blocks);
    var unloaded = new BlockPos(-1,0,0);
    when(level.hasChunkAt(unloaded)).thenReturn(false);
    assertThat(fell(level)).isEmpty();
    verify(level,never()).getBlockState(unloaded);
  }
  @Test void veinDistanceIncreasesByFivePerLevel() {
    var blocks = new HashMap<BlockPos,BlockState>();
    for(int x=0;x<25;x++) blocks.put(new BlockPos(x,0,0),Blocks.IRON_ORE.defaultBlockState());
    var context = mock(UseOnContext.class);
    var levelWorld = world(blocks);
    when(context.getLevel()).thenReturn(levelWorld);
    when(context.getClickedPos()).thenReturn(BlockPos.ZERO);
    var tool = mock(ToolStack.class);
    var data = new ModDataNBT();
    when(tool.getVolatileData()).thenReturn(data);
    for(int level=0;level<=3;level++) {
      data.putInt(IModifiable.EXPANDED,level);
      var found = new ArrayList<BlockPos>();
      new VeiningAOEIterator(2).getBlocks(tool,context,Blocks.IRON_ORE.defaultBlockState(),AreaOfEffectIterator.AOEMatchType.BREAKING).forEach(found::add);
      assertThat(found).hasSize(2+5*level);
    }
  }
}
