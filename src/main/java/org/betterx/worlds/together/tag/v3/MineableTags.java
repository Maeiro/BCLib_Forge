package org.betterx.worlds.together.tag.v3;

import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;


public class MineableTags {
    public static final TagKey<Block> AXE = BlockTags.MINEABLE_WITH_AXE;
    public static final TagKey<Block> HOE = BlockTags.MINEABLE_WITH_HOE;
    public static final TagKey<Block> PICKAXE = BlockTags.MINEABLE_WITH_PICKAXE;
    public static final TagKey<Block> SHEARS = TagManager.BLOCKS.makeCommonTag("mineable/shears");
    public static final TagKey<Block> SHOVEL = BlockTags.MINEABLE_WITH_SHOVEL;
    public static final TagKey<Block> SWORD = BlockTags.SWORD_EFFICIENT;
    public static final TagKey<Block> HAMMER = TagManager.BLOCKS.makeCommonTag("mineable/hammer");

    @Deprecated(forRemoval = true)
    public static final TagKey<Block> NEEDS_NETHERITE_TOOL = TagManager.BLOCKS.makeTag("forge", "needs_netherite_tool");

    static void prepareTags() {
    }

    public static TagKey<Block> getRequiredToolTag(int miningLevel) {
        return switch (miningLevel) {
            case 1 -> BlockTags.NEEDS_STONE_TOOL;
            case 2 -> BlockTags.NEEDS_IRON_TOOL;
            case 3 -> BlockTags.NEEDS_DIAMOND_TOOL;
            case 4 -> NEEDS_NETHERITE_TOOL;
            default -> null;
        };
    }
}
