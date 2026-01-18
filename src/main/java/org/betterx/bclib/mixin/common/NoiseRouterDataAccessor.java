package org.betterx.bclib.mixin.common;

import net.minecraft.core.HolderGetter;
import net.minecraft.world.level.levelgen.DensityFunction;
import net.minecraft.world.level.levelgen.NoiseRouter;
import net.minecraft.world.level.levelgen.NoiseRouterData;
import net.minecraft.world.level.levelgen.synth.NormalNoise;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(NoiseRouterData.class)
public interface NoiseRouterDataAccessor {
    @Invoker("noNewCaves")
    static NoiseRouter bclib_noNewCaves(
            HolderGetter<DensityFunction> densityGetter,
            HolderGetter<NormalNoise.NoiseParameters> noiseGetter,
            DensityFunction densityFunction
    ) {
        throw new AssertionError("Mixin invoker stub");
    }

    @Invoker("slideNetherLike")
    static DensityFunction bclib_slideNetherLike(
            HolderGetter<DensityFunction> densityGetter,
            int minY,
            int maxY
    ) {
        throw new AssertionError("Mixin invoker stub");
    }
}
