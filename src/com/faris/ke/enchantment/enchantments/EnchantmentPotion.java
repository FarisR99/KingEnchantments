package com.faris.ke.enchantment.enchantments;

import com.faris.ke.EnchantmentAPI;
import com.faris.ke.enchantment.KingEnchantment;
import org.apache.commons.lang.WordUtils;
import org.bukkit.enchantments.EnchantmentTarget;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author KingFaris10
 */
public class EnchantmentPotion extends KingEnchantment {
    private List<PotionEffectType> potionEffects = new ArrayList<PotionEffectType>();

    public EnchantmentPotion(PotionEffectType... potionEffectTypes) {
        super(EnchantmentAPI.getFreeEnchantmentID(), 5);
        if (potionEffectTypes != null) this.potionEffects = Arrays.asList(potionEffectTypes);
    }

    @Override
    public boolean canEnchantItem(ItemStack itemStack) {
        return itemStack != null && itemStack.getType().toString().endsWith("_SWORD") && !itemStack.containsEnchantment(this);
    }

    @Override
    public EnchantmentTarget getItemTarget() {
        return EnchantmentTarget.WEAPON;
    }

    @Override
    public String getName() {
        return potionEffects.isEmpty() ? "Potion" : WordUtils.capitalizeFully(potionEffects.get(0).getName().toLowerCase().replace("_", " ")).replace(" ", "");
    }

    @Override
    protected Object[] onHit(LivingEntity dealer, Entity damaged, double damage, ItemStack itemStack, int enchantmentLevel, boolean canEnchant) {
        if (damaged instanceof LivingEntity) {
            LivingEntity damagedEntity = ((LivingEntity) damaged);
            for (PotionEffectType potionEffectType : this.potionEffects) {
                if (Math.random() < 0.1 * enchantmentLevel)
                    damagedEntity.addPotionEffect(new PotionEffect(potionEffectType, enchantmentLevel * 5 * 20, enchantmentLevel > this.maxLevel / 2 ? 1 : 0));
            }
        }
        return new Object[]{false, damage};
    }
}
