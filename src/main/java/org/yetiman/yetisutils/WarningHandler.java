package org.yetiman.yetisutils;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;

public class WarningHandler {
    private final JavaPlugin plugin;
    private final File warningFile;
    private final FileConfiguration warningConfig;
    private final boolean logTimeAndDate;

    private final Map<UUID, WarningData> warnings = new HashMap<>();

    public WarningHandler(JavaPlugin plugin) {
        this.plugin = plugin;
        this.warningFile = new File(plugin.getDataFolder(), "warnings.yml");
        this.warningConfig = YamlConfiguration.loadConfiguration(warningFile);
        this.logTimeAndDate = plugin.getConfig().getBoolean("log-time-and-date", true);
        loadWarnings();
    }

    public void addWarning(UUID playerId, String playerName, String playerIP, String reason, String issuer) {
        WarningData data = warnings.getOrDefault(playerId, new WarningData(playerName, playerIP, 0, new ArrayList<>()));
        if (logTimeAndDate) {
            String timestamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
            data.incrementWarnings(reason, timestamp, issuer);
        } else {
            data.incrementWarnings(reason, null, issuer);
        }
        warnings.put(playerId, data);
        saveWarnings();
    }

    public void addWarning(Player player, String reason, String issuer) {
        UUID playerId = player.getUniqueId();
        String playerName = player.getName();
        String playerIP = player.getAddress().getAddress().getHostAddress();
        addWarning(playerId, playerName, playerIP, reason, issuer);
        player.sendMessage("You have been warned. Total warnings: " + getWarnings(player) + ". Reason: " + reason);
    }

    public int getWarnings(Player player) {
        return warnings.getOrDefault(player.getUniqueId(), new WarningData(player.getName(), player.getAddress().getAddress().getHostAddress(), 0, new ArrayList<>())).getWarnings();
    }

    public int getWarnings(UUID playerId) {
        return warnings.getOrDefault(playerId, new WarningData("", "", 0, new ArrayList<>())).getWarnings();
    }

    public List<String> getReasons(UUID playerId) {
        return warnings.getOrDefault(playerId, new WarningData("", "", 0, new ArrayList<>())).getDetailedReasons();
    }

    public Map<UUID, WarningData> getAllWarnings() {
        return warnings;
    }

    public boolean clearWarning(UUID playerId, int index) {
        WarningData data = warnings.get(playerId);
        if (data != null && index >= 0 && index < data.getWarnings()) {
            data.removeWarning(index);
            if (data.getWarnings() == 0) {
                warnings.remove(playerId);
            }
            saveWarnings();
            return true;
        }
        return false;
    }

    private void loadWarnings() {
        if (warningFile.exists()) {
            for (String key : warningConfig.getKeys(false)) {
                UUID uuid = UUID.fromString(key);
                String name = warningConfig.getString(key + ".name");
                String ip = warningConfig.getString(key + ".ip");
                int count = warningConfig.getInt(key + ".warnings");
                List<String> reasons = warningConfig.getStringList(key + ".reasons");
                warnings.put(uuid, new WarningData(name, ip, count, reasons));
            }
        }
    }

    private void saveWarnings() {
        for (Map.Entry<UUID, WarningData> entry : warnings.entrySet()) {
            UUID uuid = entry.getKey();
            WarningData data = entry.getValue();
            warningConfig.set(uuid.toString() + ".name", data.getName());
            warningConfig.set(uuid.toString() + ".ip", data.getIp());
            warningConfig.set(uuid.toString() + ".warnings", data.getWarnings());
            warningConfig.set(uuid.toString() + ".reasons", data.getDetailedReasons());
        }
        try {
            warningConfig.save(warningFile);
        } catch (IOException e) {
            plugin.getLogger().warning("Failed to save warnings: " + e.getMessage());
        }
    }

    public static class WarningData {
        private final String name;
        private final String ip;
        private int warnings;
        private List<String> detailedReasons;

        public WarningData(String name, String ip, int warnings, List<String> detailedReasons) {
            this.name = name;
            this.ip = ip;
            this.warnings = warnings;
            this.detailedReasons = detailedReasons;
        }

        public String getName() {
            return name;
        }

        public String getIp() {
            return ip;
        }

        public int getWarnings() {
            return warnings;
        }

        public List<String> getDetailedReasons() {
            return detailedReasons;
        }

        public void setDetailedReasons(List<String> detailedReasons) {
            this.detailedReasons = detailedReasons;
        }

        public void incrementWarnings(String reason, String timestamp, String issuer) {
            this.warnings++;
            String detailedReason = "Reason: " + reason + "\nIssued by: " + issuer;
            if (timestamp != null) {
                detailedReason += "\nDate: " + timestamp;
            }
            this.detailedReasons.add(detailedReason);
        }

        public void removeWarning(int index) {
            if (index >= 0 && index < this.detailedReasons.size()) {
                this.detailedReasons.remove(index);
                this.warnings--;
            }
        }
    }
}
