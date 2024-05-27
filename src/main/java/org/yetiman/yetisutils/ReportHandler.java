package org.yetiman.yetisutils;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;

public class ReportHandler {
    private final YETIsUtils plugin;
    private final Map<UUID, List<Report>> reports = new HashMap<>();

    public ReportHandler(YETIsUtils plugin) {
        this.plugin = plugin;
        loadReports();
    }

    public void addReport(UUID reporterId, String reporterName, String issue) {
        Report report = new Report(reporterName, issue, new Date());
        reports.computeIfAbsent(reporterId, k -> new ArrayList<>()).add(report);
        saveReports();
    }

    public List<Report> getReports(UUID reporterId) {
        return reports.getOrDefault(reporterId, Collections.emptyList());
    }

    public List<Report> getAllReports() {
        List<Report> allReports = new ArrayList<>();
        for (List<Report> reportList : reports.values()) {
            allReports.addAll(reportList);
        }
        return allReports;
    }

    public void removeReport(UUID reporterId, int index) {
        List<Report> reporterReports = reports.get(reporterId);
        if (reporterReports != null && index >= 0 && index < reporterReports.size()) {
            reporterReports.remove(index);
            if (reporterReports.isEmpty()) {
                reports.remove(reporterId);
            }
            saveReports();
        }
    }

    public UUID getReporterUUID(Report report) {
        for (Map.Entry<UUID, List<Report>> entry : reports.entrySet()) {
            if (entry.getValue().contains(report)) {
                return entry.getKey();
            }
        }
        return null;
    }

    private void loadReports() {
        File file = new File(plugin.getDataFolder(), "reports.yml");
        if (!file.exists()) {
            return;
        }
        FileConfiguration config = YamlConfiguration.loadConfiguration(file);
        for (String key : config.getKeys(false)) {
            UUID reporterId = UUID.fromString(key);
            List<Report> reporterReports = new ArrayList<>();
            List<Map<?, ?>> reportList = config.getMapList(key);
            for (Map<?, ?> map : reportList) {
                String reporter = (String) map.get("reporter");
                String issue = (String) map.get("issue");
                Date date = new Date((Long) map.get("date"));
                reporterReports.add(new Report(reporter, issue, date));
            }
            reports.put(reporterId, reporterReports);
        }
    }

    private void saveReports() {
        File file = new File(plugin.getDataFolder(), "reports.yml");
        FileConfiguration config = YamlConfiguration.loadConfiguration(file);
        for (Map.Entry<UUID, List<Report>> entry : reports.entrySet()) {
            List<Map<String, Object>> reportList = new ArrayList<>();
            for (Report report : entry.getValue()) {
                Map<String, Object> map = new HashMap<>();
                map.put("reporter", report.getReporter());
                map.put("issue", report.getIssue());
                map.put("date", report.getDate().getTime());
                reportList.add(map);
            }
            config.set(entry.getKey().toString(), reportList);
        }
        try {
            config.save(file);
        } catch (IOException e) {
            plugin.getLogger().warning("Failed to save reports: " + e.getMessage());
        }
    }
}
