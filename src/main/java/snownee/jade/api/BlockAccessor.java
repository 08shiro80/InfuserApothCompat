package snownee.jade.api;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public interface BlockAccessor {
    Level getLevel();
    BlockPos getPosition();
    BlockState getBlockState();
}
