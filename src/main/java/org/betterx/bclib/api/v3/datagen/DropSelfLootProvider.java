package org.betterx.bclib.api.v3.datagen;

import org.betterx.bclib.behaviours.interfaces.BehaviourExplosionResistant;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.predicates.ExplosionCondition;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;

public interface DropSelfLootProvider<B extends ItemLike> extends LootDropProvider {
    @Override
    default void getDroppedItemsBCL(LootTable.Builder builder) {
        ItemLike drop = (ItemLike) this;
        if (this instanceof Block block) {
            Item item = block.asItem();
            if (item == Items.AIR) {
                Item byId = BuiltInRegistries.ITEM.get(BuiltInRegistries.BLOCK.getKey(block));
                if (byId != Items.AIR) {
                    drop = byId;
                }
            }
        }
        var pool = LootPool.lootPool()
                           .setRolls(ConstantValue.exactly(1.0f))
                           .add(LootItem.lootTableItem(drop));

        if (this instanceof BehaviourExplosionResistant) {
            pool = pool.when(ExplosionCondition.survivesExplosion());
        }
        
        builder.withPool(pool);
    }
}
