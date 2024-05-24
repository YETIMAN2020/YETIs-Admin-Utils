package org.yetiman.yetisutils;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class YETIsUtils extends JavaPlugin implements Listener {
    private static YETIsUtils instance;
    private final Map<UUID, ItemStack[]> playerInventories = new HashMap<>();
    private final Map<UUID, ItemStack[]> adminInventories = new HashMap<>();
    private WarningHandler warningHandler;

    public static YETIsUtils getInstance() {
        return instance;
    }

    @Override
    public void onEnable() {
        instance = this;
        warningHandler = new WarningHandler(this);

        if (getCommand("adminmode") != null) {
            getCommand("adminmode").setExecutor(new AdminModeCommand());
        } else {
            getLogger().severe("Command adminmode not found in plugin.yml");
        }

        if (getCommand("warn") != null) {
            getCommand("warn").setExecutor(new WarnCommand());
        } else {
            getLogger().severe("Command warn not found in plugin.yml");
        }

        if (getCommand("warnings") != null) {
            getCommand("warnings").setExecutor(new WarningsCommand());
        } else {
            getLogger().severe("Command warnings not found in plugin.yml");
        }

        Bukkit.getPluginManager().registerEvents(this, this);
        loadInventories();
    }

    @Override
    public void onDisable() {
        saveInventories();
    }

    public class AdminModeCommand implements CommandExecutor {
        @Override
        public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
            if (!(sender instanceof Player)) {
                sender.sendMessage("Only players can use this command.");
                return true;
            }
            Player player = (Player) sender;
            UUID uuid = player.getUniqueId();

            if (adminInventories.containsKey(uuid)) {
                // Switch to player inventory
                playerInventories.put(uuid, player.getInventory().getContents());
                player.getInventory().setContents(adminInventories.remove(uuid));
                player.sendMessage("Switched to player inventory.");
            } else {
                // Switch to admin inventory
                adminInventories.put(uuid, player.getInventory().getContents());
                if (playerInventories.containsKey(uuid)) {
                    player.getInventory().setContents(playerInventories.get(uuid));
                } else {
                    player.getInventory().clear(); // Create an empty admin inventory if none exists
                }
                player.sendMessage("Switched to admin inventory.");
            }
            return true;
        }
    }

    public class WarnCommand implements CommandExecutor {
        @Override
        public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
            if (args.length != 1) {
                sender.sendMessage("Usage: /warn <player>");
                return true;
            }
            Player target = Bukkit.getPlayer(args[0]);
            if (target == null) {
                sender.sendMessage("Player not found.");
                return true;
            }
            warningHandler.addWarning(target);
            sender.sendMessage("Player " + target.getName() + " has been warned.");
            return true;
        }
    }

    public class WarningsCommand implements CommandExecutor {
        @Override
        public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
            if (args.length != 1) {
                sender.sendMessage("Usage: /warnings <player>");
                return true;
            }
            Player target = Bukkit.getPlayer(args[0]);
            if (target == null) {
                sender.sendMessage("Player not found.");
                return true;
            }
            int count = warningHandler.getWarnings(target);
            sender.sendMessage("Player " + target.getName() + " has " + count + " warning(s).");
            return true;
        }
    }

    private void saveInventories() {
        File folder = new File(getDataFolder(), "inventories");
        if (!folder.exists()) {
            folder.mkdirs();
        }

        saveInventoryMap(playerInventories, new File(folder, "player_inventories.yml"));
        saveInventoryMap(adminInventories, new File(folder, "admin_inventories.yml"));
    }

    private void saveInventoryMap(Map<UUID, ItemStack[]> inventoryMap, File file) {
        FileConfiguration config = YamlConfiguration.loadConfiguration(file);
        for (Map.Entry<UUID, ItemStack[]> entry : inventoryMap.entrySet()) {
            List<Map<String, Object>> serializedInventory = new ArrayList<>();
            for (ItemStack item : entry.getValue()) {
                serializedInventory.add(item == null ? null : item.serialize());
            }
            config.set(entry.getKey().toString(), serializedInventory);
        }
        try {
            config.save(file);
        } catch (IOException e) {
            getLogger().warning("Failed to save inventories: " + e.getMessage());
        }
    }

    private void loadInventories() {
        File folder = new File(getDataFolder(), "inventories");
        if (!folder.exists()) {
            folder.mkdirs();
            return;
        }

        loadInventoryMap(playerInventories, new File(folder, "player_inventories.yml"));
        loadInventoryMap(adminInventories, new File(folder, "admin_inventories.yml"));
    }

    private void loadInventoryMap(Map<UUID, ItemStack[]> inventoryMap, File file) {
        FileConfiguration config = YamlConfiguration.loadConfiguration(file);
        for (String key : config.getKeys(false)) {
            UUID uuid = UUID.fromString(key);
            List<Map<String, Object>> serializedInventory = (List<Map<String, Object>>) config.get(key);
            ItemStack[] inventory = new ItemStack[serializedInventory.size()];
            for (int i = 0; i < serializedInventory.size(); i++) {
                Map<String, Object> itemData = serializedInventory.get(i);
                inventory[i] = itemData == null ? null : ItemStack.deserialize(itemData);
            }
            inventoryMap.put(uuid, inventory);
        }
    }
}
