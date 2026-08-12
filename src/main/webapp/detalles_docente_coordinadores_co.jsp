<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="mx.edu.utez.DesarrolloAcademico.model.Usuario" %>
<%
    Usuario doc = (Usuario) request.getAttribute("dev");

    if (doc == null) {
        response.sendRedirect("gestion_docente_co.jsp");
        return;
    }

    String nombre = doc.getNombre() != null ? doc.getNombre() : "";
    String apePat = doc.getApellidoPaterno() != null ? doc.getApellidoPaterno() : "";
    String apeMat = doc.getApellidoMaterno() != null ? doc.getApellidoMaterno() : "";
    String numEmp = doc.getNumeroEmpleado() != null ? doc.getNumeroEmpleado() : "";
    String tel = doc.getTelefono() != null ? doc.getTelefono() : "";
    String correo = doc.getCorreoInstitucional() != null ? doc.getCorreoInstitucional() : "";
    String rol = doc.getRol() != null ? doc.getRol().toLowerCase() : "";

    int idDivision = 0;
    if (doc.getIdDivision() != null) {
        idDivision = doc.getIdDivision();
    }
%>
<!doctype html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Detalles del Usuario</title>
    <link rel="stylesheet" href="assets/css/bootstrap.css">
    <link rel="stylesheet" href="assets/css/bi/bootstrap-icons.css">
    <link rel="stylesheet" href="assets/css/coordinador.css">
    <style>
        .form-control[disabled], .form-select[disabled] {
            background-color: #f8f9fa;
            opacity: 1;
        }
        .role-badge {
            display: inline-flex;
            align-items: center;
            gap: 8px;
            padding: 8px 16px;
            border-radius: 50px;
            font-weight: 600;
            background-color: #e0f2f1;
            color: var(--teal-main);
            border: 1px solid var(--teal-main);
        }
    </style>
</head>
<body>

<jsp:include page="sidebar_co.jsp">
    <jsp:param name="active" value="gestion_docentes" />
</jsp:include>

<main class="main-content">
    <h3 class="page-title">DATOS DOCENTE/COORDINADOR</h3>

    <div class="d-flex align-items-center mb-4 mt-4" style="color: var(--teal-main);">
        <i class="bi bi-info-circle me-2 fs-5"></i>
        <h5 class="mb-0 fw-bold">INFORMACIÓN DEL USUARIO</h5>
    </div>
    
    <div class="mb-4">
        <% if (rol.equals("desarrollo") || rol.equals("desarrollador")) { %>
            <div class="role-badge"><i class="bi bi-laptop fs-5"></i> Desarrollador</div>
        <% } else if (rol.equals("coordinador")) { %>
            <div class="role-badge"><i class="bi bi-person-badge fs-5"></i> Coordinador</div>
        <% } else { %>
            <div class="role-badge"><i class="bi bi-person-workspace fs-5"></i> Docente</div>
        <% } %>
    </div>

    <form autocomplete="off">
        <div class="row mb-4">
            <div class="col-md-4">
                <label class="form-label fw-semibold">Nombre del Docente :</label>
                <input type="text" class="form-control" value="<%= nombre %>" disabled>
            </div>
            <div class="col-md-4">
                <label class="form-label fw-semibold">Apellido Paterno :</label>
                <input type="text" class="form-control" value="<%= apePat %>" disabled>
            </div>
            <div class="col-md-4">
                <label class="form-label fw-semibold">Apellido Materno :</label>
                <input type="text" class="form-control" value="<%= apeMat %>" disabled>
            </div>
        </div>

        <div class="row mb-4">
            <div class="col-md-4">
                <label class="form-label fw-semibold">División Académica :</label>
                <select class="form-select" disabled>
                    <option value="" <%= (idDivision == 0) ? "selected" : "" %>>N/A</option>
                    <option value="1" <%= (idDivision == 1) ? "selected" : "" %>>Datid</option>
                    <option value="2" <%= (idDivision == 2) ? "selected" : "" %>>Dacea</option>
                    <option value="3" <%= (idDivision == 3) ? "selected" : "" %>>Datefi</option>
                    <option value="4" <%= (idDivision == 4) ? "selected" : "" %>>Dami</option>
                    <option value="5" <%= (idDivision == 5) ? "selected" : "" %>>General</option>
                </select>
            </div>
            <div class="col-md-4">
                <label class="form-label fw-semibold">Número de Empleado :</label>
                <input type="text" class="form-control" value="<%= numEmp %>" disabled>
            </div>
            <div class="col-md-4">
                <label class="form-label fw-semibold">Número de Teléfono :</label>
                <input type="tel" class="form-control" value="<%= tel %>" disabled>
            </div>
        </div>

        <div class="row mb-5">
            <div class="col-md-4">
                <label class="form-label fw-semibold">Correo Institucional :</label>
                <input type="email" class="form-control" value="<%= correo %>" disabled>
            </div>
        </div>

        <div class="d-flex justify-content-end gap-3 mb-5">
            <button type="button" class="btn btn-outline-teal px-4 py-2 fw-semibold d-flex align-items-center" style="border: 2px solid var(--teal-main); color: var(--teal-main); border-radius: 6px;" onclick="window.history.back();">
                <i class="bi bi-chevron-left me-2"></i> Volver
            </button>
        </div>
    </form>
</main>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>
