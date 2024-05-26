package org.yetiman.yetisutils;

import org.bukkit.BanList;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.text.SimpleDateFormat;
import java.util.*;

public class WarningHandler {
    private final YETIsUtils plugin;
    private final Map<UUID, List<Warning>> warnings = new HashMap<>();

    public WarningHandler(YETIsUtils plugin) {
        this.plugin = plugin;
        loadWarnings();
    }

    public void addWarning(Player player, String reason, String issuer) {
        UUID playerId = player.getUniqueId();
        String playerName = player.getName();
        String playerIP = player.getAddress().getAddress().getHostAddress();
        addWarning(playerId, playerName, playerIP, reason, issuer);
    }

    public void addWarning(UUID playerId, String playerName, String playerIP, String reason, String issuer) {
        Warning warning = new Warning(playerName, playerIP, reason, issuer, new Date());
        warnings.computeIfAbsent(playerId, k -> new ArrayList<>()).add(warning);
        saveWarnings();
        checkAndBan(playerId);
    }

    public int getWarnings(UUID playerId) {
        return warnings.getOrDefault(playerId, Collections.emptyList()).size();
    }

    public String getWarningReason(UUID playerId, int index) {
        List<Warning> playerWarnings = warnings.get(playerId);
        if (playerWarnings == null || index < 0 || index >= playerWarnings.size()) {
            return null;
        }
        return playerWarnings.get(index).getReason();
    }

    public String getWarningIssuer(UUID playerId, int index) {
        List<Warning> playerWarnings = warnings.get(playerId);
        if (playerWarnings == null || index < 0 || index >= playerWarnings.size()) {
            return null;
        }
        return playerWarnings.get(index).getIssuer();
    }

    public String getWarningDate(UUID playerId, int index) {
        List<Warning> playerWarnings = warnings.get(playerId);
        if (playerWarnings == null || index < 0 || index >= playerWarnings.size()) {
            return null;
        }
        SimpleDateFormat dateFormatter = new SimpleDateFormat("dd-MM-yyyy HH:mm");
        return dateFormatter.format(playerWarnings.get(index).getDate());
    }

    public boolean clearWarning(UUID playerId, int index) {
        List<Warning> playerWarnings = warnings.get(playerId);
        if (playerWarnings == null || index < 0 || index >= playerWarnings.size()) {
            return false;
        }
        playerWarnings.remove(index);
        saveWarnings();
        return true;
    }

    private void checkAndBan(UUID playerId) {
        int maxWarnings = plugin.getConfig().getInt("maxWarningsBeforeBan", 0);
        if (maxWarnings > 0 && getWarnings(playerId) >= maxWarnings) {
            OfflinePlayer player = Bukkit.getOfflinePlayer(playerId);
            String playerIP = getPlayerIP(player);
            if (playerIP != null) {
                Bukkit.getBanList(BanList.Type.IP).addBan(playerIP, "Too many warnings", null, null);
                if (player.isOnline()) {
                    player.getPlayer().kickPlayer("You have been banned due to too many warnings.");
                }
            }
        }
    }

    private String getPlayerIP(OfflinePlayer player) {
        if (player.isOnline()) {
            return player.getPlayer().getAddress().getAddress().getHostAddress();
        }
        return null; // You need a way to get IP for offline players if you want to ban them
    }

    private void loadWarnings() {
        FileConfiguration config = plugin.getWarningsConfig();
        for (String key : config.getKeys(false)) {
            UUID playerId = UUID.fromString(key);
            List<Warning> playerWarnings = new ArrayList<>();
            List<Map<?, ?>> warningList = config.getMapList(key);
            for (Map<?, ?> map : warningList) {
                String playerName = (String) map.get("playerName");
                String playerIP = (String) map.get("playerIP");
                String reason = (String) map.get("reason");
                String issuer = (String) map.get("issuer");
                Date date = new Date((Long) map.get("date"));
                playerWarnings.add(new Warning(playerName, playerIP, reason, issuer, date));
            }
            warnings.put(playerId, playerWarnings);
        }
    }

    private void saveWarnings() {
        FileConfiguration config = plugin.getWarningsConfig();
        for (Map.Entry<UUID, List<Warning>> entry : warnings.entrySet()) {
            List<Map<String, Object>> warningList = new ArrayList<>();
            for (Warning warning : entry.getValue()) {
                Map<String, Object> map = new HashMap<>();
                map.put("playerName", warning.getPlayerName());
                map.put("playerIP", warning.getPlayerIP());
                map.put("reason", warning.getReason());
                map.put("issuer", warning.getIssuer());
                map.put("date", warning.getDate().getTime());
                warningList.add(map);
            }
            config.set(entry.getKey().toString(), warningList);
        }
        plugin.saveWarningsConfig();
    }

    public Set<UUID> getAllPlayerUUIDs() {
        return warnings.keySet();
    }
}
