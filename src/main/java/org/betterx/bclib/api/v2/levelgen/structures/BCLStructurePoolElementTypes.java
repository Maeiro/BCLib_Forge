package org.betterx.bclib.api.v2.levelgen.structures;

import org.betterx.bclib.BCLib;

import com.mojang.serialization.Codec;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.levelgen.structure.pools.StructurePoolElement;
import net.minecraft.world.level.levelgen.structure.pools.StructurePoolElementType;

import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.RegisterEvent;

import java.util.LinkedHashMap;
import java.util.Map;

@Mod.EventBusSubscriber(modid = BCLib.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class BCLStructurePoolElementTypes {
    private static final Map<ResourceLocation, StructurePoolElementType<?>> TYPES = new LinkedHashMap<>();
    public static final StructurePoolElementType<SingleEndPoolElement> END = register(
            BCLib.makeID("single_end_pool_element"), SingleEndPoolElement.CODEC);


    public static <P extends StructurePoolElement> StructurePoolElementType<P> register(
            ResourceLocation id,
            Codec<P> codec
    ) {
        @SuppressWarnings("unchecked")
        StructurePoolElementType<P> existing = (StructurePoolElementType<P>) TYPES.get(id);
        if (existing != null) {
            return existing;
        }
        StructurePoolElementType<P> type = () -> codec;
        TYPES.put(id, type);
        return type;
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onRegister(RegisterEvent event) {
        if (event.getRegistryKey().equals(Registries.STRUCTURE_POOL_ELEMENT)) {
            event.register(Registries.STRUCTURE_POOL_ELEMENT, helper -> TYPES.forEach(helper::register));
        }
    }

    public static void ensureStaticallyLoaded() {
        if (!BCLib.isDatagen()) {
            return;
        }
        TYPES.forEach((id, type) -> {
            if (!BuiltInRegistries.STRUCTURE_POOL_ELEMENT.containsKey(id)) {
                Registry.register(BuiltInRegistries.STRUCTURE_POOL_ELEMENT, id, type);
            }
        });
    }
}
