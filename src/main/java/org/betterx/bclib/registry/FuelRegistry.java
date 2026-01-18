package org.betterx.bclib.registry;

import org.betterx.bclib.BCLib;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;

import net.minecraftforge.event.furnace.FurnaceFuelBurnTimeEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.IdentityHashMap;
import java.util.Map;

/**
 * Forge fuel registry helper.
 */
@Mod.EventBusSubscriber(modid = BCLib.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public final class FuelRegistry {
    public static final FuelRegistry INSTANCE = new FuelRegistry();

    private final Map<Item, Integer> fuels = new IdentityHashMap<>();

    private FuelRegistry() {
    }

    public void add(ItemLike item, int burnTime) {
        fuels.put(item.asItem(), burnTime);
    }

    @SubscribeEvent
    public static void onFuelBurnTime(FurnaceFuelBurnTimeEvent event) {
        ItemStack stack = event.getItemStack();
        Integer burnTime = INSTANCE.fuels.get(stack.getItem());
        if (burnTime != null) {
            event.setBurnTime(burnTime);
        }
    }
}
