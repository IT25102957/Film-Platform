<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Movie Management • Alpha Studio Films</title>
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
        .table-container {
            background: rgba(255, 255, 255, 0.95);
            backdrop-filter: blur(10px);
            border-radius: 18px;
            padding: 25px;
        }
        .table-header {
            display: flex;
            justify-content: space-between;
            align-items: center;
            margin-bottom: 25px;
        }
        .btn-add {
            background: linear-gradient(135deg, #e50914, #ff6b6b);
            color: white;
            border: none;
            padding: 12px 24px;
            border-radius: 30px;
            font-weight: 600;
            text-decoration: none;
        }
        .btn-add:hover { opacity: 0.9; color: white; }
        .movie-table {
            width: 100%;
            border-collapse: collapse;
        }
        .movie-table th {
            text-align: left;
            padding: 15px 10px;
            color: #666;
            font-size: 13px;
            text-transform: uppercase;
            border-bottom: 2px solid #eee;
        }
        .movie-table td {
            padding: 15px 10px;
            border-bottom: 1px solid #f0f0f0;
            color: #1a1a2e;
        }
        .action-btn {
            color: #666;
            margin-right: 10px;
            text-decoration: none;
            font-size: 16px;
        }
        .action-btn:hover { color: #e50914; }
        .action-btn.delete:hover { color: #dc3545; }
        .format-badge {
            background: #28a745;
            color: white;
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
            <li><a href="/admin/movies" class="nav-link active"><i class="fas fa-film"></i> Movies</a></li>
            <li><a href="/admin/rentals" class="nav-link"><i class="fas fa-ticket-alt"></i> Rentals</a></li>
            <li><a href="/admin/payments" class="nav-link"><i class="fas fa-credit-card"></i> Payments</a></li>
            <li><a href="/admin/reviews" class="nav-link"><i class="fas fa-star"></i> Reviews</a></li>
            <li><a href="/admin/reports" class="nav-link"><i class="fas fa-file-alt"></i> Reports</a></li>
            <li><a href="/profile" class="nav-link"><i class="fas fa-user-circle"></i> Profile</a></li>
        </ul>
    </div>

    <div class="main-content">
        <h1 class="page-title">Movie Management</h1>

        <c:if test="${param.added != null}">
            <div class="alert alert-success"><i class="fas fa-check-circle"></i> Movie added successfully!</div>
        </c:if>
        <c:if test="${param.updated != null}">
            <div class="alert alert-success"><i class="fas fa-check-circle"></i> Movie updated successfully!</div>
        </c:if>
        <c:if test="${param.deleted != null}">
            <div class="alert alert-warning"><i class="fas fa-trash-alt"></i> Movie deleted.</div>
        </c:if>

        <div class="table-container">
            <div class="table-header">
                <h2 style="color: #1a1a2e;">All Movies</h2>
                <a href="/admin/movies/add" class="btn-add"><i class="fas fa-plus"></i> Add New Movie</a>
            </div>

            <table class="movie-table">
                <thead>
                <tr>
                    <th>ID</th>
                    <th>Title</th>
                    <th>Genre</th>
                    <th>Year</th>
                    <th>Type</th>
                    <th>Format</th>
                    <th>Price/Day</th>
                    <th>Actions</th>
                </tr>
                </thead>
                <tbody>
                <c:forEach var="movie" items="${movies}">
                    <tr>
                        <td>#${movie.id}</td>
                        <td><strong>${movie.title}</strong></td>
                        <td>${movie.genre}</td>
                        <td>${movie.year}</td>
                        <td><span class="badge bg-info">${movie.type}</span></td>
                        <td><span class="format-badge"><i class="fas fa-globe"></i> Online</span></td>
                        <td>$${String.format("%.2f", movie.basePrice)}</td>
                        <td>
                            <a href="/admin/movies/edit/${movie.id}" class="action-btn" title="Edit"><i class="fas fa-edit"></i></a>
                            <form action="/admin/movies/delete/${movie.id}" method="post" style="display:inline;">
                                <button type="submit" class="action-btn delete" title="Delete" style="background:none;border:none;padding:0;cursor:pointer;" onclick="return confirm('Delete ${movie.title}?')">
                                    <i class="fas fa-trash-alt"></i>
                                </button>
                            </form>
                        </td>
                    </tr>
                </c:forEach>
                </tbody>
            </table>

            <c:if test="${empty movies}">
                <div style="text-align: center; padding: 40px; color: #888;">
                    <i class="fas fa-film" style="font-size: 48px; margin-bottom: 15px;"></i>
                    <p>No movies in the catalog yet.</p>
                </div>
            </c:if>
        </div>
    </div>
</div>
</body>
</html>