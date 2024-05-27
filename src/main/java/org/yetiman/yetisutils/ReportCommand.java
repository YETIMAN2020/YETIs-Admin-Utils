package org.yetiman.yetisutils;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.Location;

import java.util.Collections;
import java.util.List;

public class ReportCommand implements CommandExecutor, TabCompleter {
    private final ReportHandler reportHandler;

    public ReportCommand(ReportHandler reportHandler) {
        this.reportHandler = reportHandler;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("Only players can use this command.");
            return true;
        }

        Player player = (Player) sender;
        if (args.length == 0) {
            player.sendMessage("Usage: /report <issue>");
            return true;
        }

        String issue = String.join(" ", args);
        Location location = player.getLocation();
        reportHandler.addReport(player, issue, location);
        player.sendMessage("Your report has been submitted.");
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        // Return an empty list to disable tab completion for arguments
        return Collections.emptyList();
    }
}
