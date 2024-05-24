package org.yetiman.yetisutils;

import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class WarningHandler {
    private final JavaPlugin plugin;
    private final File warningFile;
    private final FileConfiguration warningConfig;

    private final Map<UUID, WarningData> warnings = new HashMap<>();

    public WarningHandler(JavaPlugin plugin) {
        this.plugin = plugin;
        this.warningFile = new File(plugin.getDataFolder(), "warnings.yml");
        this.warningConfig = YamlConfiguration.loadConfiguration(warningFile);
        loadWarnings();
    }

    public void addWarning(Player player) {
        UUID playerId = player.getUniqueId();
        String playerName = player.getName();
        String playerIP = player.getAddress().getAddress().getHostAddress();
        WarningData data = warnings.getOrDefault(playerId, new WarningData(playerName, playerIP, 0));
        data.incrementWarnings();
        warnings.put(playerId, data);
        saveWarnings();
        player.sendMessage("You have been warned. Total warnings: " + data.getWarnings());
    }

    public int getWarnings(Player player) {
        return warnings.getOrDefault(player.getUniqueId(), new WarningData(player.getName(), player.getAddress().getAddress().getHostAddress(), 0)).getWarnings();
    }

    private void loadWarnings() {
        if (warningFile.exists()) {
            for (String key : warningConfig.getKeys(false)) {
                UUID uuid = UUID.fromString(key);
                String name = warningConfig.getString(key + ".name");
                String ip = warningConfig.getString(key + ".ip");
                int count = warningConfig.getInt(key + ".warnings");
                warnings.put(uuid, new WarningData(name, ip, count));
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
        }
        try {
            warningConfig.save(warningFile);
        } catch (IOException e) {
            plugin.getLogger().warning("Failed to save warnings: " + e.getMessage());
        }
    }

    private static class WarningData {
        private final String name;
        private final String ip;
        private int warnings;

        public WarningData(String name, String ip, int warnings) {
            this.name = name;
            this.ip = ip;
            this.warnings = warnings;
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

        public void incrementWarnings() {
            this.warnings++;
        }
    }
}
