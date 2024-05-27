package org.yetiman.yetisutils;

import java.util.Date;

public class Report {
    private final String reporter;
    private final String issue;
    private final Date date;

    public Report(String reporter, String issue, Date date) {
        this.reporter = reporter;
        this.issue = issue;
        this.date = date;
    }

    public String getReporter() {
        return reporter;
    }

    public String getIssue() {
        return issue;
    }

    public Date getDate() {
        return date;
    }
}
