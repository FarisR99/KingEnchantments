package com.faris.ke.utils;

import com.faris.ke.KingEnchantments;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class ItemUtils {
    public static ItemStack addLore(ItemStack itemStack, String line) {
        if (itemStack != null) {
            ItemMeta itemMeta = itemStack.getItemMeta();
            if (itemMeta != null) {
                List<String> lores = itemMeta.getLore();
                if (lores == null) lores = new ArrayList<String>();
                lores.add(KingEnchantments.replaceChatColours(line));
                itemMeta.setLore(lores);
                itemStack.setItemMeta(itemMeta);
            }
        }
        return itemStack;
    }

    public static ItemStack addLore(ItemStack itemStack, int lineIndex, String line) {
        if (itemStack != null) {
            ItemMeta itemMeta = itemStack.getItemMeta();
            if (itemMeta != null) {
                List<String> lores = itemMeta.getLore();
                if (lores == null) lores = new ArrayList<String>();
                while (lineIndex > lores.size()) {
                    if (lores.isEmpty()) lores.add("");
                    else lores.add(0, "");
                }
                if (lores.isEmpty()) lores.add(KingEnchantments.replaceChatColours(line));
                else lores.add(lineIndex, KingEnchantments.replaceChatColours(line));
                itemMeta.setLore(lores);
                itemStack.setItemMeta(itemMeta);
            }
        }
        return itemStack;
    }

    public static String getEnchantmentName(int enchantmentLevel) {
        if (enchantmentLevel < 1 || enchantmentLevel > 3999) return "." + enchantmentLevel;
        String s = "";
        while (enchantmentLevel >= 1000) {
            s += "M";
            enchantmentLevel -= 1000;
        }
        while (enchantmentLevel >= 900) {
            s += "CM";
            enchantmentLevel -= 900;
        }
        while (enchantmentLevel >= 500) {
            s += "D";
            enchantmentLevel -= 500;
        }
        while (enchantmentLevel >= 400) {
            s += "CD";
            enchantmentLevel -= 400;
        }
        while (enchantmentLevel >= 100) {
            s += "C";
            enchantmentLevel -= 100;
        }
        while (enchantmentLevel >= 90) {
            s += "XC";
            enchantmentLevel -= 90;
        }
        while (enchantmentLevel >= 50) {
            s += "L";
            enchantmentLevel -= 50;
        }
        while (enchantmentLevel >= 40) {
            s += "XL";
            enchantmentLevel -= 40;
        }
        while (enchantmentLevel >= 10) {
            s += "X";
            enchantmentLevel -= 10;
        }
        while (enchantmentLevel >= 9) {
            s += "IX";
            enchantmentLevel -= 9;
        }
        while (enchantmentLevel >= 5) {
            s += "V";
            enchantmentLevel -= 5;
        }
        while (enchantmentLevel >= 4) {
            s += "IV";
            enchantmentLevel -= 4;
        }
        while (enchantmentLevel >= 1) {
            s += "I";
            enchantmentLevel -= 1;
        }
        return s;
    }
}
