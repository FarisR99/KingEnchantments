package com.faris.ke.enchantment;

import com.faris.ke.EnchantmentAPI;
import com.faris.ke.KingEnchantments;
import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.enchantments.EnchantmentTarget;
import org.bukkit.enchantments.EnchantmentWrapper;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;

import java.util.Random;

/**
 * Enchantment wrapper.
 *
 * @author KingFaris10
 */
public abstract class KingEnchantment extends EnchantmentWrapper implements Listener {
    protected final static Random random = new Random();
    protected int startingLevel = 1, maxLevel = 5;

    public KingEnchantment(int enchantmentID, int maxLevel) {
        this(enchantmentID, 1, maxLevel);
    }

    public KingEnchantment(int enchantmentID, int startingLevel, int maxLevel) {
        super(enchantmentID);
        if (enchantmentID > 256) {
            throw new IllegalArgumentException("The enchantment ID has to be lower then 256!");
        }

        this.startingLevel = startingLevel;
        this.maxLevel = maxLevel;

        this.registerListener();
    }

    public abstract boolean canEnchantItem(ItemStack itemStack);

    /**
     * Get the experience level required for an enchanted book to be applied.
     *
     * @param enchantmentLevel - The level of the enchantment.
     * @return The experience level required. If -1, can't be applied.
     */
    public int getAnvilLevels(int enchantmentLevel) {
        return -1;
    }

    public abstract EnchantmentTarget getItemTarget();

    public int getMaxLevel() {
        return this.maxLevel;
    }

    public abstract String getName();

    public int getStartLevel() {
        return this.startingLevel;
    }

    public boolean hasNBTTag() {
        return true;
    }

    public KingEnchantment setMaxLevel(int maxLevel) {
        if (maxLevel >= this.startingLevel) this.maxLevel = maxLevel;
        return this;
    }

    public KingEnchantment setStartingLevel(int startingLevel) {
        if (startingLevel > 0) this.startingLevel = startingLevel;
        return this;
    }

    public KingEnchantment registerListener() {
        Bukkit.getServer().getPluginManager().registerEvents(this, KingEnchantments.getInstance());
        return this;
    }

    public KingEnchantment unregisterListener() {
        HandlerList.unregisterAll(this);
        return this;
    }

    @EventHandler
    public void onEntityDamaged(EntityDamageEvent event) {
        try {
            if (event instanceof EntityDamageByEntityEvent) {
                EntityDamageByEntityEvent eEvent = (EntityDamageByEntityEvent) event;
                if (event.getEntity() instanceof LivingEntity) {
                    if (EnchantmentAPI.hasEnchantment(event.getEntity(), this)) {
                        double newDamage = event.getDamage();
                        boolean isCancelled = false;
                        LivingEntity livingEntity = (LivingEntity) event.getEntity();
                        EntityEquipment entityInventory = livingEntity.getEquipment();
                        for (ItemStack armour : entityInventory.getArmorContents()) {
                            if (armour != null && armour.containsEnchantment(this)) {
                                Object[] damageEvent = this.onDamaged(livingEntity, eEvent.getDamager(), newDamage, armour, armour.getEnchantmentLevel(this), this.canEnchantItem(armour));
                                isCancelled = Boolean.parseBoolean(damageEvent[0].toString());
                                newDamage = Double.parseDouble(damageEvent[1].toString());
                            }
                        }
                        event.setDamage(newDamage > 0D ? newDamage : 0D);
                        if (isCancelled) event.setCancelled(true);
                    }
                }
                if (eEvent.getEntity() != null && eEvent.getDamager() instanceof LivingEntity) {
                    if (EnchantmentAPI.hasEnchantment(eEvent.getDamager(), this)) {
                        LivingEntity dealer = (LivingEntity) eEvent.getDamager();
                        ItemStack itemInHand = dealer.getEquipment().getItemInHand();
                        if (itemInHand != null && itemInHand.containsEnchantment(this)) {
                            Object[] damageEvent = this.onHit(dealer, event.getEntity(), event.getDamage(), itemInHand, itemInHand.getEnchantmentLevel(this), this.canEnchantItem(itemInHand));
                            boolean isCancelled = Boolean.parseBoolean(damageEvent[0].toString());
                            double newDamage = Double.parseDouble(damageEvent[1].toString());
                            event.setDamage(newDamage > 0D ? newDamage : 0D);
                            if (isCancelled) event.setCancelled(true);
                        }
                    }
                }
            } else {
                if (event.getEntity() instanceof LivingEntity) {
                    if (EnchantmentAPI.hasEnchantment(event.getEntity(), this)) {
                        double newDamage = event.getDamage();
                        boolean isCancelled = false;
                        LivingEntity livingEntity = (LivingEntity) event.getEntity();
                        EntityEquipment entityInventory = livingEntity.getEquipment();
                        for (ItemStack armour : entityInventory.getArmorContents()) {
                            if (armour != null && armour.containsEnchantment(this)) {
                                Object[] damageEvent = this.onDamaged(livingEntity, null, newDamage, armour, armour.getEnchantmentLevel(this), this.canEnchantItem(armour));
                                isCancelled = Boolean.parseBoolean(damageEvent[0].toString());
                                newDamage = Double.parseDouble(damageEvent[1].toString());
                            }
                        }
                        event.setDamage(newDamage > 0D ? newDamage : 0D);
                        if (isCancelled) event.setCancelled(true);
                    }
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @EventHandler
    public void onPlayerBreakBlock(BlockBreakEvent event) {
        try {
            if (event.getBlock() != null) {
                ItemStack itemInHand = event.getPlayer().getInventory().getItemInHand();
                if (itemInHand != null && itemInHand.containsEnchantment(this)) {
                    Object[] blockBreakEvent = this.onBlockBreak(event.getPlayer(), event.getBlock(), event.getExpToDrop(), itemInHand, itemInHand.getEnchantmentLevel(this), this.canEnchantItem(itemInHand));
                    if (Boolean.parseBoolean(blockBreakEvent[0].toString())) {
                        event.setCancelled(true);
                    } else {
                        event.setExpToDrop(Integer.parseInt(blockBreakEvent[1].toString()));
                    }
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        try {
            if (event.getItem() != null && event.getItem().containsEnchantment(this)) {
                if (this.onInteract(event.getAction(), event.getPlayer(), event.getItem(), event.getItem().getEnchantmentLevel(this), this.canEnchantItem(event.getItem())))
                    event.setCancelled(true);
                if (this.onInteract(event.getAction(), event.getClickedBlock(), event.getPlayer(), event.getItem(), event.getItem().getEnchantmentLevel(this), this.canEnchantItem(event.getItem())))
                    event.setCancelled(true);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @EventHandler
    public void onPlayerInteractEntity(PlayerInteractEntityEvent event) {
        try {
            ItemStack itemInHand = event.getPlayer().getInventory().getItemInHand();
            if (itemInHand != null && itemInHand.containsEnchantment(this)) {
                if (this.onInteractEntity(event.getPlayer(), event.getRightClicked(), itemInHand, itemInHand.getEnchantmentLevel(this), this.canEnchantItem(itemInHand)))
                    event.setCancelled(true);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Called when a block is broken with an item with this enchantment.
     *
     * @param player - The player.
     * @param block - The block.
     * @param droppedExp - The EXP dropped.
     * @param itemStack - The item.
     * @param enchantmentLevel - The enchantment's level.
     * @param canEnchant - If the item can have this enchantment.
     * @return An array containing if the event should be cancelled and the new dropped EXP.
     */
    protected Object[] onBlockBreak(Player player, Block block, int droppedExp, ItemStack itemStack, int enchantmentLevel, boolean canEnchant) {
        return new Object[]{false, droppedExp};
    }

    /**
     * Called when an armoured entity with this enchantment is hit.
     *
     * @param livingEntity - The living entity.
     * @param dealer - The entity that dealt this damage. (Can be null if there is no dealer)
     * @param damage - The damage dealt.
     * @param itemStack - The item.
     * @param enchantmentLevel - The enchantment's level.
     * @param canEnchant - If the item can have this enchantment.
     * @return An array containing if the event should be cancelled and the new damage.
     */
    protected Object[] onDamaged(LivingEntity livingEntity, Entity dealer, double damage, ItemStack itemStack, int enchantmentLevel, boolean canEnchant) {
        return new Object[]{false, damage};
    }

    /**
     * Called when a player enchants an item with this enchantment.
     *
     * @param player - The player.
     * @param itemStack - The item.
     * @param enchantmentLevel - The enchantment's level.
     * @return Whether the player can enchant the item or not.
     */
    public boolean onEnchant(Player player, ItemStack itemStack, int enchantmentLevel) {
        return true;
    }

    /**
     * Called when an entity with an item with this enchantment hits.
     *
     * @param dealer - The entity that damaged.
     * @param damaged - The entity that was damaged.
     * @param damage - The damage dealt.
     * @param itemStack - The item.
     * @param enchantmentLevel - The enchantment's level.
     * @param canEnchant - If the item can have this enchantment.
     * @return An array containing if the event should be cancelled and new damage.
     */
    protected Object[] onHit(LivingEntity dealer, Entity damaged, double damage, ItemStack itemStack, int enchantmentLevel, boolean canEnchant) {
        return new Object[]{false, damage};
    }

    /**
     * Called when a player with an item with this enchantment interacts.
     *
     * @param action - The action.
     * @param player - The player.
     * @param itemStack - The item.
     * @param enchantmentLevel - The enchantment's level.
     * @param canEnchant - If the item can have this enchantment.
     * @return
     */
    protected boolean onInteract(Action action, Player player, ItemStack itemStack, int enchantmentLevel, boolean canEnchant) {
        return false;
    }

    /**
     * Called when a player with an item with this enchantment interacts.
     *
     * @param action - The action.
     * @param clickedBlock - The block clicked. (Can be null if there is no block clicked)
     * @param player - The player.
     * @param itemStack - The item.
     * @param enchantmentLevel - The enchantment's level.
     * @param canEnchant - If the item can have this enchantment.
     * @return
     */
    protected boolean onInteract(Action action, Block clickedBlock, Player player, ItemStack itemStack, int enchantmentLevel, boolean canEnchant) {
        return false;
    }

    /**
     * Called when a player with an item with this enchantment right clicks an entity.
     *
     * @param player - The player.
     * @param clicked - The clicked entity.
     * @param itemStack - The item.
     * @param enchantmentLevel - The enchantment's level.
     * @param canEnchant - If the item can have this enchantment.
     * @return
     */
    protected boolean onInteractEntity(Player player, Entity clicked, ItemStack itemStack, int enchantmentLevel, boolean canEnchant) {
        return false;
    }
}
