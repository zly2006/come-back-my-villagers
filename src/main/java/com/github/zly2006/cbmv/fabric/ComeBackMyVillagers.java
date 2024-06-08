package com.github.zly2006.cbmv.fabric;

import com.google.gson.Gson;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class ComeBackMyVillagers implements ModInitializer {
    static Path configPath = FabricLoader.getInstance().getConfigDir().resolve("cbmv.json");
    public static Settings settings;
    public static final TagKey<Enchantment> villagerPossibleTag = TagKey.of(RegistryKeys.ENCHANTMENT, Identifier.of("cbmv:villager_possible_enchantments"));

    public static void saveSettings() throws IOException {
        Files.writeString(configPath, new Gson().toJson(settings));
    }

    @Override
    public void onInitialize() {

    }

    static {
        try {
            settings = new Gson().fromJson(new FileReader(configPath.toFile()), Settings.class);
        } catch (FileNotFoundException e) {
            settings = new Settings();
            try {
                Files.writeString(configPath, new Gson().toJson(settings));
            } catch (IOException ignored) {
            }
            e.printStackTrace();
        }
    }
}
