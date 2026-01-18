package org.betterx.bclib.particles;

import org.betterx.bclib.BCLib;

import com.mojang.serialization.Codec;
import net.minecraft.core.Registry;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;

import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.RegisterEvent;

import java.util.LinkedHashMap;
import java.util.Map;

@Mod.EventBusSubscriber(modid = BCLib.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class BCLParticleType {
    private static final Map<ResourceLocation, ParticleType<?>> TYPES = new LinkedHashMap<>();
    private static boolean REGISTERED_FOR_DATAGEN = false;

    public static <T extends ParticleOptions> ParticleType<T> deserializer(
            ParticleOptions.Deserializer<T> factory,
            Codec<T> codec
    ) {
        return deserializer(false, factory, codec);
    }

    public static <T extends ParticleOptions> ParticleType<T> deserializer(
            boolean overrideLimiter,
            ParticleOptions.Deserializer<T> factory,
            Codec<T> codec
    ) {
        return new ParticleType<T>(overrideLimiter, factory) {
            @Override
            public Codec<T> codec() {
                return codec;
            }
        };
    }

    public static <T extends ParticleOptions> ParticleType<T> register(
            ResourceLocation location,
            ParticleOptions.Deserializer<T> factory,
            Codec<T> codec
    ) {
        return register(location, false, factory, codec);
    }

    public static <T extends ParticleOptions> ParticleType<T> register(
            ResourceLocation location,
            boolean overrideLimiter,
            ParticleOptions.Deserializer<T> factory,
            Codec<T> codec
    ) {
        return registerInternal(location, deserializer(overrideLimiter, factory, codec));
    }

    public static SimpleParticleType simple(boolean overrideLimiter) {
        return new SimpleParticleType(overrideLimiter) {
        };
    }

    public static SimpleParticleType simple() {
        return simple(false);
    }

    public static SimpleParticleType register(ResourceLocation location) {
        return register(location, false);
    }

    public static SimpleParticleType register(ResourceLocation location, boolean overrideLimiter) {
        return registerInternal(location, simple(overrideLimiter));
    }

    public static SimpleParticleType register(
            ResourceLocation location,
            ParticleFactoryRegistry.PendingParticleFactory<SimpleParticleType> provider
    ) {
        return register(location, false, provider);
    }

    public static SimpleParticleType register(
            ResourceLocation location,
            boolean overrideLimiter,
            ParticleFactoryRegistry.PendingParticleFactory<SimpleParticleType> provider
    ) {
        SimpleParticleType type = registerInternal(location, simple(overrideLimiter));
        ParticleFactoryRegistry.getInstance().register(type, provider);
        return type;
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onRegister(RegisterEvent event) {
        if (!event.getRegistryKey().equals(Registries.PARTICLE_TYPE)) {
            return;
        }
        event.register(Registries.PARTICLE_TYPE, helper -> {
            TYPES.forEach(helper::register);
            TYPES.clear();
        });
    }

    public static void registerForDatagen() {
        if (!BCLib.isDatagen() || REGISTERED_FOR_DATAGEN) {
            return;
        }
        REGISTERED_FOR_DATAGEN = true;
        BCLib.LOGGER.info("[datagen] registering {} PARTICLE_TYPES: {}", TYPES.size(), TYPES.keySet());
        TYPES.forEach((id, type) -> {
            if (!BuiltInRegistries.PARTICLE_TYPE.containsKey(id)) {
                Registry.register(BuiltInRegistries.PARTICLE_TYPE, id, type);
            }
        });
    }

    @SuppressWarnings("unchecked")
    private static <T extends ParticleType<?>> T registerInternal(ResourceLocation location, T type) {
        ParticleType<?> existing = TYPES.get(location);
        if (existing != null) {
            return (T) existing;
        }
        TYPES.put(location, type);
        return type;
    }
}
