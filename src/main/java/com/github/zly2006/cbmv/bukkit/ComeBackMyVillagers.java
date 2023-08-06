package com.github.zly2006.cbmv.bukkit;

import com.google.common.collect.ImmutableMap;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Villager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.VillagerAcquireTradeEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.MerchantRecipe;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

public class ComeBackMyVillagers extends JavaPlugin implements Listener {
    static UUID lastModified = null;
    static int lastLevel = 0;
    static int lastChoice = 0;
    boolean curingListenerRegistered = false;

    static final ImmutableMap<Integer, TradeOfferFactory[]> LIBRARIAN_OFFERS = new ImmutableMap.Builder<Integer, TradeOfferFactory[]>()
            .put(1, new TradeOfferFactory[]{
                    new TradeOfferBuyItemFactory(Material.PAPER, 24, 16, 2),
                    new TradeOfferEnchantBookFactory(1),
                    new TradeOfferSellItemFactory(Material.BOOKSHELF, 9, 1, 1)
            })
            .put(2, new TradeOfferFactory[]{
                    new TradeOfferBuyItemFactory(Material.BOOK, 4, 12, 10),
                    new TradeOfferEnchantBookFactory(5),
                    new TradeOfferSellItemFactory(Material.LANTERN, 1, 1, 5)
            })
            .put(3, new TradeOfferFactory[]{
                    new TradeOfferBuyItemFactory(Material.INK_SAC, 5, 12, 20),
                    new TradeOfferEnchantBookFactory(10),
                    new TradeOfferSellItemFactory(Material.GLASS, 1, 4, 10)
            })
            .put(4, new TradeOfferFactory[]{
                    new TradeOfferBuyItemFactory(Material.WRITABLE_BOOK, 2, 12, 30),
                    new TradeOfferEnchantBookFactory(15),
                    new TradeOfferSellItemFactory(Material.CLOCK, 5, 1, 15),
                    new TradeOfferSellItemFactory(Material.COMPASS, 4, 1, 15)
            })
            .put(5, new TradeOfferFactory[]{
                    new TradeOfferSellItemFactory(Material.NAME_TAG, 20, 1, 30)
            }).build();
    @Override
    public void onEnable() {
        getServer().getPluginManager().registerEvents(this, this);
        try {
            getServer().getPluginManager().registerEvents(new PaperListener(), this);
            curingListenerRegistered = true;
        } catch (NoClassDefFoundError e) {
            // if failed to load class, a runtime error will be thrown
            getLogger().warning("Failed to load event handler for curing villagers. This requires Paper API. If your server is not Paper, you can only get the only trading offers back.");
        }
    }

    @EventHandler
    public void onOpJoin(PlayerJoinEvent event) {
        if (event.getPlayer().isOp()) {
            event.getPlayer().sendMessage("§c[come-back-my-villagers] Failed to load event handler for curing villagers. This requires Paper API. If your server is not Paper, you can only get the only trading offers back.\nFor more info, please see https://github.com/zly2006/come-back-my-villagers/blob/master/bukkit.md");
        }
    }

    @EventHandler
    public void onNewTrade(VillagerAcquireTradeEvent event) {
        if (event.getEntity() instanceof Villager villager) {
            Villager.Profession profession = villager.getProfession();
            if (profession == Villager.Profession.LIBRARIAN) {
                int level = villager.getVillagerLevel();
                TradeOfferFactory[] factories = LIBRARIAN_OFFERS.get(level);
                if (factories != null) {
                    Random random = new Random();
                    int index = random.nextInt(factories.length);
                    if (lastModified == villager.getUniqueId() && lastLevel == level) {
                        while (index == lastChoice) {
                            index = random.nextInt(factories.length);
                        }
                    }
                    lastChoice = index;
                    TradeOfferFactory factory = factories[index];
                    event.setRecipe(factory.create(random));
                    lastModified = villager.getUniqueId();
                    lastLevel = level;
                }
            }
        }
    }

    private interface TradeOfferFactory {
        MerchantRecipe create(Random random);
    }

    private record TradeOfferBuyItemFactory(Material buy, int count, int maxUses,
                                            int experience) implements TradeOfferFactory {
        @Override
        public MerchantRecipe create(Random random) {
            MerchantRecipe recipe = new MerchantRecipe(new ItemStack(Material.EMERALD), 0, maxUses, true, experience, 0.05f);
            recipe.addIngredient(new ItemStack(buy, count));
            return recipe;
        }
    }

    private record TradeOfferSellItemFactory(Material sell, int price, int count,
                                             int experience) implements TradeOfferFactory {

        @Override
        public MerchantRecipe create(Random random) {
            MerchantRecipe recipe = new MerchantRecipe(new ItemStack(sell, count), 0, 12, true, experience, 0.05f);
            recipe.addIngredient(new ItemStack(Material.EMERALD, price));
            return recipe;
        }
    }

    private record TradeOfferEnchantBookFactory(int experience) implements TradeOfferFactory {
        static ArrayList<Enchantment> list = new ArrayList<>(List.of(Enchantment.values()));
        static {
            list.remove(Enchantment.SOUL_SPEED);
            list.remove(Enchantment.SWIFT_SNEAK);
        }
        @Override
        public MerchantRecipe create(Random random) {
            Enchantment enchantment = list.get(random.nextInt(list.size()));
            int i = enchantment.getStartLevel();
            int j = enchantment.getMaxLevel();
            int k = random.nextInt(i, j + 1);
            ItemStack itemStack = new ItemStack(Material.ENCHANTED_BOOK);
            EnchantmentStorageMeta itemMeta = (EnchantmentStorageMeta) itemStack.getItemMeta();
            itemMeta.addStoredEnchant(enchantment, k, false);
            itemStack.setItemMeta(itemMeta);

            int l = 2 + random.nextInt(5 + k * 10) + 3 * k;
            if (enchantment.isTreasure()) {
                l *= 2;
            }

            if (l > 64) {
                l = 64;
            }

            MerchantRecipe recipe = new MerchantRecipe(itemStack, 0, 12, true, experience, 0.05f);
            recipe.addIngredient(new ItemStack(Material.EMERALD, l));
            recipe.addIngredient(new ItemStack(Material.BOOK));
            return recipe;
        }
    }
}
