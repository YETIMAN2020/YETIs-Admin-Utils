package org.yetiman.yetisutils.Reportfeatures;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class ReportNotification implements Listener {
    private ReportHandler reportHandler;

    public ReportNotification(ReportHandler reportHandler) {
        this.reportHandler = reportHandler;
        Bukkit.getPluginManager().registerEvents(this, reportHandler.getPlugin());
    }

    // Notify admins about a new report submission
    public void notifyNewReport(String playerName, String issue) {
        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            if (onlinePlayer.hasPermission("yetisutils.admin")) {
                onlinePlayer.sendMessage(ChatColor.GREEN + "New report submitted by " + ChatColor.RED + playerName + ChatColor.GREEN + ": " + ChatColor.YELLOW + issue);
            }
        }
    }

    // Notify admins about open reports when they log in
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        if (player.hasPermission("yetisutils.admin")) {
            int openReportsCount = reportHandler.getOpenReportsCount();
            player.sendMessage(ChatColor.GREEN + "There are currently " + ChatColor.RED + openReportsCount + ChatColor.GREEN + " open reports.");
        }
    }
}
