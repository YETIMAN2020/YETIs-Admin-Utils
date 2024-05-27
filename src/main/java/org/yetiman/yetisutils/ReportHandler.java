package org.yetiman.yetisutils;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;

public class ReportHandler {
    private final YETIsUtils plugin;
    private final Map<String, Map<String, String>> reports;
    private final File reportFile;
    private final FileConfiguration reportConfig;

    public ReportHandler(YETIsUtils plugin) {
        this.plugin = plugin;
        this.reports = new HashMap<>();
        this.reportFile = new File(plugin.getDataFolder(), "reports.yml");
        this.reportConfig = YamlConfiguration.loadConfiguration(reportFile);
        loadReports();
    }

    public void addReport(Player player, String issue, Location location) {
        String playerName = player.getName();
        int reportNumber = 1;
        while (reports.containsKey(playerName + reportNumber)) {
            reportNumber++;
        }
        String reportId = playerName + reportNumber;

        Map<String, String> report = new HashMap<>();
        report.put("player", playerName);
        report.put("issue", issue);
        report.put("world", location.getWorld().getName());
        report.put("x", String.valueOf(location.getX()));
        report.put("y", String.valueOf(location.getY()));
        report.put("z", String.valueOf(location.getZ()));
        report.put("date", new SimpleDateFormat("dd-MM-yy HH:mm").format(new Date()));
        reports.put(reportId, report);
        saveReports();
    }

    public Map<String, Map<String, String>> getReports() {
        return reports;
    }

    public Location getReportLocation(String reportId) {
        Map<String, String> report = reports.get(reportId);
        if (report != null) {
            String world = report.get("world");
            double x = Double.parseDouble(report.get("x"));
            double y = Double.parseDouble(report.get("y"));
            double z = Double.parseDouble(report.get("z"));
            return new Location(Bukkit.getWorld(world), x, y, z);
        }
        return null;
    }

    public void clearReport(String reportId) {
        if (reports.containsKey(reportId)) {
            reports.remove(reportId);
            saveReports();
        }
    }

    public void loadReports() {
        reports.clear();
        if (reportFile.exists()) {
            for (String key : reportConfig.getKeys(false)) {
                Map<String, String> report = new HashMap<>();
                report.put("player", reportConfig.getString(key + ".player"));
                report.put("issue", reportConfig.getString(key + ".issue"));
                report.put("world", reportConfig.getString(key + ".world"));
                report.put("x", reportConfig.getString(key + ".x"));
                report.put("y", reportConfig.getString(key + ".y"));
                report.put("z", reportConfig.getString(key + ".z"));
                report.put("date", reportConfig.getString(key + ".date"));
                reports.put(key, report);
            }
        }
    }

    public void saveReports() {
        for (String key : reports.keySet()) {
            Map<String, String> report = reports.get(key);
            reportConfig.set(key + ".player", report.get("player"));
            reportConfig.set(key + ".issue", report.get("issue"));
            reportConfig.set(key + ".world", report.get("world"));
            reportConfig.set(key + ".x", report.get("x"));
            reportConfig.set(key + ".y", report.get("y"));
            reportConfig.set(key + ".z", report.get("z"));
            reportConfig.set(key + ".date", report.get("date"));
        }
        try {
            reportConfig.save(reportFile);
        } catch (IOException e) {
            plugin.getLogger().warning("Failed to save reports: " + e.getMessage());
        }
    }
}
