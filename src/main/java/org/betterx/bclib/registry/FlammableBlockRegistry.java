package org.betterx.bclib.registry;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.FireBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.betterx.bclib.BCLib;
import org.betterx.bclib.mixin.common.FireBlockAccessor;

/**
 * Forge helper for flammable blocks.
 */
public final class FlammableBlockRegistry {
    private static final FlammableBlockRegistry INSTANCE = new FlammableBlockRegistry();

    private FlammableBlockRegistry() {
    }

    public static FlammableBlockRegistry getDefaultInstance() {
        return INSTANCE;
    }

    public void add(Block block, int encouragement, int flammability) {
        FireBlock fire = (FireBlock) Blocks.FIRE;
        if (fire instanceof FireBlockAccessor accessor) {
            accessor.bclib_setFlammable(block, encouragement, flammability);
            return;
        }
        throw new IllegalStateException("FireBlockAccessor mixin not applied");
    }

    public Entry get(Block block) {
        FireBlock fire = (FireBlock) Blocks.FIRE;
        BlockState state = block.defaultBlockState();
        return new Entry(fire.getBurnOdds(state), fire.getIgniteOdds(state));
    }

    public static final class Entry {
        private final int burnChance;
        private final int spreadChance;

        private Entry(int burnChance, int spreadChance) {
            this.burnChance = burnChance;
            this.spreadChance = spreadChance;
        }

        public int getBurnChance() {
            return burnChance;
        }

        public int getSpreadChance() {
            return spreadChance;
        }
    }

}
