<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!doctype html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Cargar Archivo</title>
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

<jsp:include page="sidebar.jsp">
    <jsp:param name="active" value="gestion_evento" />
</jsp:include>

<main class="main-content">
    <div class="d-flex justify-content-between align-items-center mb-4">
        <h3 class="page-title mb-0">INTRODUCCION A REDES</h3>
        <div class="text-dark" style="font-size: 0.95rem;">Fecha de Entrega: 31 Jul 23:59</div>
    </div>

    <div class="row mb-3">
        <div class="col-md-4">
            <label class="form-label text-muted">Nombre del evento</label>
            <input type="text" class="form-control" value="Introducion a redes" readonly>
        </div>
        <div class="col-md-4">
            <label class="form-label text-muted">Lugar</label>
            <input type="text" class="form-control" value="CDMX" readonly>
        </div>
        <div class="col-md-4">
            <label class="form-label text-muted">Institución / Empresa</label>
            <input type="text" class="form-control" value="Academia de formacion profesional del estado" readonly>
        </div>
    </div>

    <div class="row mb-3">
        <div class="col-md-4">
            <label class="form-label text-muted">Tipo de evento</label>
            <input type="text" class="form-control" value="Diplomado" readonly>
        </div>
        <div class="col-md-8">
            <label class="form-label text-muted">Descripción del evento</label>
            <input type="text" class="form-control" value="Academia de formacion profesional del estado" readonly>
        </div>
    </div>

    <div class="row mb-5 align-items-center">
        <div class="col-md-3">
            <label class="form-label text-muted">Fecha de inicio</label>
            <input type="text" class="form-control" value="05/05/26" readonly>
        </div>
        <div class="col-md-3">
            <label class="form-label text-muted">Fecha de fin</label>
            <input type="text" class="form-control" value="22/05/26" readonly>
        </div>
        <div class="col-md-6 ps-md-4 mt-3 mt-md-0">
            <div class="modalidad-label">MODALIDAD</div>
            <div class="fs-6 text-dark mt-2">Presencial</div>
        </div>
    </div>

    <h5 class="fw-bold mb-3" style="color: var(--teal-main);">Docentes Asignados</h5>
    
    <div class="data-card p-0 mb-4" style="overflow: hidden;">
        <table class="table-custom mb-0">
            <thead>
                <tr>
                    <th>Nombre</th>
                    <th>Correo</th>
                    <th>Estado</th>
                    <th>Division</th>
                </tr>
            </thead>
            <tbody>
                <tr>
                    <td>
                        <div class="docente-name-container">
                            <div class="avatar-circle"></div>
                            <div class="docente-name">
                                Luis Gerardo<br>Barron Flores
                            </div>
                        </div>
                    </td>
                    <td>ejemplo@gmail.com</td>
                    <td class="status-active">Activo</td>
                    <td>DATID</td>
                </tr>
                <tr>
                    <td>
                        <div class="docente-name-container">
                            <div class="avatar-circle"></div>
                            <div class="docente-name">
                                Luis Gerardo<br>Barron Flores
                            </div>
                        </div>
                    </td>
                    <td>ejemplo@gmail.com</td>
                    <td class="status-active">Activo</td>
                    <td>DATID</td>
                </tr>
                <tr>
                    <td>
                        <div class="docente-name-container">
                            <div class="avatar-circle"></div>
                            <div class="docente-name">
                                Luis Gerardo<br>Barron Flores
                            </div>
                        </div>
                    </td>
                    <td>ejemplo@gmail.com</td>
                    <td class="status-active">Activo</td>
                    <td>DATID</td>
                </tr>
            </tbody>
        </table>
    </div>

    <div class="d-flex justify-content-end gap-3 mb-5">
        <button type="button" class="btn-teal">Cargar Archivo</button>
        <a href="gestion_evento.jsp" class="btn-teal">Volver</a>
    </div>
</main>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
<script src="assets/js/coordinador.js"></script>
</body>
</html>
