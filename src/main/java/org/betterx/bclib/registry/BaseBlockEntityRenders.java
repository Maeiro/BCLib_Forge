package org.betterx.bclib.registry;

import org.betterx.bclib.client.render.BaseChestBlockEntityRenderer;
import org.betterx.bclib.furniture.renderer.RenderChair;
import org.betterx.bclib.items.boat.BoatTypeOverride;

import net.minecraft.client.model.BoatModel;
import net.minecraft.client.model.ChestBoatModel;
import net.minecraft.client.model.ChestRaftModel;
import net.minecraft.client.model.RaftModel;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.world.entity.EntityType;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@OnlyIn(Dist.CLIENT)
@Mod.EventBusSubscriber(modid = org.betterx.bclib.BCLib.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class BaseBlockEntityRenders {
    public static void register() {
    }

    @SubscribeEvent
    public static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerBlockEntityRenderer(BaseBlockEntities.CHEST, BaseChestBlockEntityRenderer::new);
        event.registerEntityRenderer(BaseBlockEntities.CHAIR, RenderChair::new);
    }

    @SubscribeEvent
    public static void registerLayerDefinitions(EntityRenderersEvent.RegisterLayerDefinitions event) {
        LayerDefinition boatModel = BoatModel.createBodyModel();
        LayerDefinition chestBoatModel = ChestBoatModel.createBodyModel();
        LayerDefinition raftModel = RaftModel.createBodyModel();
        LayerDefinition chestRaftModel = ChestRaftModel.createBodyModel();

        BoatTypeOverride.values().forEach(type -> {
            event.registerLayerDefinition(type.boatModelName, () -> type.isRaft ? raftModel : boatModel);
            event.registerLayerDefinition(
                    type.chestBoatModelName,
                    () -> type.isRaft ? chestRaftModel : chestBoatModel
            );
        });
    }

    public static void registerRender(EntityType<?> entity, Class<? extends EntityRenderer<?>> renderer) {
        // Forge registration handled in RegisterRenderers event.
    }
}
