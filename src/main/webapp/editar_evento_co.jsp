<%--
  Vista: editar_evento_co.jsp
  Rol: Coordinador
  Descripción: Formulario para editar los datos de un evento existente.
  Incluye los fragmentos: sidebar_co.jsp
  Formulario(s): envía a '#' por POST
  Scripts propios: assets/js/coordinador.js, assets/js/EditarEvento.js?v=5
--%>
﻿<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!doctype html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Editar Evento</title>
    <link rel="stylesheet" href="assets/css/bootstrap.css">
    <link rel="stylesheet" href="assets/css/bi/bootstrap-icons.css">
    <link rel="stylesheet" href="assets/css/coordinador.css">
    <script src="https://cdn.jsdelivr.net/npm/sweetalert2@11"></script>
</head>
<body>

<%-- Fragmento incluido: sidebar_co.jsp --%>
<jsp:include page="sidebar_co.jsp">
    <jsp:param name="active" value="gestion_evento" />
</jsp:include>

<main class="main-content">
    <h3 class="page-title">EDITAR EVENTO</h3>

        <!-- Formulario: envía a '#' por POST -->
    <form id="formEditarEvento" action="#" method="POST">
        <div class="row mb-3">
            <div class="col-md-4">
                <label class="form-label text-muted">Nombre del evento:</label>
                <input type="text" class="form-control" id="campoNombre" name="nombre" required oninput="this.value = this.value.replace(/[^a-zA-ZÃ¡Ã©Ã­Ã³ÃºÃÃ‰ÃÃ“ÃšÃ±Ã‘0-9\s.,-]/g, '')" maxlength="255">
            </div>
            <div class="col-md-4">
                <label class="form-label text-muted">Lugar:</label>
                <input type="text" class="form-control" id="campoLugar" name="lugar" required oninput="this.value = this.value.replace(/[^a-zA-ZÃ¡Ã©Ã­Ã³ÃºÃÃ‰ÃÃ“ÃšÃ±Ã‘0-9\s.,-]/g, '')" maxlength="255">
            </div>
            <div class="col-md-4">
                <label class="form-label text-muted">InstituciÃ³n / Empresa:</label>
                <input type="text" class="form-control" id="campoInstitucion" name="institucion" oninput="this.value = this.value.replace(/[^a-zA-ZÃ¡Ã©Ã­Ã³ÃºÃÃ‰ÃÃ“ÃšÃ±Ã‘0-9\s.,-]/g, '')" maxlength="255">
            </div>
        </div>

        <div class="row mb-3">
            <div class="col-md-4">
                <label class="form-label text-muted">Tipo de evento:</label>
                <select class="form-select" id="campoTipo" name="tipo" required>
                    <option value="" disabled>Selecciona un tipo</option>
                    <option value="Taller">Taller</option>
                    <option value="Diplomado">Diplomado</option>
                    <option value="Conferencia">Conferencia</option>
                    <option value="Curso">Curso</option>
                    <option value="Certificacion">CertificaciÃ³n</option>
                </select>
            </div>
            <div class="col-md-8">
                <label class="form-label text-muted">DescripciÃ³n del evento:</label>
                <textarea class="form-control" id="campoDescripcion" name="descripcion" rows="3" oninput="this.value = this.value.replace(/[^a-zA-ZÃ¡Ã©Ã­Ã³ÃºÃÃ‰ÃÃ“ÃšÃ±Ã‘0-9\s.,]/g, '')" style="resize: vertical;"></textarea>
            </div>
        </div>

        <div class="row mb-5 align-items-end">
            <div class="col-md-3">
                <label class="form-label text-muted fw-semibold mb-2">Fecha de inicio</label>
                <input type="date" class="form-control" id="campoFechaInicio" name="fecha_inicio" required>
            </div>
            <div class="col-md-3">
                <label class="form-label text-muted fw-semibold mb-2">Fecha de termino</label>
                <input type="date" class="form-control" id="campoFechaFin" name="fecha_fin" required>
            </div>
            <div class="col-md-6 custom-checkbox ps-md-4">
                <div class="modalidad-label">MODALIDAD</div>
                <div class="d-flex gap-4">
                    <div class="form-check">
                        <input class="form-check-input" type="radio" name="modalidad" value="presencial" id="modPresencial" checked>
                        <label class="form-check-label fs-6" for="modPresencial">Presencial</label>
                    </div>
                    <div class="form-check">
                        <input class="form-check-input" type="radio" name="modalidad" value="virtual" id="modVirtual">
                        <label class="form-check-label fs-6" for="modVirtual">Virtual</label>
                    </div>
                    <div class="form-check">
                        <input class="form-check-input" type="radio" name="modalidad" value="mixto" id="modMixta">
                        <label class="form-check-label fs-6" for="modMixta">Mixta</label>
                    </div>
                </div>
            </div>
        </div>

        <h5 class="fw-bold mb-3" style="color: var(--teal-main);">Docentes Asignados</h5>

        <div class="d-flex justify-content-between align-items-center mb-4">
            <div class="search-box mb-0" style="max-width: 500px;">
                <i class="bi bi-search"></i>
                <input type="text" id="buscarParticipante" placeholder="Buscar Docente asignado...">
            </div>
            <button type="button" class="btn-teal-outline" data-bs-toggle="modal" data-bs-target="#modalAsignarDocente">Agregar docente</button>
        </div>

        <div class="data-card p-0 mb-4" style="overflow: hidden;">
                        <!-- Tabla de datos: se llena dinámicamente vía JS/fetch al servlet correspondiente -->
            <table class="table-custom mb-0">
                <thead>
                <tr>
                    <th>Nombre</th>
                    <th>Correo</th>
                    <th>Estado</th>
                    <th>Acciones</th>
                </tr>
                </thead>
                <tbody id="tablaParticipantesBody">
                <tr>
                    <td colspan="4" class="text-center text-muted py-4">Cargando docentes asignados...</td>
                </tr>
                </tbody>
            </table>
        </div>

        <div class="d-flex justify-content-end gap-3 mb-5">
            <a href="gestion_evento_co.jsp" class="btn btn-outline-teal px-4 py-2 fw-semibold d-flex align-items-center" style="border: 2px solid var(--teal-main); color: var(--teal-main); border-radius: 6px;">
                <i class="bi bi-chevron-left me-2"></i> Volver
            </a>
            <button type="submit" class="btn-teal px-4 py-2" style="border-radius: 6px;">
                <i class="bi bi-save me-2"></i> Confirmar
            </button>
        </div>
    </form>
</main>

<!-- Modal Asignar Docente con buscador -->
<div class="modal fade" id="modalAsignarDocente" tabindex="-1" aria-labelledby="modalAsignarDocenteLabel" aria-hidden="true">
    <div class="modal-dialog modal-dialog-centered modal-lg">
        <div class="modal-content" style="border-radius: 12px; border: none; box-shadow: 0 4px 15px rgba(0,0,0,0.1);">
            <div class="modal-header" style="background-color: var(--teal-main); color: white; border-top-left-radius: 12px; border-top-right-radius: 12px;">
                <h5 class="modal-title" id="modalAsignarDocenteLabel">Asignar Docente al Evento</h5>
                <button type="button" class="btn-close btn-close-white" data-bs-dismiss="modal" aria-label="Close"></button>
            </div>
            <div class="modal-body p-4">
                <div class="mb-3">
                    <label class="form-label text-muted mb-2">Buscar por nombre, correo o nÃºmero de empleado:</label>
                    <input type="text" class="form-control" id="inputBuscarDocente" placeholder="Escribe para buscar..." autocomplete="off">
                </div>
                <div style="max-height: 300px; overflow-y: auto;">
                                        <!-- Tabla de datos: se llena dinámicamente vía JS/fetch al servlet correspondiente -->
                    <table class="table table-hover table-sm" id="tablaResultadosBusqueda">
                        <thead>
                        <tr>
                            <th>Nombre</th>
                            <th>Correo</th>
                            <th>Rol</th>
                            <th></th>
                        </tr>
                        </thead>
                        <tbody id="tbodyBusquedaDocentes">
                        <tr><td colspan="4" class="text-center text-muted py-3">Escribe para buscar docentes...</td></tr>
                        </tbody>
                    </table>
                </div>
            </div>
        </div>
    </div>
</div>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
<script>window.contextPath = '<%= request.getContextPath() %>';</script>
<script src="assets/js/coordinador.js" charset="UTF-8"></script>
<script src="assets/js/EditarEvento.js?v=5" charset="UTF-8"></script>
</body>
</html>
