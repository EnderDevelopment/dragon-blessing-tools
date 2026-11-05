package com.enderdevelopment.plugin;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;

public enum DragonToolType {
    DRAGON_SWORD(
            Material.NETHERITE_SWORD,
            "Dragonfang Blade",
            "sword",
            "fire_aspect",
            2,
            "Fire aspect leaves burning wounds on your foes."
    ),
    DRAGON_PICKAXE(
            Material.NETHERITE_PICKAXE,
            "Dragonclaw Pickaxe",
            "pickaxe",
            "efficiency",
            5,
            "Mines stone and ores with dragon speed."
    ),
    DRAGON_AXE(
            Material.NETHERITE_AXE,
            "Dragonscale Axe",
            "axe",
            "sharpness",
            5,
            "Splits wood as easily as a dragon tears stone."
    ),
    DRAGON_BOW(
            Material.BOW,
            "Dragonbreath Bow",
            "bow",
            "flame",
            1,
            "Arrows are imbued with the draconic flame."
    );

    private final Material material;
    private final String displayName;
    private final String abilityKey;
    private final String enchantmentKey;
    private final int enchantmentLevel;
    private final String lore;

    DragonToolType(Material material, String displayName, String abilityKey,
                   String enchantmentKey, int enchantmentLevel, String lore) {
        this.material = material;
        this.displayName = displayName;
        this.abilityKey = abilityKey;
        this.enchantmentKey = enchantmentKey;
        this.enchantmentLevel = enchantmentLevel;
        this.lore = lore;
    }

    public Material getMaterial() {
        return material;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getAbilityKey() {
        return abilityKey;
    }

    public ItemStack create(JavaPlugin plugin) {
        ItemStack item = new ItemStack(material, 1);
        ItemMeta meta = item.getItemMeta();
        if (meta == null) {
            return item;
        }

        meta.displayName(Component.text(displayName).color(NamedTextColor.DARK_RED));
        List<Component> loreComponents = new ArrayList<>();
        loreComponents.add(Component.text("A tool blessed by the ancient dragons.").color(NamedTextColor.GOLD));
        loreComponents.add(Component.text("» " + lore).color(NamedTextColor.GRAY));
        meta.lore(loreComponents);

        // Mark as a dragon tool using persistent data
        meta.getPersistentDataContainer().set(
                new NamespacedKey(plugin, "dragon_blessing_tool"),
                PersistentDataType.STRING,
                abilityKey
        );

        // Add the enchantment
        Enchantment enchantment = Registry.ENCHANTMENT.get(NamespacedKey.minecraft(enchantmentKey));
        if (enchantment != null) {
            meta.addEnchant(enchantment, enchantmentLevel, true);
        }

        // Dragon blessed tools never break
        meta.setUnbreakable(true);

        item.setItemMeta(meta);
        return item;
    }

    public static boolean isDragonTool(ItemStack item, JavaPlugin plugin) {
        if (item == null || item.getType() == Material.AIR || !item.hasItemMeta()) {
            return false;
        }
        ItemMeta meta = item.getItemMeta();
        if (meta == null) {
            return false;
        }
        return meta.getPersistentDataContainer().has(
                new NamespacedKey(plugin, "dragon_blessing_tool"),
                PersistentDataType.STRING
        );
    }

    public static DragonToolType fromItem(ItemStack item, JavaPlugin plugin) {
        if (!isDragonTool(item, plugin)) {
            return null;
        }
        ItemMeta meta = item.getItemMeta();
        if (meta == null) {
            return null;
        }
        String key = meta.getPersistentDataContainer().get(
                new NamespacedKey(plugin, "dragon_blessing_tool"),
                PersistentDataType.STRING
        );
        for (DragonToolType type : values()) {
            if (type.abilityKey.equals(key)) {
                return type;
            }
        }
        return null;
    }
}
