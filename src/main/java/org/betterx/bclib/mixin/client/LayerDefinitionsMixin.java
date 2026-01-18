package org.betterx.bclib.mixin.client;

import org.betterx.bclib.BCLib;

import org.betterx.bclib.client.LayerDefinitionTracker;

import net.minecraft.client.model.geom.LayerDefinitions;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.builders.LayerDefinition;

import com.google.common.collect.ImmutableMap;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Map;

@Mixin(LayerDefinitions.class)
public class LayerDefinitionsMixin {
    @Inject(method = "createRoots", at = @At("HEAD"))
    private static void bclib_clearLayerKeys(CallbackInfoReturnable<Map<ModelLayerLocation, LayerDefinition>> cir) {
        LayerDefinitionTracker.clear();
    }

    @Redirect(
            method = "createRoots",
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
                BCLib.LOGGER.warning("Skipping duplicate layer definition: {}", layerKey);
                return builder;
            }
        }
        return builder.put((ModelLayerLocation) key, (LayerDefinition) value);
    }

    @Redirect(
            method = "lambda$createRoots$1",
            at = @At(
                    value = "INVOKE",
                    target = "Lcom/google/common/collect/ImmutableMap$Builder;put(Ljava/lang/Object;Ljava/lang/Object;)Lcom/google/common/collect/ImmutableMap$Builder;"
            ),
            require = 0
    )
    private static ImmutableMap.Builder<ModelLayerLocation, LayerDefinition> bclib_dedupeHangingSignLayerDefinition(
            ImmutableMap.Builder<ModelLayerLocation, LayerDefinition> builder,
            Object key,
            Object value
    ) {
        if (key instanceof ModelLayerLocation layerKey && value instanceof LayerDefinition) {
            if (!LayerDefinitionTracker.tryAdd(layerKey)) {
                BCLib.LOGGER.warning("Skipping duplicate hanging sign layer definition: {}", layerKey);
                return builder;
            }
        }
        return builder.put((ModelLayerLocation) key, (LayerDefinition) value);
    }

    @Redirect(
            method = "lambda$createRoots$0",
            at = @At(
                    value = "INVOKE",
                    target = "Lcom/google/common/collect/ImmutableMap$Builder;put(Ljava/lang/Object;Ljava/lang/Object;)Lcom/google/common/collect/ImmutableMap$Builder;"
            ),
            require = 0
    )
    private static ImmutableMap.Builder<ModelLayerLocation, LayerDefinition> bclib_dedupeSignLayerDefinition(
            ImmutableMap.Builder<ModelLayerLocation, LayerDefinition> builder,
            Object key,
            Object value
    ) {
        if (key instanceof ModelLayerLocation layerKey && value instanceof LayerDefinition) {
            if (!LayerDefinitionTracker.tryAdd(layerKey)) {
                BCLib.LOGGER.warning("Skipping duplicate sign layer definition: {}", layerKey);
                return builder;
            }
        }
        return builder.put((ModelLayerLocation) key, (LayerDefinition) value);
    }
}
