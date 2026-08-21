<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!doctype html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Gestión de Periodos de Carga</title>
    <link rel="stylesheet" href="assets/css/bootstrap.css">
    <link rel="stylesheet" href="assets/css/bi/bootstrap-icons.css">
    <link rel="stylesheet" href="assets/css/coordinador.css">
    <script src="https://cdn.jsdelivr.net/npm/sweetalert2@11"></script>
</head>
<body>

<jsp:include page="sidebar_de.jsp">
    <jsp:param name="active" value="periodos_carga" />
</jsp:include>

<main class="main-content">
    <h3 class="page-title mb-4">GESTION DE PERIODOS DE CARGA</h3>

    <div class="d-flex justify-content-between align-items-center mb-4 flex-wrap gap-3">
        <!-- Buscador en tiempo real -->
        <div class="search-box mb-0" style="max-width: 600px; flex-grow: 1;">
            <i class="bi bi-search"></i>
            <input type="text" id="inputBuscar" placeholder="Buscar periodo por división...">
        </div>


    </div>

    <div class="data-card p-0 mb-4" style="overflow: hidden;">
        <table class="table-custom mb-0 text-center">
            <colgroup>
                <col style="width: 25%;">
                <col style="width: 25%;">
                <col style="width: 25%;">
                <col style="width: 10%;">
                <col style="width: 15%;">
            </colgroup>
            <thead>
            <tr>
                <th class="text-start">Division</th>
                <th>Fecha inicio</th>
                <th>fecha fin</th>
                <th>Estado</th>
                <th>Acciones</th>
            </tr>
            </thead>
            <tbody id="tablaPeriodosBody">

            </tbody>
        </table>
    </div>
</main>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
<script src="assets/js/coordinador.js" charset="UTF-8"></script>

<script src="assets/js/GestionPeriodoCarga.js" charset="UTF-8"> </script>
</body>
</html>