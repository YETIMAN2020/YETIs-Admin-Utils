package org.yetiman.yetisutils;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class WarningGUI implements Listener {
    private final YETIsUtils plugin;
    private final WarningHandler warningHandler;

    public WarningGUI(YETIsUtils plugin, WarningHandler warningHandler) {
        this.plugin = plugin;
        this.warningHandler = warningHandler;
    }

    public void openWarningGUI(Player player) {
        Inventory inv = Bukkit.createInventory(null, 45, "Player Warnings");

        Map<UUID, WarningHandler.WarningData> warnings = warningHandler.getAllWarnings();
        int index = 0;

        for (Map.Entry<UUID, WarningHandler.WarningData> entry : warnings.entrySet()) {
            if (index >= 45) break;

            UUID uuid = entry.getKey();
            WarningHandler.WarningData data = entry.getValue();
            ItemStack item = new ItemStack(Material.PLAYER_HEAD, 1);
            SkullMeta meta = (SkullMeta) item.getItemMeta();
            if (meta != null) {
                OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(uuid);
                meta.setOwningPlayer(offlinePlayer);
                meta.setDisplayName(data.getName());
                List<String> lore = new ArrayList<>();
                lore.add("Warnings: " + data.getWarnings());
                meta.setLore(lore);
                item.setItemMeta(meta);
            }
            inv.setItem(index, item);
            index++;
        }

        player.openInventory(inv);
    }

    public void openWarningReasonsGUI(Player player, UUID targetUUID) {
        WarningHandler.WarningData data = warningHandler.getAllWarnings().get(targetUUID);
        if (data == null) {
            player.sendMessage("No warnings found for this player.");
            return;
        }

        Inventory inv = Bukkit.createInventory(null, 45, "Warning Reasons");

        List<String> reasons = data.getDetailedReasons();
        for (int i = 0; i < reasons.size() && i < 45; i++) {
            ItemStack item = new ItemStack(Material.PAPER, 1);
            ItemMeta meta = item.getItemMeta();
            if (meta != null) {
                meta.setDisplayName("Warning #" + (i + 1));
                List<String> lore = new ArrayList<>();
                String[] lines = reasons.get(i).split("\n");
                for (String line : lines) {
                    lore.add(line);
                }
                meta.setLore(lore);
                item.setItemMeta(meta);
            }
            inv.setItem(i, item);
        }

        // Add the back button
        ItemStack backButton = new ItemStack(Material.BARRIER, 1);
        ItemMeta backMeta = backButton.getItemMeta();
        if (backMeta != null) {
            backMeta.setDisplayName("Back to Player Warnings");
            backButton.setItemMeta(backMeta);
        }
        inv.setItem(44, backButton);

        player.openInventory(inv);
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (event.getView().getTitle().equals("Player Warnings")) {
            event.setCancelled(true);
            if (event.getCurrentItem() != null && event.getCurrentItem().getType() == Material.PLAYER_HEAD) {
                Player player = (Player) event.getWhoClicked();
                SkullMeta meta = (SkullMeta) event.getCurrentItem().getItemMeta();
                if (meta != null && meta.getOwningPlayer() != null) {
                    UUID targetUUID = meta.getOwningPlayer().getUniqueId();
                    openWarningReasonsGUI(player, targetUUID);
                }
            }
        } else if (event.getView().getTitle().equals("Warning Reasons")) {
            event.setCancelled(true);
            if (event.getCurrentItem() != null && event.getCurrentItem().getType() == Material.BARRIER) {
                Player player = (Player) event.getWhoClicked();
                openWarningGUI(player);
            }
        }
    }
}
