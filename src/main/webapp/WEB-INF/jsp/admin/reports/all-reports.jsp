<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<%@ taglib uri="jakarta.tags.fmt" prefix="fmt" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Reports • Alpha Studio Films</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">
    <link href="https://fonts.googleapis.com/css2?family=Poppins:wght@300;400;500;600;700&display=swap" rel="stylesheet">
    <style>
        * { margin: 0; padding: 0; box-sizing: border-box; }
        body {
            font-family: 'Poppins', sans-serif;
            background: linear-gradient(135deg, #0f0c29 0%, #302b63 50%, #24243e 100%);
            min-height: 100vh;
        }
        .admin-wrapper { display: flex; min-height: 100vh; }
        .sidebar {
            width: 260px;
            background: rgba(0, 0, 0, 0.4);
            backdrop-filter: blur(15px);
            border-right: 1px solid rgba(255, 255, 255, 0.1);
            padding: 30px 0;
        }
        .sidebar-logo {
            padding: 0 25px 30px;
            font-size: 22px;
            font-weight: 700;
            background: linear-gradient(135deg, #e50914, #ff6b6b);
            -webkit-background-clip: text;
            -webkit-text-fill-color: transparent;
        }
        .nav-menu { list-style: none; }
        .nav-link {
            display: flex;
            align-items: center;
            gap: 12px;
            padding: 12px 25px;
            color: rgba(255, 255, 255, 0.8);
            text-decoration: none;
            font-weight: 500;
            border-left: 4px solid transparent;
        }
        .nav-link:hover, .nav-link.active {
            background: rgba(229, 9, 20, 0.2);
            color: white;
            border-left-color: #e50914;
        }
        .main-content { flex: 1; padding: 30px 40px; }
        .page-title {
            font-size: 32px;
            font-weight: 700;
            color: white;
            margin-bottom: 25px;
        }
        .alert {
            border-radius: 12px;
            padding: 14px 18px;
            margin-bottom: 25px;
        }

        /* Report Cards */
        .reports-grid {
            display: grid;
            grid-template-columns: repeat(3, 1fr);
            gap: 30px;
            margin-bottom: 40px;
        }
        .report-card {
            background: rgba(255, 255, 255, 0.95);
            border-radius: 18px;
            padding: 30px;
            text-align: center;
            transition: transform 0.3s;
            box-shadow: 0 10px 30px rgba(0, 0, 0, 0.2);
        }
        .report-card:hover { transform: translateY(-5px); }
        .report-icon {
            width: 80px;
            height: 80px;
            background: linear-gradient(135deg, #e50914, #ff6b6b);
            border-radius: 50%;
            display: flex;
            align-items: center;
            justify-content: center;
            margin: 0 auto 20px;
            color: white;
            font-size: 36px;
        }
        .report-icon.green { background: linear-gradient(135deg, #28a745, #20c997); }
        .report-icon.orange { background: linear-gradient(135deg, #ffc107, #ff9800); }
        .report-card h3 {
            font-size: 22px;
            font-weight: 700;
            color: #1a1a2e;
            margin-bottom: 15px;
        }
        .report-summary {
            color: #666;
            font-size: 14px;
            margin-bottom: 20px;
            line-height: 1.6;
        }
        .report-date {
            color: #888;
            font-size: 12px;
            margin-bottom: 20px;
        }
        .btn-view {
            background: linear-gradient(135deg, #e50914, #ff6b6b);
            color: white;
            border: none;
            padding: 12px 24px;
            border-radius: 30px;
            text-decoration: none;
            display: inline-block;
            margin-right: 10px;
            font-weight: 500;
        }
        .btn-export {
            background: #6c757d;
            color: white;
            border: none;
            padding: 12px 24px;
            border-radius: 30px;
            text-decoration: none;
            display: inline-block;
            font-weight: 500;
        }

        /* Generate Report Section */
        .section-card {
            background: rgba(255, 255, 255, 0.95);
            border-radius: 18px;
            padding: 30px;
            margin-bottom: 30px;
            box-shadow: 0 10px 30px rgba(0, 0, 0, 0.2);
        }
        .section-title {
            font-size: 20px;
            font-weight: 700;
            color: #1a1a2e;
            margin-bottom: 20px;
            display: flex;
            align-items: center;
            gap: 10px;
        }
        .form-row {
            display: grid;
            grid-template-columns: 1fr 1fr 1fr;
            gap: 15px;
            margin-bottom: 20px;
        }
        .form-group label {
            display: block;
            font-weight: 600;
            color: #1a1a2e;
            margin-bottom: 5px;
            font-size: 14px;
        }
        .form-control {
            width: 100%;
            padding: 12px 16px;
            border: 2px solid #e0e0e0;
            border-radius: 12px;
            font-size: 15px;
        }
        .form-control:focus { border-color: #e50914; outline: none; }
        .text-muted { color: #888; font-size: 12px; }
        .btn-generate {
            background: linear-gradient(135deg, #e50914, #ff6b6b);
            color: white;
            border: none;
            padding: 14px 30px;
            border-radius: 50px;
            font-weight: 600;
            font-size: 16px;
            cursor: pointer;
        }
        .btn-generate:hover { opacity: 0.9; }

        /* Saved Reports Table */
        .saved-table {
            width: 100%;
            border-collapse: collapse;
            margin-top: 15px;
        }
        .saved-table th {
            text-align: left;
            padding: 15px 12px;
            color: #666;
            font-size: 13px;
            text-transform: uppercase;
            border-bottom: 2px solid #eee;
        }
        .saved-table td {
            padding: 15px 12px;
            border-bottom: 1px solid #f0f0f0;
            color: #1a1a2e;
        }
        .btn-sm {
            padding: 6px 14px;
            border-radius: 20px;
            font-size: 12px;
            font-weight: 500;
            text-decoration: none;
            display: inline-block;
            margin-right: 5px;
        }
        .btn-sm-view { background: #007bff; color: white; }
        .btn-sm-export { background: #28a745; color: white; }
        .btn-sm-delete { background: #dc3545; color: white; }

        .empty-state {
            text-align: center;
            padding: 40px;
            color: #888;
        }
        .empty-state i {
            font-size: 40px;
            margin-bottom: 10px;
            opacity: 0.5;
        }

        @media (max-width: 1200px) {
            .reports-grid { grid-template-columns: repeat(2, 1fr); }
            .form-row { grid-template-columns: 1fr 1fr; }
        }
        @media (max-width: 768px) {
            .admin-wrapper { flex-direction: column; }
            .sidebar { width: 100%; }
            .reports-grid { grid-template-columns: 1fr; }
            .form-row { grid-template-columns: 1fr; }
        }
    </style>
</head>
<body>
<div class="admin-wrapper">
    <!-- Sidebar -->
    <div class="sidebar">
        <div class="sidebar-logo">
            <i class="fas fa-clapperboard"></i> Alpha Studio Admin
        </div>
        <ul class="nav-menu">
            <li><a href="/admin/dashboard" class="nav-link"><i class="fas fa-chart-pie"></i> Dashboard</a></li>
            <li><a href="/admin/users" class="nav-link"><i class="fas fa-users"></i> Users</a></li>
            <li><a href="/admin/movies" class="nav-link"><i class="fas fa-film"></i> Movies</a></li>
            <li><a href="/admin/rentals" class="nav-link"><i class="fas fa-ticket-alt"></i> Rentals</a></li>
            <li><a href="/admin/payments" class="nav-link"><i class="fas fa-credit-card"></i> Payments</a></li>
            <li><a href="/admin/reviews" class="nav-link"><i class="fas fa-star"></i> Reviews</a></li>
            <li><a href="/admin/reports" class="nav-link active"><i class="fas fa-file-alt"></i> Reports</a></li>
            <li><a href="/profile" class="nav-link"><i class="fas fa-user-circle"></i> Profile</a></li>
        </ul>
    </div>

    <!-- Main Content -->
    <div class="main-content">
        <h1 class="page-title">Analytics Reports</h1>

        <!-- Messages -->
        <c:if test="${not empty success}">
            <div class="alert alert-success"><i class="fas fa-check-circle"></i> ${success}</div>
        </c:if>
        <c:if test="${not empty error}">
            <div class="alert alert-danger"><i class="fas fa-exclamation-circle"></i> ${error}</div>
        </c:if>

        <!-- ==================== REPORT CARDS ==================== -->
        <div class="reports-grid">
            <!-- Rental Report -->
            <div class="report-card">
                <div class="report-icon">
                    <i class="fas fa-ticket-alt"></i>
                </div>
                <h3>Rental Activity</h3>
                <c:if test="${not empty reports.rental}">
                    <div class="report-summary">
                        Total: ${reports.rental.totalRentals} rentals<br>
                        Active: ${reports.rental.activeRentals} | Overdue: ${reports.rental.overdueRentals}
                    </div>
                    <div class="report-date">
                        <i class="far fa-clock"></i> ${reports.rental.formattedDate}
                    </div>
                </c:if>
                <div>
                    <a href="/admin/reports/rentals" class="btn-view"><i class="fas fa-eye"></i> View</a>
                    <a href="/admin/reports/export/rental" class="btn-export" target="_blank"><i class="fas fa-download"></i> Export</a>
                </div>
            </div>

            <!-- Revenue Report -->
            <div class="report-card">
                <div class="report-icon green">
                    <i class="fas fa-dollar-sign"></i>
                </div>
                <h3>Revenue Analysis</h3>
                <c:if test="${not empty reports.revenue}">
                    <div class="report-summary">
                        Total: $<fmt:formatNumber value="${reports.revenue.totalRevenue}" pattern="#,##0.00"/><br>
                        Net: $<fmt:formatNumber value="${reports.revenue.netRevenue}" pattern="#,##0.00"/>
                    </div>
                    <div class="report-date">
                        <i class="far fa-clock"></i> ${reports.revenue.formattedDate}
                    </div>
                </c:if>
                <div>
                    <a href="/admin/reports/revenue" class="btn-view"><i class="fas fa-eye"></i> View</a>
                    <a href="/admin/reports/export/revenue" class="btn-export" target="_blank"><i class="fas fa-download"></i> Export</a>
                </div>
            </div>

            <!-- Popularity Report -->
            <div class="report-card">
                <div class="report-icon orange">
                    <i class="fas fa-fire"></i>
                </div>
                <h3>Popularity & Trends</h3>
                <c:if test="${not empty reports.popularity}">
                    <div class="report-summary">
                        Top: ${reports.popularity.mostRentedMovie}<br>
                        Genre: ${reports.popularity.mostPopularGenre}
                    </div>
                    <div class="report-date">
                        <i class="far fa-clock"></i> ${reports.popularity.formattedDate}
                    </div>
                </c:if>
                <div>
                    <a href="/admin/reports/popularity" class="btn-view"><i class="fas fa-eye"></i> View</a>
                    <a href="/admin/reports/export/popularity" class="btn-export" target="_blank"><i class="fas fa-download"></i> Export</a>
                </div>
            </div>
        </div>

        <!-- ==================== GENERATE REPORT WITH DATE RANGE ==================== -->
        <div class="section-card">
            <h3 class="section-title">
                <span style="background: #e50914; color: white; width: 35px; height: 35px; border-radius: 50%; display: flex; align-items: center; justify-content: center; font-size: 16px;">1</span>
                Generate Custom Report
            </h3>

            <form action="/admin/reports/generate" method="post">
                <div class="form-row">
                    <div class="form-group">
                        <label><i class="fas fa-file-alt"></i> Report Type *</label>
                        <select name="reportType" class="form-control" required>
                            <option value="rental">Rental Activity</option>
                            <option value="revenue">Revenue Analysis</option>
                            <option value="popularity">Popularity & Trends</option>
                            <option value="all">All Reports</option>
                        </select>
                    </div>
                    <div class="form-group">
                        <label><i class="fas fa-calendar-alt"></i> Start Date</label>
                        <input type="date" name="startDate" class="form-control">
                        <span class="text-muted">Leave empty for beginning</span>
                    </div>
                    <div class="form-group">
                        <label><i class="fas fa-calendar-check"></i> End Date</label>
                        <input type="date" name="endDate" class="form-control">
                        <span class="text-muted">Leave empty for today</span>
                    </div>
                </div>

                <button type="submit" class="btn-generate">
                    <i class="fas fa-chart-bar"></i> Generate Report
                </button>
            </form>
        </div>

        <!-- ==================== SAVED REPORTS ==================== -->
        <div class="section-card">
            <h3 class="section-title">
                <span style="background: #28a745; color: white; width: 35px; height: 35px; border-radius: 50%; display: flex; align-items: center; justify-content: center; font-size: 16px;">2</span>
                Saved Reports
                <c:if test="${not empty savedReports}">
                    <span class="badge bg-success" style="margin-left: 10px;">${savedReports.size()}</span>
                </c:if>
            </h3>

            <c:if test="${empty savedReports}">
                <div class="empty-state">
                    <i class="fas fa-file-alt"></i>
                    <p>No saved reports yet. Generate a report above to see it here.</p>
                </div>
            </c:if>

            <c:if test="${not empty savedReports}">
                <table class="saved-table">
                    <thead>
                    <tr>
                        <th>Report ID</th>
                        <th>Report Name</th>
                        <th>Type</th>
                        <th>Date Range</th>
                        <th>Generated</th>
                        <th>Actions</th>
                    </tr>
                    </thead>
                    <tbody>
                    <c:forEach var="report" items="${savedReports}">
                        <tr>
                            <td><small style="color: #888;">${report.reportId}</small></td>
                            <td><strong>${report.reportName}</strong></td>
                            <td><span class="badge bg-info">${report.reportType}</span></td>
                            <td>${report.dateRange}</td>
                            <td><small>${report.formattedDate}</small></td>
                            <td>
                                <a href="/admin/reports/view/${report.reportId}" class="btn-sm btn-sm-view">
                                    <i class="fas fa-eye"></i> View
                                </a>
                                <a href="/admin/reports/export/${report.reportId}" class="btn-sm btn-sm-export" target="_blank">
                                    <i class="fas fa-download"></i> Export
                                </a>
                                <a href="/admin/reports/delete/${report.reportId}" class="btn-sm btn-sm-delete"
                                   onclick="return confirm('Delete this report?')">
                                    <i class="fas fa-trash-alt"></i> Delete
                                </a>
                            </td>
                        </tr>
                    </c:forEach>
                    </tbody>
                </table>
            </c:if>
        </div>

    </div>
</div>
</body>
</html>