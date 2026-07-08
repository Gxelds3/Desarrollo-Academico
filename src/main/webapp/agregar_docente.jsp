<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!doctype html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Agregar Docente</title>
    <link rel="stylesheet" href="assets/css/bootstrap.css">
    <link rel="stylesheet" href="assets/css/bi/bootstrap-icons.css">
    <link rel="stylesheet" href="assets/css/coordinador.css">
</head>
<body>

<jsp:include page="sidebar.jsp">
    <jsp:param name="active" value="gestion_docente" />
</jsp:include>

<main class="main-content">
    <h3 class="page-title">AGREGAR DOCENTE</h3>
    
    <div class="d-flex align-items-center mb-4 mt-4" style="color: var(--teal-main);">
        <i class="bi bi-info-circle me-2 fs-5"></i>
        <h5 class="mb-0 fw-bold">DATOS DEL DOCENTE</h5>
    </div>

    <form action="#" method="POST">
        <div class="row mb-4">
            <div class="col-md-4">
                <label class="form-label">Nombre del Docente <span class="text-danger">*</span> :</label>
                <input type="text" class="form-control" name="nombre" required>
            </div>
            <div class="col-md-4">
                <label class="form-label">Apellido Paterno <span class="text-danger">*</span> :</label>
                <input type="text" class="form-control" name="apellido_paterno" required>
            </div>
            <div class="col-md-4">
                <label class="form-label">Apellido Materno <span class="text-danger">*</span> :</label>
                <input type="text" class="form-control" name="apellido_materno" required>
            </div>
        </div>

        <div class="row mb-4">
            <div class="col-md-4">
                <label class="form-label">Division Academica <span class="text-danger">*</span> :</label>
                <select class="form-select" name="division" required>
                    <option value="" disabled selected></option>
                    <option value="DACEA">DACEA</option>
                    <option value="DAMI">DAMI</option>
                    <option value="DATID">DATID</option>
                    <option value="DATEFI">DATEFI</option>
                </select>
            </div>
        </div>

        <div class="row mb-4">
            <div class="col-md-4">
                <label class="form-label">Correo Institucional<span class="text-danger">*</span> :</label>
                <input type="email" class="form-control" name="correo" required>
            </div>
            <div class="col-md-4">
                <label class="form-label">Contraseña <span class="text-danger">*</span> :</label>
                <div class="password-container">
                    <input type="password" class="form-control" name="contrasena" id="pass1" required>
                    <i class="bi bi-eye-fill" onclick="togglePassword('pass1')"></i>
                </div>
            </div>
            <div class="col-md-4">
                <label class="form-label">Confirmar Contraseña<span class="text-danger">*</span> :</label>
                <div class="password-container">
                    <input type="password" class="form-control" name="confirmar_contrasena" id="pass2" required>
                    <i class="bi bi-eye-fill" onclick="togglePassword('pass2')"></i>
                </div>
            </div>
        </div>

        <div class="d-flex gap-4 mb-5 custom-checkbox">
            <div class="form-check">
                <input class="form-check-input" type="checkbox" name="rol" value="docente" id="checkDocente" checked>
                <label class="form-check-label fs-6" for="checkDocente">
                    Docente
                </label>
            </div>
            <div class="form-check">
                <input class="form-check-input" type="checkbox" name="rol" value="coordinador" id="checkCoordinador">
                <label class="form-check-label fs-6" for="checkCoordinador">
                    Coordinador
                </label>
            </div>
        </div>

        <div class="d-flex gap-3">
            <button type="submit" class="btn-teal">Agregar</button>
            <a href="gestion_docente.jsp" class="btn-teal" style="background-color: var(--teal-main);">Volver</a>
        </div>
    </form>
</main>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
<script src="assets/js/coordinador.js"></script>
<script>
    function togglePassword(inputId) {
        const input = document.getElementById(inputId);
        const icon = input.nextElementSibling;
        if (input.type === "password") {
            input.type = "text";
            icon.classList.remove("bi-eye-fill");
            icon.classList.add("bi-eye-slash-fill");
        } else {
            input.type = "password";
            icon.classList.remove("bi-eye-slash-fill");
            icon.classList.add("bi-eye-fill");
        }
    }
</script>
</body>
</html>
