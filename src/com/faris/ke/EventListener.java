package com.faris.ke;

import org.bukkit.enchantments.Enchantment;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Map;
import java.util.Random;

public class EventListener implements Listener {
    private static final Random random = new Random();

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        try {
            if (event.getAction() == Action.RIGHT_CLICK_AIR) {
                if (event.getItem() != null) {
                    if (!event.getItem().containsEnchantment(KingEnchantments.ENCHANTMENT_POISON) && KingEnchantments.ENCHANTMENT_POISON.canEnchantItem(event.getItem())) {
                        ItemStack item = EnchantmentAPI.addEnchantment(event.getPlayer(), event.getItem(), KingEnchantments.ENCHANTMENT_POISON, random.nextInt(KingEnchantments.ENCHANTMENT_POISON.getMaxLevel()) + 1);
                        event.getPlayer().getInventory().setItemInHand(item);
                    }
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerClickInventory(InventoryClickEvent event) {
        try {
            if (event.getInventory().getType() == InventoryType.ANVIL) {
                if (event.getCurrentItem() != null) {
                    Map<Enchantment, Integer> enchantmentMap = event.getCurrentItem().getEnchantments();
                    for (Enchantment enchantment : enchantmentMap.keySet()) {
                        if (EnchantmentAPI.isCustomEnchantment(enchantment)) {
                            event.setCancelled(true);
                            break;
                        }
                    }
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
