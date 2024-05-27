package org.yetiman.yetisutils;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.event.Listener;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;

import java.lang.reflect.Field;
import java.util.List;
import java.util.UUID;

public class WarningGUI implements Listener {
    private final JavaPlugin plugin;
    private final WarningHandler warningHandler;

    public WarningGUI(JavaPlugin plugin, WarningHandler warningHandler) {
        this.plugin = plugin;
        this.warningHandler = warningHandler;
    }

    public void openWarningGUI(Player player) {
        Inventory inventory = Bukkit.createInventory(null, 54, "Player Warnings");

        for (OfflinePlayer offlinePlayer : Bukkit.getOfflinePlayers()) {
            if (warningHandler.getWarnings(offlinePlayer.getUniqueId()) > 0) {
                ItemStack skull = new ItemStack(Material.PLAYER_HEAD);
                SkullMeta skullMeta = (SkullMeta) skull.getItemMeta();
                assert skullMeta != null;
                skullMeta.setOwningPlayer(offlinePlayer);
                skullMeta.setDisplayName(offlinePlayer.getName());
                skull.setItemMeta(skullMeta);
                inventory.addItem(skull);
            }
        }

        ItemStack exitItem = new ItemStack(Material.BARRIER);
        ItemMeta exitMeta = exitItem.getItemMeta();
        assert exitMeta != null;
        exitMeta.setDisplayName("§cExit");
        exitItem.setItemMeta(exitMeta);
        inventory.setItem(53, exitItem);

        player.openInventory(inventory);
    }

    public void openWarningReasonsGUI(Player player, OfflinePlayer target) {
        Inventory inventory = Bukkit.createInventory(null, 54, "Warning Reasons for " + target.getName());
        int warningsCount = warningHandler.getWarnings(target.getUniqueId());

        for (int i = 0; i < warningsCount; i++) {
            ItemStack item = new ItemStack(Material.PAPER);
            ItemMeta meta = item.getItemMeta();
            assert meta != null;
            meta.setDisplayName("Warning " + (i + 1));
            List<String> lore = List.of(
                    warningHandler.getWarningReason(target.getUniqueId(), i),
                    "Issued by: " + warningHandler.getWarningIssuer(target.getUniqueId(), i),
                    "Date: " + warningHandler.getWarningDate(target.getUniqueId(), i)
            );
            meta.setLore(lore);
            item.setItemMeta(meta);
            inventory.addItem(item);
        }

        ItemStack backItem = new ItemStack(Material.RED_STAINED_GLASS_PANE);
        ItemMeta backMeta = backItem.getItemMeta();
        assert backMeta != null;
        backMeta.setDisplayName("§cBack");
        backItem.setItemMeta(backMeta);
        inventory.setItem(53, backItem);

        player.openInventory(inventory);
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();
        Inventory inventory = event.getView().getTopInventory();

        if (event.getView().getTitle().equals("Player Warnings")) {
            event.setCancelled(true);
            ItemStack clickedItem = event.getCurrentItem();
            if (clickedItem == null || clickedItem.getType() == Material.AIR) return;

            if (clickedItem.getType() == Material.PLAYER_HEAD) {
                SkullMeta skullMeta = (SkullMeta) clickedItem.getItemMeta();
                assert skullMeta != null;
                OfflinePlayer target = Bukkit.getOfflinePlayer(skullMeta.getOwningPlayer().getUniqueId());
                openWarningReasonsGUI(player, target);
            } else if (clickedItem.getType() == Material.BARRIER) {
                player.closeInventory();
            }
        } else if (event.getView().getTitle().startsWith("Warning Reasons for ")) {
            event.setCancelled(true);
            ItemStack clickedItem = event.getCurrentItem();
            if (clickedItem == null || clickedItem.getType() == Material.AIR) return;

            if (clickedItem.getType() == Material.PAPER) {
                // Handle clicking on a warning reason
            } else if (clickedItem.getType() == Material.RED_STAINED_GLASS_PANE) {
                openWarningGUI(player);
            }
        }
    }

    private void setSkullOwner(SkullMeta skullMeta, String playerName) {
        GameProfile profile = new GameProfile(UUID.randomUUID(), playerName);
        profile.getProperties().put("textures", new Property("textures", getSkin(playerName)));

        try {
            Field profileField = skullMeta.getClass().getDeclaredField("profile");
            profileField.setAccessible(true);
            profileField.set(skullMeta, profile);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    private String getSkin(String playerName) {
        // Implement your method to get the player's skin here
        return "";
    }
}
