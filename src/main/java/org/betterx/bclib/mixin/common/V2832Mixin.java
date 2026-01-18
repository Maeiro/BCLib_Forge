package org.betterx.bclib.mixin.common;

import org.betterx.bclib.api.v2.generator.BCLChunkGenerator;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.types.templates.TypeTemplate;
import net.minecraft.util.datafix.schemas.V2832;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.Map;
import java.util.function.Supplier;

@Mixin(V2832.class)
public class V2832Mixin {
    @Redirect(
            method = "*",
            at = @At(
                    value = "INVOKE",
                    target = "Lcom/google/common/collect/ImmutableMap;of(Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;)Lcom/google/common/collect/ImmutableMap;"
            ),
            remap = false
    )
    private static ImmutableMap<String, Supplier<TypeTemplate>> bcl_addGenerator(
            Object key1,
            Object value1,
            Object key2,
            Object value2,
            Object key3,
            Object value3
    ) {
        @SuppressWarnings("unchecked")
        ImmutableMap<String, Supplier<TypeTemplate>> base = (ImmutableMap<String, Supplier<TypeTemplate>>) (ImmutableMap<?, ?>)
                ImmutableMap.of(key1, value1, key2, value2, key3, value3);
        Map<String, Supplier<TypeTemplate>> updated = BCLChunkGenerator.addGeneratorDSL(base);
        return updated == base ? base : ImmutableMap.copyOf(updated);
    }
}
