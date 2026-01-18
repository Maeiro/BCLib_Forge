package org.betterx.datagen.bclib.tests;

import org.betterx.bclib.BCLib;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;

import java.util.concurrent.CompletableFuture;

public class TestWorldgenProvider implements DataProvider {
    public TestWorldgenProvider(
            PackOutput output,
            CompletableFuture<HolderLookup.Provider> registriesFuture
    ) {
    }

    @Override
    public CompletableFuture<?> run(CachedOutput cachedOutput) {
        if (!BCLib.ADD_TEST_DATA) {
            return CompletableFuture.completedFuture(null);
        }
        return CompletableFuture.completedFuture(null);
    }

    @Override
    public String getName() {
        return "Test WorldGen Provider";
    }
}
