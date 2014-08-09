package com.faris.ke.enchantment.enchantments;

import com.faris.ke.EnchantmentAPI;
import com.faris.ke.enchantment.KingEnchantment;
import org.bukkit.enchantments.EnchantmentTarget;
import org.bukkit.inventory.ItemStack;

/**
 * @author KingFaris10
 */
public class EnchantmentGlow extends KingEnchantment {
    public EnchantmentGlow() {
        super(EnchantmentAPI.getFreeEnchantmentID(), 1);
    }

    @Override
    public boolean canEnchantItem(ItemStack itemStack) {
        return itemStack != null;
    }

    @Override
    public EnchantmentTarget getItemTarget() {
        return EnchantmentTarget.ALL;
    }

    @Override
    public String getName() {
        return "Glow";
    }

    public boolean hasNBTTag() {
        return false;
    }
}
