package org.betterx.bclib.registry;

import org.betterx.bclib.BCLib;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.RegisterEvent;

import java.util.LinkedHashMap;
import java.util.Map;

@Mod.EventBusSubscriber(modid = BCLib.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public final class RegistryBootstrap {
    private static final Map<ResourceLocation, Block> BLOCKS = new LinkedHashMap<>();
    private static final Map<ResourceLocation, Item> ITEMS = new LinkedHashMap<>();

    private RegistryBootstrap() {
    }

    static void queueBlock(ResourceLocation id, Block block) {
        BLOCKS.putIfAbsent(id, block);
    }

    static void queueItem(ResourceLocation id, Item item) {
        ITEMS.putIfAbsent(id, item);
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void register(RegisterEvent event) {
        if (event.getRegistryKey().equals(Registries.BLOCK)) {
            event.register(Registries.BLOCK, helper -> {
                BLOCKS.forEach(helper::register);
                BLOCKS.clear();
            });
        } else if (event.getRegistryKey().equals(Registries.ITEM)) {
            event.register(Registries.ITEM, helper -> {
                ITEMS.forEach((id, item) -> {
                    if (item instanceof BlockItem blockItem) {
                        try {
                            Block block = blockItem.getBlock();
                            if (BuiltInRegistries.BLOCK.getKey(block) == BuiltInRegistries.BLOCK.getDefaultKey()) {
                                BCLib.LOGGER.warning(
                                        "Skipping BlockItem " + id + " for unregistered block " + block
                                );
                                return;
                            }
                        } catch (IllegalArgumentException ex) {
                            BCLib.LOGGER.warning(
                                    "Skipping BlockItem " + id + " due to missing block delegate: " + ex.getMessage()
                            );
                            return;
                        }
                    }
                    helper.register(id, item);
                });
                ITEMS.clear();
            });
        }
    }
}
