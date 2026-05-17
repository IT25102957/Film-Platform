<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<%@ taglib uri="jakarta.tags.fmt" prefix="fmt" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Dashboard • Alpha Studio Films</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">
    <link href="https://fonts.googleapis.com/css2?family=Poppins:wght@300;400;500;600;700&display=swap" rel="stylesheet">
    <script src="https://cdn.jsdelivr.net/npm/chart.js@4.4.0/dist/chart.umd.min.js"></script>
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
        .stats-grid {
            display: grid;
            grid-template-columns: repeat(4, 1fr);
            gap: 20px;
            margin-bottom: 30px;
        }
        .stat-card {
            background: rgba(255, 255, 255, 0.95);
            border-radius: 16px;
            padding: 25px;
            display: flex;
            align-items: center;
            gap: 20px;
        }
        .stat-icon {
            width: 60px;
            height: 60px;
            background: linear-gradient(135deg, #e50914, #ff6b6b);
            border-radius: 16px;
            display: flex;
            align-items: center;
            justify-content: center;
            color: white;
            font-size: 28px;
        }
        .stat-info h3 { font-size: 28px; font-weight: 700; color: #1a1a2e; }
        .stat-info p { color: #666; font-size: 14px; }
        .chart-row {
            display: grid;
            grid-template-columns: 1fr 1fr;
            gap: 25px;
            margin-bottom: 30px;
        }
        .chart-card {
            background: rgba(255, 255, 255, 0.95);
            border-radius: 18px;
            padding: 25px;
        }
        .chart-title {
            font-size: 18px;
            font-weight: 700;
            color: #1a1a2e;
            margin-bottom: 20px;
        }
        .chart-container {
            position: relative;
            height: 250px;
            width: 100%;
        }
        .activity-card {
            background: rgba(255, 255, 255, 0.95);
            border-radius: 18px;
            padding: 25px;
            margin-bottom: 30px;
        }
        .activity-item {
            display: flex;
            align-items: center;
            gap: 15px;
            padding: 12px 0;
            border-bottom: 1px solid #f0f0f0;
        }
        .alert {
            border-radius: 12px;
            padding: 14px 20px;
            margin-bottom: 25px;
        }
        .form-control {
            width: 100%;
            padding: 12px 16px;
            border: 2px solid #e0e0e0;
            border-radius: 12px;
            font-size: 15px;
        }
        .form-control:focus { border-color: #e50914; outline: none; }
        .btn-generate {
            background: linear-gradient(135deg, #e50914, #ff6b6b);
            color: white;
            padding: 12px 24px;
            border-radius: 30px;
            border: none;
            font-weight: 600;
            cursor: pointer;
            text-decoration: none;
            display: inline-block;
        }
        .btn-generate:hover { opacity: 0.9; }
        .btn-refresh {
            background: rgba(255,255,255,0.2);
            color: white;
            border-radius: 30px;
            padding: 10px 20px;
            border: none;
            cursor: pointer;
            text-decoration: none;
            display: inline-block;
        }
        .btn-refresh:hover { background: rgba(255,255,255,0.3); }
        .saved-table {
            width: 100%;
            border-collapse: collapse;
            margin-top: 20px;
        }
        .saved-table th {
            text-align: left;
            padding: 15px 10px;
            color: #666;
            font-size: 13px;
            text-transform: uppercase;
            border-bottom: 2px solid #eee;
        }
        .saved-table td {
            padding: 15px 10px;
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
        .btn-view { background: #007bff; color: white; }
        .btn-export { background: #28a745; color: white; }
        .btn-delete { background: #dc3545; color: white; }
        @media (max-width: 1200px) {
            .stats-grid { grid-template-columns: repeat(2, 1fr); }
            .chart-row { grid-template-columns: 1fr; }
        }
        @media (max-width: 768px) {
            .admin-wrapper { flex-direction: column; }
            .sidebar { width: 100%; }
            .main-content { padding: 20px; }
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
            <li><a href="/admin/dashboard" class="nav-link active"><i class="fas fa-chart-pie"></i> Dashboard</a></li>
            <li><a href="/admin/users" class="nav-link"><i class="fas fa-users"></i> Users</a></li>
            <li><a href="/admin/movies" class="nav-link"><i class="fas fa-film"></i> Movies</a></li>
            <li><a href="/admin/rentals" class="nav-link"><i class="fas fa-ticket-alt"></i> Rentals</a></li>
            <li><a href="/admin/payments" class="nav-link"><i class="fas fa-credit-card"></i> Payments</a></li>
            <li><a href="/admin/reviews" class="nav-link"><i class="fas fa-star"></i> Reviews</a></li>
            <li><a href="/admin/reports" class="nav-link"><i class="fas fa-file-alt"></i> Reports</a></li>
            <li><a href="/profile" class="nav-link"><i class="fas fa-user-circle"></i> Profile</a></li>
        </ul>
    </div>

    <!-- Main Content -->
    <div class="main-content">
        <div style="display: flex; justify-content: space-between; align-items: center;">
            <h1 class="page-title">Dashboard Overview</h1>
            <a href="/admin/dashboard" class="btn-refresh">
                <i class="fas fa-sync-alt"></i> Refresh
            </a>
        </div>

        <!-- Messages -->
        <c:if test="${not empty success}">
            <div class="alert alert-success"><i class="fas fa-check-circle"></i> ${success}</div>
        </c:if>
        <c:if test="${not empty error}">
            <div class="alert alert-danger"><i class="fas fa-exclamation-circle"></i> ${error}</div>
        </c:if>

        <!-- Stats Row 1 -->
        <div class="stats-grid">
            <div class="stat-card">
                <div class="stat-icon"><i class="fas fa-users"></i></div>
                <div class="stat-info">
                    <h3>${stats.totalUsers}</h3>
                    <p>Total Users</p>
                </div>
            </div>
            <div class="stat-card">
                <div class="stat-icon"><i class="fas fa-film"></i></div>
                <div class="stat-info">
                    <h3>${stats.totalMovies}</h3>
                    <p>Total Movies</p>
                </div>
            </div>
            <div class="stat-card">
                <div class="stat-icon"><i class="fas fa-ticket-alt"></i></div>
                <div class="stat-info">
                    <h3>${stats.activeRentals}</h3>
                    <p>Active Rentals</p>
                </div>
            </div>
            <div class="stat-card">
                <div class="stat-icon"><i class="fas fa-dollar-sign"></i></div>
                <div class="stat-info">
                    <h3>$<c:out value="${String.format('%.2f', stats.totalRevenue)}"/></h3>
                    <p>Total Revenue</p>
                </div>
            </div>
        </div>

        <!-- Stats Row 2 -->
        <div class="stats-grid">
            <div class="stat-card">
                <div class="stat-icon" style="background: linear-gradient(135deg, #ffc107, #ff9800);">
                    <i class="fas fa-exclamation-triangle"></i>
                </div>
                <div class="stat-info">
                    <h3>${stats.overdueRentals}</h3>
                    <p>Overdue Rentals</p>
                </div>
            </div>
            <div class="stat-card">
                <div class="stat-icon" style="background: linear-gradient(135deg, #28a745, #20c997);">
                    <i class="fas fa-check-circle"></i>
                </div>
                <div class="stat-info">
                    <h3>${stats.availableCopies}</h3>
                    <p>Available Movies</p>
                </div>
            </div>
            <div class="stat-card">
                <div class="stat-icon" style="background: linear-gradient(135deg, #17a2b8, #6f42c1);">
                    <i class="fas fa-star"></i>
                </div>
                <div class="stat-info">
                    <h3>${stats.totalReviews}</h3>
                    <p>Total Reviews</p>
                </div>
            </div>
            <div class="stat-card">
                <div class="stat-icon" style="background: linear-gradient(135deg, #fd7e14, #e83e8c);">
                    <i class="fas fa-chart-line"></i>
                </div>
                <div class="stat-info">
                    <h3><c:out value="${String.format('%.1f', stats.averageRating)}"/></h3>
                    <p>Avg Rating</p>
                </div>
            </div>
        </div>

        <!-- Charts -->
        <div class="chart-row">
            <div class="chart-card">
                <h3 class="chart-title"><i class="fas fa-ticket-alt" style="color: #e50914;"></i> Rentals (Last 7 Days)</h3>
                <div class="chart-container">
                    <canvas id="rentalChart"></canvas>
                </div>
            </div>
            <div class="chart-card">
                <h3 class="chart-title"><i class="fas fa-tags" style="color: #e50914;"></i> Popular Genres</h3>
                <div class="chart-container">
                    <canvas id="genreChart"></canvas>
                </div>
            </div>
        </div>

        <!-- Recent Activity -->
        <div class="activity-card">
            <h3 class="chart-title"><i class="fas fa-history" style="color: #e50914;"></i> Recent Activity</h3>
            <c:if test="${empty stats.recentActivities}">
                <p style="color: #888; text-align: center; padding: 20px;">No recent activity</p>
            </c:if>
            <c:if test="${not empty stats.recentActivities}">
                <c:forEach var="activity" items="${stats.recentActivities}">
                    <div class="activity-item">
                        <i class="fas fa-circle" style="color: #e50914; font-size: 10px;"></i>
                        <span>${activity}</span>
                    </div>
                </c:forEach>
            </c:if>
        </div>

        <!-- Generate Report Section -->
        <div class="activity-card">
            <h3 class="chart-title">
                <i class="fas fa-file-alt" style="color: #e50914;"></i> Generate Report
            </h3>

            <form action="/admin/reports/generate" method="post" style="margin-top: 20px;">
                <div style="display: grid; grid-template-columns: 1fr 1fr 1fr; gap: 15px; margin-bottom: 20px;">
                    <div>
                        <label style="font-weight: 600; color: #1a1a2e; display: block; margin-bottom: 5px;">Report Type *</label>
                        <select name="reportType" class="form-control" required>
                            <option value="rental">Rental Activity</option>
                            <option value="revenue">Revenue Analysis</option>
                            <option value="popularity">Popularity & Trends</option>
                            <option value="all">All Reports</option>
                        </select>
                    </div>
                    <div>
                        <label style="font-weight: 600; color: #1a1a2e; display: block; margin-bottom: 5px;">Start Date</label>
                        <input type="date" name="startDate" class="form-control">
                    </div>
                    <div>
                        <label style="font-weight: 600; color: #1a1a2e; display: block; margin-bottom: 5px;">End Date</label>
                        <input type="date" name="endDate" class="form-control">
                    </div>
                </div>

                <button type="submit" class="btn-generate">
                    <i class="fas fa-chart-bar"></i> Generate Report
                </button>
            </form>
        </div>

        <!-- Saved Reports -->
        <c:if test="${not empty savedReports}">
            <div class="activity-card">
                <h3 class="chart-title">
                    <i class="fas fa-save" style="color: #28a745;"></i> Saved Reports
                    <span class="badge bg-success" style="margin-left: 10px;">${savedReports.size()}</span>
                </h3>

                <table class="saved-table">
                    <thead>
                    <tr>
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
                            <td><strong>${report.reportName}</strong></td>
                            <td><span class="badge bg-info">${report.reportType}</span></td>
                            <td>${report.dateRange}</td>
                            <td>${report.formattedDate}</td>
                            <td>
                                <a href="/admin/reports/view/${report.reportId}" class="btn-sm btn-view">
                                    <i class="fas fa-eye"></i> View
                                </a>
                                <a href="/admin/reports/export/${report.reportId}" class="btn-sm btn-export" target="_blank">
                                    <i class="fas fa-download"></i> Export
                                </a>
                                <a href="/admin/reports/delete/${report.reportId}" class="btn-sm btn-delete"
                                   onclick="return confirm('Delete this report?')">
                                    <i class="fas fa-trash-alt"></i> Delete
                                </a>
                            </td>
                        </tr>
                    </c:forEach>
                    </tbody>
                </table>
            </div>
        </c:if>

        <c:if test="${empty savedReports}">
            <div class="activity-card" style="text-align: center; color: #888;">
                <i class="fas fa-file-alt" style="font-size: 40px; margin-bottom: 10px; opacity: 0.5;"></i>
                <p>No saved reports yet.</p>
            </div>
        </c:if>

    </div>
</div>

<!-- CHARTS - No auto-refresh, no loops -->
<script>
    document.addEventListener('DOMContentLoaded', function() {
        // Rental Chart
        var rentalCtx = document.getElementById('rentalChart');
        if (rentalCtx) {
            var rentalChart = new Chart(rentalCtx, {
                type: 'line',
                data: {
                    labels: ['6 days ago', '5 days ago', '4 days ago', '3 days ago', '2 days ago', 'Yesterday', 'Today'],
                    datasets: [{
                        label: 'Rentals',
                        data: [3, 5, 2, 7, 4, 6, ${stats.todayRentals}],
                        borderColor: '#e50914',
                        backgroundColor: 'rgba(229, 9, 20, 0.1)',
                        tension: 0.3,
                        fill: true
                    }]
                },
                options: {
                    responsive: true,
                    maintainAspectRatio: false,
                    animation: false,
                    plugins: {
                        legend: { display: false }
                    },
                    scales: {
                        y: {
                            beginAtZero: true,
                            ticks: { stepSize: 1 }
                        }
                    }
                }
            });
        }

        // Genre Chart
        var genreCtx = document.getElementById('genreChart');
        if (genreCtx) {
            var genreChart = new Chart(genreCtx, {
                type: 'doughnut',
                data: {
                    labels: ['Sci-Fi', 'Crime', 'Action', 'Drama'],
                    datasets: [{
                        data: [45, 25, 20, 10],
                        backgroundColor: ['#e50914', '#ffc107', '#28a745', '#17a2b8']
                    }]
                },
                options: {
                    responsive: true,
                    maintainAspectRatio: false,
                    animation: false,
                    plugins: {
                        legend: { position: 'bottom' }
                    }
                }
            });
        }
    });
</script>
</body>
</html>