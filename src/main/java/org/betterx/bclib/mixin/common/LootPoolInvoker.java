package org.betterx.bclib.mixin.common;

import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.entries.LootPoolEntryContainer;
import net.minecraft.world.level.storage.loot.functions.LootItemFunction;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.providers.number.NumberProvider;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(LootPool.class)
public interface LootPoolInvoker {
    @Invoker("<init>")
    static LootPool callInit(
            LootPoolEntryContainer[] entries,
            LootItemCondition[] conditions,
            LootItemFunction[] functions,
            NumberProvider rolls,
            NumberProvider bonusRolls,
            String name
    ) {
        throw new AssertionError("@Invoker dummy body called");
    }
}
