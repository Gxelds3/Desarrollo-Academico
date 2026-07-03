<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!doctype html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Gestión de Eventos – Coordinador</title>
    <link rel="stylesheet" href="assets/css/bootstrap.css">
    <link rel="stylesheet" href="assets/css/bi/bootstrap-icons.css">
    <link rel="stylesheet" href="assets/css/coordinador.css">
</head>
<body>


<nav class="sidebar-hover">

    <div class="sidebar-avatar-container">
        <div class="sidebar-avatar">
        </div>
        <div class="sidebar-info text-white">
            <div class="fw-semibold mb-1">Luis Gerardo Barron Flores</div>
            <div class="small" style="color: rgba(255,255,255,0.85);">Coordinador Academico</div>
        </div>
    </div>

    <a href="vista_general_cordinador.jsp" class="sidebar-item">
        <div class="sidebar-icon">
            <i class="bi bi-calendar-event"></i>
        </div>
        <span class="sidebar-text">Eventos Proximo</span>
    </a>

    <a href="gestion_eventos_cordinador.jsp" class="sidebar-item active">
        <div class="sidebar-icon">
            <i class="bi bi-calendar2-check"></i>
        </div>
        <span class="sidebar-text">Gestion de Eventos</span>
    </a>

    <a href="#" class="sidebar-item">
        <div class="sidebar-icon">
            <i class="bi bi-person-badge"></i>
        </div>
        <span class="sidebar-text">Gestion de Docentes</span>
    </a>

    <a href="#" class="sidebar-item">
        <div class="sidebar-icon">
            <i class="bi bi-person"></i>
        </div>
        <span class="sidebar-text">Mi Cuenta</span>
    </a>

</nav>

<main class="main-content">

    <h3 class="page-title">GESTION DE EVENTOS</h3>

    <div class="d-flex gap-2 mb-4 flex-wrap">
        <a href="#" class="nav-pill active">Todo</a>
        <a href="#" class="nav-pill">Diplomado</a>
        <a href="#" class="nav-pill">Conferencia</a>
        <a href="#" class="nav-pill">Ejemplo</a>
        <a href="#" class="nav-pill">Ejemplo</a>
    </div>

    <div class="data-card">

        <div class="search-box">
            <i class="bi bi-search"></i>
            <input type="text" id="searchInput" placeholder="Buscar Evento por nombre...">
        </div>

        <div class="table-responsive">
            <table class="table-custom" id="eventsTable">
                <thead>
                <tr>
                    <th>Titulo</th>
                    <th>Tipo</th>
                    <th>Institución</th>
                    <th>Fecha</th>
                    <th>Acciones</th>
                </tr>
                </thead>
                <tbody>
                <tr>
                    <td>
                        <div>Luis Gerardo</div>
                        <div>Barron Flores</div>
                    </td>
                    <td>
                        <div>ejemplo@gmail.</div>
                        <div>com</div>
                    </td>
                    <td>
                        <div>ejemplo@gmail.</div>
                        <div>com</div>
                    </td>
                    <td class="text-success fw-semibold">Activo</td>
                    <td>
                        <a href="#" class="action-btn delete" title="Eliminar"><i class="bi bi-trash"></i></a>
                        <a href="#" class="action-btn" title="Editar"><i class="bi bi-pencil"></i></a>
                        <a href="#" class="action-btn" title="Ver"><i class="bi bi-eye"></i></a>
                    </td>
                </tr>
                <tr>
                    <td>
                        <div>Luis Gerardo</div>
                        <div>Barron Flores</div>
                    </td>
                    <td>
                        <div>ejemplo@gmail.</div>
                        <div>com</div>
                    </td>
                    <td>
                        <div>ejemplo@gmail.</div>
                        <div>com</div>
                    </td>
                    <td class="text-success fw-semibold">Activo</td>
                    <td>
                        <a href="#" class="action-btn delete" title="Eliminar"><i class="bi bi-trash"></i></a>
                        <a href="#" class="action-btn" title="Editar"><i class="bi bi-pencil"></i></a>
                        <a href="#" class="action-btn" title="Ver"><i class="bi bi-eye"></i></a>
                    </td>
                </tr>
                <tr>
                    <td>
                        <div>Luis Gerardo</div>
                        <div>Barron Flores</div>
                    </td>
                    <td>
                        <div>ejemplo@gmail.</div>
                        <div>com</div>
                    </td>
                    <td>
                        <div>ejemplo@gmail.</div>
                        <div>com</div>
                    </td>
                    <td class="text-success fw-semibold">Activo</td>
                    <td>
                        <a href="#" class="action-btn delete" title="Eliminar"><i class="bi bi-trash"></i></a>
                        <a href="#" class="action-btn" title="Editar"><i class="bi bi-pencil"></i></a>
                        <a href="#" class="action-btn" title="Ver"><i class="bi bi-eye"></i></a>
                    </td>
                </tr>
                </tbody>
            </table>
        </div>

        <div class="d-flex justify-content-between align-items-center mt-2 flex-wrap gap-3">
            <div style="width: 150px;"></div>

            <div class="pagination-container mt-0 mx-auto">
                <a href="#" class="page-btn"><i class="bi bi-chevron-left"></i></a>
                <a href="#" class="page-btn active">1</a>
                <a href="#" class="page-btn">2</a>
                <a href="#" class="page-btn">3</a>
                <span class="page-btn dots">...</span>
                <a href="#" class="page-btn">10</a>
                <a href="#" class="page-btn"><i class="bi bi-chevron-right"></i></a>
            </div>

            <a href="#" class="btn-teal">
                <i class="bi bi-calendar-plus"></i> Agregar Evento
            </a>
        </div>

    </div>

</main>


<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>

<script src="assets/js/coordinador.js"></script>
</body>
</html>