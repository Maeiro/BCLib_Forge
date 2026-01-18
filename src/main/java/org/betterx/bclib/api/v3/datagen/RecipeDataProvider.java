package org.betterx.bclib.api.v3.datagen;

import org.betterx.bclib.BCLib;

import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.RecipeProvider;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

public class RecipeDataProvider extends RecipeProvider {
    private static List<DatapackRecipeBuilder> RECIPES;
    private static List<Runnable> DEFERRED;
    private static boolean deferredBuilt;

    @Nullable
    protected final List<String> modIDs;

    public RecipeDataProvider(@Nullable List<String> modIDs, PackOutput output) {
        super(output);
        this.modIDs = modIDs;
    }

    @Override
    public void buildRecipes(Consumer<FinishedRecipe> exporter) {
        runDeferred();
        if (RECIPES == null) return;

        for (var r : RECIPES) {
            if (modIDs.size() == 0 || modIDs.indexOf(r.getNamespace()) >= 0) {
                r.build(exporter);
            }
        }
    }

    @ApiStatus.Internal
    public static void register(DatapackRecipeBuilder builder) {
        //thi is only used withe the Data Generator, so we do not keep this list on a regular run
        if (!BCLib.isDatagen()) {
            return;
        }
        if (RECIPES == null) RECIPES = new ArrayList<>();
        RECIPES.add(builder);
    }

    public static void defer(Runnable task) {
        if (!BCLib.isDatagen() || task == null) {
            return;
        }
        if (DEFERRED == null) {
            DEFERRED = new ArrayList<>();
        }
        DEFERRED.add(task);
    }

    private static void runDeferred() {
        if (deferredBuilt) {
            return;
        }
        deferredBuilt = true;
        if (DEFERRED == null) {
            return;
        }
        for (Runnable task : DEFERRED) {
            task.run();
        }
        DEFERRED.clear();
    }
}
