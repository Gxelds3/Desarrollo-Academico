<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!doctype html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Página no encontrada</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/bootstrap.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/bi/bootstrap-icons.css">
</head>
<body class="d-flex align-items-center justify-content-center vh-100 bg-light">
    <div class="text-center">
        <i class="bi bi-exclamation-triangle text-warning" style="font-size: 5rem;"></i>
        <h1 class="display-1 fw-bold text-dark mt-3">404</h1>
        <h3 class="text-secondary mb-4">Página no encontrada</h3>
        <p class="text-muted mb-5">Lo sentimos, la página que buscas no existe o fue movida.</p>
        <a href="${pageContext.request.contextPath}/login.jsp" class="btn btn-primary px-4 py-2" style="background-color: #00847b; border-color: #00847b;">Volver al inicio</a>
    </div>
</body>
</html>
