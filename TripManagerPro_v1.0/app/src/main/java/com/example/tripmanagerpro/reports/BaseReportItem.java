package com.example.tripmanagerpro.reports;

public abstract class BaseReportItem {

    public abstract String formatForReportRow();

    protected String safeValue(String value, int maxLength) {
        if (value == null) {
            return "";
        }

        String trimmed = value.trim();
        if (trimmed.length() <= maxLength) {
            return trimmed;
        }

        return trimmed.substring(0, maxLength - 3) + "...";
    }
}