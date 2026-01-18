package org.betterx.bclib.particles;

import org.betterx.bclib.BCLib;

import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterParticleProvidersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.IdentityHashMap;
import java.util.Map;

/**
 * Forge particle factory registry helper.
 */
@Mod.EventBusSubscriber(modid = BCLib.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public final class ParticleFactoryRegistry {
    public interface PendingParticleFactory<T extends ParticleOptions> {
        ParticleProvider<T> create(SpriteSet sprites);
    }

    private static final ParticleFactoryRegistry INSTANCE = new ParticleFactoryRegistry();
    private final Map<ParticleType<?>, PendingParticleFactory<?>> factories = new IdentityHashMap<>();

    private ParticleFactoryRegistry() {
    }

    public static ParticleFactoryRegistry getInstance() {
        return INSTANCE;
    }

    public <T extends ParticleOptions> void register(ParticleType<T> type, PendingParticleFactory<T> factory) {
        factories.put(type, factory);
    }

    @SubscribeEvent
    public static void onRegisterParticleProviders(RegisterParticleProvidersEvent event) {
        INSTANCE.factories.forEach((type, factory) -> registerFactory(event, type, factory));
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private static void registerFactory(
            RegisterParticleProvidersEvent event,
            ParticleType<?> type,
            PendingParticleFactory<?> factory
    ) {
        event.registerSpriteSet((ParticleType) type, spriteSet -> factory.create(spriteSet));
    }
}
