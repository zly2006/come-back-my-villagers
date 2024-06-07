package com.github.zly2006.cbmv.fabric;

import net.fabricmc.api.ModInitializer;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;

public class ComeBackMyVillagers implements ModInitializer {
    public static final TagKey<Enchantment> villagerPossibleTag = TagKey.of(RegistryKeys.ENCHANTMENT, Identifier.of("cbmv:villager_possible_enchantments"));
    @Override
    public void onInitialize() {

    }
}
