package org.betterx.bclib.api.v2.levelgen.surface.rules;

import org.betterx.bclib.BCLib;
import org.betterx.bclib.interfaces.NumericProvider;

import com.mojang.serialization.Codec;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.valueproviders.UniformFloat;
import net.minecraft.world.level.levelgen.SurfaceRules;

import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.RegisterEvent;

import java.util.LinkedHashMap;
import java.util.Map;

@Mod.EventBusSubscriber(modid = BCLib.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class Conditions {
    public static final ThresholdCondition DOUBLE_BLOCK_SURFACE_NOISE = new ThresholdCondition(
            4141,
            0,
            UniformFloat.of(-0.4f, 0.4f),
            0.1,
            0.1
    );

    public static final ThresholdCondition FORREST_FLOOR_SURFACE_NOISE_A = new ThresholdCondition(
            614,
            0,
            UniformFloat.of(-0.2f, 0f),
            0.1,
            0.1
    );

    public static final ThresholdCondition FORREST_FLOOR_SURFACE_NOISE_B = new ThresholdCondition(
            614,
            0,
            UniformFloat.of(-0.7f, -0.5f),
            0.1,
            0.1
    );

    public static final ThresholdCondition NETHER_SURFACE_NOISE = new ThresholdCondition(
            245,
            0,
            UniformFloat.of(-0.7f, -0.5f),
            0.05,
            0.05
    );

    public static final ThresholdCondition NETHER_SURFACE_NOISE_LARGE = new ThresholdCondition(
            523,
            0,
            UniformFloat.of(-0.4f, -0.3f),
            0.5,
            0.5
    );

    public static final VolumeThresholdCondition NETHER_VOLUME_NOISE = new VolumeThresholdCondition(
            245,
            0,
            UniformFloat.of(-0.1f, 0.2f),
            0.1,
            0.2,
            0.1
    );

    public static final VolumeThresholdCondition NETHER_VOLUME_NOISE_LARGE = new VolumeThresholdCondition(
            523,
            0,
            UniformFloat.of(-0.1f, 0.4f),
            0.2,
            0.2,
            0.2
    );

    public static final NumericProvider NETHER_NOISE = new NetherNoiseCondition();

    private static boolean REGISTERED = false;

    private static final Map<ResourceLocation, Codec<? extends SurfaceRules.ConditionSource>> CONDITIONS =
            new LinkedHashMap<>();
    private static final Map<ResourceLocation, Codec<? extends SurfaceRules.RuleSource>> RULES =
            new LinkedHashMap<>();

    public static void register(ResourceLocation location, Codec<? extends SurfaceRules.ConditionSource> codec) {
        CONDITIONS.putIfAbsent(location, codec);
    }

    public static void registerRule(ResourceLocation location, Codec<? extends SurfaceRules.RuleSource> codec) {
        RULES.putIfAbsent(location, codec);
    }

    public static void registerNumeric(ResourceLocation location, Codec<? extends NumericProvider> codec) {
        Registry.register(NumericProvider.NUMERIC_PROVIDER, location, codec);
    }

    public static void registerAll() {
        if (REGISTERED) {
            return;
        }
        REGISTERED = true;
        registerNumeric(BCLib.makeID("rnd_int"), RandomIntProvider.CODEC);
        registerNumeric(BCLib.makeID("nether_noise"), NetherNoiseCondition.CODEC);
        register(BCLib.makeID("threshold_condition"), ThresholdCondition.CODEC);
        register(BCLib.makeID("volume_threshold_condition"), VolumeThresholdCondition.CODEC);
        register(BCLib.makeID("rough_noise_condition"), RoughNoiseCondition.CODEC);
        registerRule(new ResourceLocation("bclib_switch_rule"), SwitchRuleSource.CODEC);
        if (BCLib.isDatagen()) {
            registerForDatagen();
        }
    }

    private static void registerForDatagen() {
        CONDITIONS.forEach((id, codec) -> {
            if (!BuiltInRegistries.MATERIAL_CONDITION.containsKey(id)) {
                Registry.register(BuiltInRegistries.MATERIAL_CONDITION, id, codec);
            }
        });
        RULES.forEach((id, codec) -> {
            if (!BuiltInRegistries.MATERIAL_RULE.containsKey(id)) {
                Registry.register(BuiltInRegistries.MATERIAL_RULE, id, codec);
            }
        });
        CONDITIONS.clear();
        RULES.clear();
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onRegister(RegisterEvent event) {
        if (!REGISTERED) {
            registerAll();
        }
        if (event.getRegistryKey().equals(Registries.MATERIAL_CONDITION)) {
            event.register(Registries.MATERIAL_CONDITION, helper -> {
                CONDITIONS.forEach(helper::register);
                CONDITIONS.clear();
            });
        } else if (event.getRegistryKey().equals(Registries.MATERIAL_RULE)) {
            event.register(Registries.MATERIAL_RULE, helper -> {
                RULES.forEach(helper::register);
                RULES.clear();
            });
        }
    }
}
