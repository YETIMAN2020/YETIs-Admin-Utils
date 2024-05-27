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

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class WarningGUI implements Listener {
    private final YETIsUtils plugin;
    private final WarningHandler warningHandler;

    public WarningGUI(YETIsUtils plugin, WarningHandler warningHandler) {
        this.plugin = plugin;
        this.warningHandler = warningHandler;
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    public void openWarningGUI(Player player) {
        List<UUID> warnedPlayers = warningHandler.getWarnedPlayers();
        Inventory warningMenu = Bukkit.createInventory(null, 54, "Player Warnings");

        for (UUID uuid : warnedPlayers) {
            OfflinePlayer warnedPlayer = Bukkit.getOfflinePlayer(uuid);
            ItemStack skull = new ItemStack(Material.PLAYER_HEAD);
            SkullMeta skullMeta = (SkullMeta) skull.getItemMeta();
            if (skullMeta != null) {
                skullMeta.setOwningPlayer(warnedPlayer);
                skullMeta.setDisplayName(warnedPlayer.getName());
                skull.setItemMeta(skullMeta);
            }
            warningMenu.addItem(skull);
        }

        player.openInventory(warningMenu);
    }

    public void openPlayerWarnings(Player player, OfflinePlayer target) {
        List<Map<String, String>> warnings = warningHandler.getWarningsList(target.getUniqueId());
        Inventory playerWarningsMenu = Bukkit.createInventory(null, 54, "Warnings for " + target.getName());

        for (int i = 0; i < warnings.size(); i++) {
            Map<String, String> warning = warnings.get(i);
            ItemStack paper = new ItemStack(Material.PAPER);
            ItemMeta meta = paper.getItemMeta();
            if (meta != null) {
                meta.setDisplayName("Warning #" + (i + 1));
                meta.setLore(Arrays.asList(
                        "Reason: " + warning.get("reason"),
                        "Issuer: " + warning.get("issuer"),
                        "Date: " + warning.get("date"),
                        "IP: " + warning.get("ip")
                ));
                paper.setItemMeta(meta);
            }
            playerWarningsMenu.addItem(paper);
        }

        ItemStack backButton = new ItemStack(Material.BARRIER);
        ItemMeta backMeta = backButton.getItemMeta();
        if (backMeta != null) {
            backMeta.setDisplayName("Back");
            backButton.setItemMeta(backMeta);
        }
        playerWarningsMenu.setItem(53, backButton);

        player.openInventory(playerWarningsMenu);
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player)) return;
        Player player = (Player) event.getWhoClicked();

        if (event.getView().getTitle().equals("Player Warnings")) {
            event.setCancelled(true);
            ItemStack clickedItem = event.getCurrentItem();
            if (clickedItem == null || !clickedItem.hasItemMeta()) return;

            SkullMeta skullMeta = (SkullMeta) clickedItem.getItemMeta();
            if (skullMeta == null || skullMeta.getOwningPlayer() == null) return;

            OfflinePlayer target = skullMeta.getOwningPlayer();
            openPlayerWarnings(player, target);
        } else if (event.getView().getTitle().startsWith("Warnings for ")) {
            event.setCancelled(true);
            ItemStack clickedItem = event.getCurrentItem();
            if (clickedItem == null || !clickedItem.hasItemMeta()) return;

            ItemMeta meta = clickedItem.getItemMeta();
            if (meta == null) return;

            String displayName = meta.getDisplayName();
            if (displayName.equals("Back")) {
                openWarningGUI(player);
            }
        }
    }
}
