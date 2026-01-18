package org.betterx.bclib.mixin.client;

import org.betterx.bclib.BCLib;

import org.betterx.bclib.client.LayerDefinitionTracker;

import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.builders.LayerDefinition;

import net.minecraftforge.client.ForgeHooksClient;

import com.google.common.collect.ImmutableMap;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(value = ForgeHooksClient.class, remap = false)
public class ForgeHooksClientMixin {
    @Redirect(
            method = "lambda$loadLayerDefinitions$2",
            at = @At(
                    value = "INVOKE",
                    target = "Lcom/google/common/collect/ImmutableMap$Builder;put(Ljava/lang/Object;Ljava/lang/Object;)Lcom/google/common/collect/ImmutableMap$Builder;"
            )
    )
    private static ImmutableMap.Builder<ModelLayerLocation, LayerDefinition> bclib_dedupeLayerDefinition(
            ImmutableMap.Builder<ModelLayerLocation, LayerDefinition> builder,
            Object key,
            Object value
    ) {
        if (key instanceof ModelLayerLocation layerKey && value instanceof LayerDefinition) {
            if (!LayerDefinitionTracker.tryAdd(layerKey)) {
                BCLib.LOGGER.warning("Skipping duplicate forge layer definition: {}", layerKey);
                return builder;
            }
        }
        return builder.put((ModelLayerLocation) key, (LayerDefinition) value);
    }
}
