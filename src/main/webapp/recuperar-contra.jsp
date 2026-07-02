<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!doctype html>
<html lang="es">
<head>
  <meta charset="UTF-8">
  <meta name="viewport"
        content="width=device-width, user-scalable=no, initial-scale=1.0, maximum-scale=1.0, minimum-scale=1.0">
  <meta http-equiv="X-UA-Compatible" content="ie=edge">
  <title>Recuperación de Contraseña</title>
  <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/bootstrap.css">
  <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/bi/bootstrap-icons.css">
</head>
<body class="d-flex align-items-center justify-content-center min-vh-100 py-4 bg-white">

<div class="container">
  <div class="row justify-content-center">
    <div class="col-12 col-sm-10 col-md-6 col-lg-4">

      <div class="p-4">

        <div class="text-center mb-4">
          <img src="${pageContext.request.contextPath}/assets/img/logo_recu.png" alt="Recuperar" class="img-fluid" style="max-width: 100px; height: auto;">
          <h2 class="fw-bold mb-3" style="font-size: 2rem; color: #4cbab8;">Recuperar contraseña</h2>
          <p class="text-dark">Ingresa el correo electrónico con el que te registraste</p>
        </div>

        <c:if test="${not empty error}">
          <div class="alert alert-danger d-flex align-items-center py-2 rounded-3" role="alert">
            <i class="bi bi-exclamation-triangle-fill me-2"></i>
            <div class="small">${error}</div>
          </div>
        </c:if>

        <c:if test="${not empty mensaje}">
          <div class="alert alert-info d-flex align-items-center py-2 rounded-3" role="alert">
            <i class="bi bi-info-circle-fill me-2"></i>
            <div class="small">${mensaje}</div>
          </div>
        </c:if>

        <form action="recuperar" method="post">

          <div class="mb-3">
            <label for="txtCorreo" class="form-label fw-bold text-dark mb-1 small">Correo institucional:</label>
            <div class="input-group">
              <span class="input-group-text bg-transparent border-end-0 text-secondary py-2"><i class="bi bi-envelope"></i></span>
              <input type="email" class="form-control border-start-0 py-2" id="txtCorreo" name="email" placeholder="Correo institucional" required
                     style="box-shadow: none; border-color: #ced4da;"
                     onfocus="this.style.borderColor='#4cbab8'"
                     onblur="this.style.borderColor='#ced4da'">
            </div>
          </div>

          <button class="btn w-100 py-2 rounded-3 text-white fw-medium" type="submit"
                  style="background-color: #4cbab8; border-color: #4cbab8;"
                  onmouseover="this.style.backgroundColor='#3ba3a1'; this.style.borderColor='#3ba3a1';"
                  onmouseout="this.style.backgroundColor='#4cbab8'; this.style.borderColor='#4cbab8';">
            Recuperar contraseña
          </button>

          <div class="text-center small mt-3">
            <span class="text-dark">¿Ya te acordaste de tu cuenta? </span>
            <a href="login.jsp" class="text-decoration-none fw-semibold" style="color: #4ea9e6;"
               onmouseover="this.className='text-decoration-underline fw-semibold'"
               onmouseout="this.className='text-decoration-none fw-semibold'">
              Inicia sesión aquí
            </a>
          </div>
        </form>

      </div>
    </div>
  </div>
</div>

<script src="${pageContext.request.contextPath}/assets/js/bootstrap.js"></script>
</body>
</html>