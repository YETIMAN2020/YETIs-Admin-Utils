package org.yetiman.yetisutils;

import org.bukkit.Location;

public class Report {
    private final String playerName;
    private final String report;
    private final String date;
    private final String time;
    private final Location location;

    public Report(String playerName, String report, String date, String time, Location location) {
        this.playerName = playerName;
        this.report = report;
        this.date = date;
        this.time = time;
        this.location = location;
    }

    public String getPlayerName() {
        return playerName;
    }

    public String getReport() {
        return report;
    }

    public String getDate() {
        return date;
    }

    public String getTime() {
        return time;
    }

    public Location getLocation() {
        return location;
    }
}
