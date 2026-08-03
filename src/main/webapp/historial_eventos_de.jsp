<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!doctype html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Historial de Eventos</title>
    <link rel="stylesheet" href="assets/css/bootstrap.css">
    <link rel="stylesheet" href="assets/css/bi/bootstrap-icons.css">
    <link rel="stylesheet" href="assets/css/coordinador.css">
    <style>

        .page-btn.disabled {
            opacity: 0.5;
            pointer-events: none;
            cursor: default;
        }
    </style>
</head>
<body>

<jsp:include page="sidebar_de.jsp">
    <jsp:param name="active" value="eventos" />
</jsp:include>

<main class="main-content">
    <h3 class="page-title mb-4">HISTORIAL DE EVENTOS</h3>

    <div class="d-flex flex-wrap gap-2 mb-4" id="filtrosTipo">
        <a href="#" class="nav-pill active" data-tipo="todos">Todos</a>
        <a href="#" class="nav-pill" data-tipo="diplomado">Diplomado</a>
        <a href="#" class="nav-pill" data-tipo="conferencia">Conferencia</a>
        <a href="#" class="nav-pill" data-tipo="taller">Taller</a>
        <a href="#" class="nav-pill" data-tipo="curso">Curso</a>
        <a href="#" class="nav-pill" data-tipo="certificacion">Certificacion</a>
    </div>

    <div class="d-flex justify-content-between align-items-center mb-4">
        <div class="search-box mb-0" style="max-width: 600px; flex-grow: 1;">
            <i class="bi bi-search"></i>
            <input type="text" id="buscarEvento" placeholder="Buscar Evento por nombre ...">
        </div>
    </div>

    <div class="data-card p-0 mb-4" style="overflow: hidden;">
        <table class="table-custom mb-0 text-center">
            <colgroup>
                <col style="width: 30%;">
                <col style="width: 15%;">
                <col style="width: 25%;">
                <col style="width: 20%;">
                <col style="width: 10%;">
            </colgroup>
            <thead>
            <tr>
                <th class="text-start">Titulo</th>
                <th>Tipo</th>
                <th>Institución</th>
                <th>Fecha</th>
                <th>Acciones</th>
            </tr>
            </thead>
            <tbody id="tablaEventosBody">
            <tr>
                <td colspan="5" class="text-center text-muted py-4">Cargando eventos...</td>
            </tr>
            </tbody>
        </table>
    </div>

    <!-- Contenedor dinámico de Paginación -->
    <div class="pagination-container" id="paginacionContainer"></div>

</main>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
<script>
    window.contextPath = '${pageContext.request.contextPath}';
    // Obtiene el idUsuario o idPersona según cómo se llame en tu objeto Usuario de sesión
    window.idPersona = ${not empty sessionScope.usuario.idUsuario ? sessionScope.usuario.idUsuario : (not empty sessionScope.usuario.idPersona ? sessionScope.usuario.idPersona : 0)};
</script>
<script src="assets/js/coordinador.js"></script>
<script src="assets/js/historial_evento.js"></script>
</body>
</html>