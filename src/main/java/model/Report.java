package com.movieapp.filmplatform.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Data
@NoArgsConstructor
@AllArgsConstructor
public abstract class Report {
    private String reportId;
    private String reportName;
    private LocalDateTime generatedDate;
    private String reportType;
    private String summary;
    private LocalDate startDate;
    private LocalDate endDate;

    public abstract String generateContent();
    public abstract String getChartData();
    public abstract String getIconClass();

    public String getFormattedDate() {
        if (generatedDate == null) return "";
        return generatedDate.format(DateTimeFormatter.ofPattern("MMM dd, yyyy HH:mm"));
    }

    public String getDateRange() {
        if (startDate != null && endDate != null) {
            return startDate.format(DateTimeFormatter.ofPattern("MMM dd, yyyy")) +
                    " to " + endDate.format(DateTimeFormatter.ofPattern("MMM dd, yyyy"));
        }
        return "All Time";
    }
}