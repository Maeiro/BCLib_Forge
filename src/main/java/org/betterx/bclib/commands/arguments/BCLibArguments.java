package org.betterx.bclib.commands.arguments;

import org.betterx.bclib.BCLib;

import net.minecraft.commands.synchronization.ArgumentTypeInfo;
import net.minecraft.commands.synchronization.ArgumentTypeInfos;
import net.minecraft.commands.synchronization.SingletonArgumentInfo;
import net.minecraft.core.registries.Registries;

import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.RegisterEvent;

import com.mojang.brigadier.arguments.ArgumentType;

@Mod.EventBusSubscriber(modid = BCLib.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class BCLibArguments {
    public static void register() {
    }

    @SubscribeEvent
    public static void onRegister(RegisterEvent event) {
        event.register(Registries.COMMAND_ARGUMENT_TYPE, helper -> {
            register(
                    helper,
                    BCLib.makeID("template_placement"),
                    TemplatePlacementArgument.class,
                    SingletonArgumentInfo.contextFree(TemplatePlacementArgument::templatePlacement)
            );

            register(
                    helper,
                    BCLib.makeID("float3"),
                    Float3ArgumentType.class,
                    new Float3ArgumentInfo()
            );

            register(
                    helper,
                    BCLib.makeID("connector"),
                    ConnectorArgument.class,
                    SingletonArgumentInfo.contextFree(ConnectorArgument::id)
            );
        });
    }

    private static <T extends ArgumentType<?>> void register(
            RegisterEvent.RegisterHelper<ArgumentTypeInfo<?, ?>> helper,
            net.minecraft.resources.ResourceLocation id,
            Class<T> type,
            ArgumentTypeInfo<T, ?> info
    ) {
        ArgumentTypeInfos.registerByClass(type, info);
        helper.register(id, info);
    }
}
