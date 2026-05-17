<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<%@ taglib uri="jakarta.tags.fmt" prefix="fmt" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Popularity Report • Alpha Studio Films</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">
    <link href="https://fonts.googleapis.com/css2?family=Poppins:wght@300;400;500;600;700&display=swap" rel="stylesheet">
    <script src="https://cdn.jsdelivr.net/npm/chart.js"></script>
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
        .report-card {
            background: rgba(255, 255, 255, 0.95);
            border-radius: 18px;
            padding: 30px;
            margin-bottom: 30px;
        }
        .report-header {
            display: flex;
            justify-content: space-between;
            align-items: center;
            margin-bottom: 25px;
            padding-bottom: 20px;
            border-bottom: 2px solid #e0e0e0;
        }
        .highlight-grid {
            display: grid;
            grid-template-columns: 1fr 1fr;
            gap: 25px;
            margin-bottom: 30px;
        }
        .highlight-card {
            background: linear-gradient(135deg, #f8f9fa, #e9ecef);
            border-radius: 16px;
            padding: 25px;
            text-align: center;
        }
        .highlight-icon {
            font-size: 40px;
            color: #e50914;
            margin-bottom: 15px;
        }
        .highlight-title {
            font-size: 14px;
            color: #666;
            margin-bottom: 10px;
        }
        .highlight-value {
            font-size: 28px;
            font-weight: 700;
            color: #1a1a2e;
        }
        .top-movies {
            background: #f8f9fa;
            border-radius: 16px;
            padding: 25px;
            margin: 20px 0;
        }
        .top-movie-item {
            display: flex;
            align-items: center;
            padding: 12px;
            border-bottom: 1px solid #e0e0e0;
        }
        .top-movie-rank {
            width: 30px;
            height: 30px;
            background: #e50914;
            color: white;
            border-radius: 50%;
            display: flex;
            align-items: center;
            justify-content: center;
            font-weight: 700;
            margin-right: 15px;
        }
        .chart-container {
            height: 300px;
            margin-top: 30px;
        }
        .btn-export {
            background: #28a745;
            color: white;
            border: none;
            padding: 12px 24px;
            border-radius: 30px;
            text-decoration: none;
        }
    </style>
</head>
<body>
<div class="admin-wrapper">
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

    <div class="main-content">
        <div style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 20px;">
            <h1 class="page-title">Popularity & Trends Report</h1>
            <a href="/admin/reports/export/popularity" class="btn-export" target="_blank">
                <i class="fas fa-download"></i> Export Report
            </a>
        </div>

        <div class="report-card">
            <div class="report-header">
                <div>
                    <h2 style="color: #1a1a2e;">${report.reportName}</h2>
                    <p style="color: #888;">Report ID: ${report.reportId} | Generated: ${report.formattedDate}</p>
                </div>
            </div>

            <div class="highlight-grid">
                <div class="highlight-card">
                    <div class="highlight-icon"><i class="fas fa-crown"></i></div>
                    <div class="highlight-title">Most Rented Movie</div>
                    <div class="highlight-value">${report.mostRentedMovie}</div>
                    <div style="color: #666; margin-top: 5px;">${report.mostRentedCount} rentals</div>
                </div>
                <div class="highlight-card">
                    <div class="highlight-icon"><i class="fas fa-tags"></i></div>
                    <div class="highlight-title">Most Popular Genre</div>
                    <div class="highlight-value">${report.mostPopularGenre}</div>
                    <div style="color: #666; margin-top: 5px;">${report.genreRentalCount} rentals</div>
                </div>
            </div>

            <div class="highlight-card" style="margin-bottom: 20px;">
                <div class="highlight-icon"><i class="fas fa-star"></i></div>
                <div class="highlight-title">Top Rated Movie</div>
                <div class="highlight-value">${report.topRatedMovie}</div>
                <div style="color: #666; margin-top: 5px;">${String.format("%.1f", report.topRating)} / 5 stars</div>
            </div>

            <c:if test="${not empty report.topMovies}">
                <div class="top-movies">
                    <h4 style="margin-bottom: 20px;"><i class="fas fa-list"></i> Top 5 Most Rented Movies</h4>
                    <c:forEach var="movie" items="${report.topMovies}" varStatus="status">
                        <div class="top-movie-item">
                            <span class="top-movie-rank">${status.index + 1}</span>
                            <span>${movie}</span>
                        </div>
                    </c:forEach>
                </div>
            </c:if>

            <div class="chart-container">
                <canvas id="popularityChart"></canvas>
            </div>
        </div>

        <div style="margin-top: 20px;">
            <a href="/admin/reports" style="color: white; text-decoration: none;">
                <i class="fas fa-arrow-left"></i> Back to Reports
            </a>
        </div>
    </div>
</div>

<script>
    const ctx = document.getElementById('popularityChart').getContext('2d');
    new Chart(ctx, {
        type: 'bar',
        data: {
            labels: ['${report.mostRentedMovie}', '${report.topRatedMovie}', '${report.mostPopularGenre}'],
            datasets: [{
                label: 'Popularity Metrics',
                data: [${report.mostRentedCount}, ${report.topRating * 10}, ${report.genreRentalCount}],
                backgroundColor: ['#e50914', '#ffc107', '#28a745']
            }]
        },
        options: {
            responsive: true,
            maintainAspectRatio: false
        }
    });
</script>
</body>
</html>