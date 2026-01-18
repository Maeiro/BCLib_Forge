package org.betterx.bclib.api.v3.levelgen.features;

import org.betterx.bclib.BCLib;
import org.betterx.bclib.api.v3.levelgen.features.config.*;
import org.betterx.bclib.api.v3.levelgen.features.features.*;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;

import org.jetbrains.annotations.ApiStatus;

import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.RegisterEvent;

import java.util.LinkedHashMap;
import java.util.Map;

@Mod.EventBusSubscriber(modid = BCLib.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class BCLFeature<F extends Feature<FC>, FC extends FeatureConfiguration> {
    private static final Map<ResourceLocation, Feature<?>> FEATURES = new LinkedHashMap<>();
    private static boolean REGISTERED_FOR_DATAGEN = false;
    public static class Unregistered<F extends Feature<FC>, FC extends FeatureConfiguration> extends BCLFeature<F, FC> {
        private BCLFeature<F, FC> registered;

        Unregistered(
                BCLConfigureFeature<F, FC> configuredFeature,
                Holder<PlacedFeature> placed,
                GenerationStep.Decoration decoration
        ) {
            super(configuredFeature, placed, decoration);
            registered = null;
        }

        @Override
        public BCLFeature<F, FC> register(BootstapContext<PlacedFeature> bootstrapContext) {
            if (registered != null) return registered;
            Holder<PlacedFeature> holder = BCLPlacedFeatureBuilder.register(
                    bootstrapContext,
                    getPlacedFeature()
            );
            BCLPlacedFeatureBuilder.UNBOUND_FEATURES.remove(this);
            registered = new BCLFeature<>(configuredFeature, holder, decoration);
            return registered;
        }
    }

    public static final Feature<PlaceFacingBlockConfig> PLACE_BLOCK = register(
            BCLib.makeID("place_block"),
            new PlaceBlockFeature<>(PlaceFacingBlockConfig.CODEC)
    );


    public static final Feature<TemplateFeatureConfig> TEMPLATE = register(
            BCLib.makeID("template"),
            new TemplateFeature(
                    TemplateFeatureConfig.CODEC)
    );

    public static final Feature<NoneFeatureConfiguration> MARK_POSTPROCESSING = register(
            BCLib.makeID(
                    "mark_postprocessing"),
            new MarkPostProcessingFeature()
    );

    public static final Feature<SequenceFeatureConfig> SEQUENCE = register(
            BCLib.makeID("sequence"),
            new SequenceFeature()
    );

    public static final Feature<ConditionFeatureConfig> CONDITION = register(
            BCLib.makeID("condition"),
            new ConditionFeature()
    );

    public static final Feature<PillarFeatureConfig> PILLAR = register(
            BCLib.makeID("pillar"),
            new PillarFeature()
    );
    public final BCLConfigureFeature<F, FC> configuredFeature;
    public final Holder<PlacedFeature> placedFeature;
    public final GenerationStep.Decoration decoration;

    @ApiStatus.Internal
    BCLFeature(
            BCLConfigureFeature<F, FC> configuredFeature,
            Holder<PlacedFeature> placed,
            GenerationStep.Decoration decoration
    ) {
        this.configuredFeature = configuredFeature;
        this.placedFeature = placed;
        this.decoration = decoration;
    }

    /**
     * Get raw feature.
     *
     * @return {@link Feature}.
     */
    public F getFeature() {
        return configuredFeature.getFeature();
    }

    /**
     * Get configured feature.
     *
     * @return {@link PlacedFeature}.
     */
    public Holder<PlacedFeature> getPlacedFeature() {
        return placedFeature;
    }

    /**
     * Get feature decoration step.
     *
     * @return {@link GenerationStep.Decoration}.
     */
    public GenerationStep.Decoration getDecoration() {
        return decoration;
    }

    public FC getConfiguration() {
        return configuredFeature.getConfiguration();
    }


    public static <C extends FeatureConfiguration, F extends Feature<C>> F register(
            ResourceLocation location,
            F feature
    ) {
        @SuppressWarnings("unchecked")
        F existing = (F) FEATURES.get(location);
        if (existing != null) {
            return existing;
        }
        FEATURES.put(location, feature);
        return feature;
    }

    public static void registerForDatagen() {
        if (!BCLib.isDatagen() || REGISTERED_FOR_DATAGEN) {
            return;
        }
        REGISTERED_FOR_DATAGEN = true;
        BCLib.LOGGER.info("[datagen] registering {} FEATURES: {}", FEATURES.size(), FEATURES.keySet());
        FEATURES.forEach((id, feature) -> {
            if (!BuiltInRegistries.FEATURE.containsKey(id)) {
                Registry.register(BuiltInRegistries.FEATURE, id, feature);
            }
        });
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onRegister(RegisterEvent event) {
        if (event.getRegistryKey().equals(Registries.FEATURE)) {
            event.register(Registries.FEATURE, helper -> FEATURES.forEach((id, feature) -> {
                if (!BuiltInRegistries.FEATURE.containsKey(id)) {
                    helper.register(id, feature);
                }
            }));
        }
    }

    public BCLFeature<F, FC> register(BootstapContext<PlacedFeature> bootstrapContext) {
        return this;
    }
}
