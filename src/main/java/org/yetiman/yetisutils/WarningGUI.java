package org.yetiman.yetisutils;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class WarningGUI implements Listener {
    private final YETIsUtils plugin;
    private final WarningHandler warningHandler;

    public WarningGUI(YETIsUtils plugin, WarningHandler warningHandler) {
        this.plugin = plugin;
        this.warningHandler = warningHandler;
    }

    public void openWarningGUI(Player player) {
        Inventory inventory = Bukkit.createInventory(null, 45, "Warnings");
        for (OfflinePlayer offlinePlayer : Bukkit.getOfflinePlayers()) {
            int warningsCount = warningHandler.getWarnings(offlinePlayer.getUniqueId());
            if (warningsCount > 0) {
                ItemStack playerHead = new ItemStack(Material.PLAYER_HEAD);
                ItemMeta meta = playerHead.getItemMeta();
                meta.setDisplayName(offlinePlayer.getName());
                List<String> lore = new ArrayList<>();
                lore.add("Warnings: " + warningsCount);
                meta.setLore(lore);
                playerHead.setItemMeta(meta);
                inventory.addItem(playerHead);
            }
        }
        player.openInventory(inventory);
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (event.getView().getTitle().equals("Warnings") && event.getCurrentItem() != null) {
            event.setCancelled(true);
            ItemStack clickedItem = event.getCurrentItem();
            if (clickedItem.getType() == Material.PLAYER_HEAD) {
                Player player = (Player) event.getWhoClicked();
                String playerName = clickedItem.getItemMeta().getDisplayName();
                OfflinePlayer target = Bukkit.getOfflinePlayer(playerName);
                openWarningsDetailsGUI(player, target.getUniqueId());
            }
        }
    }

    public void openWarningsDetailsGUI(Player player, UUID targetId) {
        Inventory inventory = Bukkit.createInventory(null, 45, "Warning Reasons");
        int warningsCount = warningHandler.getWarnings(targetId);
        for (int i = 0; i < warningsCount; i++) {
            ItemStack paper = new ItemStack(Material.PAPER);  // Changed from OAK_SIGN to PAPER
            ItemMeta meta = paper.getItemMeta();
            meta.setDisplayName("Warning " + (i + 1));
            List<String> lore = new ArrayList<>();
            lore.add("Reason: " + warningHandler.getWarningReason(targetId, i));
            lore.add("Issued by: " + warningHandler.getWarningIssuer(targetId, i));
            lore.add("Date: " + warningHandler.getWarningDate(targetId, i));
            meta.setLore(lore);
            paper.setItemMeta(meta);
            inventory.addItem(paper);
        }
        ItemStack backButton = new ItemStack(Material.BARRIER);
        ItemMeta backMeta = backButton.getItemMeta();
        backMeta.setDisplayName("Back");
        backButton.setItemMeta(backMeta);
        inventory.setItem(40, backButton);
        player.openInventory(inventory);
    }

    @EventHandler
    public void onInventoryClickWarningReasons(InventoryClickEvent event) {
        if (event.getView().getTitle().equals("Warning Reasons") && event.getCurrentItem() != null) {
            event.setCancelled(true);
            ItemStack clickedItem = event.getCurrentItem();
            if (clickedItem.getType() == Material.BARRIER) {
                Player player = (Player) event.getWhoClicked();
                openWarningGUI(player);
            }
        }
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        if (event.getView().getTitle().equals("Warnings") || event.getView().getTitle().equals("Warning Reasons")) {
            // Handle any additional actions on inventory close if needed
        }
    }
}
