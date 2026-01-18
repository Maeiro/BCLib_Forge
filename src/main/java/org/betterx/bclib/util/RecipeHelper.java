package org.betterx.bclib.util;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;

public class RecipeHelper {
    public static boolean exists(ItemLike item) {
        if (item == null) {
            return false;
        }
        if (item instanceof Block) {
            Block block = (Block) item;
            ResourceLocation blockKey = BuiltInRegistries.BLOCK.getKey(block);
            if (blockKey == null || blockKey == BuiltInRegistries.BLOCK.getDefaultKey()) {
                return false;
            }
            ResourceLocation itemKey = BuiltInRegistries.ITEM.getKey(block.asItem());
            return itemKey != null && itemKey != BuiltInRegistries.ITEM.getDefaultKey();
        }
        ResourceLocation itemKey = BuiltInRegistries.ITEM.getKey(item.asItem());
        return itemKey != null && itemKey != BuiltInRegistries.ITEM.getDefaultKey();
    }

    public static boolean exists(ItemLike... items) {
        for (ItemLike item : items) {
            if (!exists(item)) {
                return false;
            }
        }
        return true;
    }
}
