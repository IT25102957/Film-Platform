<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Inventory Dashboard • Alpha Studio Films</title>
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
        .stats-grid {
            display: grid;
            grid-template-columns: repeat(4, 1fr);
            gap: 25px;
            margin-bottom: 35px;
        }
        .stat-card {
            background: rgba(255, 255, 255, 0.95);
            border-radius: 18px;
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
            font-size: 28px;
            color: white;
        }
        .stat-info h3 {
            font-size: 28px;
            font-weight: 700;
            color: #1a1a2e;
        }
        .stat-info p {
            color: #666;
            font-size: 14px;
        }
        .section-card {
            background: rgba(255, 255, 255, 0.95);
            border-radius: 18px;
            padding: 25px;
            margin-bottom: 30px;
        }
        .section-title {
            font-size: 20px;
            font-weight: 700;
            color: #1a1a2e;
            margin-bottom: 20px;
        }
        .alert-list {
            list-style: none;
        }
        .alert-item {
            display: flex;
            align-items: center;
            gap: 15px;
            padding: 12px 0;
            border-bottom: 1px solid #eee;
        }
        .alert-badge {
            padding: 4px 12px;
            border-radius: 30px;
            font-size: 12px;
            font-weight: 600;
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
            <li><a href="/admin/inventory" class="nav-link active"><i class="fas fa-boxes"></i> Inventory</a></li>
            <li><a href="/profile" class="nav-link"><i class="fas fa-user-circle"></i> Profile</a></li>
        </ul>
    </div>

    <div class="main-content">
        <h1 class="page-title">Inventory Dashboard</h1>

        <div class="stats-grid">
            <div class="stat-card">
                <div class="stat-icon"><i class="fas fa-film"></i></div>
                <div class="stat-info">
                    <h3>${stats.totalMovies}</h3>
                    <p>Total Movies</p>
                </div>
            </div>
            <div class="stat-card">
                <div class="stat-icon"><i class="fas fa-copy"></i></div>
                <div class="stat-info">
                    <h3>${stats.totalCopies}</h3>
                    <p>Total Copies</p>
                </div>
            </div>
            <div class="stat-card">
                <div class="stat-icon"><i class="fas fa-check-circle"></i></div>
                <div class="stat-info">
                    <h3>${stats.availableCopies}</h3>
                    <p>Available</p>
                </div>
            </div>
            <div class="stat-card">
                <div class="stat-icon"><i class="fas fa-chart-line"></i></div>
                <div class="stat-info">
                    <h3>${String.format("%.1f", stats.occupancyRate)}%</h3>
                    <p>Occupancy Rate</p>
                </div>
            </div>
        </div>

        <div style="display: grid; grid-template-columns: 1fr 1fr; gap: 30px;">
            <div class="section-card">
                <h3 class="section-title"><i class="fas fa-exclamation-triangle" style="color: #ffc107;"></i> Low Stock Alerts</h3>
                <c:if test="${empty stats.lowStockMovies}">
                    <p style="color: #666;">No low stock alerts</p>
                </c:if>
                <c:if test="${not empty stats.lowStockMovies}">
                    <ul class="alert-list">
                        <c:forEach var="movie" items="${stats.lowStockMovies}">
                            <li class="alert-item">
                                <span class="alert-badge" style="background: #ffc107; color: #333;">Low</span>
                                <span><strong>${movie.title}</strong> - ${movie.availableCopies} left</span>
                            </li>
                        </c:forEach>
                    </ul>
                </c:if>
            </div>

            <div class="section-card">
                <h3 class="section-title"><i class="fas fa-times-circle" style="color: #dc3545;"></i> Out of Stock</h3>
                <c:if test="${empty stats.outOfStockMovies}">
                    <p style="color: #666;">All movies in stock!</p>
                </c:if>
                <c:if test="${not empty stats.outOfStockMovies}">
                    <ul class="alert-list">
                        <c:forEach var="movie" items="${stats.outOfStockMovies}">
                            <li class="alert-item">
                                <span class="alert-badge" style="background: #dc3545; color: white;">Out</span>
                                <span><strong>${movie.title}</strong></span>
                            </li>
                        </c:forEach>
                    </ul>
                </c:if>
            </div>
        </div>

        <div class="section-card">
            <h3 class="section-title"><i class="fas fa-list"></i> All Inventory</h3>
            <table style="width:100%; border-collapse: collapse;">
                <thead>
                <tr style="border-bottom: 2px solid #eee;">
                    <th style="padding: 12px; text-align: left;">Movie</th>
                    <th style="padding: 12px; text-align: left;">Available/Total</th>
                    <th style="padding: 12px; text-align: left;">Status</th>
                    <th style="padding: 12px; text-align: left;">Stock Level</th>
                </tr>
                </thead>
                <tbody>
                <c:forEach var="movie" items="${movies}">
                    <tr style="border-bottom: 1px solid #f0f0f0;">
                        <td style="padding: 12px;"><strong>${movie.title}</strong></td>
                        <td style="padding: 12px;">${movie.availableCopies}/${movie.totalCopies}</td>
                        <td style="padding: 12px;">
                                    <span style="padding: 4px 12px; border-radius: 30px; font-size: 12px; font-weight: 600;
                                            background: ${movie.availableCopies > 2 ? '#28a745' : (movie.availableCopies > 0 ? '#ffc107' : '#dc3545')};
                                            color: ${movie.availableCopies > 0 ? 'white' : 'white'};">
                                            ${movie.stockStatus}
                                    </span>
                        </td>
                        <td style="padding: 12px;">
                            <div style="display: flex; align-items: center; gap: 10px;">
                                <div style="width: 150px; height: 8px; background: #e0e0e0; border-radius: 4px;">
                                    <div style="width: ${(movie.availableCopies / movie.totalCopies) * 100}%; height: 100%;
                                            background: linear-gradient(135deg, #28a745, #20c997); border-radius: 4px;"></div>
                                </div>
                                <span>${String.format("%.0f", (movie.availableCopies / movie.totalCopies) * 100)}%</span>
                            </div>
                        </td>
                    </tr>
                </c:forEach>
                </tbody>
            </table>
        </div>
    </div>
</div>
</body>
</html>