<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!doctype html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Editar Desarrollador</title>
    <link rel="stylesheet" href="assets/css/bootstrap.css">
    <link rel="stylesheet" href="assets/css/bi/bootstrap-icons.css">
    <link rel="stylesheet" href="assets/css/coordinador.css">
    <script src="https://cdn.jsdelivr.net/npm/sweetalert2@11"></script>
</head>
<body>

<jsp:include page="sidebar_de.jsp">
    <jsp:param name="active" value="gestion_usuarios" />
    <jsp:param name="active_sub" value="desarrollador" />
</jsp:include>

<main class="main-content">
    <h3 class="page-title">EDITAR DESARROLLADOR</h3>

    <div class="d-flex align-items-center mb-4 mt-4" style="color: var(--teal-main);">
        <i class="bi bi-info-circle me-2 fs-5"></i>
        <h5 class="mb-0 fw-bold">DATOS DEL DESARROLLADOR</h5>
    </div>

    <form id="formEditarDesarrollador" autocomplete="off">
        <input type="hidden" id="campoIdUsuario" name="id_usuario">

        <div class="row mb-4">
            <div class="col-md-4">
                <label for="campoNombre" class="form-label">Nombre <span class="text-danger">*</span> :</label>
                <input type="text" class="form-control" id="campoNombre" name="nombre" required>
            </div>
            <div class="col-md-4">
                <label for="campoApellidoP" class="form-label">Apellido Paterno <span class="text-danger">*</span> :</label>
                <input type="text" class="form-control" id="campoApellidoP" name="apellido_paterno" required>
            </div>
            <div class="col-md-4">
                <label for="campoApellidoM" class="form-label">Apellido Materno :</label>
                <input type="text" class="form-control" id="campoApellidoM" name="apellido_materno">
            </div>
        </div>

        <div class="row mb-4">
            <div class="col-md-4">
                <label for="campoDivision" class="form-label">División Académica <span class="text-danger">*</span> :</label>
                <select class="form-select" id="campoDivision" name="division" required>
                    <option value="" disabled selected>Seleccione división</option>
                    <option value="1">Datid</option>
                    <option value="2">Dacea</option>
                    <option value="3">Datefi</option>
                    <option value="4">Dami</option>
                    <option value="5">General</option>
                </select>
                <input type="hidden" id="campoDivisionHidden" name="idDivision">
            </div>
            <div class="col-md-4">
                <label for="campoNumEmpleado" class="form-label">Número de Empleado <span class="text-danger">*</span> :</label>
                <input type="text" class="form-control" id="campoNumEmpleado" name="numero_empleado" required>
            </div>
            <div class="col-md-4">
                <label for="campoTelefono" class="form-label">Número de Teléfono :</label>
                <input type="tel" class="form-control" id="campoTelefono" name="telefono">
            </div>
        </div>

        <div class="row mb-4">
            <div class="col-md-4">
                <label for="campoCorreo" class="form-label">Correo Institucional <span class="text-danger">*</span> :</label>
                <input type="email" class="form-control" id="campoCorreo" name="correo" required>
            </div>
        </div>

        <!-- Sección Cambiar Contraseña -->
        <div class="bg-teal text-white p-2 mb-4 fs-5" style="background-color: var(--teal-main);">
            Cambio de contraseña (Opcional)
        </div>

        <div class="row mb-4">

            <div class="col-4 mb-4">
                <label class="form-label text-muted">Contraseña Actual:</label>
                <div class="input-group">
                    <input type="password"
                           id="passActual"
                           class="form-control"
                           value="<%= "" %>" readonly>
                    <span class="input-group-text bg-white border-start-0" onclick="togglePassword('passActual')" style="cursor: pointer;">
                        <i id="icon-passActual" class="bi bi-eye-fill text-muted"></i>
                    </span>
                </div>
            </div>

            <div class="col-4 mb-4">
                <label for="passNueva" class="form-label">Nueva Contraseña (Opcional):</label>
                <div class="input-group">
                    <input type="password" class="form-control" id="passNueva" name="contrasena" placeholder="12 a 15 caracteres">
                    <button class="btn btn-outline-secondary" type="button" onclick="togglePass('passNueva')">
                        <i class="bi bi-eye-slash"></i>
                    </button>
                </div>
                <small class="text-muted">Debe tener entre 12 y 15 caracteres</small>
            </div>

            <div class="col-4 mb-4">
                <label for="passConfirm" class="form-label">Confirmar Contraseña:</label>
                <div class="input-group">
                    <input type="password" class="form-control" id="passConfirm" name="confirmarContrasena" placeholder="Repite la contraseña">
                    <button class="btn btn-outline-secondary" type="button" onclick="togglePass('passConfirm')">
                        <i class="bi bi-eye-slash"></i>
                    </button>
                </div>
            </div>
        </div>

        <div class="d-flex justify-content-end gap-3 mb-5">
            <a href="gestion_desarrolladores_de.jsp" class="btn btn-outline-teal px-4 py-2 fw-semibold d-flex align-items-center" style="border: 2px solid var(--teal-main); color: var(--teal-main); border-radius: 6px;">
                <i class="bi bi-chevron-left me-2"></i> Volver
            </a>
            <button type="submit" id="btnGuardar" class="btn-teal px-5 py-2" style="border-radius: 6px;">
                <i class="bi bi-save me-2"></i> Guardar Cambios
            </button>
        </div>
    </form>
</main>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
<script>window.contextPath = '<%= request.getContextPath() %>';</script>
<script src="assets/js/coordinador.js"></script>
<script>
    function togglePass(inputId) {
        var input = document.getElementById(inputId);
        var icon = input.parentElement.querySelector('i');
        if (input.type === 'password') {
            input.type = 'text';
            icon.className = 'bi bi-eye';
        } else {
            input.type = 'password';
            icon.className = 'bi bi-eye-slash';
        }
    }
</script>
<script src="assets/js/EditarDesarrollador.js?v=1.0"></script>
</body>
</html>