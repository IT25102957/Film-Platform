<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title><c:choose><c:when test="${isEdit}">Edit</c:when><c:otherwise>Add</c:otherwise></c:choose> Movie • Alpha Studio Films</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">
    <link href="https://fonts.googleapis.com/css2?family=Poppins:wght@300;400;500;600;700&display=swap" rel="stylesheet">
    <style>
        * { margin: 0; padding: 0; box-sizing: border-box; }
        body {
            font-family: 'Poppins', sans-serif;
            background: linear-gradient(135deg, #0f0c29 0%, #302b63 50%, #24243e 100%);
            min-height: 100vh;
            padding: 30px;
        }
        .container { max-width: 800px; margin: 0 auto; }
        .form-card {
            background: rgba(255, 255, 255, 0.95);
            backdrop-filter: blur(10px);
            border-radius: 24px;
            padding: 40px;
            box-shadow: 0 25px 50px rgba(0, 0, 0, 0.3);
        }
        .form-title { font-size: 28px; font-weight: 700; color: #1a1a2e; margin-bottom: 30px; }
        .form-group { margin-bottom: 22px; }
        .form-group label { display: block; font-weight: 600; color: #1a1a2e; margin-bottom: 8px; }
        .form-control { width: 100%; padding: 12px 16px; border: 2px solid #e0e0e0; border-radius: 12px; font-size: 15px; transition: border-color 0.3s; }
        .form-control:focus { border-color: #e50914; outline: none; }
        .form-row { display: grid; grid-template-columns: 1fr 1fr; gap: 20px; }
        .btn-submit { background: linear-gradient(135deg, #e50914, #ff6b6b); color: white; border: none; padding: 14px 30px; border-radius: 50px; font-weight: 600; font-size: 16px; cursor: pointer; width: 100%; }
        .btn-cancel { background: #6c757d; color: white; border: none; padding: 14px 30px; border-radius: 50px; font-weight: 600; text-decoration: none; display: inline-block; text-align: center; }
        .hint { font-size: 13px; color: #666; margin-top: 6px; }
    </style>
</head>
<body>
<div class="container">
    <div class="form-card">
        <a href="/admin/movies" style="color: #e50914; text-decoration: none; margin-bottom: 20px; display: inline-block;">
            <i class="fas fa-arrow-left"></i> Back to Movies
        </a>

        <h2 class="form-title">
            <c:choose>
                <c:when test="${isEdit}">Edit Movie</c:when>
                <c:otherwise>Add New Movie</c:otherwise>
            </c:choose>
        </h2>

        <form action="<c:choose><c:when test='${isEdit}'>/admin/movies/edit/${movie.id}</c:when><c:otherwise>/admin/movies/add</c:otherwise></c:choose>" method="post">
            <div class="form-row">
                <div class="form-group">
                    <label><i class="fas fa-film"></i> Title</label>
                    <input type="text" name="title" class="form-control" value="${movie.title}" required>
                </div>
                <div class="form-group">
                    <label><i class="fas fa-tag"></i> Genre</label>
                    <input type="text" name="genre" class="form-control" value="${movie.genre}" required>
                </div>
            </div>

            <div class="form-row">
                <div class="form-group">
                    <label><i class="fas fa-calendar"></i> Year</label>
                    <input type="number" name="year" class="form-control" value="${isEdit ? movie.year : 2026}" min="1900" max="2026" required>
                </div>
                <div class="form-group">
                    <label><i class="fas fa-dollar-sign"></i> Rental Price ($/day)</label>
                    <input type="number" name="basePrice" class="form-control" value="${movie.basePrice}" min="0.01" step="0.01" required>
                    <div class="hint">This price will be shown exactly on the rental page.</div>
                </div>
            </div>

            <div class="form-group">
                <label><i class="fas fa-tags"></i> Movie Type</label>
                <select name="type" class="form-control" required>
                    <option value="New Release" ${movie.type == 'New Release' ? 'selected' : ''}>New Release</option>
                    <option value="Classic" ${movie.type == 'Classic' ? 'selected' : ''}>Classic</option>
                </select>
            </div>

            <div class="form-group">
                <label><i class="fas fa-image"></i> Poster URL</label>
                <input type="text" name="posterUrl" class="form-control" value="${movie.posterUrl}" placeholder="https://image-url.com/poster.jpg">
            </div>

            <div class="form-group">
                <label><i class="fas fa-align-left"></i> Description</label>
                <textarea name="description" class="form-control" rows="3">${movie.description}</textarea>
            </div>

            <div class="form-group">
                <label><i class="fab fa-youtube"></i> Trailer URL</label>
                <input type="text" name="trailerUrl" class="form-control" value="${movie.trailerUrl}" placeholder="https://youtube.com/watch?v=...">
            </div>

            <div style="display: flex; gap: 15px; margin-top: 30px;">
                <button type="submit" class="btn-submit">
                    <i class="fas fa-save"></i>
                    <c:choose>
                        <c:when test="${isEdit}">Update Movie</c:when>
                        <c:otherwise>Add Movie</c:otherwise>
                    </c:choose>
                </button>
                <a href="/admin/movies" class="btn-cancel" style="flex:1;">
                    <i class="fas fa-times"></i> Cancel
                </a>
            </div>
        </form>
    </div>
</div>
</body>
</html>
