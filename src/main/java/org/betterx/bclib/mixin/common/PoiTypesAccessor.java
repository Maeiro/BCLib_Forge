package org.betterx.bclib.mixin.common;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.ai.village.poi.PoiType;
import net.minecraft.world.entity.ai.village.poi.PoiTypes;
import net.minecraft.world.level.block.state.BlockState;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

import java.util.Map;
import java.util.Set;

@Mixin(PoiTypes.class)
public interface PoiTypesAccessor {
    @Invoker("register")
    static PoiType bclib_register(
            Registry<PoiType> registry,
            ResourceKey<PoiType> key,
            Set<BlockState> matchingStates,
            int maxTickets,
            int validRanges
    ) {
        throw new AssertionError("Mixin invoker stub");
    }

    @Accessor("TYPE_BY_STATE")
    static Map<BlockState, PoiType> bclib_getTypeByState() {
        throw new AssertionError("Mixin accessor stub");
    }
}
