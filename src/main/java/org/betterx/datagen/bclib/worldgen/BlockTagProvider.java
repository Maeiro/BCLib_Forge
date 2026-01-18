package org.betterx.datagen.bclib.worldgen;

import org.betterx.bclib.BCLib;
import org.betterx.bclib.api.v3.datagen.TagDataProvider;
import org.betterx.worlds.together.WorldsTogether;
import org.betterx.worlds.together.tag.v3.TagManager;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.world.level.block.Block;

import net.minecraftforge.common.data.ExistingFileHelper;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public class BlockTagProvider extends TagDataProvider<Block> {
    public BlockTagProvider(
            PackOutput output,
            CompletableFuture<HolderLookup.Provider> registriesFuture,
            ExistingFileHelper existingFileHelper
    ) {
        super(TagManager.BLOCKS, List.of(BCLib.MOD_ID, WorldsTogether.MOD_ID, "c"), output, registriesFuture, existingFileHelper);
    }

    public static void bootstrap(BootstapContext<Block> bootstrapContext) {

    }
}

