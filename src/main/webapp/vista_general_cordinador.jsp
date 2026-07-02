<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!doctype html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Vista General – Coordinador</title>
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

    <a href="vista_general_cordinador.jsp" class="sidebar-item active">
        <div class="sidebar-icon">
            <i class="bi bi-calendar-event"></i>
        </div>
        <span class="sidebar-text">Eventos Proximo</span>
    </a>

    <a href="gestion_eventos_cordinador.jsp" class="sidebar-item">
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
    

    <div class="search-box mb-5">
        <i class="bi bi-search"></i>
        <input type="text" id="searchInput" placeholder="Buscar evento...">
    </div>

    <div class="row mb-5 gx-4">
        <div class="col-md-6 col-lg-5 mb-3">
            <div class="stat-card">
                <div class="stat-icon">
                    <i class="bi bi-calendar-check"></i>
                </div>
                <div>
                    <div class="text-muted small">Eventos registrados</div>
                    <div class="fs-4 fw-bold lh-1 mt-1">16</div>
                    <div class="text-muted" style="font-size: 0.75rem;">Total eventos</div>
                </div>
            </div>
        </div>
        <div class="col-md-6 col-lg-5 mb-3">
            <div class="stat-card">
                <div class="stat-icon">
                    <i class="bi bi-people"></i>
                </div>
                <div class="flex-grow-1">
                    <div class="text-muted small">Docentes registrados</div>
                    <div class="fs-4 fw-bold lh-1 mt-1">50</div>
                    <div class="text-muted" style="font-size: 0.75rem;">Total docentes</div>
                </div>
                <a href="#" class="btn-teal">Ver detalles</a>
            </div>
        </div>
    </div>

    <div class="d-flex justify-content-between align-items-center mb-3">
        <h5 class="fw-bold mb-0 text-uppercase" style="letter-spacing: 0.05em; color: #333;">PRÓXIMOS EVENTOS</h5>
        <a href="#" class="btn-teal">Ver todos los eventos</a>
    </div>

    <div id="eventsList">
        <div class="event-card">
            <div>
                <div class="fw-bold fs-5 mb-1">Taller de innovacion</div>
                <div class="text-muted small">Junio 5 - agosto 20</div>
            </div>
            <a href="#" class="btn-teal">Ver detalles</a>
        </div>
        <div class="event-card">
            <div>
                <div class="fw-bold fs-5 mb-1">Taller de innovacion</div>
                <div class="text-muted small">Junio 5 - agosto 20</div>
            </div>
            <a href="#" class="btn-teal">Ver detalles</a>
        </div>
    </div>

</main>


<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>

<script src="assets/js/coordinador.js"></script>
<script>
    document.getElementById('searchInput').addEventListener('input', function () {
        const query = this.value.toLowerCase();
        document.querySelectorAll('#eventsList .event-card').forEach(function (card) {
            const title = card.querySelector('.fw-bold').textContent.toLowerCase();
            card.style.display = title.includes(query) ? 'flex' : 'none';
        });
    });
</script>
</body>
</html>
