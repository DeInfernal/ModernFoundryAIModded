package modernmods.modernfoundry.smeltery.block.entity.multiblock;

import modernmods.modernfoundry.test.BaseMcTest;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import org.junit.jupiter.api.Test;
import java.util.HashSet;
import static org.mockito.Mockito.*;
import static org.assertj.core.api.Assertions.assertThat;

class SmelteryFloorTest extends BaseMcTest {
  static class TestSmeltery extends SmelteryMultiblock {
    final java.util.Set<BlockPos> filled = new HashSet<>();
    TestSmeltery() { super(null); }
    @Override protected boolean isValidBlock(Level world,BlockPos pos,CuboidSide side,boolean frame) { return filled.contains(pos); }
    boolean floor(Level level) { return detectCap(level,BlockPos.ZERO,new BlockPos(2,0,2),CuboidSide.FLOOR,p -> {}).isSuccess(); }
  }
  @Test void requiresAllNineFloorBlocks() {
    var structure = new TestSmeltery();
    var level=mock(Level.class);
    when(level.hasChunksAt(any(),any())).thenReturn(true);
    structure.filled.add(new BlockPos(1,0,1));
    assertThat(structure.floor(level)).isFalse();
    for(int x=0;x<3;x++)for(int z=0;z<3;z++) structure.filled.add(new BlockPos(x,0,z));
    assertThat(structure.floor(level)).isTrue();
    structure.filled.remove(new BlockPos(0,0,0));
    assertThat(structure.floor(level)).isFalse();
  }
}
