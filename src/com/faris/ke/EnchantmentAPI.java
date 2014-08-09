package com.faris.ke;

import com.faris.ke.enchantment.KingEnchantment;
import com.faris.ke.utils.NbtFactory;
import com.faris.ke.utils.ReflectionUtils;
import org.bukkit.command.defaults.EnchantCommand;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.enchantments.EnchantmentWrapper;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

/**
 * API for KingEnchantments.
 *
 * @author KingFaris10
 */
public class EnchantmentAPI {
    private static List<KingEnchantment> registeredEnchantments = new ArrayList<KingEnchantment>(); // Don't use reflection on me. D:

    /**
     * Add an enchantment to an item.
     *
     * @param itemStack - The item.
     * @param enchantment - The enchantment.
     * @param enchantmentLevel - The enchantment level.
     * @return The new enchanted item stack.
     * @throws Exception - May throw an exception due to reflection utilisation..
     */
    public static ItemStack addEnchantment(ItemStack itemStack, KingEnchantment enchantment, int enchantmentLevel) throws Exception {
        return addEnchantment(null, itemStack, enchantment, enchantmentLevel);
    }

    /**
     * Add an enchantment to an item.
     *
     * @param player - The player applying the enchantment.
     * @param itemStack - The item.
     * @param enchantment - The enchantment.
     * @param enchantmentLevel - The enchantment level.
     * @return The new enchanted item stack.
     * @throws Exception - May throw an exception due to reflection utilisation..
     */
    public static ItemStack addEnchantment(Player player, ItemStack itemStack, KingEnchantment enchantment, int enchantmentLevel) throws Exception {
        if (itemStack != null && enchantment != null) {
            if (itemStack.containsEnchantment(enchantment)) itemStack.removeEnchantment(enchantment);
            if (enchantment.onEnchant(player, itemStack, enchantmentLevel))
                itemStack.addUnsafeEnchantment(enchantment, enchantmentLevel);
            else return itemStack;

            if (enchantment.hasNBTTag()) {
                ItemStack craftItemStack = NbtFactory.getCraftItemStack(itemStack);
                NbtFactory.NbtCompound nbtCompound = NbtFactory.fromItemTag(craftItemStack);
                if (nbtCompound == null) nbtCompound = NbtFactory.createCompound();
                System.out.println(nbtCompound.toString());
                itemStack = craftItemStack;
            }
            return itemStack;
        }
        return itemStack;
    }

    /**
     * Do not use this.
     */
    public static void clearEnchantments() {
        registeredEnchantments.clear();
    }

    /**
     * Get a Bukkit Enchantment by the enchantment ID. (Returns custom enchantments too)
     *
     * @param enchantmentID - The enchantment ID.
     * @return The Bukkit Enchantment.
     */
    public static Enchantment getBukkitEnchantment(int enchantmentID) {
        Enchantment enchantment = Enchantment.getById(enchantmentID);
        if (enchantment == null) enchantment = getEnchantment(enchantmentID);
        return enchantment;
    }

    /**
     * Get a custom enchantment by the Bukkit Enchantment.
     *
     * @param enchantment - The Bukkit Enchantment.
     * @return The custom enchantment.
     */
    public static KingEnchantment getEnchantment(Enchantment enchantment) {
        return enchantment != null ? getEnchantment(enchantment.getId()) : null;
    }

    /**
     * Get a custom enchantment by the enchantment ID.
     *
     * @param enchantmentID - The enchantment ID.
     * @return The custom enchantment.
     */
    public static KingEnchantment getEnchantment(int enchantmentID) {
        for (KingEnchantment registeredEnchantment : registeredEnchantments) {
            if (registeredEnchantment != null && registeredEnchantment.getId() == enchantmentID)
                return registeredEnchantment;
        }
        return null;
    }

    /**
     * Get all the registered custom enchantments.
     *
     * @return All the registered custom enchantments.
     */
    public static List<KingEnchantment> getEnchantments() {
        return Collections.unmodifiableList(registeredEnchantments);
    }

    /**
     * Get the next enchantment ID that is not registered.
     *
     * @return The next free enchantment ID.
     */
    public static int getFreeEnchantmentID() {
        return getFreeEnchantmentID(256);
    }

    private static int getFreeEnchantmentID(int maxID) {
        for (int id = 1; id < maxID; id++) {
            if (getBukkitEnchantment(id) == null) return id;
        }
        return -1;
    }

    /**
     * Check if an entity has an item with an enchantment.
     *
     * @param entity - The entity.
     * @param kingEnchantment - The custom enchantment.
     * @return If the entity has an item with that enchantment.
     */
    public static boolean hasEnchantment(Entity entity, KingEnchantment kingEnchantment) {
        if (entity != null && kingEnchantment != null && entity instanceof LivingEntity) {
            LivingEntity livingEntity = (LivingEntity) entity;
            if (livingEntity instanceof Player) {
                Player player = (Player) livingEntity;
                for (ItemStack item : player.getInventory().getContents()) {
                    if (item != null && item.containsEnchantment(kingEnchantment)) return true;
                }
                for (ItemStack armour : player.getInventory().getArmorContents()) {
                    if (armour != null && armour.containsEnchantment(kingEnchantment)) return true;
                }
            } else {
                EntityEquipment entityEquipment = livingEntity.getEquipment();
                for (ItemStack armour : entityEquipment.getArmorContents()) {
                    if (armour != null && armour.containsEnchantment(kingEnchantment)) return true;
                }
            }
        }
        return false;
    }

    /**
     * Check if an enchantment is a custom enchantment.
     *
     * @param enchantment - The Bukkit enchantment.
     * @return True if the enchantment is a custom enchantment, false if not.
     */
    public static boolean isCustomEnchantment(Enchantment enchantment) {
        if (enchantment != null) {
            int enchantmentID = enchantment.getId();
            for (KingEnchantment registeredEnchantment : registeredEnchantments) {
                if (registeredEnchantment != null && enchantmentID == registeredEnchantment.getId()) return true;
            }
        }
        return false;
    }

    /**
     * Register a custom enchantment.
     *
     * @param pluginLogger - The plugin registering an enchantment's logger.
     * @param enchantment - The custom enchantment.
     * @return If the task was successful, return the enchantment, if not, return null.
     * @throws Exception - May throw an exception due to reflection utilisation.
     */
    public static KingEnchantment registerEnchantment(Logger pluginLogger, KingEnchantment enchantment) throws Exception {
        if (enchantment != null && !isCustomEnchantment(enchantment) && enchantment.getName() != null) {
            ReflectionUtils.FieldAccess enchantmentNamesField = ReflectionUtils.getField(EnchantCommand.class, "ENCHANTMENT_NAMES");
            List<String> enchantmentNames = (List<String>) enchantmentNamesField.getObject(null);
            if (enchantmentNames == null) enchantmentNames = new ArrayList<String>();
            if (!enchantmentNames.contains(enchantment.getName())) enchantmentNames.add(enchantment.getName());
            enchantmentNamesField.setFinal(enchantmentNames);

            ReflectionUtils.FieldAccess acceptingNewField = ReflectionUtils.getField(Enchantment.class, "acceptingNew");
            boolean wasAccepting = acceptingNewField.get(Boolean.class);
            acceptingNewField.set(true);
            EnchantmentWrapper.registerEnchantment(enchantment);
            acceptingNewField.set(wasAccepting);

            registeredEnchantments.add(enchantment);
            (pluginLogger != null ? pluginLogger : KingEnchantments.getInstance().getLogger()).info("Registered " + enchantment.getName() + " (" + enchantment.getId() + ") successfully.");
            return enchantment;
        } else {
            if (enchantment != null)
                KingEnchantments.getInstance().getLogger().warning("Failed to register " + enchantment.getName() + " (" + enchantment.getId() + ").");
            return null;
        }
    }

    /**
     * Unregister a custom enchantment by its ID.
     *
     * @param enchantmentID - The enchantment ID.
     * @return Whether the task was successful or not.
     * @throws Exception - May throw an exception due to reflection utilisation.
     */
    public static boolean unregisterEnchantment(int enchantmentID) throws Exception {
        return unregisterEnchantment(getEnchantment(enchantmentID));
    }

    /**
     * Unregister a custom enchantment.
     *
     * @param enchantment - The enchantment.
     * @return Whether the task was successful or not.
     * @throws Exception - May throw an exception due to reflection utilisation.
     */
    public static boolean unregisterEnchantment(KingEnchantment enchantment) throws Exception {
        if (enchantment != null && isCustomEnchantment(enchantment)) {
            enchantment.unregisterListener();
            unregisterOfficialEnchantment(enchantment);

            ReflectionUtils.FieldAccess enchantmentNamesField = ReflectionUtils.getField(EnchantCommand.class, "ENCHANTMENT_NAMES");
            List<String> enchantmentNames = (List<String>) enchantmentNamesField.get(List.class);
            if (enchantmentNames == null) enchantmentNames = new ArrayList<String>();
            enchantmentNames.remove(enchantment.getName());
            enchantmentNamesField.set(enchantmentNames);

            registeredEnchantments.remove(enchantment.getName());
            return true;
        } else {
            return true;
        }
    }

    private static void unregisterOfficialEnchantment(Enchantment enchantment) throws Exception {
        ReflectionUtils.FieldAccess byIdField = ReflectionUtils.getField(Enchantment.class, "byId");
        Map<Integer, Enchantment> byIdMap = byIdField.get(Map.class);
        byIdMap.remove(enchantment.getId());

        ReflectionUtils.FieldAccess byNameField = ReflectionUtils.getField(Enchantment.class, "byName");
        Map<String, Enchantment> byNameMap = byNameField.get(Map.class);
        byNameMap.remove(enchantment.getName());
    }
}
