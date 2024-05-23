package org.yetiman.yetisutils;


import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class YETIsUtils extends JavaPlugin {

    @Override
    public void onEnable() {
        // Save the default config file if it does not exist
        this.saveDefaultConfig();

        // Register the command and set its executor
        getCommand("admintoggle").setExecutor(new AdminToggleCommand());
    }

    public class AdminToggleCommand implements CommandExecutor {
        @Override
        public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
            if (sender instanceof Player) {
                Player player = (Player) sender;

                // Check for permission
                if (!player.hasPermission("yetisutils.admintoggle")) {
                    player.sendMessage("You do not have permission to use this command.");
                    return true;
                }

                UUID playerUUID = player.getUniqueId();

                // Check if there is an existing stored inventory
                ItemStack[] storedInventory = loadInventoryFromFile(playerUUID);
                if (storedInventory != null && !isInventoryEmpty(storedInventory)) {
                    // Retrieve player's inventory
                    player.getInventory().setContents(storedInventory);
                    deleteInventoryFile(playerUUID); // Remove the file after retrieving
                    player.sendMessage("Your inventory has been retrieved.");
                } else {
                    // Store player's current inventory
                    saveInventoryToFile(playerUUID, player.getInventory().getContents());
                    player.getInventory().clear();
                    player.sendMessage("Your inventory has been stored.");
                }
                return true;
            }
            return false;
        }

        private boolean isInventoryEmpty(ItemStack[] inventory) {
            for (ItemStack item : inventory) {
                if (item != null) {
                    return false;
                }
            }
            return true;
        }

        private void saveInventoryToFile(UUID playerUUID, ItemStack[] inventory) {
            File folder = new File(getDataFolder(), "inventories");
            if (!folder.exists()) {
                folder.mkdirs();
            }
            File file = new File(folder, playerUUID.toString() + ".yml");
            FileConfiguration config = YamlConfiguration.loadConfiguration(file);

            if (inventory != null) {
                List<Map<String, Object>> serializedInventory = new ArrayList<>();
                for (ItemStack item : inventory) {
                    serializedInventory.add(item == null ? null : item.serialize());
                }
                config.set("inventory", serializedInventory);
                try {
                    config.save(file);
                } catch (IOException e) {
                    getLogger().warning("Failed to save inventory for player " + playerUUID.toString() + ": " + e.getMessage());
                }
            }
        }

        private ItemStack[] loadInventoryFromFile(UUID playerUUID) {
            File folder = new File(getDataFolder(), "inventories");
            File file = new File(folder, playerUUID.toString() + ".yml");
            if (!file.exists()) {
                return null;
            }

            FileConfiguration config = YamlConfiguration.loadConfiguration(file);
            List<Map<String, Object>> inventoryList = (List<Map<String, Object>>) config.get("inventory");
            if (inventoryList == null) {
                return null;
            }

            ItemStack[] inventory = new ItemStack[inventoryList.size()];
            for (int i = 0; i < inventoryList.size(); i++) {
                Map<String, Object> itemData = inventoryList.get(i);
                inventory[i] = itemData == null ? null : ItemStack.deserialize(itemData);
            }

            return inventory;
        }

        private void deleteInventoryFile(UUID playerUUID) {
            File folder = new File(getDataFolder(), "inventories");
            File file = new File(folder, playerUUID.toString() + ".yml");
            if (file.exists()) {
                file.delete();
            }
        }
    }
}