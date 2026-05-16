package com.movieapp.filmplatform.controller;

import com.movieapp.filmplatform.model.*;
import com.movieapp.filmplatform.service.ReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Controller
public class ReportController {

    @Autowired
    private ReportService reportService;

    // ==================== MAIN DASHBOARD ====================

    @GetMapping("/admin/dashboard")
    public String dashboard(Model model, HttpSession session) throws IOException {
        User user = (User) session.getAttribute("loggedInUser");
        if (user == null || !user.getRole().equals("admin")) return "redirect:/login";

        DashboardStats stats = reportService.getDashboardStats();
        List<Report> savedReports = reportService.getSavedReports();

        model.addAttribute("stats", stats);
        model.addAttribute("savedReports", savedReports);
        model.addAttribute("loggedInUser", user);
        return "admin/dashboard/dashboard";
    }

    // ==================== ALL REPORTS PAGE ====================

    @GetMapping("/admin/reports")
    public String allReports(Model model, HttpSession session) throws IOException {
        User user = (User) session.getAttribute("loggedInUser");
        if (user == null || !user.getRole().equals("admin")) return "redirect:/login";

        Map<String, Report> reports = reportService.generateAllReports(null, null);
        List<Report> savedReports = reportService.getSavedReports();

        model.addAttribute("reports", reports);
        model.addAttribute("savedReports", savedReports);
        model.addAttribute("loggedInUser", user);
        return "admin/reports/all-reports";
    }

    // ==================== GENERATE REPORT WITH DATE RANGE ====================

    @PostMapping("/admin/reports/generate")
    public String generateReport(
            @RequestParam("reportType") String reportType,
            @RequestParam(value = "startDate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(value = "endDate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            HttpSession session,
            RedirectAttributes redirectAttributes) {

        User user = (User) session.getAttribute("loggedInUser");
        if (user == null || !user.getRole().equals("admin")) return "redirect:/login";

        try {
            Report report = null;

            switch (reportType) {
                case "rental":
                    report = reportService.generateRentalReport(startDate, endDate);
                    break;
                case "revenue":
                    report = reportService.generateRevenueReport(startDate, endDate);
                    break;
                case "popularity":
                    report = reportService.generatePopularityReport(startDate, endDate);
                    break;
                case "all":
                    Map<String, Report> allReports = reportService.generateAllReports(startDate, endDate);
                    for (Report r : allReports.values()) {
                        reportService.saveReport(r);
                    }
                    redirectAttributes.addFlashAttribute("success",
                            "All 3 reports generated for: " +
                                    (startDate != null ? startDate : "Beginning") + " to " +
                                    (endDate != null ? endDate : "Today"));
                    return "redirect:/admin/reports";
            }

            if (report != null) {
                reportService.saveReport(report);
                redirectAttributes.addFlashAttribute("success",
                        report.getReportName() + " generated successfully!");
            }

        } catch (IOException e) {
            redirectAttributes.addFlashAttribute("error", "Error generating report: " + e.getMessage());
        }

        return "redirect:/admin/reports";
    }

    // ==================== DELETE REPORT ====================

    @GetMapping("/admin/reports/delete/{reportId}")
    public String deleteReport(@PathVariable String reportId,
                               HttpSession session,
                               RedirectAttributes redirectAttributes) {
        User user = (User) session.getAttribute("loggedInUser");
        if (user == null || !user.getRole().equals("admin")) return "redirect:/login";

        boolean deleted = reportService.deleteReport(reportId);
        if (deleted) {
            redirectAttributes.addFlashAttribute("success", "Report deleted successfully.");
        } else {
            redirectAttributes.addFlashAttribute("error", "Report not found.");
        }

        return "redirect:/admin/reports";
    }

    // ==================== VIEW SAVED REPORT ====================

    @GetMapping("/admin/reports/view/{reportId}")
    public String viewReport(@PathVariable String reportId, Model model, HttpSession session) {
        User user = (User) session.getAttribute("loggedInUser");
        if (user == null || !user.getRole().equals("admin")) return "redirect:/login";

        Report report = reportService.getSavedReports().stream()
                .filter(r -> r.getReportId().equals(reportId))
                .findFirst().orElse(null);

        if (report == null) {
            return "redirect:/admin/reports";
        }

        model.addAttribute("report", report);
        model.addAttribute("loggedInUser", user);

        switch (report.getReportType()) {
            case "RENTAL": return "admin/reports/rental-report";
            case "REVENUE": return "admin/reports/revenue-report";
            case "POPULARITY": return "admin/reports/popularity-report";
            default: return "redirect:/admin/reports";
        }
    }

    // ==================== EXPORT REPORT ====================

    @GetMapping("/admin/reports/export/{reportId}")
    @ResponseBody
    public String exportReport(@PathVariable String reportId, HttpSession session) {
        User user = (User) session.getAttribute("loggedInUser");
        if (user == null || !user.getRole().equals("admin")) return "Unauthorized";

        Report report = reportService.getSavedReports().stream()
                .filter(r -> r.getReportId().equals(reportId))
                .findFirst().orElse(null);

        if (report == null) return "Report not found";
        return reportService.exportReportAsText(report);
    }

    // ==================== EXPORT LEGACY REPORTS ====================

    @GetMapping("/admin/reports/export/{type}")
    @ResponseBody
    public String exportLegacyReport(@PathVariable String type, HttpSession session) throws IOException {
        User user = (User) session.getAttribute("loggedInUser");
        if (user == null || !user.getRole().equals("admin")) return "Unauthorized";

        Report report = null;
        switch (type.toLowerCase()) {
            case "rental": report = reportService.generateRentalReport(null, null); break;
            case "revenue": report = reportService.generateRevenueReport(null, null); break;
            case "popularity": report = reportService.generatePopularityReport(null, null); break;
        }

        if (report == null) return "Report not found";
        return reportService.exportReportAsText(report);
    }

    // ==================== DASHBOARD REFRESH ====================

    @PostMapping("/admin/dashboard/refresh")
    public String refreshDashboard(RedirectAttributes redirectAttributes) {
        redirectAttributes.addFlashAttribute("success", "Dashboard data refreshed!");
        return "redirect:/admin/dashboard";
    }

    // ==================== INDIVIDUAL REPORT PAGES ====================

    @GetMapping("/admin/reports/rentals")
    public String rentalReport(Model model, HttpSession session) throws IOException {
        User user = (User) session.getAttribute("loggedInUser");
        if (user == null || !user.getRole().equals("admin")) return "redirect:/login";

        RentalReport report = reportService.generateRentalReport(null, null);
        model.addAttribute("report", report);
        model.addAttribute("loggedInUser", user);
        return "admin/reports/rental-report";
    }

    @GetMapping("/admin/reports/revenue")
    public String revenueReport(Model model, HttpSession session) throws IOException {
        User user = (User) session.getAttribute("loggedInUser");
        if (user == null || !user.getRole().equals("admin")) return "redirect:/login";

        RevenueReport report = reportService.generateRevenueReport(null, null);
        model.addAttribute("report", report);
        model.addAttribute("loggedInUser", user);
        return "admin/reports/revenue-report";
    }

    @GetMapping("/admin/reports/popularity")
    public String popularityReport(Model model, HttpSession session) throws IOException {
        User user = (User) session.getAttribute("loggedInUser");
        if (user == null || !user.getRole().equals("admin")) return "redirect:/login";

        PopularityReport report = reportService.generatePopularityReport(null, null);
        model.addAttribute("report", report);
        model.addAttribute("loggedInUser", user);
        return "admin/reports/popularity-report";
    }
}