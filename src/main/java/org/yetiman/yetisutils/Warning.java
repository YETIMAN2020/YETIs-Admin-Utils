package org.yetiman.yetisutils;

import java.util.Date;

public class Warning {
    private final String playerName;
    private final String playerIP;
    private final String reason;
    private final String issuer;
    private final Date date;

    public Warning(String playerName, String playerIP, String reason, String issuer, Date date) {
        this.playerName = playerName;
        this.playerIP = playerIP;
        this.reason = reason;
        this.issuer = issuer;
        this.date = date;
    }

    public String getPlayerName() {
        return playerName;
    }

    public String getPlayerIP() {
        return playerIP;
    }

    public String getReason() {
        return reason;
    }

    public String getIssuer() {
        return issuer;
    }

    public Date getDate() {
        return date;
    }
}
