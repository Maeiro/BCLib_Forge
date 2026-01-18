package org.betterx.datagen.bclib.worldgen;

import org.betterx.bclib.BCLib;
import org.betterx.bclib.api.v3.datagen.RegistriesDataProvider;
import org.betterx.datagen.bclib.BCLRegistrySupplier;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;

import java.util.concurrent.CompletableFuture;

public class BCLibRegistriesDataProvider extends RegistriesDataProvider {
    public BCLibRegistriesDataProvider(
            PackOutput generator,
            CompletableFuture<HolderLookup.Provider> registriesFuture
    ) {
        super(BCLib.LOGGER, BCLRegistrySupplier.INSTANCE, generator, registriesFuture);
    }
}
