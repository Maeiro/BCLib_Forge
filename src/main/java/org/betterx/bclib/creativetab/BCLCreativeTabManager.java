package org.betterx.bclib.creativetab;

import org.betterx.bclib.BCLib;
import org.betterx.bclib.registry.BaseRegistry;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.level.ItemLike;

import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.RegisterEvent;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

@Mod.EventBusSubscriber(modid = BCLib.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class BCLCreativeTabManager {
    private static final Map<ResourceLocation, CreativeModeTab> PENDING_TABS = new LinkedHashMap<>();
    public final String modID;
    protected final List<BCLCreativeTab> tabs = new LinkedList<>();
    private boolean registered;

    public static BCLCreativeTabManager create(String modID) {
        return new BCLCreativeTabManager(modID);
    }

    protected BCLCreativeTabManager(String modID) {
        this.modID = modID;
    }

    public BCLCreativeTab.Builder createTab(String name) {
        return new BCLCreativeTab.Builder(this, name);
    }

    public BCLCreativeTab.Builder createBlockTab(ItemLike icon) {
        return new BCLCreativeTab.Builder(this, "blocks").setIcon(icon).setPredicate(BCLCreativeTab.BLOCKS);
    }

    public BCLCreativeTab.Builder createItemsTab(ItemLike icon) {
        return new BCLCreativeTab.Builder(this, "items").setIcon(icon);
    }

    public BCLCreativeTabManager processBCLRegistry() {
        process(BaseRegistry.getModItems(modID));
        process(BaseRegistry.getModBlockItems(modID));
        return this;
    }

    public BCLCreativeTabManager process(List<Item> items) {
        for (Item item : items) {
            for (BCLCreativeTab tab : tabs) {
                if (tab.predicate.contains(item)) {
                    tab.addItem(item);
                    break;
                }
            }
        }

        return this;
    }

    public void register() {
        if (registered) {
            return;
        }
        registered = true;
        for (BCLCreativeTab tab : tabs) {
            CreativeModeTab tabItem = CreativeModeTab
                    .builder()
                    .icon(() -> new ItemStack(tab.icon))
                    .title(tab.title)
                    .displayItems((parameters, output) -> {
                        output.acceptAll(tab.items.stream().map(ItemStack::new).toList());
                        //tab.items.clear();
                    }).build();

            PENDING_TABS.putIfAbsent(tab.id, tabItem);
        }

        //this.tabs.clear();
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onRegister(RegisterEvent event) {
        if (!event.getRegistryKey().equals(Registries.CREATIVE_MODE_TAB)) {
            return;
        }
        event.register(Registries.CREATIVE_MODE_TAB, helper -> {
            PENDING_TABS.forEach(helper::register);
            PENDING_TABS.clear();
        });
    }
}
