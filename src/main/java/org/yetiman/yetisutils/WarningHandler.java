package org.yetiman.yetisutils;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;

public class WarningHandler {
    private final YETIsUtils plugin;
    private final File warningsFile;
    private final FileConfiguration warningsConfig;

    public WarningHandler(YETIsUtils plugin) {
        this.plugin = plugin;
        this.warningsFile = new File(plugin.getDataFolder(), "warnings.yml");
        this.warningsConfig = YamlConfiguration.loadConfiguration(warningsFile);
    }

    public void addWarning(Player player, String reason, String issuer) {
        UUID playerId = player.getUniqueId();
        String playerName = player.getName();
        String playerIP = player.getAddress().getAddress().getHostAddress();
        addWarning(playerId, playerName, playerIP, reason, issuer);
    }

    public void addWarning(UUID playerId, String playerName, String playerIP, String reason, String issuer) {
        List<Map<String, String>> warnings = (List<Map<String, String>>) warningsConfig.getList(playerId.toString(), new ArrayList<>());
        Map<String, String> warning = new HashMap<>();
        warning.put("Reason", reason);
        warning.put("IssuedBy", issuer);
        warning.put("Date", new SimpleDateFormat("dd-MM-yy HH-mm").format(new Date()));
        warnings.add(warning);
        warningsConfig.set(playerId.toString(), warnings);
        saveWarningsConfig();
    }

    public int getWarnings(UUID playerId) {
        List<Map<String, String>> warnings = (List<Map<String, String>>) warningsConfig.getList(playerId.toString(), new ArrayList<>());
        return warnings.size();
    }

    public String getWarningReason(UUID playerId, int index) {
        List<Map<String, String>> warnings = (List<Map<String, String>>) warningsConfig.getList(playerId.toString(), new ArrayList<>());
        if (index < warnings.size()) {
            return warnings.get(index).get("Reason");
        }
        return null;
    }

    public String getWarningIssuer(UUID playerId, int index) {
        List<Map<String, String>> warnings = (List<Map<String, String>>) warningsConfig.getList(playerId.toString(), new ArrayList<>());
        if (index < warnings.size()) {
            return warnings.get(index).get("IssuedBy");
        }
        return null;
    }

    public String getWarningDate(UUID playerId, int index) {
        List<Map<String, String>> warnings = (List<Map<String, String>>) warningsConfig.getList(playerId.toString(), new ArrayList<>());
        if (index < warnings.size()) {
            return warnings.get(index).get("Date");
        }
        return null;
    }

    public boolean clearWarning(UUID playerId, int index) {
        List<Map<String, String>> warnings = (List<Map<String, String>>) warningsConfig.getList(playerId.toString(), new ArrayList<>());
        if (index < warnings.size()) {
            warnings.remove(index);
            warningsConfig.set(playerId.toString(), warnings);
            saveWarningsConfig();
            return true;
        }
        return false;
    }

    private void saveWarningsConfig() {
        try {
            warningsConfig.save(warningsFile);
        } catch (IOException e) {
            plugin.getLogger().severe("Could not save warnings config: " + e.getMessage());
        }
    }
}
