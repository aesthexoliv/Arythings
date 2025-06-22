package net.arclieq.arythings.util;

import net.arclieq.arythings.Arythings;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;

public class ModTags {

    public static class Blocks {
        public static final TagKey<Block> NEEDS_NETIAMOND_TOOL = createTag("needs_netiamond_tool");
        public static final TagKey<Block> INCORRECT_FOR_NETIAMOND_TOOL = createTag("incorrect_for_netiamond_tool");
        public static final TagKey<Block> NEEDS_LUZZANTUM_TOOL = createTag("needs_luzzantum_tool");
        public static final TagKey<Block> INCORRECT_FOR_LUZZANTUM_TOOL = createTag("incorrect_for_luzzantum_tool");
        public static final TagKey<Block> NEEDS_LUMIT_TOOL = createTag("needs_lumit_tool");
        public static final TagKey<Block> INCORRECT_FOR_LUMIT_TOOL = createTag("incorrect_for_lumit_tool");
        public static final TagKey<Block> NEEDS_MYTHRIL_TOOL = createTag("needs_mythril_tool");
        public static final TagKey<Block> INCORRECT_FOR_MYTHRIL_TOOL = createTag("incorrect_for_mythril_tool");

        private static TagKey<Block> createTag(String name) {
            return TagKey.of(RegistryKeys.BLOCK, Identifier.of(Arythings.MOD_ID, name));
        }
    
        
    }
    public static class Items {
        public static final TagKey<Item> TRANSFORMABLE_ITEMS = createTag("transformable_items");

        private static TagKey<Item> createTag(String name) {
            return TagKey.of(RegistryKeys.ITEM, Identifier.of(Arythings.MOD_ID, name));
        }
        
    }
    
}
