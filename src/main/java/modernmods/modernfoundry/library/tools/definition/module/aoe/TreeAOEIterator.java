package modernmods.modernfoundry.library.tools.definition.module.aoe;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.state.BlockState;
import modernmods.hilt.data.loadable.primitive.IntLoadable;
import modernmods.hilt.data.loadable.record.RecordLoadable;
import modernmods.modernfoundry.library.tools.nbt.IToolStackView;
import javax.annotation.Nullable;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

/** Whole connected trunks/branches, enabled only by nonpersistent leaves in their canopy. */
public record TreeAOEIterator(int width, int depth) implements AreaOfEffectIterator.Loadable, HiddenAOEIterator {
  public static final RecordLoadable<TreeAOEIterator> LOADER = RecordLoadable.create(
    IntLoadable.FROM_ZERO.defaultField("width_bonus", 0, true, TreeAOEIterator::width),
    IntLoadable.FROM_ZERO.defaultField("depth_bonus", 0, true, TreeAOEIterator::depth), TreeAOEIterator::new);
  private static final int MAX_LOGS = 4096;
  private static final int MAX_RADIUS = 32;
  private static final int MAX_HEIGHT = 128;

  @Override
  public RecordLoadable<TreeAOEIterator> getLoader() { return LOADER; }

  @Override
  public Iterable<BlockPos> getBlocks(IToolStackView tool, UseOnContext context, BlockState state, AOEMatchType matchType) {
    if (!state.is(modernmods.modernfoundry.common.TinkerTags.Blocks.TREE_LOGS)) { return List.of(); }
    return calculate(context.getLevel(), context.getPlayer(), context.getClickedPos(), context.getClickedFace(), state, width, depth);
  }

  public static boolean isNaturalLeaf(BlockState state) {
    return state.hasProperty(LeavesBlock.PERSISTENT) && !state.getValue(LeavesBlock.PERSISTENT);
  }

  /** A bounded pre-scan must finish before yielding blocks; bare/player-leaf columns stay 1×1. */
  public static Iterable<BlockPos> calculate(Level level, @Nullable Player player, BlockPos origin, Direction face, BlockState state, int extraWidth, int extraDepth) {
    var logs = new ArrayList<BlockPos>();
    var visited = new HashSet<BlockPos>();
    var queue = new ArrayDeque<BlockPos>();
    queue.add(origin.immutable());
    visited.add(origin.immutable());
    boolean leaves = false;
    while (!queue.isEmpty()) {
      BlockPos pos = queue.remove();
      if (!level.hasChunkAt(pos)) { return List.of(); }
      BlockState current = level.getBlockState(pos);
      if (!current.is(state.getBlock())) { continue; }
      if (logs.size() >= MAX_LOGS || Math.abs(pos.getX() - origin.getX()) > MAX_RADIUS
          || Math.abs(pos.getZ() - origin.getZ()) > MAX_RADIUS || Math.abs(pos.getY() - origin.getY()) > MAX_HEIGHT) {
        return List.of();
      }
      logs.add(pos);
      // Diagonal neighbors cover 2×2 trunks, acacia bends and branching canopies.
      for (int x = -1; x <= 1; x++) {
        for (int y = -1; y <= 1; y++) {
          for (int z = -1; z <= 1; z++) {
            if (x == 0 && y == 0 && z == 0) { continue; }
            BlockPos neighbor = pos.offset(x, y, z);
            if (!level.hasChunkAt(neighbor)) { return List.of(); }
            BlockState adjacent = level.getBlockState(neighbor);
            if (isNaturalLeaf(adjacent)) { leaves = true; }
            if (adjacent.is(state.getBlock()) && visited.add(neighbor)) { queue.add(neighbor); }
          }
        }
      }
    }
    if (!leaves) { return List.of(); }
    logs.remove(origin);
    return logs;
  }
}
