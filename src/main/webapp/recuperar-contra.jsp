<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!doctype html>
<html lang="es">
<head>
  <meta charset="UTF-8">
  <meta name="viewport"
        content="width=device-width, user-scalable=no, initial-scale=1.0, maximum-scale=1.0, minimum-scale=1.0">
  <meta http-equiv="X-UA-Compatible" content="ie=edge">
  <title>Recuperación de Contraseña</title>
  <link rel="stylesheet" href="assets/css/bootstrap.css">
  <link rel="stylesheet" href="assets/css/bi/bootstrap-icons.css">
  <style>
    body {background-color: #f8f9fa;}
  </style>
</head>
<body class="d-flex align-items-center justify-content-center min-vh-100 py-4">

<div class="container">
  <div class="row justify-content-center">
    <div class="col-12 col-md-8 col-lg-5">

      <div class="card shadow border-0 rounded-3">
        <div class="card-body p-5">

          <div class="text-center mb-4">
            <div class="display-4 text-primary mb-2">
              <i class="bi bi-person-plus-fill"></i>
            </div>
            <h2 class="fw-bold h4 text-dark">Recuperar Contraseña</h2>
            <p class="text-muted small">Ingresa el correo electrónico con el que te registraste</p>
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

          <form action="recuperar" method="post">

            <div class="form-floating mb-3">
              <input type="email" class="form-control" id="txtCorreo" name="email" placeholder="Correo electrónico" required>
              <label for="txtCorreo">
                <i class="bi bi-envelope me-1"></i> Correo electrónico
              </label>
            </div>

            <button class="btn btn-primary w-100 py-2 fw-semibold shadow-sm" type="submit">
              <i class="bi bi-check-circle me-2"></i> Recuperar Contraseña
            </button>

            <div class="d-flex flex-column align-items-center gap-2 small mt-4">
              <div>
                <span class="text-muted">¿Ya te acordaste de tu cuenta?</span>
                <a href="login.jsp" class="text-decoration-none fw-medium">Inicia sesión aquí</a>
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
