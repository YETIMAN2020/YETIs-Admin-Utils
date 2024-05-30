package org.yetiman.yetisutils.Reportfeatures;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.ChatColor;
import org.yetiman.yetisutils.YETIsUtils;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;

public class ReportHandler {
    private final YETIsUtils plugin;
    private final Map<String, Map<String, String>> reports = new HashMap<>();
    private final ReportNotification reportNotification;

    public ReportHandler(YETIsUtils plugin) {
        this.plugin = plugin;
        this.reportNotification = new ReportNotification(this);
        loadReports();
    }

    public YETIsUtils getPlugin() {
        return plugin;
    }

    public void addReport(OfflinePlayer player, String issue, Location location) {
        String reportId = generateReportId(player);
        Map<String, String> reportData = new HashMap<>();
        reportData.put("player", player.getName());
        reportData.put("issue", issue);
        reportData.put("world", location.getWorld().getName());
        reportData.put("x", String.valueOf(location.getX()));
        reportData.put("y", String.valueOf(location.getY()));
        reportData.put("z", String.valueOf(location.getZ()));
        reportData.put("date", new SimpleDateFormat("dd-MM-yy HH:mm").format(new Date()));

        reports.put(reportId, reportData);
        saveReportToFile(reportId, reportData);
        notifyAdmins(player.getName(), issue);
    }

    private String generateReportId(OfflinePlayer player) {
        int count = 1;
        while (reports.containsKey(player.getName() + count)) {
            count++;
        }
        return player.getName() + count;
    }

    public int getOpenReportsCount() {
        return reports.size();
    }

    public List<Map.Entry<String, Map<String, String>>> getAllReports() {
        return new ArrayList<>(reports.entrySet());
    }

    public Map<String, String> getReport(String reportId) {
        return reports.get(reportId);
    }

    public void removeReport(String reportId) {
        reports.remove(reportId);
        File reportFile = new File(plugin.getDataFolder(), "reports/" + reportId + ".yml");
        if (reportFile.exists()) {
            reportFile.delete();
        }
    }

    private void saveReportToFile(String reportId, Map<String, String> reportData) {
        File reportFile = new File(plugin.getDataFolder(), "reports/" + reportId + ".yml");
        FileConfiguration config = YamlConfiguration.loadConfiguration(reportFile);
        for (Map.Entry<String, String> entry : reportData.entrySet()) {
            config.set(entry.getKey(), entry.getValue());
        }
        try {
            config.save(reportFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void loadReports() {
        reports.clear();
        File reportsFolder = new File(plugin.getDataFolder(), "reports");
        if (!reportsFolder.exists()) {
            reportsFolder.mkdirs();
        }
        File[] files = reportsFolder.listFiles((dir, name) -> name.endsWith(".yml"));
        if (files != null) {
            for (File file : files) {
                FileConfiguration config = YamlConfiguration.loadConfiguration(file);
                Map<String, String> reportData = new HashMap<>();
                for (String key : config.getKeys(false)) {
                    reportData.put(key, config.getString(key));
                }
                reports.put(file.getName().replace(".yml", ""), reportData);
            }
        }
    }

    public void saveReports() {
        for (Map.Entry<String, Map<String, String>> entry : reports.entrySet()) {
            saveReportToFile(entry.getKey(), entry.getValue());
        }
    }

    private void notifyAdmins(String playerName, String issue) {
        reportNotification.notifyNewReport(playerName, issue);
    }
}
