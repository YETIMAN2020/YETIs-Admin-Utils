package org.yetiman.yetisutils;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

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

        if (args.length == 0) {
            sender.sendMessage("Usage: /report <issue>");
            return true;
        }

        Player player = (Player) sender;
        UUID playerId = player.getUniqueId();
        String playerName = player.getName();
        String issue = String.join(" ", args);

        reportHandler.addReport(playerId, playerName, issue);
        sender.sendMessage("Your report has been submitted.");

        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        return Collections.emptyList();
    }
}
