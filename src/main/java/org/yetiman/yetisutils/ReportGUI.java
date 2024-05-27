package org.yetiman.yetisutils;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class ReportGUI implements Listener {
    private final YETIsUtils plugin;
    private final ReportHandler reportHandler;

    public ReportGUI(YETIsUtils plugin, ReportHandler reportHandler) {
        this.plugin = plugin;
        this.reportHandler = reportHandler;
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    public void openReportMenu(Player player) {
        List<Map.Entry<String, Map<String, String>>> sortedReports = getSortedReports();
        Inventory reportMenu = Bukkit.createInventory(null, 54, "Reports");

        for (int i = 0; i < sortedReports.size(); i++) {
            Map.Entry<String, Map<String, String>> entry = sortedReports.get(i);
            String reportId = entry.getKey();
            Map<String, String> report = entry.getValue();

            ItemStack item = new ItemStack(Material.PAPER);
            ItemMeta meta = item.getItemMeta();
            if (meta != null) {
                meta.setDisplayName("Report #" + reportId);
                meta.setLore(Arrays.asList(
                        "Player: " + report.get("player"),
                        "Issue: " + report.get("issue"),
                        "Date: " + report.get("date")
                ));
                item.setItemMeta(meta);
            }
            reportMenu.addItem(item);
        }

        player.openInventory(reportMenu);
    }

    public void openReportDetails(Player player, String reportId) {
        Map<String, String> report = reportHandler.getReports().get(reportId);
        Inventory reportDetails = Bukkit.createInventory(null, 27, "Report Details");

        ItemStack reportItem = new ItemStack(Material.PAPER);
        ItemMeta reportMeta = reportItem.getItemMeta();
        if (reportMeta != null) {
            reportMeta.setDisplayName("Report #" + reportId);
            reportMeta.setLore(Arrays.asList(
                    "Player: " + report.get("player"),
                    "Issue: " + report.get("issue"),
                    "Date: " + report.get("date")
            ));
            reportItem.setItemMeta(reportMeta);
        }

        ItemStack backButton = new ItemStack(Material.BARRIER);
        ItemMeta backMeta = backButton.getItemMeta();
        if (backMeta != null) {
            backMeta.setDisplayName("Back");
            backButton.setItemMeta(backMeta);
        }

        ItemStack teleportButton = new ItemStack(Material.ENDER_PEARL);
        ItemMeta teleportMeta = teleportButton.getItemMeta();
        if (teleportMeta != null) {
            teleportMeta.setDisplayName("Teleport to Location");
            teleportButton.setItemMeta(teleportMeta);
        }

        ItemStack closeButton = new ItemStack(Material.RED_STAINED_GLASS_PANE);
        ItemMeta closeMeta = closeButton.getItemMeta();
        if (closeMeta != null) {
            closeMeta.setDisplayName("Close Report");
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

            String reportId = clickedItem.getItemMeta().getDisplayName().split("#")[1];
            openReportDetails(player, reportId);
        } else if (event.getView().getTitle().equals("Report Details")) {
            event.setCancelled(true);
            ItemStack clickedItem = event.getCurrentItem();
            if (clickedItem == null || !clickedItem.hasItemMeta()) return;

            ItemMeta meta = clickedItem.getItemMeta();
            if (meta == null) return;

            String displayName = meta.getDisplayName();
            String reportId = event.getInventory().getItem(10).getItemMeta().getDisplayName().split("#")[1];

            if (displayName.equals("Back")) {
                openReportMenu(player);
            } else if (displayName.equals("Teleport to Location")) {
                Location location = reportHandler.getReportLocation(reportId);
                if (location != null) {
                    player.teleport(location);
                    player.sendMessage("Teleported to the report location.");
                } else {
                    player.sendMessage("Failed to find the report location.");
                }
            } else if (displayName.equals("Close Report")) {
                reportHandler.clearReport(reportId);
                player.sendMessage("Report #" + reportId + " has been closed.");
                openReportMenu(player);
            }
        }
    }

    private List<Map.Entry<String, Map<String, String>>> getSortedReports() {
        List<Map.Entry<String, Map<String, String>>> entries = new ArrayList<>(reportHandler.getReports().entrySet());

        entries.sort(Comparator.comparing(entry -> {
            String dateString = entry.getValue().get("date");
            try {
                return new SimpleDateFormat("dd-MM-yy HH:mm").parse(dateString);
            } catch (ParseException e) {
                e.printStackTrace();
                return new Date(0); // fallback to epoch time in case of parse error
            }
        }));

        return entries;
    }
}
