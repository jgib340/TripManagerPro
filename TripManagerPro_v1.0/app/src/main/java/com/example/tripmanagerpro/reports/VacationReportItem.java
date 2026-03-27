package com.example.tripmanagerpro.reports;

import java.util.Locale;

public class VacationReportItem extends BaseReportItem {

    private final String title;
    private final String hotel;
    private final String startDate;
    private final String endDate;

    public VacationReportItem(String title, String hotel, String startDate, String endDate) {
        this.title = title;
        this.hotel = hotel;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    @Override
    public String formatForReportRow() {
        return String.format(
                Locale.US,
                "%-18s %-18s %-12s %-12s%n",
                safeValue(title, 18),
                safeValue(hotel, 18),
                safeValue(startDate, 12),
                safeValue(endDate, 12)
        );
    }
}