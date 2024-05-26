package org.yetiman.yetisutils;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.UUID;

public class WarningHandler {
    private final YETIsUtils plugin;
    private final FileConfiguration warningsConfig;

    public WarningHandler(YETIsUtils plugin) {
        this.plugin = plugin;
        this.warningsConfig = plugin.getWarningsConfig();
    }

    public void addWarning(Player player, String reason, String issuer) {
        UUID playerId = player.getUniqueId();
        addWarning(playerId, player.getName(), player.getAddress().getAddress().getHostAddress(), reason, issuer);
    }

    public void addWarning(UUID playerId, String playerName, String playerIP, String reason, String issuer) {
        String playerPath = "warnings." + playerId;
        List<String> reasons = warningsConfig.getStringList(playerPath + ".reasons");
        reasons.add(reason);
        List<String> issuers = warningsConfig.getStringList(playerPath + ".issuers");
        issuers.add(issuer);
        List<Long> dates = warningsConfig.getLongList(playerPath + ".dates");
        dates.add(System.currentTimeMillis());
        warningsConfig.set(playerPath + ".name", playerName);
        warningsConfig.set(playerPath + ".ip", playerIP);
        warningsConfig.set(playerPath + ".reasons", reasons);
        warningsConfig.set(playerPath + ".issuers", issuers);
        warningsConfig.set(playerPath + ".dates", dates);
        plugin.saveWarningsConfig();

        // Debugging: Log the warning being added
        plugin.getLogger().info("Warning added: " + reason + " | Issued by: " + issuer + " | Date: " + new java.util.Date(dates.get(dates.size() - 1)));
        plugin.getLogger().info("Current warning data: " + warningsConfig.getStringList(playerPath + ".reasons") +
                ", " + warningsConfig.getStringList(playerPath + ".issuers") + ", " + warningsConfig.getLongList(playerPath + ".dates"));
    }

    public int getWarnings(UUID playerId) {
        String playerPath = "warnings." + playerId;
        List<String> reasons = warningsConfig.getStringList(playerPath + ".reasons");
        int warningsCount = reasons.size();
        // Debugging: Log the number of warnings and reasons list
        plugin.getLogger().info("Number of warnings for player " + playerId + ": " + warningsCount);
        plugin.getLogger().info("Reasons list: " + reasons);
        return warningsCount;
    }

    public String getWarningReason(UUID playerId, int index) {
        String playerPath = "warnings." + playerId + ".reasons";
        List<String> reasons = warningsConfig.getStringList(playerPath);
        if (index >= 0 && index < reasons.size()) {
            String reason = reasons.get(index);
            // Debugging: Log the reason being retrieved
            plugin.getLogger().info("Retrieved reason for warning " + (index + 1) + ": " + reason);
            return reason;
        }
        plugin.getLogger().warning("No reason found for warning " + (index + 1));
        return null;
    }

    public String getWarningIssuer(UUID playerId, int index) {
        String playerPath = "warnings." + playerId + ".issuers";
        List<String> issuers = warningsConfig.getStringList(playerPath);
        if (index >= 0 && index < issuers.size()) {
            String issuer = issuers.get(index);
            // Debugging: Log the issuer being retrieved
            plugin.getLogger().info("Retrieved issuer for warning " + (index + 1) + ": " + issuer);
            return issuer;
        }
        plugin.getLogger().warning("No issuer found for warning " + (index + 1));
        return null;
    }

    public String getWarningDate(UUID playerId, int index) {
        String playerPath = "warnings." + playerId + ".dates";
        List<Long> dates = warningsConfig.getLongList(playerPath);
        if (index >= 0 && index < dates.size()) {
            long timestamp = dates.get(index);
            String date = new SimpleDateFormat("dd-MM-yy HH:mm").format(new java.util.Date(timestamp));
            // Debugging: Log the date being retrieved
            plugin.getLogger().info("Retrieved date for warning " + (index + 1) + ": " + date);
            return date;
        }
        plugin.getLogger().warning("No date found for warning " + (index + 1));
        return null;
    }

    public boolean clearWarning(UUID playerId, int index) {
        String playerPath = "warnings." + playerId;
        List<String> reasons = warningsConfig.getStringList(playerPath + ".reasons");
        List<String> issuers = warningsConfig.getStringList(playerPath + ".issuers");
        List<Long> dates = warningsConfig.getLongList(playerPath + ".dates");
        if (index >= 0 && index < reasons.size()) {
            reasons.remove(index);
            issuers.remove(index);
            dates.remove(index);
            warningsConfig.set(playerPath + ".reasons", reasons);
            warningsConfig.set(playerPath + ".issuers", issuers);
            warningsConfig.set(playerPath + ".dates", dates);
            plugin.saveWarningsConfig();
            // Debugging: Log the warning being cleared
            plugin.getLogger().info("Cleared warning " + (index + 1));
            return true;
        }
        plugin.getLogger().warning("Failed to clear warning " + (index + 1) + " for player " + playerId);
        return false;
    }
}
