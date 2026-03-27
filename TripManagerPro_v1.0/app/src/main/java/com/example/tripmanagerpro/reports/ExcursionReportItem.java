package com.example.tripmanagerpro.reports;

import java.util.Locale;

public class ExcursionReportItem extends BaseReportItem {

    private final String title;
    private final String date;

    public ExcursionReportItem(String title, String date) {
        this.title = title;
        this.date = date;
    }

    @Override
    public String formatForReportRow() {
        return String.format(
                Locale.US,
                "%-24s %-14s%n",
                safeValue(title, 24),
                safeValue(date, 14)
        );
    }
}