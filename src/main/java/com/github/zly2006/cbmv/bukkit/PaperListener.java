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
    final ComeBackMyVillagers plugin;
    public PaperListener(ComeBackMyVillagers plugin) {
        this.plugin = plugin;
        ReputationType.MAJOR_POSITIVE.name(); // ensure class is loaded
    }
    @EventHandler
    public void onCure(EntityTransformEvent event) {
        if (event.getTransformReason() == EntityTransformEvent.TransformReason.CURED) {
            if (event.getEntity() instanceof ZombieVillager zombieVillager && event.getTransformedEntity() instanceof Villager) {
                final OfflinePlayer conversionPlayer = zombieVillager.getConversionPlayer();
                if (conversionPlayer != null) {
                    conversionPlayer.getPlayer().sendMessage("§a你成功治愈了一名村民！");
                    plugin.getServer().getScheduler().runTaskLater(plugin, () -> {
                        Villager villager = (Villager) event.getEntity().getWorld().getEntity(event.getTransformedEntity().getUniqueId());
                        int reputationValue = villager.getReputation(conversionPlayer.getUniqueId()).getReputation(ReputationType.MAJOR_POSITIVE);
                        conversionPlayer.getPlayer().sendMessage("§a你的声望为：" + reputationValue);
                        Reputation reputation = villager.getReputation(conversionPlayer.getUniqueId());
                        if (reputation != null) {
                            reputation = new Reputation();
                        }
                        if (villager.getReputation(conversionPlayer.getUniqueId()).getReputation(ReputationType.MAJOR_POSITIVE) >= 20) {
                            conversionPlayer.getPlayer().sendMessage("§a你的声望已经足够高了！");
                        }
                        reputation.setReputation(ReputationType.MAJOR_POSITIVE, 9990);
                        villager.setReputation(conversionPlayer.getUniqueId(), reputation);
                        System.out.println(villager.getReputation(conversionPlayer.getUniqueId()).getReputation(ReputationType.MAJOR_POSITIVE));
                        conversionPlayer.getPlayer().sendMessage("§a你的声望已经提升到了最高！");
                    }, 1);
                }
            }
        }
    }
}
