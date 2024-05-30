package org.yetiman.yetisutils.Reportfeatures;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

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
            sender.sendMessage(ChatColor.RED + "Only players can use this command.");
            return true;
        }

        if (args.length < 1) {
            sender.sendMessage(ChatColor.RED + "Usage: /report <issue>");
            return true;
        }

        Player player = (Player) sender;
        String issue = String.join(" ", args);
        Location location = player.getLocation();
        reportHandler.addReport(player, issue, location);
        sender.sendMessage(ChatColor.GREEN + "Report submitted. Thank you!");
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        return Collections.emptyList(); // No tab completion needed for the report command
    }
}
