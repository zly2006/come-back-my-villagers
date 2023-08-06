package com.github.zly2006.cbmv.bukkit;

import com.destroystokyo.paper.entity.villager.Reputation;
import com.destroystokyo.paper.entity.villager.ReputationType;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Villager;
import org.bukkit.entity.ZombieVillager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityTransformEvent;

public class PaperListener implements Listener {
    public PaperListener() {
        ReputationType.MAJOR_POSITIVE.name(); // ensure class is loaded
    }
    @EventHandler
    public void onCure(EntityTransformEvent event) {
        if (event.getTransformReason() == EntityTransformEvent.TransformReason.CURED) {
            if (event.getEntity() instanceof ZombieVillager zombieVillager && event.getTransformedEntity() instanceof Villager villager) {
                final OfflinePlayer conversionPlayer = zombieVillager.getConversionPlayer();
                if (conversionPlayer != null) {
                    conversionPlayer.getPlayer().sendMessage("§a你成功治愈了一名村民！");
                    Reputation reputation = villager.getReputation(conversionPlayer.getUniqueId());
                    if (reputation != null) {
                        reputation.setReputation(ReputationType.MAJOR_POSITIVE, 9990);
                    }
                }
            }
        }
    }
}
