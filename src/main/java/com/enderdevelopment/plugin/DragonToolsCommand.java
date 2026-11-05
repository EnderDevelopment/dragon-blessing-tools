package com.enderdevelopment.plugin;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public class DragonToolsCommand implements CommandExecutor {

    private final Main plugin;

    public DragonToolsCommand(Main plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command,
                             @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(Component.text("Only players can open the dragon tools menu.").color(NamedTextColor.RED));
            return true;
        }

        player.sendMessage(Component.text("======== Dragon Blessed Tools ========").color(NamedTextColor.DARK_RED));
        for (DragonToolType type : DragonToolType.values()) {
            ItemStack item = type.create(plugin);
            player.sendMessage(Component.text("- " + type.getDisplayName())
                    .color(NamedTextColor.GOLD)
                    .hoverEvent(item.asHoverEvent()));
        }
        player.sendMessage(Component.text("Use /dragonblessing to receive them.").color(NamedTextColor.GRAY));
        return true;
    }
}
