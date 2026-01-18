package org.betterx.bclib.api.v3.datagen;

import org.betterx.bclib.api.v2.advancement.AdvancementManager;

import net.minecraft.advancements.Advancement;
import net.minecraft.core.HolderLookup;

import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.common.data.ForgeAdvancementProvider;

import java.util.List;
import java.util.function.Consumer;

public abstract class AdvancementDataProvider implements ForgeAdvancementProvider.AdvancementGenerator {
    protected final List<String> modIDs;

    protected AdvancementDataProvider(List<String> modIDs) {
        this.modIDs = modIDs;
    }

    protected abstract void bootstrap();

    @Override
    public void generate(
            HolderLookup.Provider registries,
            Consumer<Advancement> consumer,
            ExistingFileHelper existingFileHelper
    ) {
        bootstrap();
        AdvancementManager.registerAllDataGen(modIDs, consumer);
    }
}
