package org.yetiman.yetisutils;

import org.bukkit.Bukkit;
import org.bukkit.Location;
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
import java.text.SimpleDateFormat;
import java.util.*;

public class YETIsUtils extends JavaPlugin {
    private static YETIsUtils instance;
    private final Map<UUID, ItemStack[]> playerInventories = new HashMap<>();
    private final Map<UUID, ItemStack[]> adminInventories = new HashMap<>();
    private WarningHandler warningHandler;
    private WarningGUI warningGUI;
    private ReportHandler reportHandler;
    private ReportGUI reportGUI;

    public static YETIsUtils getInstance() {
        return instance;
    }

    @Override
    public void onEnable() {
        instance = this;
        saveDefaultConfig();
        warningHandler = new WarningHandler(this);
        warningGUI = new WarningGUI(this, warningHandler);

        if (getConfig().getBoolean("enableReportFeature", true)) {
            reportHandler = new ReportHandler(this);
            reportGUI = new ReportGUI(this, reportHandler);

            if (getCommand("report") != null) {
                getCommand("report").setExecutor(new ReportCommand(reportHandler));
                getCommand("report").setTabCompleter(new ReportCommand(reportHandler));
            } else {
                getLogger().severe("Command report not found in plugin.yml");
            }

            if (getCommand("reportmenu") != null) {
                getCommand("reportmenu").setExecutor(new ReportMenuCommand(reportHandler, reportGUI));
            } else {
                getLogger().severe("Command reportmenu not found in plugin.yml");
            }

            Bukkit.getPluginManager().registerEvents(reportGUI, this);
            reportHandler.loadReports();
        }

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

        if (getCommand("yetisutils") != null) {
            getCommand("yetisutils").setExecutor(new ReloadCommand(this));
        } else {
            getLogger().severe("Command yetisutils not found in plugin.yml");
        }

        Bukkit.getPluginManager().registerEvents(warningGUI, this);
        loadInventories();
        warningHandler.loadWarnings();
    }

    @Override
    public void onDisable() {
        saveInventories();
        warningHandler.saveWarnings();
        if (reportHandler != null) {
            reportHandler.saveReports();
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
            String date = new SimpleDateFormat("dd-MM-yy HH:mm").format(new Date());
            warningHandler.addWarning(target, reason, issuer, date);
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
                    player.sendMessage((i + 1) + ". " + reason + " - " + issuer + " - " + date);
                }
            }
            return true;
        }
    }

    public class ReportCommand implements CommandExecutor, TabCompleter {
        private final ReportHandler reportHandler;

        public ReportCommand(ReportHandler reportHandler) {
            this.reportHandler = reportHandler;
        }

        @Override
        public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
            if (!(sender instanceof Player)) {
                sender.sendMessage("Only players can use this command.");
                return true;
            }

            if (args.length < 1) {
                sender.sendMessage("Usage: /report <issue>");
                return true;
            }

            Player player = (Player) sender;
            String issue = String.join(" ", args);
            Location location = player.getLocation();
            reportHandler.addReport(player, issue, location);
            sender.sendMessage("Report submitted. Thank you!");
            return true;
        }

        @Override
        public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
            return Collections.emptyList(); // No tab completion needed for the report command
        }
    }

    public class ReportMenuCommand implements CommandExecutor {
        private final ReportHandler reportHandler;
        private final ReportGUI reportGUI;

        public ReportMenuCommand(ReportHandler reportHandler, ReportGUI reportGUI) {
            this.reportHandler = reportHandler;
            this.reportGUI = reportGUI;
        }

        @Override
        public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
            if (!(sender instanceof Player)) {
                sender.sendMessage("Only players can use this command.");
                return true;
            }

            Player player = (Player) sender;
            reportGUI.openReportMenu(player);
            return true;
        }
    }

    public class ReloadCommand implements CommandExecutor {
        private final YETIsUtils plugin;

        public ReloadCommand(YETIsUtils plugin) {
            this.plugin = plugin;
        }

        @Override
        public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
            plugin.reloadConfig();
            sender.sendMessage("[YETIsUtils] Plugin configurations reloaded.");
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
