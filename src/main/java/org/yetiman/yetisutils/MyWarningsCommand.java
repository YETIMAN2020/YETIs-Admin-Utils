package org.yetiman.yetisutils;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class MyWarningsCommand implements CommandExecutor {
    private final WarningHandler warningHandler;

    public MyWarningsCommand(WarningHandler warningHandler) {
        this.warningHandler = warningHandler;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("Only players can use this command.");
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

                if (reason != null && issuer != null && date != null) {
                    player.sendMessage((i + 1) + ". Reason: " + reason + " - Issued by: " + issuer + " - Date: " + date);
                } else {
                    player.sendMessage((i + 1) + ". Reason: " + reason + " - Some information is missing.");
                }
            }
        }
        return true;
    }
}
