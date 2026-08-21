<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!doctype html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Agregar Evento</title>
    <link rel="stylesheet" href="assets/css/bootstrap.css">
    <link rel="stylesheet" href="assets/css/bi/bootstrap-icons.css">
    <link rel="stylesheet" href="assets/css/coordinador.css">
    <script src="https://cdn.jsdelivr.net/npm/sweetalert2@11"></script>
</head>
<body>

<jsp:include page="sidebar_de.jsp">
    <jsp:param name="active" value="gestion_eventos" />
</jsp:include>

<main class="main-content">
    <h3 class="page-title">AGREGAR EVENTO</h3>

    <div class="d-flex align-items-center mb-4 mt-4" style="color: var(--teal-main);">
        <i class="bi bi-info-circle me-2 fs-5"></i>
        <h5 class="mb-0 fw-bold">DATOS DEL EVENTO</h5>
    </div>

    <form id="formAgregarEvento" method="POST">
        <div class="row mb-3">
            <div class="col-md-4">
                <label class="form-label">Nombre del evento <span class="text-danger">*</span></label>
                <input type="text" class="form-control" name="nombre" required oninput="this.value = this.value.replace(/[^a-zA-ZáéíóúÁÉÍÓÚñÑ0-9\s.,-]/g, '')" maxlength="255">
            </div>
            <div class="col-md-4">
                <label class="form-label">Lugar <span class="text-danger">*</span></label>
                <input type="text" class="form-control" name="lugar" required oninput="this.value = this.value.replace(/[^a-zA-ZáéíóúÁÉÍÓÚñÑ0-9\s.,-]/g, '')" maxlength="255">
            </div>
            <div class="col-md-4">
                <label class="form-label">Institución / Empresa</label>
                <input type="text" class="form-control" name="institucion" oninput="this.value = this.value.replace(/[^a-zA-ZáéíóúÁÉÍÓÚñÑ0-9\s.,-]/g, '')" maxlength="255">
            </div>
        </div>

        <div class="row mb-3">
            <div class="col-md-4">
                <label class="form-label">Tipo de evento <span class="text-danger">*</span></label>
                <select class="form-select" name="tipo" required>
                    <option value="" disabled selected>Selecciona una opción</option>
                    <option value="Diplomado">Diplomado</option>
                    <option value="Conferencia">Conferencia</option>
                    <option value="Taller">Taller</option>
                    <option value="Curso">Curso</option>
                    <option value="Certificacion">Certificación</option>
                </select>
            </div>
            <div class="col-md-8">
                <label class="form-label text-muted">Descripción del evento:</label>
                <textarea class="form-control" id="campoDescripcion" name="descripcion" rows="3" oninput="this.value = this.value.replace(/[^a-zA-ZáéíóúÁÉÍÓÚñÑ0-9\s.,]/g, '')" style="resize: vertical;"></textarea>
            </div>
        </div>

        <div class="row mb-5 align-items-end">
            <div class="col-md-3">
                <label class="form-label">Fecha de inicio <span class="text-danger">*</span></label>
                <input type="date" class="form-control" name="fechaInicio" required>
            </div>
            <div class="col-md-3">
                <label class="form-label">Fecha de fin <span class="text-danger">*</span></label>
                <input type="date" class="form-control" name="fechaFin" required>
            </div>
            <div class="col-md-3">
                <label class="form-label">Modalidad <span class="text-danger">*</span></label>
                <select class="form-select" name="modalidad" required>
                    <option value="" disabled selected>Selecciona una opción</option>
                    <option value="presencial">Presencial</option>
                    <option value="virtual">Virtual</option>
                    <option value="mixto">Mixta</option>
                </select>
            </div>
            <div class="col-md-3">
                <label class="form-label">División Académica <span class="text-danger">*</span></label>
                <select class="form-select" name="division" id="campoDivision" required>
                    <option value="" disabled selected>Selecciona una división</option>
                    <option value="2">DACEA</option>
                    <option value="4">DAMI</option>
                    <option value="1">DATID</option>
                    <option value="3">DATEFI</option>
                </select>
            </div>
        </div>

          <hr class="my-4">
          <h5 class="fw-bold mb-3" style="color: var(--teal-main);">Docentes Asignados</h5>

          <div class="d-flex justify-content-between align-items-center mb-4">
              <div class="search-box mb-0" style="max-width: 500px;">
                  <i class="bi bi-search"></i>
                  <input type="text" id="buscarParticipante" placeholder="Buscar Docente asignado...">
              </div>
              <button type="button" class="btn-teal-outline" data-bs-toggle="modal" data-bs-target="#modalAsignarDocente">Agregar docente</button>
          </div>

          <div class="data-card p-0 mb-4" style="overflow: hidden;">
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
                      <td colspan="4" class="text-center text-muted py-4">Aun no hay docentes asignados.</td>
                  </tr>
                  </tbody>
              </table>
          </div>
          <div id="hiddenDocentesContainer"></div>

<div class="d-flex justify-content-end gap-3">
            <a href="gestion_eventos_de.jsp" class="btn btn-outline-teal px-4 py-2 fw-semibold d-flex align-items-center" style="border: 2px solid var(--teal-main); color: var(--teal-main); border-radius: 6px;">
                <i class="bi bi-chevron-left me-2"></i> Volver
            </a>
            <button type="submit" class="btn-teal px-4 py-2" style="border-radius: 6px;" id="btnGuardar">
                <i class="bi bi-save me-2"></i> Guardar Evento
            </button>
        </div>
    </form>

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
                    <label class="form-label text-muted mb-2">Buscar por nombre, correo o numero de empleado:</label>
                    <input type="text" class="form-control" id="inputBuscarDocente" placeholder="Escribe para buscar..." autocomplete="off">
                </div>
                <div style="max-height: 300px; overflow-y: auto;">
                    <table class="table table-hover table-sm" id="tablaResultadosBusqueda">
                        <thead>
                        <tr>
                            <th>Nombre</th>
                            <th>Correo</th>
                            <th>Rol</th>
                            <th>División</th>
                            <th></th>
                        </tr>
                        </thead>
                        <tbody id="tbodyBusquedaDocentes">
                        <tr><td colspan="5" class="text-center text-muted py-3">Escribe para buscar docentes...</td></tr>
                        </tbody>
                    </table>
                </div>
            </div>
        </div>
    </div>
</div>


</main>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
<script>window.contextPath = '<%= request.getContextPath() %>';</script>
<script src="assets/js/coordinador.js" charset="UTF-8"></script>
<script src="assets/js/agregarEvento.js" charset="UTF-8">  </script>
</body>
</html>
