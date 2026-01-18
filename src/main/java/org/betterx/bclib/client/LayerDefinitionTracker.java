package org.betterx.bclib.client;

import net.minecraft.client.model.geom.ModelLayerLocation;

import com.google.common.collect.Sets;

import java.util.Set;

public final class LayerDefinitionTracker {
    private static final Set<ModelLayerLocation> LAYER_KEYS = Sets.newHashSet();

    private LayerDefinitionTracker() {
    }

    public static void clear() {
        LAYER_KEYS.clear();
    }

    public static boolean tryAdd(ModelLayerLocation layerKey) {
        return LAYER_KEYS.add(layerKey);
    }
}
