package org.yetiman.yetisutils;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ReportGUI implements Listener {
    private final YETIsUtils plugin;
    private final ReportHandler reportHandler;

    public ReportGUI(YETIsUtils plugin, ReportHandler reportHandler) {
        this.plugin = plugin;
        this.reportHandler = reportHandler;
    }

    public static void openReportGUI(Player player, ReportHandler reportHandler) {
        Inventory inv = Bukkit.createInventory(null, 54, "Player Reports");

        List<Report> allReports = reportHandler.getAllReports();
        SimpleDateFormat dateFormatter = new SimpleDateFormat("dd-MM-yy HH-mm");

        for (int i = 0; i < allReports.size(); i++) {
            Report report = allReports.get(i);
            ItemStack paper = new ItemStack(Material.PAPER);
            ItemMeta meta = paper.getItemMeta();
            if (meta != null) {
                meta.setDisplayName(ChatColor.YELLOW + "Report #" + (i + 1));
                List<String> lore = new ArrayList<>();
                lore.add(ChatColor.GOLD + "Reporter: " + report.getReporter());
                lore.add(ChatColor.GOLD + "Issue: " + report.getIssue());
                lore.add(ChatColor.AQUA + "Date: " + dateFormatter.format(report.getDate()));
                meta.setLore(lore);
                paper.setItemMeta(meta);
            }
            inv.addItem(paper);
        }

        player.openInventory(inv);
    }

    public static void openReportDetailGUI(Player player, Report report, int reportIndex) {
        Inventory inv = Bukkit.createInventory(null, 27, "Report Details");

        ItemStack paper = new ItemStack(Material.PAPER);
        ItemMeta meta = paper.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(ChatColor.YELLOW + "Report Details");
            List<String> lore = new ArrayList<>();
            lore.add(ChatColor.GOLD + "Reporter: " + report.getReporter());
            lore.add(ChatColor.GOLD + "Issue: " + report.getIssue());
            lore.add(ChatColor.AQUA + "Date: " + new SimpleDateFormat("dd-MM-yy HH-mm").format(report.getDate()));
            meta.setLore(lore);
            paper.setItemMeta(meta);
        }
        inv.setItem(13, paper); // Center the paper in the inventory

        ItemStack closeButton = createGuiItem(Material.RED_STAINED_GLASS_PANE, ChatColor.RED + "Close Report");
        inv.setItem(26, closeButton); // Place the close button in the bottom right

        inv.setItem(18, createGuiItem(Material.ARROW, ChatColor.GREEN + "Back to Reports"));

        player.openInventory(inv);
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (event.getView().getTitle().equals("Player Reports")) {
            event.setCancelled(true);
            if (event.getCurrentItem() == null || event.getCurrentItem().getType() != Material.PAPER) {
                return;
            }

            Player player = (Player) event.getWhoClicked();
            int reportIndex = event.getSlot();
            List<Report> allReports = reportHandler.getAllReports();
            if (reportIndex < allReports.size()) {
                Report report = allReports.get(reportIndex);
                openReportDetailGUI(player, report, reportIndex);
            }
        } else if (event.getView().getTitle().equals("Report Details")) {
            event.setCancelled(true);
            if (event.getCurrentItem() == null) {
                return;
            }

            Player player = (Player) event.getWhoClicked();
            ItemStack clickedItem = event.getCurrentItem();
            ItemMeta meta = clickedItem.getItemMeta();
            if (meta == null) {
                return;
            }

            if (clickedItem.getType() == Material.RED_STAINED_GLASS_PANE && meta.getDisplayName().equals(ChatColor.RED + "Close Report")) {
                int reportIndex = event.getInventory().getItem(13).getAmount() - 1;
                List<Report> allReports = reportHandler.getAllReports();
                if (reportIndex < allReports.size()) {
                    Report report = allReports.get(reportIndex);
                    UUID reporterId = reportHandler.getReporterUUID(report);
                    if (reporterId != null) {
                        reportHandler.removeReport(reporterId, reportIndex);
                        player.sendMessage(ChatColor.GREEN + "Report closed.");
                        player.closeInventory();
                        openReportGUI(player, reportHandler);
                    }
                }
            } else if (clickedItem.getType() == Material.ARROW && meta.getDisplayName().equals(ChatColor.GREEN + "Back to Reports")) {
                player.closeInventory();
                openReportGUI(player, reportHandler);
            }
        }
    }

    private static ItemStack createGuiItem(Material material, String name, String... lore) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(name);
            List<String> metaLore = new ArrayList<>();
            for (String loreEntry : lore) {
                metaLore.add(loreEntry);
            }
            meta.setLore(metaLore);
            item.setItemMeta(meta);
        }
        return item;
    }
}
