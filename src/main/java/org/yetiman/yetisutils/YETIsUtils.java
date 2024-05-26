package org.yetiman.yetisutils;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class YETIsUtils extends JavaPlugin {
    private static YETIsUtils instance;
    private final Map<UUID, ItemStack[]> playerInventories = new HashMap<>();
    private final Map<UUID, ItemStack[]> adminInventories = new HashMap<>();
    private WarningHandler warningHandler;
    private WarningGUI warningGUI;

    private File warningsFile;
    private FileConfiguration warningsConfig;

    public static YETIsUtils getInstance() {
        return instance;
    }

    @Override
    public void onEnable() {
        instance = this;
        saveDefaultConfig();
        createWarningsFile();
        warningHandler = new WarningHandler(this);
        warningGUI = new WarningGUI(this, warningHandler);

        if (getCommand("adminmode") != null) {
            getCommand("adminmode").setExecutor(new AdminModeCommand());
        } else {
            getLogger().severe("Command adminmode not found in plugin.yml");
        }

        if (getCommand("warn") != null) {
            getCommand("warn").setExecutor(new WarnCommand());
            getCommand("warn").setTabCompleter(new WarnCommand());
        } else {
            getLogger().severe("Command warn not found in plugin.yml");
        }

        if (getCommand("warnings") != null) {
            getCommand("warnings").setExecutor(new WarningsCommand());
            getCommand("warnings").setTabCompleter(new WarningsCommand());
        } else {
            getLogger().severe("Command warnings not found in plugin.yml");
        }

        if (getCommand("warnmenu") != null) {
            getCommand("warnmenu").setExecutor(new WarningGUICommand());
        } else {
            getLogger().severe("Command warnmenu not found in plugin.yml");
        }

        if (getCommand("warnclear") != null) {
            getCommand("warnclear").setExecutor(new WarnClearCommand());
            getCommand("warnclear").setTabCompleter(new WarnClearCommand());
        } else {
            getLogger().severe("Command warnclear not found in plugin.yml");
        }

        if (getCommand("mywarnings") != null) {
            getCommand("mywarnings").setExecutor(new MyWarningsCommand(warningHandler));
        } else {
            getLogger().severe("Command mywarnings not found in plugin.yml");
        }

        Bukkit.getPluginManager().registerEvents(warningGUI, this);
        loadInventories();
    }

    @Override
    public void onDisable() {
        saveInventories();
    }

    public void createWarningsFile() {
        warningsFile = new File(getDataFolder(), "warnings.yml");
        if (!warningsFile.exists()) {
            warningsFile.getParentFile().mkdirs();
            try {
                warningsFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        warningsConfig = YamlConfiguration.loadConfiguration(warningsFile);
    }

    public FileConfiguration getWarningsConfig() {
        return warningsConfig;
    }

    public void saveWarningsConfig() {
        try {
            warningsConfig.save(warningsFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
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

    public class WarnCommand implements CommandExecutor, TabCompleter {
        @Override
        public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
            if (args.length < 2) {
                sender.sendMessage("Usage: /warn <player> <reason>");
                return true;
            }
            OfflinePlayer target = Bukkit.getOfflinePlayer(args[0]);
            if (target == null || !target.hasPlayedBefore()) {
                sender.sendMessage("Player not found.");
                return true;
            }
            String reason = String.join(" ", Arrays.copyOfRange(args, 1, args.length));
            String issuer = sender.getName();
            if (target.isOnline()) {
                Player onlinePlayer = target.getPlayer();
                if (onlinePlayer != null) {
                    warningHandler.addWarning(onlinePlayer, reason, issuer);
                }
            } else {
                UUID playerId = target.getUniqueId();
                String playerName = target.getName();
                String playerIP = ""; // IP address is not available for offline players
                warningHandler.addWarning(playerId, playerName, playerIP, reason, issuer);
            }
            sender.sendMessage("Player " + target.getName() + " has been warned for: " + reason);
            return true;
        }

        @Override
        public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
            if (args.length == 1) {
                List<String> completions = new ArrayList<>();
                String partialName = args[0].toLowerCase();

                for (OfflinePlayer player : Bukkit.getOfflinePlayers()) {
                    String name = player.getName();
                    if (name != null && name.toLowerCase().startsWith(partialName)) {
                        completions.add(name);
                    }
                }
                return completions;
            }
            return Collections.emptyList();
        }
    }

    public class WarningsCommand implements CommandExecutor, TabCompleter {
        @Override
        public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
            if (args.length != 1) {
                sender.sendMessage("Usage: /warnings <player>");
                return true;
            }
            OfflinePlayer target = Bukkit.getOfflinePlayer(args[0]);
            if (target == null || !target.hasPlayedBefore()) {
                sender.sendMessage("Player not found.");
                return true;
            }
            int count = warningHandler.getWarnings(target.getUniqueId());
            sender.sendMessage("Player " + target.getName() + " has " + count + " warning(s).");
            return true;
        }

        @Override
        public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
            if (args.length == 1) {
                List<String> playerNames = new ArrayList<>();
                for (OfflinePlayer player : Bukkit.getOfflinePlayers()) {
                    playerNames.add(player.getName());
                }
                return playerNames;
            }
            return Collections.emptyList();
        }
    }

    public class WarningGUICommand implements CommandExecutor {
        @Override
        public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
            if (!(sender instanceof Player)) {
                sender.sendMessage("Only players can use this command.");
                return true;
            }
            Player player = (Player) sender;
            warningGUI.openWarningGUI(player);
            return true;
        }
    }

    public class WarnClearCommand implements CommandExecutor, TabCompleter {
        @Override
        public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
            if (args.length != 2) {
                sender.sendMessage("Usage: /warnclear <player> <warning number>");
                return true;
            }
            OfflinePlayer target = Bukkit.getOfflinePlayer(args[0]);
            if (target == null || !target.hasPlayedBefore()) {
                sender.sendMessage("Player not found.");
                return true;
            }
            int warningNumber;
            try {
                warningNumber = Integer.parseInt(args[1]) - 1;
            } catch (NumberFormatException e) {
                sender.sendMessage("Warning number must be an integer.");
                return true;
            }
            if (warningHandler.clearWarning(target.getUniqueId(), warningNumber)) {
                sender.sendMessage("Warning " + (warningNumber + 1) + " for player " + target.getName() + " has been cleared.");
            } else {
                sender.sendMessage("Player " + target.getName() + " does not have a warning " + (warningNumber + 1) + ".");
            }
            return true;
        }

        @Override
        public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
            if (args.length == 1) {
                List<String> completions = new ArrayList<>();
                String partialName = args[0].toLowerCase();

                for (OfflinePlayer player : Bukkit.getOfflinePlayers()) {
                    String name = player.getName();
                    if (name != null && name.toLowerCase().startsWith(partialName)) {
                        completions.add(name);
                    }
                }
                return completions;
            }
            return Collections.emptyList();
        }
    }

    public class MyWarningsCommand implements CommandExecutor {
        private final WarningHandler warningHandler;

        public MyWarningsCommand(WarningHandler warningHandler) {
            this.warningHandler = warningHandler;
        }

        @Override
        public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
            if (!(sender instanceof Player)) {
                sender.sendMessage("Only players can use this command.");
                return true;
            }

            Player player = (Player) sender;
            int count = warningHandler.getWarnings(player.getUniqueId());

            if (count == 0) {
                player.sendMessage("You have no warnings.");
            } else {
                player.sendMessage("You have " + count + " warning(s):");
                for (int i = 0; i < count; i++) {
                    String reason = warningHandler.getWarningReason(player.getUniqueId(), i);
                    String issuer = warningHandler.getWarningIssuer(player.getUniqueId(), i);
                    String date = warningHandler.getWarningDate(player.getUniqueId(), i);

                    if (reason != null && issuer != null && date != null) {
                        player.sendMessage((i + 1) + ". Reason: " + reason + " - Issued by: " + issuer + " - Date: " + date);
                    } else {
                        player.sendMessage((i + 1) + ". Reason: " + reason + " - Some information is missing.");
                    }
                }
            }
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
