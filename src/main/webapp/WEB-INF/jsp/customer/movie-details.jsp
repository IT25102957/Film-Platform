<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>${movie.title} • Alpha Studio Films</title>
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
        }
        .nav-links a:hover { color: #ff6b6b; }
        .container { max-width: 1000px; margin: 0 auto; padding: 30px 20px; }

        .detail-card {
            background: rgba(255, 255, 255, 0.95);
            border-radius: 24px;
            padding: 40px;
            box-shadow: 0 25px 50px rgba(0, 0, 0, 0.3);
            margin-bottom: 30px;
        }
        .movie-header {
            display: grid;
            grid-template-columns: 280px 1fr;
            gap: 40px;
            margin-bottom: 30px;
        }
        .poster-placeholder {
            width: 100%; height: 380px;
            background: linear-gradient(135deg, #667eea, #764ba2);
            border-radius: 16px;
            display: flex; align-items: center; justify-content: center;
            color: white; font-size: 80px;
        }
        .info-section h1 { font-size: 36px; font-weight: 700; color: #1a1a2e; margin-bottom: 10px; }
        .badge-container { display: flex; gap: 10px; margin-bottom: 20px; flex-wrap: wrap; }
        .type-badge {
            background: linear-gradient(135deg, #e50914, #ff6b6b);
            color: white; padding: 6px 16px; border-radius: 30px; font-size: 14px; font-weight: 600;
        }
        .available-badge {
            background: #28a745; color: white;
            padding: 6px 16px; border-radius: 30px; font-size: 14px; font-weight: 600;
        }
        .rented-badge {
            background: #007bff; color: white;
            padding: 6px 16px; border-radius: 30px; font-size: 14px; font-weight: 600;
        }
        .detail-grid { display: grid; grid-template-columns: 1fr 1fr; gap: 20px; margin: 25px 0; }
        .detail-item { display: flex; align-items: center; gap: 10px; }
        .detail-icon {
            width: 40px; height: 40px; background: #f0f0f0;
            border-radius: 12px; display: flex; align-items: center;
            justify-content: center; color: #e50914;
        }
        .price-section {
            background: linear-gradient(135deg, #f8f9fa, #e9ecef);
            padding: 25px; border-radius: 16px; margin: 25px 0;
        }
        .price-display { font-size: 42px; font-weight: 700; color: #e50914; }
        .action-buttons { display: flex; gap: 15px; flex-wrap: wrap; }
        .btn-primary {
            background: linear-gradient(135deg, #e50914, #ff6b6b);
            color: white; border: none; padding: 16px 32px;
            border-radius: 50px; font-weight: 600; font-size: 16px;
            text-decoration: none; display: inline-block;
        }
        .btn-watch {
            background: linear-gradient(135deg, #28a745, #20c997);
            color: white; border: none; padding: 16px 32px;
            border-radius: 50px; font-weight: 600; font-size: 16px;
            text-decoration: none; display: inline-block;
        }
        .btn-secondary {
            background: transparent; color: #e50914; border: 2px solid #e50914;
            padding: 14px 30px; border-radius: 50px; font-weight: 600; text-decoration: none;
        }

        /* Reviews Section */
        .reviews-section {
            background: rgba(255, 255, 255, 0.95);
            border-radius: 24px;
            padding: 40px;
            box-shadow: 0 25px 50px rgba(0, 0, 0, 0.3);
        }
        .reviews-header {
            display: flex;
            justify-content: space-between;
            align-items: center;
            margin-bottom: 25px;
            padding-bottom: 20px;
            border-bottom: 2px solid #e0e0e0;
        }
        .reviews-title { font-size: 24px; font-weight: 700; color: #1a1a2e; }
        .rating-summary { display: flex; align-items: center; gap: 15px; }
        .rating-stars-large { font-size: 28px; color: #ffc107; }
        .rating-number { font-size: 36px; font-weight: 700; color: #1a1a2e; }
        .review-card {
            background: #f8f9fa;
            border-radius: 16px;
            padding: 25px;
            margin-bottom: 20px;
        }
        .review-header {
            display: flex;
            justify-content: space-between;
            align-items: flex-start;
            margin-bottom: 12px;
        }
        .reviewer-name { font-weight: 600; color: #1a1a2e; }
        .reviewer-badge {
            padding: 3px 10px; border-radius: 20px;
            font-size: 11px; font-weight: 600; text-transform: uppercase;
        }
        .review-stars { color: #ffc107; font-size: 16px; }
        .review-date { color: #888; font-size: 13px; margin-bottom: 12px; }
        .review-comment { color: #444; line-height: 1.7; margin-bottom: 15px; }
        .spoiler-alert {
            background: #dc3545; color: white;
            padding: 12px 16px; border-radius: 10px; margin-bottom: 15px; font-size: 14px;
        }
        .spoiler-btn {
            background: none; border: none; color: white;
            text-decoration: underline; cursor: pointer; font-weight: 500;
        }
        .review-footer {
            display: flex; justify-content: space-between; align-items: center;
        }
        .helpful-btn {
            background: none; border: none; color: #28a745;
            cursor: pointer; font-size: 14px; padding: 5px 10px; border-radius: 20px;
        }
        .helpful-btn:hover { background: rgba(40, 167, 69, 0.1); }
        .review-actions a {
            color: #666; margin-left: 15px; text-decoration: none; font-size: 14px;
        }
        .review-actions a:hover { color: #e50914; }
        .no-reviews { text-align: center; padding: 50px; color: #888; }
        .no-reviews i { font-size: 60px; margin-bottom: 20px; opacity: 0.5; }
        .alert { border-radius: 12px; padding: 14px 20px; margin-bottom: 20px; }

        @media (max-width: 768px) {
            .movie-header { grid-template-columns: 1fr; }
            .reviews-header { flex-direction: column; gap: 20px; }
        }
    </style>
</head>
<body>
<nav class="navbar">
    <span class="logo"><i class="fas fa-clapperboard"></i> Alpha Studio Films</span>
    <div class="nav-links">
        <a href="/movies"><i class="fas fa-film"></i> Movies</a>
        <a href="/my-rentals"><i class="fas fa-ticket-alt"></i> My Rentals</a>
        <a href="/my-reviews"><i class="fas fa-star"></i> My Reviews</a>
        <a href="/recommendations"><i class="fas fa-thumbs-up"></i> For You</a>
        <c:if test="${user.role == 'admin'}">
            <a href="/admin/dashboard"><i class="fas fa-chart-pie"></i> Admin</a>
        </c:if>
    </div>
</nav>

<div class="container">
    <!-- Movie Details -->
    <div class="detail-card">
        <a href="/movies" style="color: #e50914; text-decoration: none; margin-bottom: 20px; display: inline-block;">
            <i class="fas fa-arrow-left"></i> Back to Movies
        </a>

        <div class="movie-header">
            <div class="poster-section">
                <c:if test="${not empty movie.posterUrl and movie.posterUrl.length() > 10}">
                    <img src="${movie.posterUrl}" alt="${movie.title}" style="width:100%;border-radius:16px;object-fit:cover;">
                </c:if>
                <c:if test="${empty movie.posterUrl or movie.posterUrl.length() <= 10}">
                    <div class="poster-placeholder"><i class="fas fa-film"></i></div>
                </c:if>
            </div>

            <div class="info-section">
                <h1>${movie.title}</h1>
                <div class="badge-container">
                    <span class="type-badge">${movie.type}</span>
                    <span class="available-badge">
                            <i class="fas fa-check-circle"></i> Available Now
                        </span>
                    <c:if test="${hasActiveRental}">
                            <span class="rented-badge">
                                <i class="fas fa-play-circle"></i> Currently Rented
                            </span>
                    </c:if>
                </div>

                <p style="color: #666; line-height: 1.6; margin-bottom: 20px;">
                    ${not empty movie.description ? movie.description : 'No description available.'}
                </p>

                <div class="detail-grid">
                    <div class="detail-item">
                        <span class="detail-icon"><i class="fas fa-tag"></i></span>
                        <div><strong>Genre</strong><br>${movie.genre}</div>
                    </div>
                    <div class="detail-item">
                        <span class="detail-icon"><i class="fas fa-calendar"></i></span>
                        <div><strong>Year</strong><br>${movie.year}</div>
                    </div>
                    <div class="detail-item">
                        <span class="detail-icon"><i class="fas fa-globe"></i></span>
                        <div><strong>Format</strong><br>Online Streaming</div>
                    </div>
                    <div class="detail-item">
                        <span class="detail-icon"><i class="fas fa-dollar-sign"></i></span>
                        <div><strong>Base Price</strong><br>$${String.format("%.2f", movie.basePrice)} / day</div>
                    </div>
                </div>

                <c:if test="${not empty movie.trailerUrl}">
                    <div style="margin: 20px 0;">
                        <a href="${movie.trailerUrl}" target="_blank" style="color: #e50914; text-decoration: none;">
                            <i class="fab fa-youtube"></i> Watch Trailer
                        </a>
                    </div>
                </c:if>

                <div class="price-section">
                    <div style="display: flex; justify-content: space-between; align-items: center; flex-wrap: wrap; gap: 20px;">
                        <div>
                            <span style="color: #666;">Rental Price</span>
                            <div class="price-display">$${String.format("%.2f", movie.calculateRentalPrice(1))}</div>
                            <span style="color: #888;">per day</span>
                        </div>
                        <div style="text-align: right;">
                            <div><strong>3 days:</strong> $${String.format("%.2f", movie.calculateRentalPrice(3))}</div>
                            <div><strong>7 days:</strong> $${String.format("%.2f", movie.calculateRentalPrice(7))}</div>
                        </div>
                    </div>
                </div>

                <div class="action-buttons">
                    <c:choose>
                        <c:when test="${hasActiveRental}">
                            <a href="/watch/${movie.id}" class="btn-watch">
                                <i class="fas fa-play-circle"></i> Watch Now
                            </a>
                        </c:when>
                        <c:otherwise>
                            <a href="/rent/${movie.id}" class="btn-primary">
                                <i class="fas fa-ticket-alt"></i> Rent Now
                            </a>
                        </c:otherwise>
                    </c:choose>
                    <a href="/review/write/${movie.id}" class="btn-secondary">
                        <i class="fas fa-star"></i> Write a Review
                    </a>
                </div>
            </div>
        </div>
    </div>

    <!-- ==================== REVIEWS SECTION ==================== -->
    <div class="reviews-section">
        <div class="reviews-header">
            <div class="reviews-title">
                <i class="fas fa-star" style="color: #ffc107;"></i> Customer Reviews
            </div>
            <c:if test="${ratingAggregator.totalReviews > 0}">
                <div class="rating-summary">
                    <div class="rating-stars-large">${ratingAggregator.ratingStars}</div>
                    <div>
                        <div class="rating-number">${String.format("%.1f", ratingAggregator.weightedAverageRating)}</div>
                        <div style="color: #666; font-size: 14px;">
                            out of 5 • ${ratingAggregator.totalReviews} review${ratingAggregator.totalReviews > 1 ? 's' : ''}
                        </div>
                    </div>
                </div>
            </c:if>
        </div>

        <c:if test="${not empty success}">
            <div class="alert alert-success"><i class="fas fa-check-circle"></i> ${success}</div>
        </c:if>
        <c:if test="${not empty error}">
            <div class="alert alert-danger"><i class="fas fa-exclamation-circle"></i> ${error}</div>
        </c:if>

        <!-- No Reviews -->
        <c:if test="${empty reviews}">
            <div class="no-reviews">
                <i class="far fa-comment-dots"></i>
                <h3>No reviews yet</h3>
                <p>Be the first to share your thoughts about this movie!</p>
                <a href="/review/write/${movie.id}" class="btn-primary" style="margin-top: 20px; display: inline-block;">
                    <i class="fas fa-pen"></i> Write a Review
                </a>
            </div>
        </c:if>

        <!-- Reviews List -->
        <c:forEach var="review" items="${reviews}">
            <div class="review-card">
                <div class="review-header">
                    <div class="reviewer-info" style="display: flex; align-items: center; gap: 12px;">
                        <span class="reviewer-name">${review.customerName}</span>
                        <span class="reviewer-badge" style="background: ${review.reviewType == 'Critic' ? '#e50914' : '#6c757d'}; color: white;">
                                ${review.reviewType}
                        </span>
                    </div>
                    <div class="review-stars">${review.stars}</div>
                </div>

                <div class="review-date">
                    <i class="far fa-calendar-alt"></i> ${review.formattedDate}
                </div>

                <c:if test="${review.containsSpoiler}">
                    <div class="spoiler-alert" id="spoiler-alert-${review.reviewId}">
                        <i class="fas fa-exclamation-triangle"></i>
                        This review contains spoilers.
                        <button class="spoiler-btn" onclick="showSpoiler(${review.reviewId})">Click to reveal</button>
                    </div>
                    <div class="review-comment" id="spoiler-content-${review.reviewId}" style="display: none;">
                            ${review.comment}
                    </div>
                </c:if>
                <c:if test="${!review.containsSpoiler}">
                    <div class="review-comment">${review.comment}</div>
                </c:if>

                <div class="review-footer">
                    <button class="helpful-btn" onclick="markHelpful(${review.reviewId})" id="helpful-btn-${review.reviewId}">
                        <i class="far fa-thumbs-up"></i>
                        Helpful • <span id="helpful-count-${review.reviewId}">${review.helpfulVotes}</span>
                    </button>

                    <!-- Edit/Delete only for review owner or admin -->
                    <div class="review-actions">
                        <c:if test="${user.id == review.customerId}">
                            <a href="/review/edit/${review.reviewId}">
                                <i class="fas fa-edit"></i> Edit
                            </a>
                            <a href="/review/delete/${review.reviewId}"
                               onclick="return confirm('Delete this review?')">
                                <i class="fas fa-trash-alt"></i> Delete
                            </a>
                        </c:if>
                        <c:if test="${user.role == 'admin' && user.id != review.customerId}">
                            <a href="/review/delete/${review.reviewId}"
                               onclick="return confirm('Delete this review as admin?')" style="color: #dc3545;">
                                <i class="fas fa-trash-alt"></i> Delete
                            </a>
                        </c:if>
                    </div>
                </div>
            </div>
        </c:forEach>
    </div>
</div>

<script>
    function showSpoiler(reviewId) {
        document.getElementById('spoiler-alert-' + reviewId).style.display = 'none';
        document.getElementById('spoiler-content-' + reviewId).style.display = 'block';
    }

    function markHelpful(reviewId) {
        fetch('/review/helpful/' + reviewId, { method: 'POST' })
            .then(response => {
                if (response.ok) {
                    const countSpan = document.getElementById('helpful-count-' + reviewId);
                    countSpan.textContent = parseInt(countSpan.textContent) + 1;
                    const btn = document.getElementById('helpful-btn-' + reviewId);
                    btn.innerHTML = '<i class="fas fa-check"></i> Marked Helpful • ' + countSpan.textContent;
                    btn.style.color = '#1a1a2e';
                    btn.disabled = true;
                }
            });
    }
</script>
</body>
</html>