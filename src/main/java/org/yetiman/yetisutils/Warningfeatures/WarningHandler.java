package org.yetiman.yetisutils.Warningfeatures;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.yetiman.yetisutils.YETIsUtils;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;

public class WarningHandler {
    private final YETIsUtils plugin;
    private final Map<UUID, List<WarningRecord>> warnings = new HashMap<>();

    public WarningHandler(YETIsUtils plugin) {
        this.plugin = plugin;
        loadWarnings();
    }

    public void addWarning(OfflinePlayer player, String reason, String issuer, String date) {
        UUID playerUUID = player.getUniqueId();
        List<WarningRecord> playerWarnings = warnings.computeIfAbsent(playerUUID, k -> new ArrayList<>());
        playerWarnings.add(new WarningRecord(reason, issuer, date));
        saveWarningToFile(player, playerWarnings);

        // Notify the player if they are online
        if (player.isOnline()) {
            Player onlinePlayer = (Player) player;
            onlinePlayer.sendMessage(ChatColor.RED + "You have been warned for: " + ChatColor.YELLOW + reason);
        }
        // Notify the issuer
        Player issuerPlayer = Bukkit.getPlayer(issuer);
        if (issuerPlayer != null && issuerPlayer.isOnline()) {
            issuerPlayer.sendMessage(ChatColor.GREEN + "Warning issued to " + ChatColor.WHITE + player.getName() + ChatColor.GREEN + " for: " + ChatColor.YELLOW + reason);
        }

        OfflinePlayer target = Bukkit.getOfflinePlayer(playerUUID);
        // Send a single notification to Discord
        String warningMessage = "Player " + target.getName() + " has been warned by " + issuer + " for " + reason;
        YETIsUtils.getInstance().notifyDiscord(warningMessage, true);

    }

    public int getWarnings(UUID playerUUID) {
        List<WarningRecord> playerWarnings = warnings.get(playerUUID);
        return playerWarnings != null ? playerWarnings.size() : 0;
    }

    public String getWarningReason(UUID playerUUID, int index) {
        List<WarningRecord> playerWarnings = warnings.get(playerUUID);
        if (playerWarnings != null && index < playerWarnings.size()) {
            return playerWarnings.get(index).getReason();
        }
        return null;
    }

    public String getWarningIssuer(UUID playerUUID, int index) {
        List<WarningRecord> playerWarnings = warnings.get(playerUUID);
        if (playerWarnings != null && index < playerWarnings.size()) {
            return playerWarnings.get(index).getIssuer();
        }
        return null;
    }

    public String getWarningDate(UUID playerUUID, int index) {
        List<WarningRecord> playerWarnings = warnings.get(playerUUID);
        if (playerWarnings != null && index < playerWarnings.size()) {
            return playerWarnings.get(index).getDate();
        }
        return null;
    }

    public void clearWarnings(UUID playerUUID) {
        warnings.remove(playerUUID);
        File warningFile = new File(plugin.getDataFolder(), "warnings/" + playerUUID + ".yml");
        if (warningFile.exists()) {
            warningFile.delete();
        }
    }

    public boolean removeWarning(UUID playerUUID, int index) {
        List<WarningRecord> playerWarnings = warnings.get(playerUUID);
        if (playerWarnings != null && index < playerWarnings.size()) {
            playerWarnings.remove(index);
            OfflinePlayer player = Bukkit.getOfflinePlayer(playerUUID);
            saveWarningToFile(player, playerWarnings);
            return true;
        }
        return false;
    }

    public void loadWarnings() {
        warnings.clear();
        File warningsFolder = new File(plugin.getDataFolder(), "warnings");
        if (!warningsFolder.exists()) {
            warningsFolder.mkdirs();
        }
        File[] files = warningsFolder.listFiles((dir, name) -> name.endsWith(".yml"));
        if (files != null) {
            for (File file : files) {
                UUID playerUUID = UUID.fromString(file.getName().replace(".yml", ""));
                FileConfiguration config = YamlConfiguration.loadConfiguration(file);
                List<WarningRecord> playerWarnings = new ArrayList<>();
                for (String key : config.getKeys(false)) {
                    if (key.equals("Player") || key.equals("IP")) continue;
                    String reason = config.getString(key + ".reason");
                    String issuer = config.getString(key + ".issuer");
                    String date = config.getString(key + ".date");
                    playerWarnings.add(new WarningRecord(reason, issuer, date));
                }
                warnings.put(playerUUID, playerWarnings);
            }
        }
    }

    public void saveWarnings() {
        for (Map.Entry<UUID, List<WarningRecord>> entry : warnings.entrySet()) {
            OfflinePlayer player = Bukkit.getOfflinePlayer(entry.getKey());
            saveWarningToFile(player, entry.getValue());
        }
    }

    private void saveWarningToFile(OfflinePlayer player, List<WarningRecord> playerWarnings) {
        File warningFile = new File(plugin.getDataFolder(), "warnings/" + player.getUniqueId() + ".yml");
        FileConfiguration config = YamlConfiguration.loadConfiguration(warningFile);

        // Set player name and IP at the top of the file
        config.set("Player", player.getName());
        if (player.isOnline()) {
            Player onlinePlayer = (Player) player;
            config.set("IP", onlinePlayer.getAddress().getAddress().getHostAddress());
        }

        for (int i = 0; i < playerWarnings.size(); i++) {
            WarningRecord warning = playerWarnings.get(i);
            config.set(i + ".reason", warning.getReason());
            config.set(i + ".issuer", warning.getIssuer());
            config.set(i + ".date", warning.getDate());
        }
        try {
            config.save(warningFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public List<WarningRecord> getPlayerWarnings(UUID playerUUID) {
        return warnings.getOrDefault(playerUUID, new ArrayList<>());
    }

    public Set<UUID> getPlayersWithWarnings() {
        return warnings.keySet();
    }

    public static class WarningRecord {
        private final String reason;
        private final String issuer;
        private final String date;

        public WarningRecord(String reason, String issuer, String date) {
            this.reason = reason;
            this.issuer = issuer;
            this.date = date;
        }

        public String getReason() {
            return reason;
        }

        public String getIssuer() {
            return issuer;
        }

        public String getDate() {
            return date;
        }
    }
}
