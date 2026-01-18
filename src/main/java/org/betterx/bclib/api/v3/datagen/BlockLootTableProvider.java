package org.betterx.bclib.api.v3.datagen;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.data.loot.LootTableSubProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;

import java.util.List;
import java.util.Set;
import java.util.function.BiConsumer;

public class BlockLootTableProvider extends LootTableProvider {
    public BlockLootTableProvider(
            PackOutput output,
            List<String> modIDs
    ) {
        super(
                output,
                Set.of(),
                List.of(new SubProviderEntry(() -> new BlockLootSubProvider(modIDs), LootContextParamSets.BLOCK))
        );
    }

    private static class BlockLootSubProvider implements LootTableSubProvider {
        private final List<String> modIDs;

        private BlockLootSubProvider(List<String> modIDs) {
            this.modIDs = modIDs;
        }

        @Override
        public void generate(BiConsumer<ResourceLocation, LootTable.Builder> biConsumer) {
            for (Block block : BuiltInRegistries.BLOCK) {
                if (block instanceof LootDropProvider dropper) {
                    ResourceLocation id = BuiltInRegistries.BLOCK.getKey(block);
                    if (id != null && modIDs.contains(id.getNamespace())) {
                        LootTable.Builder builder = LootTable.lootTable();
                        dropper.getDroppedItemsBCL(builder);
                        biConsumer.accept(id.withPrefix("blocks/"), builder);
                    }
                }
            }
        }
    }
}
