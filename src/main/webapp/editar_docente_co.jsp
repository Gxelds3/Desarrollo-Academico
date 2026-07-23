<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!doctype html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Editar Docente</title>
    <link rel="stylesheet" href="assets/css/bootstrap.css">
    <link rel="stylesheet" href="assets/css/bi/bootstrap-icons.css">
    <link rel="stylesheet" href="assets/css/coordinador.css">
</head>
<body>

<jsp:include page="sidebar.jsp">
    <jsp:param name="active" value="gestion_docente" />
</jsp:include>

<main class="main-content">
    <h3 class="page-title">EDITAR DOCENTE/COORDINADOR</h3>
    
    <div class="d-flex align-items-center mb-4 mt-4" style="color: var(--teal-main);">
        <i class="bi bi-info-circle me-2 fs-5"></i>
        <h5 class="mb-0 fw-bold">EDITAR DOCENTE/COORDINADOR</h5>
    </div>

    <form action="#" method="POST">
        <div class="row mb-4">
            <div class="col-md-4">
                <label class="form-label">Nombre del Docente <span class="text-danger">*</span> :</label>
                <input type="text" class="form-control" name="nombre" value="Mariano" required>
            </div>
            <div class="col-md-4">
                <label class="form-label">Apellido Paterno <span class="text-danger">*</span> :</label>
                <input type="text" class="form-control" name="apellido_paterno" value="Fernández" required>
            </div>
            <div class="col-md-4">
                <label class="form-label">Apellido Materno <span class="text-danger">*</span> :</label>
                <input type="text" class="form-control" name="apellido_materno" value="Gutierrez" required>
            </div>
        </div>

        <div class="row mb-4">
            <div class="col-md-4">
                <label class="form-label">Division Academica <span class="text-danger">*</span> :</label>
                <select class="form-select" name="division" required>
                    <option value="" disabled selected></option>
                    <option value="DACEA">DACEA</option>
                    <option value="DAMI">DAMI</option>
                    <option value="DATID" selected>DATID</option>
                    <option value="DATEFI">DATEFI</option>
                </select>
            </div>
            <div class="col-md-4">
                <label class="form-label">Numero de Empleado <span class="text-danger">*</span> :</label>
                <input type="text" class="form-control" name="numero_empleado" value="67" required>
            </div>
            <div class="col-md-4">
                <label class="form-label">Numero de Telefono <span class="text-danger">*</span> :</label>
                <input type="text" class="form-control" name="telefono" value="7771234567" required>
            </div>
        </div>

        <div class="row mb-5">
            <div class="col-md-4">
                <label class="form-label">Correo Institucional<span class="text-danger">*</span> :</label>
                <input type="email" class="form-control" name="correo" value="20206ds045@utez.edu.mx" required>
            </div>
        </div>

        <div class="bg-teal text-white p-2 mb-4 fs-5" style="background-color: var(--teal-main);">
            Cambio de contraseña
        </div>

        <div class="row mb-5">
            <div class="col-md-4">
                <label class="form-label text-muted">Contraseña Actual:</label>
                <input type="password" class="form-control bg-white">
            </div>
            <div class="col-md-4">
                <label class="form-label text-muted">Nueva Contraseña:</label>
                <input type="password" class="form-control bg-white">
            </div>
            <div class="col-md-4">
                <label class="form-label text-muted">Confirmar Contraseña:</label>
                <input type="password" class="form-control bg-white">
            </div>
        </div>

        <div class="d-flex justify-content-end gap-3">
            <a href="gestion_docente_co.jsp" class="btn btn-outline-teal px-4 py-2 fw-semibold d-flex align-items-center" style="border: 2px solid var(--teal-main); color: var(--teal-main); border-radius: 6px;">
                <i class="bi bi-chevron-left me-2"></i> Volver
            </a>
            <button type="submit" class="btn-teal px-5 py-2" style="border-radius: 6px;">Editar</button>
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
