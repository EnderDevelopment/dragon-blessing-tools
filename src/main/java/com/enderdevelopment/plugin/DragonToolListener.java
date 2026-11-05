package com.enderdevelopment.plugin;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.concurrent.ThreadLocalRandom;

public class DragonToolListener implements Listener {

    private final JavaPlugin plugin;

    public DragonToolListener(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onEntityDamage(EntityDamageByEntityEvent event) {
        if (!(event.getDamager() instanceof Player player)) {
            return;
        }
        if (!(event.getEntity() instanceof LivingEntity target)) {
            return;
        }

        ItemStack held = player.getInventory().getItemInMainHand();
        DragonToolType type = DragonToolType.fromItem(held, plugin);
        if (type == null) {
            return;
        }

        switch (type) {
            case DRAGON_SWORD -> {
                // Sword: chance to ignite the target in dragon fire
                if (ThreadLocalRandom.current().nextDouble() < 0.3) {
                    target.setFireTicks(100);
                    target.getWorld().playSound(target.getLocation(), Sound.ENTITY_ENDER_DRAGON_GROWL, 1.0f, 1.0f);
                    target.getWorld().spawnParticle(Particle.FLAME, target.getEyeLocation(), 20, 0.5, 0.5, 0.5, 0.05);
                    if (target instanceof Player victim) {
                        victim.sendMessage(Component.text("The dragon's fire burns you!").color(NamedTextColor.RED));
                    }
                }
            }
            case DRAGON_AXE -> {
                // Axe: leech a little health back (dragon vitality)
                player.setHealth(Math.min(player.getHealth() + 1, player.getMaxHealth()));
                player.getWorld().spawnParticle(Particle.HEART, player.getEyeLocation(), 5, 0.3, 0.3, 0.3, 0.1);
            }
            default -> {
                // Other tools have no combat effect
            }
        }
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        ItemStack held = player.getInventory().getItemInMainHand();
        DragonToolType type = DragonToolType.fromItem(held, plugin);
        if (type == null) {
            return;
        }

        switch (type) {
            case DRAGON_PICKAXE -> {
                // Pickaxe: veins of ores drop more (dragon greed)
                Block block = event.getBlock();
                if (!block.getDrops(held).isEmpty()) {
                    double roll = ThreadLocalRandom.current().nextDouble();
                    if (roll < 0.35) {
                        // Drop an extra copy of the first drop
                        ItemStack extra = block.getDrops(held).iterator().next().clone();
                        block.getWorld().dropItemNaturally(block.getLocation(), extra);
                        player.getWorld().playSound(block.getLocation(), Sound.ENTITY_ENDER_DRAGON_FLAP, 0.6f, 1.2f);
                    }
                }
            }
            case DRAGON_AXE -> {
                // Axe: sometimes applies speed after breaking
                if (ThreadLocalRandom.current().nextDouble() < 0.25) {
                    player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 60, 0, true));
                }
            }
            default -> {
                // Nothing special
            }
        }
    }

    @EventHandler
    public void onProjectileHit(ProjectileHitEvent event) {
        Projectile projectile = event.getEntity();
        if (!(projectile.getShooter() instanceof Player player)) {
            return;
        }
        if (event.getHitEntity() == null) {
            return;
        }

        ItemStack held = player.getInventory().getItemInMainHand();
        DragonToolType type = DragonToolType.fromItem(held, plugin);
        if (type == DragonToolType.DRAGON_BOW) {
            // Bow: arrows leave a small burst of dragon fire
            Location hit = projectile.getLocation();
            hit.getWorld().createExplosion(hit, 1.2f, false, false, player);
            hit.getWorld().spawnParticle(Particle.DRAGON_BREATH, hit, 15, 0.5, 0.5, 0.5, 0.1);
            hit.getWorld().playSound(hit, Sound.ITEM_FIRECHARGE_USE, 1.0f, 0.7f);
        }
    }
}
