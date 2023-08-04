package com.github.zly2006.cbmv.bukkit;

import com.google.common.collect.ImmutableMap;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Villager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.VillagerAcquireTradeEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.MerchantRecipe;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class ComeBackMyVillagers extends JavaPlugin implements Listener {
    @Override
    public void onEnable() {
        getServer().getPluginManager().registerEvents(this, this);
    }

    @EventHandler
    public void onNewTrade(VillagerAcquireTradeEvent event) {
        if (event.getEntity() instanceof Villager villager) {
            Villager.Profession profession = villager.getProfession();
            if (profession == Villager.Profession.LIBRARIAN) {
                ImmutableMap.builder().
                        put(1, new TradeOfferFactory[]{
                                new TradeOfferBuyItemFactory(Material.PAPER, 24, 16, 2),
                                new TradeOfferEnchantBookFactory(1),
                                new TradeOfferSellItemFactory(Material.BOOKSHELF, 9, 12, 1)
                        })
                        .put(2, new TradeOfferFactory[]{
                                new TradeOfferBuyItemFactory(Material.BOOK, 4, 12, 10),
                                new TradeOfferEnchantBookFactory(5),
                                new TradeOfferSellItemFactory(Material.LANTERN, 1, 12, 5)
                        })
                        .put(3, new TradeOfferFactory[]{
                                new TradeOfferBuyItemFactory(Material.INK_SAC, 5, 12, 20),
                                new TradeOfferEnchantBookFactory(10),
                                new TradeOfferSellItemFactory(Material.GLASS, 4, 12, 10)
                        })
                        .put(4, new TradeOfferFactory[]{
                                new TradeOfferBuyItemFactory(Material.WRITABLE_BOOK, 2, 12, 30),
                                new TradeOfferEnchantBookFactory(15),
                                new TradeOfferSellItemFactory(Material.CLOCK, 5, 12, 15),
                                new TradeOfferSellItemFactory(Material.COMPASS, 4, 12, 15)
                        })
                        .put(5, new TradeOfferFactory[]{
                                new TradeOfferSellItemFactory(Material.NAME_TAG, 20, 12, 30)
                        }).build();

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

    private record TradeOfferSellItemFactory(Material sell, int price, int maxUses,
                                             int experience) implements TradeOfferFactory {

        @Override
        public MerchantRecipe create(Random random) {
            MerchantRecipe recipe = new MerchantRecipe(new ItemStack(Material.EMERALD, price), 0, maxUses, true, experience, 0.05f);
            recipe.addIngredient(new ItemStack(sell));
            return recipe;
        }
    }

    private record TradeOfferEnchantBookFactory(int experience) implements TradeOfferFactory {
        static List<Enchantment> list = Arrays.stream(Enchantment.values()).filter(x -> x.canEnchantItem(new ItemStack(Material.BOOK))).toList();
        @Override
        public MerchantRecipe create(Random random) {
            Enchantment enchantment = list.get(random.nextInt(list.size()));
            int i = enchantment.getStartLevel();
            int j = enchantment.getMaxLevel();
            System.out.println("i = " + i + ", j = " + j);
            int k = random.nextInt(i, j);
            ItemStack itemStack = new ItemStack(Material.ENCHANTED_BOOK);
            itemStack.addEnchantment(enchantment, k);
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
