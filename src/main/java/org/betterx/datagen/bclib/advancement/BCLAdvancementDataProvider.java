package org.betterx.datagen.bclib.advancement;

import org.betterx.bclib.BCLib;
import org.betterx.bclib.api.v3.datagen.AdvancementDataProvider;
import org.betterx.worlds.together.WorldsTogether;

import java.util.List;

public class BCLAdvancementDataProvider extends AdvancementDataProvider {
    public BCLAdvancementDataProvider() {
        super(List.of(BCLib.MOD_ID, WorldsTogether.MOD_ID));
    }

    @Override
    protected void bootstrap() {

    }
}
