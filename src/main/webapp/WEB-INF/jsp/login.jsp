<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Login • Alpha Studio Films</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">
    <link href="https://fonts.googleapis.com/css2?family=Poppins:wght@300;400;500;600;700&display=swap" rel="stylesheet">
    <style>
        * { margin: 0; padding: 0; box-sizing: border-box; }
        body {
            font-family: 'Poppins', sans-serif;
            background: linear-gradient(135deg, #0f0c29 0%, #302b63 50%, #24243e 100%);
            min-height: 100vh;
            display: flex;
            align-items: center;
            justify-content: center;
            padding: 20px;
            position: relative;
            overflow-x: hidden;
        }
        body::before {
            content: '';
            position: absolute;
            width: 200%;
            height: 200%;
            background: url('https://www.transparenttextures.com/patterns/cinema-stripes.png');
            opacity: 0.05;
            animation: slide 60s linear infinite;
        }
        @keyframes slide {
            0% { transform: translate(0, 0); }
            100% { transform: translate(-50%, -50%); }
        }
        .login-container {
            position: relative;
            z-index: 10;
            width: 100%;
            max-width: 450px;
        }
        .login-card {
            background: rgba(255, 255, 255, 0.95);
            backdrop-filter: blur(10px);
            border-radius: 24px;
            padding: 40px 35px;
            box-shadow: 0 25px 50px -12px rgba(0, 0, 0, 0.5), 0 0 0 1px rgba(255, 255, 255, 0.1) inset;
            transition: transform 0.3s ease;
            border: 1px solid rgba(255, 255, 255, 0.2);
        }
        .login-card:hover { transform: translateY(-5px); }
        .brand {
            text-align: center;
            margin-bottom: 30px;
        }
        .brand-icon {
            font-size: 48px;
            color: #e50914;
            margin-bottom: 10px;
            text-shadow: 0 4px 12px rgba(229, 9, 20, 0.3);
        }
        .brand h2 {
            font-weight: 700;
            background: linear-gradient(135deg, #e50914, #ff6b6b);
            -webkit-background-clip: text;
            -webkit-text-fill-color: transparent;
            background-clip: text;
            letter-spacing: 1px;
            margin-bottom: 5px;
        }
        .brand p {
            color: #666;
            font-size: 14px;
            font-weight: 400;
        }
        .alert {
            border-radius: 12px;
            padding: 12px 16px;
            font-size: 14px;
            font-weight: 500;
            border: none;
            display: flex;
            align-items: center;
            gap: 10px;
            margin-bottom: 24px;
        }
        .alert-success {
            background: linear-gradient(135deg, #00b09b, #96c93d);
            color: white;
        }
        .alert-danger {
            background: linear-gradient(135deg, #ff416c, #ff4b2b);
            color: white;
        }
        .alert-info {
            background: linear-gradient(135deg, #2193b0, #6dd5ed);
            color: white;
        }
        .alert-warning {
            background: linear-gradient(135deg, #f2994a, #f2c94c);
            color: white;
        }
        .form-group { margin-bottom: 22px; }
        .input-group {
            position: relative;
            display: flex;
            align-items: center;
            border-bottom: 2px solid #ddd;
            transition: border-color 0.3s;
        }
        .input-group:focus-within { border-bottom-color: #e50914; }
        .input-icon {
            color: #888;
            font-size: 18px;
            width: 30px;
            text-align: center;
            transition: color 0.3s;
        }
        .input-group:focus-within .input-icon { color: #e50914; }
        .input-field {
            width: 100%;
            padding: 12px 5px 12px 0;
            border: none;
            outline: none;
            background: transparent;
            font-size: 16px;
            font-weight: 500;
            color: #333;
        }
        .input-field::placeholder {
            color: #aaa;
            font-weight: 400;
        }
        .form-footer {
            display: flex;
            justify-content: space-between;
            align-items: center;
            margin-bottom: 25px;
            font-size: 14px;
        }
        .remember-me {
            display: flex;
            align-items: center;
            gap: 8px;
            color: #555;
            cursor: pointer;
        }
        .remember-me input {
            accent-color: #e50914;
            width: 16px;
            height: 16px;
        }
        .forgot-link {
            color: #e50914;
            text-decoration: none;
            font-weight: 500;
            transition: all 0.2s;
        }
        .forgot-link:hover {
            color: #b2070f;
            text-decoration: underline;
        }
        .btn-login {
            width: 100%;
            padding: 14px;
            border: none;
            border-radius: 50px;
            background: linear-gradient(135deg, #e50914, #ff6b6b);
            color: white;
            font-weight: 600;
            font-size: 16px;
            letter-spacing: 0.5px;
            box-shadow: 0 8px 20px rgba(229, 9, 20, 0.3);
            transition: all 0.3s;
            margin-bottom: 20px;
        }
        .btn-login:hover {
            transform: translateY(-2px);
            box-shadow: 0 12px 25px rgba(229, 9, 20, 0.4);
            background: linear-gradient(135deg, #ff0a16, #ff7b7b);
        }
        .register-link {
            text-align: center;
            color: #666;
            font-size: 15px;
        }
        .register-link a {
            color: #e50914;
            text-decoration: none;
            font-weight: 600;
            margin-left: 5px;
            transition: color 0.2s;
        }
        .register-link a:hover {
            color: #b2070f;
            text-decoration: underline;
        }
        @media (max-width: 480px) {
            .login-card { padding: 30px 20px; }
        }
    </style>
</head>
<body>
<div class="login-container">
    <div class="login-card">
        <div class="brand">
            <div class="brand-icon">
                <i class="fas fa-clapperboard"></i>
            </div>
            <h2>Alpha Studio Films</h2>
            <p>Your Premium Movie Rental</p>
        </div>

        <c:if test="${param.registered != null}">
            <div class="alert alert-success">
                <i class="fas fa-check-circle"></i>
                Registration successful! Please login.
            </div>
        </c:if>
        <c:if test="${param.logout != null}">
            <div class="alert alert-info">
                <i class="fas fa-sign-out-alt"></i>
                You have been logged out.
            </div>
        </c:if>
        <c:if test="${param.deleted != null}">
            <div class="alert alert-warning">
                <i class="fas fa-exclamation-triangle"></i>
                Account deleted successfully.
            </div>
        </c:if>
        <c:if test="${not empty error}">
            <div class="alert alert-danger">
                <i class="fas fa-exclamation-circle"></i>
                    ${error}
            </div>
        </c:if>

        <form action="/login" method="post">
            <div class="form-group">
                <div class="input-group">
                    <span class="input-icon"><i class="fas fa-envelope"></i></span>
                    <input type="email" name="email" class="input-field" placeholder="Email address" required autofocus>
                </div>
            </div>

            <div class="form-group">
                <div class="input-group">
                    <span class="input-icon"><i class="fas fa-lock"></i></span>
                    <input type="password" name="password" class="input-field" placeholder="Password" required>
                </div>
            </div>

            <div class="form-footer">
                <label class="remember-me">
                    <input type="checkbox" name="remember"> Remember me
                </label>
                <a href="#" class="forgot-link">Forgot password?</a>
            </div>

            <button type="submit" class="btn-login">
                <i class="fas fa-sign-in-alt"></i> Sign In
            </button>

            <div class="register-link">
                Don't have an account?
                <a href="/register">Create one now <i class="fas fa-arrow-right"></i></a>
            </div>
        </form>
    </div>
</div>
</body>
</html>