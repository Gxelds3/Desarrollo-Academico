<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!doctype html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Ver Más Evento</title>
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

<jsp:include page="sidebar_de.jsp">
    <jsp:param name="active" value="gestion_eventos_de" />
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
            <textarea  class="form-control" rows="3" id="campoDescripcion" value="" readonly></textarea>
        </div>
    </div>

    <div class="row mb-4">
        <div class="col-md-2">
            <label class="form-label text-muted">Fecha de inicio:</label>
            <input type="text" class="form-control" id="campoFechaInicio" value="" readonly>
        </div>
        <div class="col-md-2">
            <label class="form-label text-muted">Fecha de fin:</label>
            <input type="text" class="form-control" id="campoFechaFin" value="" readonly>
        </div>
        <div class="col-md-2 mt-3 mt-md-0 d-flex flex-column justify-content-center">
            <div class="modalidad-label mb-1">Modalidad</div>
            <div class="fs-6 text-dark" id="campoModalidad">-</div>
        </div>
        <div class="col-md-3 mt-3 mt-md-0">
            <div class="info-card-outline h-100 d-flex flex-column justify-content-center align-items-center text-center">
                <div class="d-flex align-items-center mb-1">
                    <div class="info-card-icon"><i class="bi bi-clock"></i></div>
                    <span class="fw-bold text-teal" style="color: var(--teal-main);">Fecha limite de entrega</span>
                </div>
                <div class="text-dark fw-semibold" id="fechaLimiteTexto">-</div>
                <div class="badge-light-green" id="diasRestantesBadge">Calculando...</div>
            </div>
        </div>
        <div class="col-md-3 mt-3 mt-md-0">
            <div class="info-card-outline h-100 d-flex flex-column justify-content-center align-items-center text-center">
                <div class="d-flex align-items-center mb-1">
                    <div class="info-card-icon"><i class="bi bi-people"></i></div>
                    <span class="fw-bold text-teal" style="color: var(--teal-main);">Documentos entregados</span>
                </div>
                <div class="fs-5 fw-bold text-dark mt-1" id="documentosEntregadosTexto">0 de 0</div>
                <div class="d-flex align-items-center w-100 justify-content-center gap-2">
                    <div class="progress-bar-custom">
                        <div class="progress-bar-fill" id="barraProgresoFill" style="width: 0%;"></div>
                    </div>
                    <span class="fw-bold text-teal" id="porcentajeTexto" style="font-size: 0.85rem; color: var(--teal-main);">0%</span>
                </div>
            </div>
        </div>
    </div>

    <h5 class="fw-bold mb-3" style="color: var(--teal-main);">Docentes Asignados</h5>

    <div class="data-card p-0 mb-4" style="overflow: hidden;">
        <table class="table-custom mb-0 text-center">
            <thead>
            <tr>
                <th class="text-start">Nombre</th>
                <th>Correo</th>
                <th>Estado</th>
                <th>Entregado</th>
            </tr>
            </thead>
            <tbody id="tablaDocentesBody">
            <!-- Se llena con JS -->
            </tbody>
        </table>
    </div>

    <div class="d-flex justify-content-end">
        <a href="gestion_eventos_de.jsp" class="btn-teal">Volver</a>
    </div>
</main>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
<script src="https://cdn.jsdelivr.net/npm/sweetalert2@11"></script>
<script src="assets/js/coordinador.js"></script>
<script>window.contextPath = '<%= request.getContextPath() %>';</script>
<script src="assets/js/VerMasEventoDe.js"></script>
</body>
</html>
