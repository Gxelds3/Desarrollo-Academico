<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="mx.edu.utez.DesarrolloAcademico.model.dao.UsuarioListaDao" %>
<%@ page import="mx.edu.utez.DesarrolloAcademico.model.dao.UsuarioDao" %>
<%@ page import="mx.edu.utez.DesarrolloAcademico.model.Evento" %>
<%@ page import="java.util.List" %>
<%
    UsuarioListaDao eventoDao = new UsuarioListaDao();
    UsuarioDao usuarioDao = new UsuarioDao();

    List<Evento> listaEventos = usuarioDao.obtenerProximosEventos();
    int totalEventos1 = eventoDao.contarEventos();
%>
<!doctype html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Mis Eventos</title>
    <link rel="stylesheet" href="assets/css/bootstrap.css">
    <link rel="stylesheet" href="assets/css/bi/bootstrap-icons.css">
    <link rel="stylesheet" href="assets/css/coordinador.css">
</head>
<body>

<jsp:include page="sidebar.jsp">
    <jsp:param name="active" value="mi_evento" />
</jsp:include>

<main class="main-content">
    <h3 class="page-title mb-4">MIS EVENTOS</h3>

    <div class="d-flex justify-content-between align-items-center mb-4">
        <div class="search-box mb-0" style="max-width: 600px; flex-grow: 1;">
            <i class="bi bi-search"></i>
            <input type="text" id="buscarMisEventos" placeholder="Buscar Evento por nombre ...">
        </div>
    </div>

    <div class="data-card p-0 mb-4" style="overflow: hidden;">
        <table class="table-custom mb-0">
            <colgroup>
                <col style="width: 35%;">
                <col style="width: 15%;">
                <col style="width: 22%;">
                <col style="width: 18%;">
                <col style="width: 10%;">
            </colgroup>
            <thead>
            <tr>
                <th>Título</th>
                <th>Tipo</th>
                <th>Institución</th>
                <th>Fecha</th>
                <th>Acciones</th>
            </tr>
            </thead>
            <tbody id="tablaMisEventosBody">
            <%
                if (listaEventos != null && !listaEventos.isEmpty()) {
                    for (Evento ev : listaEventos) {
            %>
            <tr>
                <td class="fw-bold"><%= ev.getNombre() != null ? ev.getNombre() : "Sin nombre" %></td>
                <!-- Corregido: Ahora evalúa getTipo_Evento() y no getID() -->
                <td><%= ev.getTipo_Evento() != null ? ev.getTipo_Evento() : "N/A" %></td>
                <td><%= ev.getInstitucion() != null ? ev.getInstitucion() : "N/A" %></td>
                <td><%= ev.getFecha_Inicio() != null ? ev.getFecha_Inicio() : "" %> - <%= ev.getFecha_Fin() != null ? ev.getFecha_Fin() : "" %></td>
                <td>
                    <a href="ver_mas_evento_co.jsp?id=<%= ev.getID() %>" class="btn-teal btn-sm">
                        Ver detalles
                    </a>
                </td>
            </tr>
            <%
                }
            } else {
            %>
            <tr>
                <td colspan="5" class="text-center text-muted py-4">
                    <i class="bi bi-calendar-x fs-3 d-block mb-2"></i>
                    No hay próximos eventos registrados.
                </td>
            </tr>
            <%
                }
            %>
            </tbody>
        </table>
    </div>

</main>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
<script>
    window.contextPath = '<%= request.getContextPath() %>';
    window.urlVerEvento = 'ver_mas_evento_co.jsp';
    window.urlCargarArchivo = 'cargar_archivo_co.jsp';
</script>
<script src="assets/js/coordinador.js"></script>
</body>
</html>