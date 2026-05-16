<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Register • Alpha Studio Films</title>
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
        .register-container {
            position: relative;
            z-index: 10;
            width: 100%;
            max-width: 480px;
        }
        .register-card {
            background: rgba(255, 255, 255, 0.95);
            backdrop-filter: blur(10px);
            border-radius: 24px;
            padding: 40px 35px;
            box-shadow: 0 25px 50px -12px rgba(0, 0, 0, 0.5), 0 0 0 1px rgba(255, 255, 255, 0.1) inset;
            transition: transform 0.3s ease;
            border: 1px solid rgba(255, 255, 255, 0.2);
        }
        .register-card:hover { transform: translateY(-5px); }
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
        .alert-danger {
            background: linear-gradient(135deg, #ff416c, #ff4b2b);
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
        .password-hint {
            font-size: 12px;
            color: #888;
            margin-top: 6px;
            margin-left: 30px;
        }
        .btn-register {
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
        .btn-register:hover {
            transform: translateY(-2px);
            box-shadow: 0 12px 25px rgba(229, 9, 20, 0.4);
            background: linear-gradient(135deg, #ff0a16, #ff7b7b);
        }
        .login-link {
            text-align: center;
            color: #666;
            font-size: 15px;
        }
        .login-link a {
            color: #e50914;
            text-decoration: none;
            font-weight: 600;
            margin-left: 5px;
            transition: color 0.2s;
        }
        .login-link a:hover {
            color: #b2070f;
            text-decoration: underline;
        }
        @media (max-width: 480px) {
            .register-card { padding: 30px 20px; }
        }
    </style>
</head>
<body>
<div class="register-container">
    <div class="register-card">
        <div class="brand">
            <div class="brand-icon">
                <i class="fas fa-ticket-alt"></i>
            </div>
            <h2>Alpha Studio Films</h2>
            <p>Start renting movies today</p>
        </div>

        <c:if test="${not empty error}">
            <div class="alert alert-danger">
                <i class="fas fa-exclamation-circle"></i>
                    ${error}
            </div>
        </c:if>

        <form action="/register" method="post">
            <div class="form-group">
                <div class="input-group">
                    <span class="input-icon"><i class="fas fa-user"></i></span>
                    <input type="text" name="name" class="input-field" placeholder="Full Name" required autofocus>
                </div>
            </div>

            <div class="form-group">
                <div class="input-group">
                    <span class="input-icon"><i class="fas fa-envelope"></i></span>
                    <input type="email" name="email" class="input-field" placeholder="Email address" required>
                </div>
            </div>

            <div class="form-group">
                <div class="input-group">
                    <span class="input-icon"><i class="fas fa-lock"></i></span>
                    <input type="password" name="password" class="input-field" placeholder="Password" required>
                </div>
                <div class="password-hint">
                    <i class="fas fa-info-circle"></i> Minimum 6 characters
                </div>
            </div>

            <button type="submit" class="btn-register">
                <i class="fas fa-user-plus"></i> Create Account
            </button>

            <div class="login-link">
                Already have an account?
                <a href="/login">Sign In <i class="fas fa-arrow-right"></i></a>
            </div>
        </form>
    </div>
</div>
</body>
</html>