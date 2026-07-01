<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!doctype html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport"
          content="width=device-width, user-scalable=no, initial-scale=1.0, maximum-scale=1.0, minimum-scale=1.0">
    <meta http-equiv="X-UA-Compatible" content="ie=edge">
    <title>Iniciar sesión</title>
    <link rel="stylesheet" href="assets/css/bootstrap.css">
    <link rel="stylesheet" href="assets/css/bi/bootstrap-icons.css">
    <style>
        body {background-color: #f8f9fa;}
    </style>
</head>
<body class="d-flex align-items-center justify-content-center vh-100">

<div class="container">
    <div class="row justify-content-center">
        <div class="col-12 col-md-6 col-lg-4">

            <div class="card shadow border-0 rounded-3">
                <div class="card-body p-5">

                    <div class="text-center mb-4">
                        <div class="display-4 text-primary mb-2">
                            <i class="bi bi-person-circle"></i>
                        </div>
                        <h2 class="fw-bold h4 text-dark">Bienvenido</h2>
                        <p class="text-muted small">Ingresa tus credenciales para acceder</p>
                    </div>

                    <c:if test="${not empty error}">
                        <div class="alert alert-danger d-flex align-items-center py-2" role="alert">
                            <i class="bi bi-exclamation-triangle-fill me-2"></i>
                            <div class="small">
                                ${error}
                            </div>
                        </div>
                    </c:if>

                    <c:if test="${not empty mensaje}">
                        <div class="alert alert-info d-flex align-items-center py-2" role="alert">
                            <i class="bi bi-info-circle-fill me-2"></i>
                            <div class="small">
                                    ${mensaje}
                            </div>
                        </div>
                    </c:if>

                    <form action="login" method="post">

                        <div class="form-floating mb-3">
                            <input type="text" class="form-control" id="txtUsuario" name="email" placeholder="Usuario" required>
                            <label for="txtUsuario">
                                <i class="bi bi-person me-1"></i> Email
                            </label>
                        </div>

                        <div class="form-floating mb-3">
                            <input type="password" class="form-control" id="txtPassword" name="contra" placeholder="Contraseña" required>
                            <label for="txtPassword">
                                <i class="bi bi-lock me-1"></i> Contraseña
                            </label>
                        </div>

                        <button class="btn btn-primary w-100 py-2 fw-semibold shadow-sm" type="submit">
                            <i class="bi bi-box-arrow-in-right me-2"></i> Iniciar Sesión
                        </button>

                        <div class="d-flex flex-column align-items-center gap-2 small mt-2">
                            <div>
                                <span class="text-muted">¿No tienes cuenta?</span>
                                <a href="registro.jsp" class="text-decoration-none fw-medium">Regístrate</a>
                            </div>
                            <div>
                                <a href="recuperar-contra.jsp" class="text-decoration-none text-muted">Olvidé mi contraseña</a>
                            </div>
                        </div>
                    </form>
                </div>
            </div>
        </div>
    </div>
</div>

<script src="assets/js/bootstrap.js"></script>
</body>
</html>