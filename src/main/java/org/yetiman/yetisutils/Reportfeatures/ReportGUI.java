package org.yetiman.yetisutils.Reportfeatures;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.yetiman.yetisutils.YETIsUtils;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

public class ReportGUI implements Listener {
    private final ReportHandler reportHandler;
    private final YETIsUtils plugin;
    private boolean eventsRegistered = false;

    public ReportGUI(YETIsUtils plugin, ReportHandler reportHandler) {
        this.reportHandler = reportHandler;
        this.plugin = plugin;
        if (!eventsRegistered) {
            Bukkit.getPluginManager().registerEvents(this, plugin);
            eventsRegistered = true;
        }
    }

    public void openReportMenu(Player player) {
        List<Map.Entry<String, Map<String, String>>> reports = reportHandler.getAllReports();
        reports.sort(Comparator.comparing(o -> o.getValue().get("date"))); // Sort by date

        Inventory reportMenu = Bukkit.createInventory(null, 54, "Reports");

        for (int i = 0; i < reports.size(); i++) {
            Map.Entry<String, Map<String, String>> reportEntry = reports.get(i);
            Map<String, String> report = reportEntry.getValue();
            ItemStack item = new ItemStack(Material.BOOK);
            ItemMeta meta = item.getItemMeta();
            if (meta != null) {
                meta.setDisplayName(ChatColor.YELLOW + "Report ID: " + reportEntry.getKey());
                meta.setLore(Arrays.asList(
                        ChatColor.GREEN + "Player: " + ChatColor.WHITE + report.get("player"),
                        ChatColor.RED + "Issue: " + ChatColor.WHITE + report.get("issue"),
                        ChatColor.BLUE + "Date: " + ChatColor.WHITE + report.get("date")
                ));
                item.setItemMeta(meta);
            }
            reportMenu.addItem(item);
        }

        player.openInventory(reportMenu);
    }

    public void openReportDetails(Player player, String reportId) {
        Map<String, String> report = reportHandler.getReport(reportId);
        if (report == null) {
            player.sendMessage(ChatColor.RED + "Report not found.");
            return;
        }

        Inventory reportDetails = Bukkit.createInventory(null, 27, "Report Details");

        ItemStack reportItem = new ItemStack(Material.PAPER);
        ItemMeta reportMeta = reportItem.getItemMeta();
        if (reportMeta != null) {
            reportMeta.setDisplayName(ChatColor.YELLOW + "Report ID: " + reportId);
            reportMeta.setLore(Arrays.asList(
                    ChatColor.GREEN + "Player: " + ChatColor.WHITE + report.get("player"),
                    ChatColor.RED + "Issue: " + ChatColor.WHITE + report.get("issue"),
                    ChatColor.BLUE + "Date: " + ChatColor.WHITE + report.get("date")
            ));
            reportItem.setItemMeta(reportMeta);
        }

        ItemStack backButton = new ItemStack(Material.BARRIER);
        ItemMeta backMeta = backButton.getItemMeta();
        if (backMeta != null) {
            backMeta.setDisplayName(ChatColor.RED + "Back");
            backButton.setItemMeta(backMeta);
        }

        ItemStack teleportButton = new ItemStack(Material.ENDER_PEARL);
        ItemMeta teleportMeta = teleportButton.getItemMeta();
        if (teleportMeta != null) {
            teleportMeta.setDisplayName(ChatColor.GREEN + "Teleport to Location");
            teleportButton.setItemMeta(teleportMeta);
        }

        ItemStack closeButton = new ItemStack(Material.RED_STAINED_GLASS_PANE);
        ItemMeta closeMeta = closeButton.getItemMeta();
        if (closeMeta != null) {
            closeMeta.setDisplayName(ChatColor.RED + "Close Report");
            closeButton.setItemMeta(closeMeta);
        }

        reportDetails.setItem(10, reportItem);
        reportDetails.setItem(12, teleportButton);
        reportDetails.setItem(14, closeButton);
        reportDetails.setItem(16, backButton);

        player.openInventory(reportDetails);
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player)) return;
        Player player = (Player) event.getWhoClicked();

        if (event.getView().getTitle().equals("Reports")) {
            event.setCancelled(true);
            ItemStack clickedItem = event.getCurrentItem();
            if (clickedItem == null || !clickedItem.hasItemMeta()) return;

            String reportId = clickedItem.getItemMeta().getDisplayName().replace(ChatColor.YELLOW + "Report ID: ", "");
            openReportDetails(player, reportId);
        } else if (event.getView().getTitle().equals("Report Details")) {
            event.setCancelled(true);
            ItemStack clickedItem = event.getCurrentItem();
            if (clickedItem == null || !clickedItem.hasItemMeta()) return;

            ItemMeta meta = clickedItem.getItemMeta();
            if (meta == null) return;

            String displayName = meta.getDisplayName();
            String reportId = event.getInventory().getItem(10).getItemMeta().getDisplayName().replace(ChatColor.YELLOW + "Report ID: ", "");

            if (displayName.equals(ChatColor.RED + "Back")) {
                openReportMenu(player);
            } else if (displayName.equals(ChatColor.GREEN + "Teleport to Location")) {
                Map<String, String> report = reportHandler.getReport(reportId);
                if (report == null) {
                    player.sendMessage(ChatColor.RED + "Report location not found.");
                    return;
                }
                Location location = new Location(
                        Bukkit.getWorld(report.get("world")),
                        Double.parseDouble(report.get("x")),
                        Double.parseDouble(report.get("y")),
                        Double.parseDouble(report.get("z"))
                );
                player.teleport(location);
                player.sendMessage(ChatColor.GREEN + "Teleported to the report location.");
            } else if (displayName.equals(ChatColor.RED + "Close Report")) {
                reportHandler.removeReport(reportId);
                player.sendMessage(ChatColor.RED + "Report ID: " + reportId + " has been closed.");
                openReportMenu(player);
            }
        }
    }
}
