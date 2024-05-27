package org.yetiman.yetisutils;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class WarningHandler {
    private final YETIsUtils plugin;
    private final File warningsFile;
    private final FileConfiguration warningsConfig;

    private final Map<UUID, List<Map<String, String>>> warnings = new HashMap<>();

    public WarningHandler(YETIsUtils plugin) {
        this.plugin = plugin;
        this.warningsFile = new File(plugin.getDataFolder(), "warnings.yml");
        this.warningsConfig = YamlConfiguration.loadConfiguration(warningsFile);
        loadWarnings();
    }

    public void addWarning(OfflinePlayer player, String reason, String issuer, String date) {
        UUID uuid = player.getUniqueId();
        List<Map<String, String>> playerWarnings = warnings.computeIfAbsent(uuid, k -> new ArrayList<>());

        Map<String, String> warning = new HashMap<>();
        warning.put("reason", reason);
        warning.put("issuer", issuer);
        warning.put("date", date);
        warning.put("ip", player.isOnline() ? player.getPlayer().getAddress().getAddress().getHostAddress() : "N/A");
        playerWarnings.add(warning);

        saveWarnings();
    }

    public int getWarnings(UUID uuid) {
        List<Map<String, String>> playerWarnings = warnings.get(uuid);
        return (playerWarnings != null) ? playerWarnings.size() : 0;
    }

    public String getWarningReason(UUID uuid, int index) {
        List<Map<String, String>> playerWarnings = warnings.get(uuid);
        if (playerWarnings != null && index >= 0 && index < playerWarnings.size()) {
            return playerWarnings.get(index).get("reason");
        }
        return null;
    }

    public String getWarningIssuer(UUID uuid, int index) {
        List<Map<String, String>> playerWarnings = warnings.get(uuid);
        if (playerWarnings != null && index >= 0 && index < playerWarnings.size()) {
            return playerWarnings.get(index).get("issuer");
        }
        return null;
    }

    public String getWarningDate(UUID uuid, int index) {
        List<Map<String, String>> playerWarnings = warnings.get(uuid);
        if (playerWarnings != null && index >= 0 && index < playerWarnings.size()) {
            return playerWarnings.get(index).get("date");
        }
        return null;
    }

    public String getWarningIP(UUID uuid, int index) {
        List<Map<String, String>> playerWarnings = warnings.get(uuid);
        if (playerWarnings != null && index >= 0 && index < playerWarnings.size()) {
            return playerWarnings.get(index).get("ip");
        }
        return null;
    }

    public boolean clearWarning(UUID uuid, int index) {
        List<Map<String, String>> playerWarnings = warnings.get(uuid);
        if (playerWarnings != null && index >= 0 && index < playerWarnings.size()) {
            playerWarnings.remove(index);
            saveWarnings();
            return true;
        }
        return false;
    }

    public List<UUID> getWarnedPlayers() {
        return new ArrayList<>(warnings.keySet());
    }

    public List<Map<String, String>> getWarningsList(UUID uuid) {
        return warnings.getOrDefault(uuid, new ArrayList<>());
    }

    public void loadWarnings() {
        if (!warningsFile.exists()) {
            return;
        }

        warnings.clear();
        for (String key : warningsConfig.getKeys(false)) {
            UUID uuid = UUID.fromString(key);
            List<Map<String, String>> playerWarnings = (List<Map<String, String>>) warningsConfig.getList(key);
            warnings.put(uuid, playerWarnings);
        }
    }

    public void saveWarnings() {
        for (Map.Entry<UUID, List<Map<String, String>>> entry : warnings.entrySet()) {
            warningsConfig.set(entry.getKey().toString(), entry.getValue());
        }
        try {
            warningsConfig.save(warningsFile);
        } catch (IOException e) {
            plugin.getLogger().warning("Failed to save warnings: " + e.getMessage());
        }
    }
}
