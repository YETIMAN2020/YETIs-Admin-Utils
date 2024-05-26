package org.yetiman.yetisutils;

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
        Inventory inv = Bukkit.createInventory(null, 45, "Player Warnings");
        for (UUID uuid : warningHandler.getAllPlayerUUIDs()) {
            ItemStack head = getPlayerHead(Bukkit.getOfflinePlayer(uuid));
            ItemMeta meta = head.getItemMeta();
            if (meta != null) {
                meta.setDisplayName(Bukkit.getOfflinePlayer(uuid).getName());
                head.setItemMeta(meta);
            }
            inv.addItem(head);
        }
        ItemStack exitButton = createGuiItem(Material.BARRIER, ChatColor.RED + "Exit");
        inv.setItem(44, exitButton);
        player.openInventory(inv);
    }

    public void openReasonsGUI(Player player, UUID targetUUID) {
        Inventory inv = Bukkit.createInventory(null, 45, "Warning Reasons for " + Bukkit.getOfflinePlayer(targetUUID).getName());
        int warningCount = warningHandler.getWarnings(targetUUID);
        for (int i = 0; i < warningCount; i++) {
            String reason = warningHandler.getWarningReason(targetUUID, i);
            String issuer = warningHandler.getWarningIssuer(targetUUID, i);
            String date = warningHandler.getWarningDate(targetUUID, i);
            ItemStack paper = createGuiItem(Material.PAPER, ChatColor.YELLOW + "Warning " + (i + 1),
                    ChatColor.GOLD + reason,
                    ChatColor.AQUA + "Issued by: " + issuer,
                    ChatColor.GREEN + "Date: " + date);
            inv.addItem(paper);
        }
        ItemStack backButton = createGuiItem(Material.BARRIER, ChatColor.RED + "Back");
        inv.setItem(44, backButton);
        player.openInventory(inv);
    }

    private ItemStack getPlayerHead(OfflinePlayer player) {
        ItemStack head = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta meta = (SkullMeta) head.getItemMeta();
        if (meta != null) {
            meta.setOwningPlayer(player);
            head.setItemMeta(meta);
        }
        return head;
    }

    private ItemStack createGuiItem(Material material, String name, String... lore) {
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

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (event.getView().getTitle().equals("Player Warnings")) {
            event.setCancelled(true);
            if (event.getCurrentItem() != null && event.getCurrentItem().getType() == Material.PLAYER_HEAD) {
                Player player = (Player) event.getWhoClicked();
                String playerName = event.getCurrentItem().getItemMeta().getDisplayName();
                OfflinePlayer target = Bukkit.getOfflinePlayer(playerName);
                openReasonsGUI(player, target.getUniqueId());
            } else if (event.getCurrentItem() != null && event.getCurrentItem().getType() == Material.BARRIER) {
                event.getWhoClicked().closeInventory();
            }
        } else if (event.getView().getTitle().startsWith("Warning Reasons for ")) {
            event.setCancelled(true);
            if (event.getCurrentItem() != null && event.getCurrentItem().getType() == Material.BARRIER) {
                Player player = (Player) event.getWhoClicked();
                openWarningGUI(player);
            }
        }
    }
}
