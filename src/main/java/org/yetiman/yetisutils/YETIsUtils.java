package org.yetiman.yetisutils;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.TextChannel;
import org.bukkit.BanList;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.yetiman.yetisutils.Reportfeatures.ReportCommand;
import org.yetiman.yetisutils.Reportfeatures.ReportGUI;
import org.yetiman.yetisutils.Reportfeatures.ReportHandler;
import org.yetiman.yetisutils.Reportfeatures.ReportMenuCommand;
import org.yetiman.yetisutils.Reportfeatures.ReportNotification;
import org.yetiman.yetisutils.Warningfeatures.WarningClearCommand;
import org.yetiman.yetisutils.Warningfeatures.WarningGUI;
import org.yetiman.yetisutils.Warningfeatures.WarningHandler;

import javax.security.auth.login.LoginException;
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
    private ReportNotification reportNotification;

    private boolean logTimeAndDate;
    private boolean enablePlayerWarningsView;
    private boolean debug;
    private int maxWarningsBeforeBan;
    private boolean enableReportFeature;
    private String discordBotToken;
    private String discordWarningChannelId;
    private String discordReportChannelId;
    private JDA jda;

    public static YETIsUtils getInstance() {
        return instance;
    }

    @Override
    public void onEnable() {
        instance = this;
        saveDefaultConfig();
        loadConfigSettings();
        initializeHandlersAndGUIs();
        registerCommands();
        registerEvents();
        loadInventories();
        initializeDiscordBot();
    }

    @Override
    public void onDisable() {
        saveInventories();
        warningHandler.saveWarnings();
        reportHandler.saveReports();
        if (jda != null) {
            jda.shutdown();
        }
    }

    private void loadConfigSettings() {
        FileConfiguration config = getConfig();
        logTimeAndDate = config.getBoolean("log-time-and-date", true);
        enablePlayerWarningsView = config.getBoolean("enablePlayerWarningsView", true);
        debug = config.getBoolean("debug", false);
        maxWarningsBeforeBan = config.getInt("maxWarningsBeforeBan", 0);
        enableReportFeature = config.getBoolean("enableReportFeature", true);
        discordBotToken = config.getString("discord-bot-token", "");
        discordWarningChannelId = config.getString("discord-warning-channel-id", "");
        discordReportChannelId = config.getString("discord-report-channel-id", "");

        // Debug statements
        getLogger().info("Discord Bot Token: " + discordBotToken);
        getLogger().info("Discord Warning Channel ID: " + discordWarningChannelId);
        getLogger().info("Discord Report Channel ID: " + discordReportChannelId);
    }

    private void initializeHandlersAndGUIs() {
        warningHandler = new WarningHandler(this);
        warningGUI = new WarningGUI(this, warningHandler);
        reportHandler = new ReportHandler(this);
        reportGUI = new ReportGUI(this, reportHandler);
        reportNotification = new ReportNotification(reportHandler);
    }

    private void registerCommands() {
        getCommand("adminmode").setExecutor(new AdminModeCommand());
        getCommand("warn").setExecutor(new WarningCommand(warningHandler));
        getCommand("warn").setTabCompleter(new WarningCommand(warningHandler));
        getCommand("warnings").setExecutor(new WarningCommand(warningHandler));
        getCommand("warnings").setTabCompleter(new WarningCommand(warningHandler));
        getCommand("warnmenu").setExecutor(new WarningGUICommand());
        getCommand("warnclear").setExecutor(new WarningClearCommand(warningHandler));
        getCommand("warnclear").setTabCompleter(new WarningClearCommand(warningHandler));
        if (enablePlayerWarningsView) {
            getCommand("mywarns").setExecutor(new MyWarnsCommand(warningHandler));
        } else {
            getCommand("mywarns").setExecutor(new FeatureDisabledCommand());
        }
        if (enableReportFeature) {
            getCommand("report").setExecutor(new ReportCommand(reportHandler));
            getCommand("report").setTabCompleter(new ReportCommand(reportHandler));
            getCommand("reportmenu").setExecutor(new ReportMenuCommand(reportHandler, reportGUI));
        } else {
            getCommand("report").setExecutor(new FeatureDisabledCommand());
            getCommand("reportmenu").setExecutor(new FeatureDisabledCommand());
        }
        getCommand("yetisutils").setExecutor(new ReloadCommand(this));
    }

    private void registerEvents() {
        Bukkit.getPluginManager().registerEvents(warningGUI, this);
        if (enableReportFeature) {
            Bukkit.getPluginManager().registerEvents(reportGUI, this);
            Bukkit.getPluginManager().registerEvents(reportNotification, this);
        }
    }

    public int getMaxWarningsBeforeBan() {
        return maxWarningsBeforeBan;
    }

    public void notifyDiscord(String message, boolean isWarning) {
        if (jda == null) {
            getLogger().warning("Discord bot is not initialized.");
            return;
        }

        String channelId = isWarning ? discordWarningChannelId : discordReportChannelId;
        TextChannel channel = jda.getTextChannelById(channelId);
        if (channel == null) {
            getLogger().warning("Discord channel ID is not valid.");
            return;
        }

        channel.sendMessage(message).queue();
    }

    private void initializeDiscordBot() {
        if (discordBotToken.isEmpty() || (discordWarningChannelId.isEmpty() && discordReportChannelId.isEmpty())) {
            getLogger().warning("Discord bot token or channel ID is not set.");
            return;
        }

        try {
            jda = JDABuilder.createDefault(discordBotToken).build();
            jda.awaitReady();
            getLogger().info("Discord bot initialized successfully.");
        } catch (LoginException e) {
            getLogger().warning("Invalid token provided for Discord bot.");
        } catch (InterruptedException e) {
            getLogger().warning("Discord bot initialization interrupted.");
            Thread.currentThread().interrupt(); // Restore the interrupted status
        }
    }

    public class AdminModeCommand implements CommandExecutor {
        @Override
        public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
            if (!(sender instanceof Player)) {
                sender.sendMessage(ChatColor.RED + "Only players can use this command.");
                return true;
            }
            Player player = (Player) sender;
            UUID uuid = player.getUniqueId();

            if (adminInventories.containsKey(uuid)) {
                // Switch to player inventory
                playerInventories.put(uuid, player.getInventory().getContents());
                player.getInventory().setContents(adminInventories.remove(uuid));
                player.sendMessage(ChatColor.GREEN + "Switched to player inventory.");
            } else {
                // Switch to admin inventory
                adminInventories.put(uuid, player.getInventory().getContents());
                if (playerInventories.containsKey(uuid)) {
                    player.getInventory().setContents(playerInventories.get(uuid));
                } else {
                    player.getInventory().clear(); // Create an empty admin inventory if none exists
                }
                player.sendMessage(ChatColor.YELLOW + "Switched to admin inventory.");
            }
            return true;
        }
    }

    public class WarningCommand implements CommandExecutor, TabCompleter {
        private final WarningHandler warningHandler;

        public WarningCommand(WarningHandler warningHandler) {
            this.warningHandler = warningHandler;
        }

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
            String dateStr = new SimpleDateFormat("dd-MM-yy HH:mm").format(new Date());

            warningHandler.addWarning(target, reason, issuer, dateStr);

            sender.sendMessage("Player " + target.getName() + " has been warned for: " + reason);

            String warningMessage = "Player " + target.getName() + " has been warned by " + issuer + " for " + reason;
            notifyDiscord(warningMessage, true);

            // Ban the player if they exceed the maxWarningsBeforeBan
            int maxWarningsBeforeBan = YETIsUtils.getInstance().getMaxWarningsBeforeBan();
            if (maxWarningsBeforeBan > 0 && warningHandler.getWarnings(target.getUniqueId()) >= maxWarningsBeforeBan) {
                if (target.isOnline()) {
                    Player onlinePlayer = (Player) target;
                    Bukkit.getBanList(BanList.Type.NAME).addBan(target.getName(), "Exceeded warning limit", null, issuer);
                    Bukkit.getBanList(BanList.Type.IP).addBan(onlinePlayer.getAddress().getAddress().getHostAddress(), "Exceeded warning limit", null, issuer);
                    onlinePlayer.kickPlayer("You have been banned for exceeding the warning limit.");
                } else {
                    Bukkit.getBanList(BanList.Type.NAME).addBan(target.getName(), "Exceeded warning limit", null, issuer);
                }
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

    public class WarningClearCommand implements CommandExecutor, TabCompleter {
        private final WarningHandler warningHandler;

        public WarningClearCommand(WarningHandler warningHandler) {
            this.warningHandler = warningHandler;
        }

        @Override
        public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
            if (args.length < 2) {
                sender.sendMessage(ChatColor.RED + "Usage: /warnclear <player> <warning number or *>");
                return true;
            }
            OfflinePlayer target = Bukkit.getOfflinePlayer(args[0]);
            if (target == null || !target.hasPlayedBefore()) {
                sender.sendMessage(ChatColor.RED + "Player not found.");
                return true;
            }
            UUID playerUUID = target.getUniqueId();
            if (args[1].equals("*")) {
                warningHandler.clearWarnings(playerUUID);
                sender.sendMessage(ChatColor.GREEN + "All warnings for " + ChatColor.WHITE + target.getName() + ChatColor.GREEN + " have been cleared.");
            } else {
                try {
                    int warningNumber = Integer.parseInt(args[1]);
                    if (warningHandler.removeWarning(playerUUID, warningNumber - 1)) {
                        sender.sendMessage(ChatColor.GREEN + "Warning " + warningNumber + " for " + ChatColor.WHITE + target.getName() + ChatColor.GREEN + " has been cleared.");
                    } else {
                        sender.sendMessage(ChatColor.RED + "Warning number " + warningNumber + " not found for " + target.getName() + ".");
                    }
                } catch (NumberFormatException e) {
                    sender.sendMessage(ChatColor.RED + "Invalid warning number: " + args[1]);
                }
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

    public class WarningGUICommand implements CommandExecutor {
        @Override
        public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
            if (!(sender instanceof Player)) {
                sender.sendMessage(ChatColor.RED + "Only players can use this command.");
                return true;
            }
            Player player = (Player) sender;
            warningGUI.openWarningGUI(player);
            return true;
        }
    }

    public class MyWarnsCommand implements CommandExecutor {
        private final WarningHandler warningHandler;

        public MyWarnsCommand(WarningHandler warningHandler) {
            this.warningHandler = warningHandler;
        }

        @Override
        public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
            if (!(sender instanceof Player)) {
                sender.sendMessage(ChatColor.RED + "Only players can use this command.");
                return true;
            }

            Player player = (Player) sender;
            warningGUI.openPlayerWarnings(player, player);
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
            if (!enableReportFeature) {
                sender.sendMessage(ChatColor.RED + "This feature is disabled.");
                return true;
            }

            if (!(sender instanceof Player)) {
                sender.sendMessage(ChatColor.RED + "Only players can use this command.");
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
            sender.sendMessage(ChatColor.GREEN + "Report submitted. Thank you!");

            String reportMessage = "New report from " + player.getName() + ": " + issue;
            notifyDiscord(reportMessage, false);

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
            if (!enableReportFeature) {
                sender.sendMessage(ChatColor.RED + "This feature is disabled.");
                return true;
            }

            if (!(sender instanceof Player)) {
                sender.sendMessage(ChatColor.RED + "Only players can use this command.");
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
            plugin.loadConfigSettings();
            plugin.onDisable();
            plugin.onEnable();
            sender.sendMessage(ChatColor.GREEN + "[YETIsUtils] Plugin configurations reloaded and plugin reset.");
            return true;
        }
    }

    public class FeatureDisabledCommand implements CommandExecutor {
        @Override
        public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
            sender.sendMessage(ChatColor.RED + "This feature is disabled.");
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
