package io.github.tomokuroki.heavenlyrealm;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.TallGrassBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;

public final class HeavenGrassBlock extends TallGrassBlock {
    public HeavenGrassBlock(BlockBehaviour.Properties properties) {
        super(properties);
    }

    @Override
    protected boolean mayPlaceOn(BlockState state, BlockGetter level, BlockPos pos) {
        return state.is(HeavenlyRealm.CLOUD_MARBLE.get()) || super.mayPlaceOn(state, level, pos);
    }
}
