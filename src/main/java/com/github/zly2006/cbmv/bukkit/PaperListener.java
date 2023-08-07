package com.github.zly2006.cbmv.bukkit;

import com.destroystokyo.paper.entity.villager.Reputation;
import com.destroystokyo.paper.entity.villager.ReputationType;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Villager;
import org.bukkit.entity.ZombieVillager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityTransformEvent;

import java.lang.reflect.Field;

public class PaperListener implements Listener {
    final boolean useNms;
    final ComeBackMyVillagers plugin;

    void removeFinal(Field field) throws NoSuchFieldException, IllegalAccessException {
        Field modifiers = Field.class.getDeclaredField("modifiers");
        modifiers.setAccessible(true);
        modifiers.setInt(field, field.getModifiers() & ~java.lang.reflect.Modifier.FINAL);
    }

    public PaperListener(boolean useNms, ComeBackMyVillagers plugin) throws ClassNotFoundException, NoSuchFieldException, IllegalAccessException {
        this.useNms = useNms;
        this.plugin = plugin;
        ReputationType.MAJOR_POSITIVE.name(); // ensure class is loaded\
        if (useNms) {
            Class<?> reputationType = Class.forName("net.minecraft.world.entity.ai.gossip.ReputationType");
            Field name = reputationType.getDeclaredField("i");
            name.setAccessible(true);
            Field shareDecrement = reputationType.getDeclaredField("m");
            shareDecrement.setAccessible(true);
            removeFinal(shareDecrement);
            Field maxValue = reputationType.getDeclaredField("k");
            maxValue.setAccessible(true);
            removeFinal(maxValue);
            for (Object type : reputationType.getEnumConstants()) {
                if (name.get(type).equals("major_positive")) {
                    maxValue.setInt(type, 100);
                    shareDecrement.setInt(type, 100);
                }
                if (name.get(type).equals("minor_positive")) {
                    maxValue.setInt(type, 200);
                }
            }
            plugin.getLogger().info("Patched reputation types.");
        }
    }

    // see: https://hub.spigotmc.org/stash/projects/SPIGOT/repos/craftbukkit/browse/nms-patches/net/minecraft/world/entity/monster/EntityZombieVillager.patch#11,72
    @EventHandler
    public void onCure(EntityTransformEvent event) {
        if (!useNms && event.getTransformReason() == EntityTransformEvent.TransformReason.CURED) {
            if (event.getEntity() instanceof ZombieVillager zombieVillager && event.getTransformedEntity() instanceof Villager) {
                final OfflinePlayer conversionPlayer = zombieVillager.getConversionPlayer();
                if (conversionPlayer != null) {
                    zombieVillager.setConversionPlayer(null); // cancel vanilla curing
                    plugin.getServer().getScheduler().runTaskLater(plugin, () -> {
                        Villager villager = (Villager) event.getEntity().getWorld().getEntity(event.getTransformedEntity().getUniqueId());
                        if (villager != null) {
                            Reputation reputation = villager.getReputation(conversionPlayer.getUniqueId());
                            if (reputation == null) {
                                reputation = new Reputation();
                            }
                            reputation.setReputation(ReputationType.MAJOR_POSITIVE, reputation.getReputation(ReputationType.MAJOR_POSITIVE) + 20);
                            villager.setReputation(conversionPlayer.getUniqueId(), reputation);
                        }
                    }, 1);
                }
            }
        }
    }
}
