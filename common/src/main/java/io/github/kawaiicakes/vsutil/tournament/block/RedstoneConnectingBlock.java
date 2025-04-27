package io.github.kawaiicakes.vsutil.tournament.block;

import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;

public interface RedstoneConnectingBlock {
    Boolean canConnectTo(BlockState state, Direction direction);
}
