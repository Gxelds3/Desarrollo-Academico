<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!doctype html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Gestión de Docentes</title>
    <link rel="stylesheet" href="assets/css/bootstrap.css">
    <link rel="stylesheet" href="assets/css/bi/bootstrap-icons.css">
    <link rel="stylesheet" href="assets/css/coordinador.css">
</head>
<body>

<jsp:include page="sidebar.jsp">
    <jsp:param name="active" value="gestion_docente" />
</jsp:include>

<main class="main-content">
    <h3 class="page-title mb-4">GESTION DE PERSONAL</h3>

    <div class="d-flex justify-content-between align-items-center mb-4">
        <div class="search-box mb-0" style="max-width: 600px; flex-grow: 1; margin-right: 20px;">
            <i class="bi bi-search"></i>
            <input type="text" placeholder="Buscar Docente por nombre, correo ...">
        </div>
        <a href="agregar_docente_co.jsp" class="btn-teal">
            <i class="bi bi-person-plus"></i> Agregar Docente/Desarrollador
        </a>
    </div>

    <div class="data-card p-0 mb-4" style="overflow: hidden;">
        <table class="table-custom mb-0">
            <colgroup>
                <col style="width: 28%;">
                <col style="width: 22%;">
                <col style="width: 12%;">
                <col style="width: 12%;">
                <col style="width: 12%;">
                <col style="width: 14%;">
            </colgroup>
            <thead>
                <tr>
                    <th>Nombre</th>
                    <th>Correo</th>
                    <th>Division</th>
                    <th>Num. Empleado</th>
                    <th>Estado</th>
                    <th>Acciones</th>
                </tr>
            </thead>
            <tbody>
                <tr>
                    <td class="text-start">
                        <div class="docente-name-container">
                            <div class="avatar-circle" style="flex-shrink:0;"></div>
                            <div class="docente-name" style="overflow:hidden; text-overflow:ellipsis; white-space:nowrap;">
                                Luis Gerardo Barron Flores
                            </div>
                        </div>
                    </td>
                    <td>ejemplo@gmail.com</td>
                    <td>DATID</td>
                    <td>67</td>
                    <td style="color: #28a745; font-weight: 600;">Activo</td>
                    <td style="white-space: nowrap;">
                        <a href="ver_mas_evento_co.jsp" class="action-btn" title="Ver"><i class="bi bi-eye"></i></a>
                        <a href="editar_docente_co.jsp" class="action-btn" title="Editar"><i class="bi bi-pencil"></i></a>
                        <a href="#" class="action-btn delete" title="Eliminar"><i class="bi bi-trash"></i></a>
                    </td>
                </tr>
                <tr>
                    <td class="text-start">
                        <div class="docente-name-container">
                            <div class="avatar-circle" style="flex-shrink:0;"></div>
                            <div class="docente-name" style="overflow:hidden; text-overflow:ellipsis; white-space:nowrap;">
                                Maria Fernanda Lopez Ruiz
                            </div>
                        </div>
                    </td>
                    <td>mflopez@gmail.com</td>
                    <td>DACEA</td>
                    <td>42</td>
                    <td style="color: #28a745; font-weight: 600;">Activo</td>
                    <td style="white-space: nowrap;">
                        <a href="ver_mas_evento_co.jsp" class="action-btn" title="Ver"><i class="bi bi-eye"></i></a>
                        <a href="editar_docente_co.jsp" class="action-btn" title="Editar"><i class="bi bi-pencil"></i></a>
                        <a href="#" class="action-btn delete" title="Eliminar"><i class="bi bi-trash"></i></a>
                    </td>
                </tr>
                <tr>
                    <td class="text-start">
                        <div class="docente-name-container">
                            <div class="avatar-circle" style="flex-shrink:0; background-color: #ccc;"></div>
                            <div class="docente-name" style="overflow:hidden; text-overflow:ellipsis; white-space:nowrap;">
                                Carlos Alberto Mendez Torres
                            </div>
                        </div>
                    </td>
                    <td>cmendez@gmail.com</td>
                    <td>DAMI</td>
                    <td>89</td>
                    <td style="color: #d32f2f; font-weight: 600;">Inactivo</td>
                    <td style="white-space: nowrap;">
                        <a href="ver_mas_evento_co.jsp" class="action-btn" title="Ver"><i class="bi bi-eye"></i></a>
                        <a href="editar_docente_co.jsp" class="action-btn" title="Editar"><i class="bi bi-pencil"></i></a>
                        <a href="#" class="action-btn delete" title="Eliminar"><i class="bi bi-trash"></i></a>
                    </td>
                </tr>
                <tr>
                    <td class="text-start">
                        <div class="docente-name-container">
                            <div class="avatar-circle" style="flex-shrink:0;"></div>
                            <div class="docente-name" style="overflow:hidden; text-overflow:ellipsis; white-space:nowrap;">
                                Ana Patricia Guerrero Vega
                            </div>
                        </div>
                    </td>
                    <td>aguerrero@gmail.com</td>
                    <td>DATEFI</td>
                    <td>15</td>
                    <td style="color: #28a745; font-weight: 600;">Activo</td>
                    <td style="white-space: nowrap;">
                        <a href="ver_mas_evento_co.jsp" class="action-btn" title="Ver"><i class="bi bi-eye"></i></a>
                        <a href="editar_docente_co.jsp" class="action-btn" title="Editar"><i class="bi bi-pencil"></i></a>
                        <a href="#" class="action-btn delete" title="Eliminar"><i class="bi bi-trash"></i></a>
                    </td>
                </tr>
                <tr>
                    <td class="text-start">
                        <div class="docente-name-container">
                            <div class="avatar-circle" style="flex-shrink:0; background-color: #ccc;"></div>
                            <div class="docente-name" style="overflow:hidden; text-overflow:ellipsis; white-space:nowrap;">
                                Roberto Sanchez Delgado
                            </div>
                        </div>
                    </td>
                    <td>rsanchez@gmail.com</td>
                    <td>DATID</td>
                    <td>33</td>
                    <td style="color: #d32f2f; font-weight: 600;">Inactivo</td>
                    <td style="white-space: nowrap;">
                        <a href="ver_mas_evento_co.jsp" class="action-btn" title="Ver"><i class="bi bi-eye"></i></a>
                        <a href="editar_docente_co.jsp" class="action-btn" title="Editar"><i class="bi bi-pencil"></i></a>
                        <a href="#" class="action-btn delete" title="Eliminar"><i class="bi bi-trash"></i></a>
                    </td>
                </tr>
            </tbody>
        </table>
    </div>

    <div class="pagination-container">
        <a href="#" class="page-btn"><i class="bi bi-chevron-left"></i></a>
        <a href="#" class="page-btn active">1</a>
        <a href="#" class="page-btn">2</a>
        <a href="#" class="page-btn">3</a>
        <span class="page-btn dots">...</span>
        <a href="#" class="page-btn">10</a>
        <a href="#" class="page-btn"><i class="bi bi-chevron-right"></i></a>
    </div>

</main>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
<script src="assets/js/coordinador.js"></script>
</body>
</html>
