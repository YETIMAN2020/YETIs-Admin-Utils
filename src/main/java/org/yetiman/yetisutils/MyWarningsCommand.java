package org.yetiman.yetisutils;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class MyWarningsCommand implements CommandExecutor {
    private final WarningHandler warningHandler;
    private final YETIsUtils plugin;

    public MyWarningsCommand(WarningHandler warningHandler, YETIsUtils plugin) {
        this.warningHandler = warningHandler;
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("Only players can use this command.");
            return true;
        }

        if (!plugin.isEnablePlayerWarningsView()) {
            sender.sendMessage("Viewing your own warnings is currently disabled.");
            return true;
        }

        Player player = (Player) sender;
        int count = warningHandler.getWarnings(player.getUniqueId());

        if (count == 0) {
            player.sendMessage("You have no warnings.");
        } else {
            player.sendMessage("You have " + count + " warning(s):");
            for (int i = 0; i < count; i++) {
                String reason = warningHandler.getWarningReason(player.getUniqueId(), i);
                String issuer = warningHandler.getWarningIssuer(player.getUniqueId(), i);
                String date = warningHandler.getWarningDate(player.getUniqueId(), i);
                player.sendMessage((i + 1) + ". " + reason + " | Issued by: " + issuer + " | Date: " + date);
            }
        }
        return true;
    }
}
