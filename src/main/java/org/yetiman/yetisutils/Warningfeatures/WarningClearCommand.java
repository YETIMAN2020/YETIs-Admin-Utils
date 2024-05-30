package org.yetiman.yetisutils.Warningfeatures;

import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.yetiman.yetisutils.YETIsUtils;

import java.util.UUID;

public class WarningClearCommand implements CommandExecutor {
    private final WarningHandler warningHandler;

    public WarningClearCommand(WarningHandler warningHandler) {
        this.warningHandler = warningHandler;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length < 2) {
            sender.sendMessage(ChatColor.RED + "Usage: /warnclear <player> <warning number or *>");
            return true;
        }
        OfflinePlayer target = YETIsUtils.getInstance().getServer().getOfflinePlayer(args[0]);
        if (target == null || !target.hasPlayedBefore()) {
            sender.sendMessage(ChatColor.RED + "Player not found.");
            return true;
        }
        UUID playerUUID = target.getUniqueId();
        if (args[1].equals("*")) {
            warningHandler.clearWarnings(playerUUID);
            sender.sendMessage(ChatColor.GREEN + "All warnings for " + ChatColor.WHITE + target.getName() + ChatColor.GREEN + " have been cleared.");
        } else {
            try {
                int warningNumber = Integer.parseInt(args[1]);
                if (warningHandler.removeWarning(playerUUID, warningNumber - 1)) {
                    sender.sendMessage(ChatColor.GREEN + "Warning " + warningNumber + " for " + ChatColor.WHITE + target.getName() + ChatColor.GREEN + " has been cleared.");
                } else {
                    sender.sendMessage(ChatColor.RED + "Warning number " + warningNumber + " not found for " + target.getName() + ".");
                }
            } catch (NumberFormatException e) {
                sender.sendMessage(ChatColor.RED + "Invalid warning number: " + args[1]);
            }
        }
        return true;
    }
}
