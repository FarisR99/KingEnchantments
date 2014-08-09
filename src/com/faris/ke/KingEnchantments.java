package com.faris.ke;

import com.faris.ke.enchantment.KingEnchantment;
import com.faris.ke.enchantment.enchantments.EnchantmentGlow;
import com.faris.ke.enchantment.enchantments.EnchantmentPotion;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffectType;

/**
 * Enchantment plugin.
 *
 * @author KingFaris10
 */
public class KingEnchantments extends JavaPlugin {
    private static KingEnchantments instance;

    public static KingEnchantment ENCHANTMENT_GLOW = null;
    public static KingEnchantment ENCHANTMENT_POISON = null;
    public static KingEnchantment ENCHANTMENT_WITHER = null;

    public void onEnable() {
        instance = this;

        if (this.registerDefaultEnchantments())
            this.getServer().getPluginManager().registerEvents(new EventListener(), this);
    }

    public void onDisable() {
        for (KingEnchantment registeredEnchantment : EnchantmentAPI.getEnchantments()) {
            try {
                EnchantmentAPI.unregisterEnchantment(registeredEnchantment);
            } catch (Exception ex) {
                ex.printStackTrace();
                continue;
            }
        }
        EnchantmentAPI.clearEnchantments();
    }

    protected boolean registerDefaultEnchantments() {
        try {
            ENCHANTMENT_GLOW = EnchantmentAPI.registerEnchantment(this.getLogger(), new EnchantmentGlow());
            ENCHANTMENT_POISON = EnchantmentAPI.registerEnchantment(this.getLogger(), new EnchantmentPotion(PotionEffectType.POISON));
            ENCHANTMENT_WITHER = EnchantmentAPI.registerEnchantment(this.getLogger(), new EnchantmentPotion(PotionEffectType.WITHER));
            return true;
        } catch (Exception ex) {
            ex.printStackTrace();
            ENCHANTMENT_GLOW = null;
            ENCHANTMENT_POISON = null;
            ENCHANTMENT_WITHER = null;
            EnchantmentAPI.clearEnchantments();
            return false;
        }
    }

    public static KingEnchantments getInstance() {
        return instance;
    }
}
