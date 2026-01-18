package org.betterx.bclib.api.v3.levelgen.features.blockpredicates;

import org.betterx.bclib.BCLib;
import org.betterx.worlds.together.tag.v3.CommonBlockTags;

import com.mojang.serialization.Codec;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.blockpredicates.BlockPredicate;
import net.minecraft.world.level.levelgen.blockpredicates.BlockPredicateType;
import net.minecraft.world.level.material.Fluids;

import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.RegisterEvent;

@Mod.EventBusSubscriber(modid = BCLib.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class BlockPredicates {
    public static final BlockPredicate ONLY_NYLIUM = BlockPredicate.matchesTag(BlockTags.NYLIUM);
    public static final BlockPredicate ONLY_MYCELIUM = BlockPredicate.matchesTag(CommonBlockTags.MYCELIUM);
    public static final BlockPredicate ONLY_GRAVEL_OR_SAND = BlockPredicate.matchesBlocks(
            Blocks.GRAVEL,
            Blocks.SAND,
            Blocks.RED_SAND
    );
    public static final BlockPredicate ONLY_SOUL_GROUND = BlockPredicate.matchesTag(CommonBlockTags.SOUL_GROUND);
    public static final BlockPredicate ONLY_NETHER_GROUND = BlockPredicate.matchesTag(CommonBlockTags.NETHER_TERRAIN);
    public static final BlockPredicate ONLY_NETHER_GROUND_AND_BASALT = BlockPredicate.anyOf(
            ONLY_NETHER_GROUND,
            BlockPredicate.matchesBlocks(Blocks.BASALT)
    );
    public static final BlockPredicate ONLY_GROUND = BlockPredicate.matchesTag(CommonBlockTags.TERRAIN);

    public static final BlockPredicate ONLY_LAVA = BlockPredicate.matchesFluids(Fluids.LAVA);
    public static final BlockPredicate ONLY_GROUND_OR_LAVA = BlockPredicate.anyOf(
            BlockPredicate.matchesTag(CommonBlockTags.TERRAIN),
            BlockPredicate.matchesFluids(Fluids.LAVA)
    );
    public static BlockPredicateType<IsFullShape> FULL_SHAPE;
    private static final ResourceLocation FULL_SHAPE_ID = BCLib.makeID("full_shape");

    private static <P extends BlockPredicate> BlockPredicateType<P> createType(Codec<P> codec) {
        return () -> codec;
    }

    @SubscribeEvent
    public static void register(RegisterEvent event) {
        event.register(Registries.BLOCK_PREDICATE_TYPE, helper -> {
            FULL_SHAPE = createType(IsFullShape.CODEC);
            helper.register(FULL_SHAPE_ID, FULL_SHAPE);
        });
    }

    public static void ensureStaticInitialization() {
        if (FULL_SHAPE == null) {
            FULL_SHAPE = createType(IsFullShape.CODEC);
        }
        if (!BuiltInRegistries.BLOCK_PREDICATE_TYPE.containsKey(FULL_SHAPE_ID)) {
            Registry.register(BuiltInRegistries.BLOCK_PREDICATE_TYPE, FULL_SHAPE_ID, FULL_SHAPE);
        }
    }
}
