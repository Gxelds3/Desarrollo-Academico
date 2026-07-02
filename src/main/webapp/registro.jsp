<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!doctype html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <meta http-equiv="X-UA-Compatible" content="ie=edge">
    <title>Crear Cuenta</title>
    <link rel="stylesheet" href="assets/css/bootstrap.css">
    <link rel="stylesheet" href="assets/css/bi/bootstrap-icons.css">
    <link rel="stylesheet" href="assets/css/registro.css">
</head>
<body class="d-flex align-items-center justify-content-center min-vh-100 body-registro">

<div class="container my-4">
    <div class="row justify-content-center">
        <div class="col-12 col-sm-10 col-md-6 col-lg-4">
            <div class="p-2">

                <div class="text-center mb-4">
                    <div class="mb-2">
                        <i class="bi bi-person-fill-add" style="font-size: 5.5rem; color: #51B9B7;"></i>
                    </div>
                    <h2 class="fw-bold fs-2 texto-primario">Crear Cuenta</h2>
                </div>

                <%-- Alertas de error --%>
                <c:if test="${not empty error}">
                    <div class="alert alert-danger py-2 rounded-3" role="alert">
                        <div class="small"><i class="bi bi-exclamation-triangle-fill me-2"></i>${error}</div>
                    </div>
                </c:if>

                <form action="registro" method="post">

                    <div class="row g-2 mb-3">
                        <div class="col-6">
                            <label for="nombre" class="form-label label-custom">Nombre(s):</label>
                            <div class="position-relative">
                                <i class="bi bi-person-fill position-absolute input-icon"></i>
                                <input type="text" class="form-control input-custom" id="nombre" name="nombre" placeholder="Nombre(s)" required>
                            </div>
                        </div>
                        <div class="col-6">
                            <label for="apellidos" class="form-label label-custom">Apellidos:</label>
                            <div class="position-relative">
                                <i class="bi bi-person-vcard position-absolute input-icon"></i>
                                <input type="text" class="form-control input-custom" id="apellidos" name="apellidos" placeholder="Apellidos" required>
                            </div>
                        </div>
                    </div>

                    <div class="mb-3">
                        <label for="NumeroEmpleado" class="form-label label-custom">Número de Empleado:</label>
                        <div class="position-relative">
                            <i class="bi bi-person-badge-fill position-absolute input-icon"></i>
                            <input type="text" class="form-control input-custom" id="NumeroEmpleado" name="numeroEmpleado" placeholder="Número de Empleado" required>
                        </div>
                    </div>

                    <div class="mb-3">
                        <label for="correoInstitucional" class="form-label label-custom">Correo institucional:</label>
                        <div class="position-relative">
                            <i class="bi bi-envelope position-absolute input-icon"></i>
                            <input type="email" class="form-control input-custom" id="correoInstitucional" name="correoInstitucional" placeholder="Correo institucional" required>
                        </div>
                    </div>

                    <div class="mb-3">
                        <label for="telefono" class="form-label label-custom">Teléfono:</label>
                        <div class="position-relative">
                            <i class="bi bi-telephone position-absolute input-icon"></i>
                            <input type="tel" class="form-control input-custom" id="telefono" name="telefono" placeholder="Teléfono" required>
                        </div>
                    </div>

                    <div class="mb-3">
                        <label for="division" class="form-label label-custom">División:</label>
                        <select class="form-select select-custom text-muted" id="division" name="division" required>
                            <option value="" disabled selected hidden>División</option>
                            <option value="1">División Académica 1</option>
                            <option value="2">División Académica 2</option>
                        </select>
                    </div>

                    <div class="mb-3">
                        <div class="d-flex justify-content-between align-items-center">
                            <label for="txtPassword" class="form-label label-custom mb-1">Contraseña:</label>
                            <i class="bi bi-eye toggle-password-icon" id="togglePassword"></i>
                        </div>
                        <div class="position-relative">
                            <i class="bi bi-lock-fill position-absolute input-icon"></i>
                            <input type="password" class="form-control input-custom" id="txtPassword" name="contra" placeholder="Contraseña" required>
                        </div>
                    </div>

                    <div class="mb-4">
                        <div class="d-flex justify-content-between align-items-center">
                            <label for="txtConfirmPassword" class="form-label label-custom mb-1">Confirmar contraseña:</label>
                            <i class="bi bi-eye toggle-password-icon" id="toggleConfirmPassword"></i>
                        </div>
                        <div class="position-relative">
                            <i class="bi bi-lock-fill position-absolute input-icon"></i>
                            <input type="password" class="form-control input-custom" id="txtConfirmPassword" name="confirmContra" placeholder="Confirmar contraseña" required>
                        </div>
                    </div>

                    <div class="d-flex justify-content-center gap-4 mb-4">
                        <div class="form-check">
                            <input class="form-check-input checkbox-border" type="checkbox" value="coordinador" id="chkCordinador" name="roles" checked>
                            <label class="form-check-label fw-bold text-dark small" for="chkCordinador">Cordinador</label>
                        </div>
                        <div class="form-check">
                            <input class="form-check-input checkbox-border" type="checkbox" value="desarrollo" id="chkDesarrollo" name="roles">
                            <label class="form-check-label fw-bold text-dark small" for="chkDesarrollo">Desarrollo Academico</label>
                        </div>
                    </div>

                    <button class="btn w-100 mb-3 fw-bold btn-registrarme" type="submit">
                        Registrarme
                    </button>

                    <div class="text-center small">
                        <span class="text-dark">¿Ya te acordaste de tu cuenta? </span>
                        <a href="login.jsp" class="text-decoration-none enlace-login">Inicia sesión aquí</a>
                    </div>
                </form>

            </div>
        </div>
    </div>
</div>

<script src="assets/js/bootstrap.js"></script>
<script src="assets/js/login.js"></script>
</body>
</html>