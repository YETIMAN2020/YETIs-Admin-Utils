package org.yetiman.yetisutils.Warningfeatures;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
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
import org.yetiman.yetisutils.YETIsUtils;

import java.util.*;

public class WarningGUI implements Listener {
    private final WarningHandler warningHandler;
    private final YETIsUtils plugin;

    public WarningGUI(YETIsUtils plugin, WarningHandler warningHandler) {
        this.warningHandler = warningHandler;
        this.plugin = plugin;
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    public void openWarningGUI(Player player) {
        Inventory warningMenu = Bukkit.createInventory(null, 54, ChatColor.RED + "Warnings Menu");

        for (UUID playerUUID : warningHandler.getPlayersWithWarnings()) {
            OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(playerUUID);
            ItemStack playerHead = new ItemStack(Material.PLAYER_HEAD);
            SkullMeta skullMeta = (SkullMeta) playerHead.getItemMeta();
            if (skullMeta != null) {
                skullMeta.setOwningPlayer(offlinePlayer);
                skullMeta.setDisplayName(ChatColor.YELLOW + offlinePlayer.getName());
                skullMeta.setLore(Collections.singletonList(ChatColor.GREEN + "Click to view warnings"));
                playerHead.setItemMeta(skullMeta);
            }
            warningMenu.addItem(playerHead);
        }

        player.openInventory(warningMenu);
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player)) return;
        Player player = (Player) event.getWhoClicked();

        if (event.getView().getTitle().equals(ChatColor.RED + "Warnings Menu") || event.getView().getTitle().startsWith(ChatColor.RED + "Warnings for")) {
            event.setCancelled(true);
            ItemStack clickedItem = event.getCurrentItem();
            if (clickedItem == null || !clickedItem.hasItemMeta()) return;

            if (event.getView().getTitle().equals(ChatColor.RED + "Warnings Menu")) {
                SkullMeta meta = (SkullMeta) clickedItem.getItemMeta();
                if (meta == null) return;

                OfflinePlayer targetPlayer = meta.getOwningPlayer();
                if (targetPlayer != null) {
                    openPlayerWarnings(player, targetPlayer);
                }
            }
        }
    }

    public void openPlayerWarnings(Player viewer, OfflinePlayer target) {
        List<WarningHandler.WarningRecord> playerWarnings = warningHandler.getPlayerWarnings(target.getUniqueId());
        Inventory warningsInventory = Bukkit.createInventory(null, 54, ChatColor.RED + "Warnings for " + target.getName());

        for (int i = 0; i < playerWarnings.size(); i++) {
            WarningHandler.WarningRecord warning = playerWarnings.get(i);
            ItemStack warningItem = new ItemStack(Material.PAPER);
            ItemMeta warningMeta = warningItem.getItemMeta();
            if (warningMeta != null) {
                warningMeta.setDisplayName(ChatColor.YELLOW + "Warning " + (i + 1));
                warningMeta.setLore(Arrays.asList(
                        ChatColor.GREEN + "Reason: " + ChatColor.WHITE + warning.getReason(),
                        ChatColor.RED + "Issuer: " + ChatColor.WHITE + warning.getIssuer(),
                        ChatColor.BLUE + "Date: " + ChatColor.WHITE + warning.getDate()
                ));
                warningItem.setItemMeta(warningMeta);
            }
            warningsInventory.addItem(warningItem);
        }

        viewer.openInventory(warningsInventory);
    }
}
