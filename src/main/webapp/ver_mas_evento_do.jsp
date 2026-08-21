<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!doctype html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Ver Más Evento – Docente</title>
    <link rel="stylesheet" href="assets/css/bootstrap.css">
    <link rel="stylesheet" href="assets/css/bi/bootstrap-icons.css">
    <link rel="stylesheet" href="assets/css/coordinador.css">
    <style>
        .form-control[readonly] {
            background-color: #f8f9fa;
            opacity: 1;
        }
    </style>
</head>
<body>

<jsp:include page="sidebar_do.jsp">
    <jsp:param name="active" value="mis_eventos" />
</jsp:include>

<main class="main-content">
    <h3 class="page-title mb-4" id="tituloEvento">EVENTO</h3>

    <div class="row mb-3">
        <div class="col-md-4">
            <label class="form-label text-muted">Nombre del evento:</label>
            <input type="text" class="form-control" id="campoNombre" value="" readonly>
        </div>
        <div class="col-md-4">
            <label class="form-label text-muted">Lugar:</label>
            <input type="text" class="form-control" id="campoLugar" value="" readonly>
        </div>
        <div class="col-md-4">
            <label class="form-label text-muted">Institución / Empresa:</label>
            <input type="text" class="form-control" id="campoInstitucion" value="" readonly>
        </div>
    </div>

    <div class="row mb-3">
        <div class="col-md-4">
            <label class="form-label text-muted">Tipo de evento:</label>
            <input type="text" class="form-control" id="campoTipo" value="" readonly>
        </div>
        <div class="col-md-8">
            <label class="form-label text-muted">Descripción del evento:</label>
            <input type="text" class="form-control" id="campoDescripcion" value="" readonly>
        </div>
    </div>

    <div class="row mb-5">
        <div class="col-md-3">
            <label class="form-label text-muted">Fecha de inicio:</label>
            <input type="text" class="form-control" id="campoFechaInicio" value="" readonly>
        </div>
        <div class="col-md-3">
            <label class="form-label text-muted">Fecha de fin:</label>
            <input type="text" class="form-control" id="campoFechaFin" value="" readonly>
        </div>
        <div class="col-md-2 mt-3 mt-md-0 d-flex flex-column justify-content-center">
            <div class="modalidad-label mb-1">Modalidad</div>
            <div class="fs-6 text-dark" id="campoModalidad">-</div>
        </div>
        <div class="col-md-4 mt-3 mt-md-0">
            <div class="info-card-outline h-100 d-flex flex-column justify-content-center align-items-center text-center">
                <div class="d-flex align-items-center mb-1">
                    <div class="info-card-icon"><i class="bi bi-clock"></i></div>
                    <span class="fw-bold text-teal" style="color: var(--teal-main);">Fecha limite de entrega</span>
                </div>
                <div class="text-dark fw-semibold" id="fechaLimiteTexto">-</div>
                <div class="badge-light-green" id="diasRestantesBadge">Calculando...</div>
            </div>
        </div>
    </div>

    <div class="d-flex justify-content-end">
        <a href="mi_evento_do.jsp" class="btn-teal">Volver</a>
    </div>
</main>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
<script src="https://cdn.jsdelivr.net/npm/sweetalert2@11"></script>
<script src="assets/js/coordinador.js" charset="UTF-8"></script>
<script>window.contextPath = '<%= request.getContextPath() %>';</script>
<script src="assets/js/VerMasEventoDo.js" charset="UTF-8"></script>
</body>
</html>
