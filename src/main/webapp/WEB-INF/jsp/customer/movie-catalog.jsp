<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Movie Catalog • Alpha Studio Films</title>
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
        .navbar {
            background: rgba(0, 0, 0, 0.3);
            backdrop-filter: blur(10px);
            padding: 15px 40px;
            display: flex;
            justify-content: space-between;
            align-items: center;
            position: sticky;
            top: 0;
            z-index: 100;
            border-bottom: 1px solid rgba(255, 255, 255, 0.1);
        }
        .logo {
            font-size: 28px;
            font-weight: 700;
            background: linear-gradient(135deg, #e50914, #ff6b6b);
            -webkit-background-clip: text;
            -webkit-text-fill-color: transparent;
        }
        .nav-links a {
            color: white;
            margin-left: 25px;
            text-decoration: none;
            font-weight: 500;
            transition: color 0.2s;
        }
        .nav-links a:hover, .nav-links a.active { color: #ff6b6b; }
        .container { max-width: 1400px; margin: 0 auto; padding: 30px 20px; }
        .search-section {
            background: rgba(255, 255, 255, 0.95);
            border-radius: 20px;
            padding: 30px;
            margin-bottom: 30px;
        }
        .search-form { display: flex; gap: 15px; flex-wrap: wrap; }
        .search-input {
            flex: 1; min-width: 250px; padding: 14px 20px;
            border: 2px solid #e0e0e0; border-radius: 50px; font-size: 16px;
        }
        .search-input:focus { border-color: #e50914; outline: none; }
        .genre-select {
            padding: 14px 20px; border: 2px solid #e0e0e0;
            border-radius: 50px; font-size: 16px; background: white; cursor: pointer;
        }
        .btn-search {
            background: linear-gradient(135deg, #e50914, #ff6b6b);
            color: white; border: none; padding: 14px 30px;
            border-radius: 50px; font-weight: 600; cursor: pointer; text-decoration: none;
            display: inline-block;
        }
        .btn-search:hover { opacity: 0.9; color: white; }
        .section-title { color: white; margin-bottom: 25px; font-size: 28px; font-weight: 600; }
        .movie-grid {
            display: grid;
            grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
            gap: 30px;
        }
        .movie-card {
            background: rgba(255, 255, 255, 0.95);
            border-radius: 16px; overflow: hidden;
            box-shadow: 0 10px 30px rgba(0, 0, 0, 0.3);
            transition: transform 0.3s;
        }
        .movie-card:hover { transform: translateY(-8px); }
        .movie-poster {
            height: 350px;
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            display: flex; align-items: center; justify-content: center;
            color: white; position: relative;
        }
        .poster-placeholder { font-size: 60px; opacity: 0.7; }
        .movie-type-badge {
            position: absolute; top: 15px; left: 15px;
            background: linear-gradient(135deg, #e50914, #ff6b6b);
            color: white; padding: 5px 12px; border-radius: 30px;
            font-size: 12px; font-weight: 600;
        }
        .available-badge {
            position: absolute; top: 15px; right: 15px;
            background: #28a745; color: white;
            padding: 5px 12px; border-radius: 30px;
            font-size: 12px; font-weight: 600;
        }
        .movie-info { padding: 20px; }
        .movie-title {
            font-size: 18px; font-weight: 700; color: #1a1a2e;
            margin-bottom: 8px; white-space: nowrap; overflow: hidden; text-overflow: ellipsis;
        }
        .movie-meta {
            display: flex; justify-content: space-between;
            color: #666; font-size: 13px; margin-bottom: 12px;
        }
        .movie-price {
            font-size: 22px; font-weight: 700; color: #e50914;
            margin-bottom: 15px;
        }
        .movie-price small { font-size: 13px; color: #888; font-weight: 400; }
        .btn-view {
            width: 100%; background: linear-gradient(135deg, #e50914, #ff6b6b);
            color: white; border: none; padding: 12px; border-radius: 10px;
            font-weight: 600; cursor: pointer; text-align: center;
            text-decoration: none; display: block;
        }
        .btn-view:hover { opacity: 0.9; color: white; }
        .empty-state { text-align: center; padding: 60px; color: #aaa; }
        .empty-state i { font-size: 60px; margin-bottom: 20px; opacity: 0.5; }
        @media (max-width: 768px) {
            .navbar { flex-direction: column; gap: 15px; padding: 15px 20px; }
            .nav-links a { margin: 0 10px; font-size: 14px; }
        }
    </style>
</head>
<body>
<nav class="navbar">
    <span class="logo"><i class="fas fa-clapperboard"></i> Alpha Studio Films</span>
    <div class="nav-links">
        <a href="/movies" class="active"><i class="fas fa-film"></i> Movies</a>
        <a href="/my-rentals"><i class="fas fa-ticket-alt"></i> My Rentals</a>
        <a href="/recommendations"><i class="fas fa-star"></i> For You</a>
        <a href="/payment-history"><i class="fas fa-credit-card"></i> Payments</a>
        <c:if test="${user.role == 'admin'}">
            <a href="/admin/dashboard"><i class="fas fa-chart-pie"></i> Admin</a>
        </c:if>
    </div>
    <div style="display: flex; align-items: center; gap: 15px;">
        <a href="/profile" style="color: white; text-decoration: none;"><i class="fas fa-user-circle"></i> ${user.name}</a>
        <a href="/logout" style="color: #ff6b6b; text-decoration: none;"><i class="fas fa-sign-out-alt"></i></a>
    </div>
</nav>

<div class="container">
    <!-- Search Section -->
    <div class="search-section">
        <form action="/movies" method="get" class="search-form">
            <input type="text" name="search" class="search-input" placeholder="Search movies by title or genre..." value="${searchQuery}">
            <select name="genre" class="genre-select">
                <option value="">All Genres</option>
                <c:forEach var="g" items="${genres}">
                    <option value="${g}" ${selectedGenre == g ? 'selected' : ''}>${g}</option>
                </c:forEach>
            </select>
            <button type="submit" class="btn-search"><i class="fas fa-search"></i> Search</button>
            <a href="/movies" class="btn-search" style="background: #6c757d;"><i class="fas fa-times"></i> Clear</a>
        </form>
    </div>

    <!-- Section Title -->
    <h2 class="section-title">
        <c:choose>
            <c:when test="${not empty searchQuery}">Search Results for "${searchQuery}"</c:when>
            <c:when test="${not empty selectedGenre}">${selectedGenre} Movies</c:when>
            <c:otherwise>Now Showing</c:otherwise>
        </c:choose>
        <span style="font-size: 16px; margin-left: 15px; color: #aaa;">${movies.size()} movies found</span>
    </h2>

    <!-- Movie Grid -->
    <div class="movie-grid">
        <c:forEach var="movie" items="${movies}">
            <div class="movie-card">
                <div class="movie-poster">
                    <c:if test="${not empty movie.posterUrl}">
                        <img src="${movie.posterUrl}" alt="${movie.title}" style="width:100%;height:100%;object-fit:cover;">
                    </c:if>
                    <c:if test="${empty movie.posterUrl}">
                        <i class="fas fa-film poster-placeholder"></i>
                    </c:if>
                    <span class="movie-type-badge">${movie.type}</span>
                    <span class="available-badge">
                            <i class="fas fa-check-circle"></i> Available
                        </span>
                </div>
                <div class="movie-info">
                    <h3 class="movie-title">${movie.title}</h3>
                    <div class="movie-meta">
                        <span><i class="fas fa-tag"></i> ${movie.genre}</span>
                        <span><i class="fas fa-calendar"></i> ${movie.year}</span>
                    </div>
                    <div class="movie-meta">
                        <span><i class="fas fa-globe"></i> Online Streaming</span>
                    </div>
                    <div class="movie-price">
                        $${String.format("%.2f", movie.calculateRentalPrice(1))} <small>/ day</small>
                    </div>
                    <a href="/movies/${movie.id}" class="btn-view">
                        <i class="fas fa-info-circle"></i> View Details
                    </a>
                </div>
            </div>
        </c:forEach>
    </div>

    <!-- Empty State -->
    <c:if test="${empty movies}">
        <div class="empty-state">
            <i class="fas fa-film"></i>
            <h3>No movies found</h3>
            <p>Try adjusting your search or browse all movies.</p>
            <a href="/movies" class="btn-search" style="display: inline-block; margin-top: 20px;">Browse All Movies</a>
        </div>
    </c:if>
</div>
</body>
</html>