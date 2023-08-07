package com.github.zly2006.cbmv.bukkit;

import com.destroystokyo.paper.entity.villager.Reputation;
import com.destroystokyo.paper.entity.villager.ReputationType;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Villager;
import org.bukkit.entity.ZombieVillager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPotionEffectEvent;
import org.bukkit.event.entity.EntityTransformEvent;

public class PaperListener implements Listener {
    final ComeBackMyVillagers plugin;
    public PaperListener(ComeBackMyVillagers plugin) {
        this.plugin = plugin;
        ReputationType.MAJOR_POSITIVE.name(); // ensure class is loaded
    }
    @EventHandler
    // see: https://hub.spigotmc.org/stash/projects/SPIGOT/repos/craftbukkit/browse/nms-patches/net/minecraft/world/entity/monster/EntityZombieVillager.patch#11,72
    public void onFinishCure(EntityPotionEffectEvent event) {
        if (event.getCause() == EntityPotionEffectEvent.Cause.CONVERSION) {

        }
    }

    @EventHandler
    public void onCure(EntityTransformEvent event) {
        if (event.getTransformReason() == EntityTransformEvent.TransformReason.CURED) {
            if (event.getEntity() instanceof ZombieVillager zombieVillager && event.getTransformedEntity() instanceof Villager) {
                final OfflinePlayer conversionPlayer = zombieVillager.getConversionPlayer();
                if (conversionPlayer != null) {
                    zombieVillager.setConversionPlayer(null); // cancel vanilla curing
                    plugin.getServer().getScheduler().runTaskLater(plugin, () -> {
                        Villager villager = (Villager) event.getEntity().getWorld().getEntity(event.getTransformedEntity().getUniqueId());
                        if (villager != null) {
                            Reputation reputation = villager.getReputation(conversionPlayer.getUniqueId());
                            if (reputation != null) {
                                reputation = new Reputation();
                            }
                            assert reputation != null;
                            reputation.setReputation(ReputationType.MAJOR_POSITIVE, reputation.getReputation(ReputationType.MAJOR_POSITIVE) + 20);
                            villager.setReputation(conversionPlayer.getUniqueId(), reputation);
                        }
                    }, 1);
                }
            }
        }
    }
}
