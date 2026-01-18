package org.betterx.bclib.client.textures;

import net.minecraft.client.renderer.texture.atlas.sources.DirectoryLister;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class SpriteLister extends DirectoryLister {
    public SpriteLister(String string) {
        super(string, string + "/");
    }
}
