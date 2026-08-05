<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!doctype html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Editar Docente</title>
    <link rel="stylesheet" href="assets/css/bootstrap.css">
    <link rel="stylesheet" href="assets/css/bi/bootstrap-icons.css">
    <link rel="stylesheet" href="assets/css/coordinador.css">
    <script src="https://cdn.jsdelivr.net/npm/sweetalert2@11"></script>
</head>
<body>

<jsp:include page="sidebar_de.jsp">
    <jsp:param name="active" value="gestion_usuarios" />
    <jsp:param name="active_sub" value="docente" />
</jsp:include>

<main class="main-content">
    <h3 class="page-title">EDITAR DOCENTE</h3>

    <div class="d-flex align-items-center mb-4 mt-4" style="color: var(--teal-main);">
        <i class="bi bi-info-circle me-2 fs-5"></i>
        <h5 class="mb-0 fw-bold">DATOS DEL DOCENTE</h5>
    </div>

    <form id="formEditarDocente" autocomplete="off">
        <input type="hidden" id="campoIdUsuario" name="id_usuario">

        <div class="row mb-4">
            <div class="col-md-4">
                <label for="campoNombre" class="form-label">Nombre del Docente <span class="text-danger">*</span> :</label>
                <input type="text" class="form-control" id="campoNombre" name="nombre" required>
            </div>
            <div class="col-md-4">
                <label for="campoApellidoP" class="form-label">Apellido Paterno <span class="text-danger">*</span> :</label>
                <input type="text" class="form-control" id="campoApellidoP" name="apellido_paterno" required>
            </div>
            <div class="col-md-4">
                <label for="campoApellidoM" class="form-label">Apellido Materno :</label>
                <input type="text" class="form-control" id="campoApellidoM" name="apellido_materno">
            </div>
        </div>

        <div class="row mb-4">
            <div class="col-md-4">
                <label for="campoDivision" class="form-label">División Académica <span class="text-danger">*</span> :</label>
                <select class="form-select" id="campoDivision" name="division" required>
                    <option value="" disabled selected>Seleccione división</option>
                    <option value="1">Datid</option>
                    <option value="2">Dacea</option>
                    <option value="3">Datefi</option>
                    <option value="4">Dami</option>
                    <option value="5">General</option>
                </select>
            </div>
            <div class="col-md-4">
                <label for="campoNumEmpleado" class="form-label">Número de Empleado <span class="text-danger">*</span> :</label>
                <input type="text" class="form-control" id="campoNumEmpleado" name="numero_empleado" required>
            </div>
            <div class="col-md-4">
                <label for="campoTelefono" class="form-label">Número de Teléfono :</label>
                <input type="tel" class="form-control" id="campoTelefono" name="telefono">
            </div>
        </div>

        <div class="row mb-4">
            <div class="col-md-4">
                <label for="campoCorreo" class="form-label">Correo Institucional <span class="text-danger">*</span> :</label>
                <input type="email" class="form-control" id="campoCorreo" name="correo" required>
            </div>

            <!-- Contraseña -->
            <div class="col-md-4">
                <label for="campoContrasena" class="form-label">Contraseña <span class="text-danger">*</span> :</label>
                <div class="input-group">
                    <input type="text" class="form-control" id="campoContrasena" name="contrasena" placeholder="Contraseña" required>
                    <button class="btn btn-outline-secondary" type="button" id="btnTogglePass">
                        <i class="bi bi-eye-slash" id="iconoPass"></i>
                    </button>
                </div>
            </div>
        </div>

        <div class="d-flex justify-content-end gap-2">
            <a href="gestion_docente_co.jsp" class="btn btn-secondary">Cancelar</a>
            <button type="submit" class="btn btn-primary" style="background-color: var(--teal-main); border: none;">Guardar Cambios</button>
        </div>
    </form>
</main>

<script>
    const contextPath = '<%= request.getContextPath() %>';

    // 1. Alternar ver/ocultar contraseña
    document.getElementById('btnTogglePass').addEventListener('click', function() {
        const input = document.getElementById('campoContrasena');
        const icono = document.getElementById('iconoPass');
        if (input.type === 'text') {
            input.type = 'password';
            icono.classList.replace('bi-eye-slash', 'bi-eye');
        } else {
            input.type = 'text';
            icono.classList.replace('bi-eye', 'bi-eye-slash');
        }
    });

    // 2. Cargar datos al abrir la página
    document.addEventListener("DOMContentLoaded", function() {
        const params = new URLSearchParams(window.location.search);
        const idDocente = params.get('id');

        if (!idDocente) {
            Swal.fire({
                icon: 'error',
                title: 'Falta ID del docente',
                text: 'No se especificó un ID de usuario en la URL.',
                confirmButtonColor: '#00847b'
            });
            return;
        }

        // Buscar en la lista de usuarios
        fetch(contextPath + '/ListarUsuariosServlet')
            .then(res => res.json())
            .then(usuarios => {
                const doc = usuarios.find(u => (u.idUsuario == idDocente || u.id == idDocente));
                if (!doc) {
                    Swal.fire({
                        icon: 'error',
                        title: 'Usuario no encontrado',
                        text: 'No se encontró un docente con ID: ' + idDocente,
                        confirmButtonColor: '#00847b'
                    });
                    return;
                }

                // Inyectar datos en los campos
                document.getElementById('campoIdUsuario').value = doc.idUsuario || doc.id || '';
                document.getElementById('campoNombre').value = doc.nombre || '';
                document.getElementById('campoApellidoP').value = doc.apellidoPaterno || doc.apellido_paterno || '';
                document.getElementById('campoApellidoM').value = doc.apellidoMaterno || doc.apellido_materno || '';
                document.getElementById('campoDivision').value = doc.idDivision || doc.division || '';
                document.getElementById('campoNumEmpleado').value = doc.numeroEmpleado || doc.numero_empleado || '';
                document.getElementById('campoTelefono').value = doc.telefono || '';
                document.getElementById('campoCorreo').value = doc.correo || '';
                document.getElementById('campoContrasena').value = doc.contrasena || doc.password || '';
            })
            .catch(err => {
                console.error('Error al cargar datos:', err);
                Swal.fire({
                    icon: 'error',
                    title: 'Error de respuesta',
                    text: 'Error al comunicarse con ListarUsuariosServlet',
                    confirmButtonColor: '#00847b'
                });
            });
    });

    // 3. Guardar Cambios
    document.getElementById('formEditarDocente').addEventListener('submit', function (e) {
        e.preventDefault();

        const datos = new URLSearchParams(new FormData(this));

        fetch(contextPath + '/EditarUsuarioServlet', {
            method: 'POST',
            headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
            body: datos.toString()
        })
            .then(res => res.json())
            .then(resultado => {
                if (resultado.success) {
                    Swal.fire({
                        icon: 'success',
                        title: '¡Docente actualizado!',
                        text: 'Los datos fueron guardados exitosamente.',
                        confirmButtonColor: '#00847b'
                    }).then(() => {
                        window.location.href = 'gestion_docente_co.jsp';
                    });
                } else {
                    Swal.fire({
                        icon: 'error',
                        title: 'Error',
                        text: resultado.message || 'No se pudo actualizar.',
                        confirmButtonColor: '#00847b'
                    });
                }
            })
            .catch(err => {
                console.error('Error al guardar:', err);
                Swal.fire({
                    icon: 'error',
                    title: 'Error de conexión',
                    text: 'Falló la comunicación con EditarUsuarioServlet',
                    confirmButtonColor: '#00847b'
                });
            });
    });
</script>

</body>
</html>