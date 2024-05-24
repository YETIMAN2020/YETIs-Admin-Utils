package org.yetiman.yetisutils;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class WarningGUI implements Listener {
    private final JavaPlugin plugin;
    private final WarningHandler warningHandler;

    public WarningGUI(JavaPlugin plugin, WarningHandler warningHandler) {
        this.plugin = plugin;
        this.warningHandler = warningHandler;
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    public void openWarningGUI(Player player) {
        Map<UUID, WarningHandler.WarningData> warnings = warningHandler.getAllWarnings();
        Inventory warningInventory = Bukkit.createInventory(null, 45, "Player Warnings");

        for (Map.Entry<UUID, WarningHandler.WarningData> entry : warnings.entrySet()) {
            WarningHandler.WarningData data = entry.getValue();
            ItemStack skull = new ItemStack(Material.PLAYER_HEAD, 1);
            SkullMeta meta = (SkullMeta) skull.getItemMeta();
            if (meta != null) {
                meta.setOwningPlayer(Bukkit.getOfflinePlayer(entry.getKey()));
                meta.setDisplayName(data.getName());
                List<String> lore = new ArrayList<>();
                lore.add("Warnings: " + data.getWarnings());
                lore.add("IP: " + data.getIp());
                meta.setLore(lore);
                skull.setItemMeta(meta);
            }
            warningInventory.addItem(skull);
        }

        player.openInventory(warningInventory);
    }

    public void openReasonsGUI(Player player, UUID targetUUID) {
        List<String> reasons = warningHandler.getReasons(targetUUID);
        Inventory reasonsInventory = Bukkit.createInventory(null, 45, "Warning Reasons");

        for (int i = 0; i < reasons.size(); i++) {
            String reason = reasons.get(i);
            ItemStack sign = new ItemStack(Material.OAK_SIGN, 1);
            ItemMeta meta = sign.getItemMeta();
            if (meta != null) {
                String[] reasonParts = reason.split("\n");
                meta.setDisplayName("Warning " + (i + 1));
                List<String> lore = new ArrayList<>();
                lore.add(reasonParts[0]); // reason
                lore.add(reasonParts[1]); // timestamp
                meta.setLore(lore);
                sign.setItemMeta(meta);
            }
            reasonsInventory.addItem(sign);
        }

        // Add the "Back" button
        ItemStack backButton = new ItemStack(Material.BARRIER);
        ItemMeta backMeta = backButton.getItemMeta();
        if (backMeta != null) {
            backMeta.setDisplayName("Back to Player Warnings");
            backButton.setItemMeta(backMeta);
        }
        reasonsInventory.setItem(36, backButton); // Bottom left slot (5th row, 1st column)

        player.openInventory(reasonsInventory);
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (event.getView().getTitle().equals("Player Warnings")) {
            if (event.getClickedInventory() != null && event.getClickedInventory().equals(event.getView().getTopInventory())) {
                event.setCancelled(true); // Prevent items from being added to the custom GUI

                ItemStack clickedItem = event.getCurrentItem();
                if (clickedItem != null && clickedItem.getType() == Material.PLAYER_HEAD) {
                    SkullMeta meta = (SkullMeta) clickedItem.getItemMeta();
                    if (meta != null && meta.getOwningPlayer() != null) {
                        UUID targetUUID = meta.getOwningPlayer().getUniqueId();
                        openReasonsGUI((Player) event.getWhoClicked(), targetUUID);
                    }
                }
            }
        } else if (event.getView().getTitle().equals("Warning Reasons")) {
            event.setCancelled(true); // Prevent any item interaction in the reasons GUI

            ItemStack clickedItem = event.getCurrentItem();
            if (clickedItem != null && clickedItem.getType() == Material.BARRIER) {
                openWarningGUI((Player) event.getWhoClicked());
            }
        }
    }

    @EventHandler
    public void onInventoryDrag(InventoryDragEvent event) {
        if (event.getView().getTitle().equals("Player Warnings") || event.getView().getTitle().equals("Warning Reasons")) {
            if (event.getRawSlots().stream().anyMatch(slot -> slot < event.getView().getTopInventory().getSize())) {
                event.setCancelled(true); // Prevent items from being added to the custom GUI
            }
        }
    }
}
