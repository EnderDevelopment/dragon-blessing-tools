package com.enderdevelopment.plugin;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

public class DragonBlessingCommand implements CommandExecutor {

    private final JavaPlugin plugin;

    public DragonBlessingCommand(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command,
                             @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(Component.text("Only players can receive the dragon blessing.").color(NamedTextColor.RED));
            return true;
        }

        Player target = player;
        if (args.length > 0) {
            Player found = plugin.getServer().getPlayerExact(args[0]);
            if (found != null) {
                target = found;
            } else {
                player.sendMessage(Component.text("Player not found: " + args[0]).color(NamedTextColor.RED));
                return true;
            }
        }

        // Bless each tool and give it
        for (DragonToolType type : DragonToolType.values()) {
            ItemStack tool = type.create(plugin);
            target.getInventory().addItem(tool);
        }

        target.sendMessage(Component.text("The ancient dragons have blessed you with their tools!").color(NamedTextColor.GOLD));
        if (target != player) {
            player.sendMessage(Component.text("You blessed " + target.getName() + " with the dragon tools.").color(NamedTextColor.GOLD));
        }
        return true;
    }
}
