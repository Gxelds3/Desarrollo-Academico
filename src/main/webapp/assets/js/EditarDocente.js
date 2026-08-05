const contextPath = window.contextPath || '';
const params = new URLSearchParams(window.location.search);
const idDocente = params.get('id');

const campoIdUsuario = document.getElementById('campoIdUsuario');
const campoNombre = document.getElementById('campoNombre');
const campoApellidoP = document.getElementById('campoApellidoP');
const campoApellidoM = document.getElementById('campoApellidoM');
const campoDivision = document.getElementById('campoDivision');
const campoNumEmpleado = document.getElementById('campoNumEmpleado');
const campoTelefono = document.getElementById('campoTelefono');
const campoCorreo = document.getElementById('campoCorreo');
const campoContrasena = document.getElementById('campoContrasena'); // <-- Agregado para la contraseña

function llenarFormularioDocente(data) {
    if (campoIdUsuario) campoIdUsuario.value = data.idUsuario || data.id || '';
    if (campoNombre) campoNombre.value = data.nombre || '';
    if (campoApellidoP) campoApellidoP.value = data.apellidoPaterno || '';
    if (campoApellidoM) campoApellidoM.value = data.apellidoMaterno || '';
    if (campoDivision) campoDivision.value = data.idDivision || '';
    if (campoNumEmpleado) campoNumEmpleado.value = data.numeroEmpleado || '';
    if (campoTelefono) campoTelefono.value = data.telefono || '';
    if (campoCorreo) campoCorreo.value = data.correo || '';
    if (campoContrasena) campoContrasena.value = data.contrasena || ''; // <-- Llena la contraseña en pantalla
}

function cargarDatosDocente() {
    if (!idDocente) {
        if (typeof Swal !== 'undefined') {
            Swal.fire({
                icon: 'error',
                title: 'Falta ID del usuario',
                text: 'Accede a esta página desde la gestión de docentes.',
                confirmButtonColor: '#00847b'
            });
        }
        return;
    }

    // Intenta primero con el endpoint directo por ID (si existe)
    fetch(contextPath + '/ObtenerDocente?id=' + idDocente)
        .then(response => {
            if (!response.ok) throw new Error('Error al obtener datos');
            return response.json();
        })
        .then(data => {
            llenarFormularioDocente(data);
        })
        .catch(() => {
            // Si falla el endpoint directo, busca en la lista general de usuarios
            fetch(contextPath + '/ListarUsuariosServlet')
                .then(res => {
                    if (!res.ok) throw new Error('HTTP error ' + res.status);
                    return res.json();
                })
                .then(usuarios => {
                    const docente = usuarios.find(u => (u.idUsuario == idDocente || u.id == idDocente));
                    if (!docente) {
                        if (typeof Swal !== 'undefined') {
                            Swal.fire({
                                icon: 'error',
                                title: 'Usuario no encontrado',
                                text: 'El usuario no existe o no se pudo cargar.',
                                confirmButtonColor: '#00847b'
                            });
                        }
                        return;
                    }
                    llenarFormularioDocente(docente);
                })
                .catch(err => {
                    console.error('Error al cargar datos:', err);
                    if (typeof Swal !== 'undefined') {
                        Swal.fire({
                            icon: 'error',
                            title: 'Error de conexión',
                            text: 'No se pudieron cargar los datos del servidor.',
                            confirmButtonColor: '#00847b'
                        });
                    }
                });
        });
}

// Iniciar carga de datos al abrir la página
document.addEventListener("DOMContentLoaded", function() {
    cargarDatosDocente();
});

// Manejo del envío del formulario
const formEditar = document.querySelector('form');
if (formEditar) {
    formEditar.addEventListener('submit', function (e) {
        e.preventDefault();

        const datos = new URLSearchParams();
        datos.append('id_usuario', campoIdUsuario ? campoIdUsuario.value : '');
        datos.append('nombre', campoNombre ? campoNombre.value : '');
        datos.append('apellido_paterno', campoApellidoP ? campoApellidoP.value : '');
        datos.append('apellido_materno', campoApellidoM ? campoApellidoM.value : '');
        datos.append('division', campoDivision ? campoDivision.value : '');
        datos.append('numero_empleado', campoNumEmpleado ? campoNumEmpleado.value : '');
        datos.append('telefono', campoTelefono ? campoTelefono.value : '');
        datos.append('correo', campoCorreo ? campoCorreo.value : '');

        // Incluir la contraseña en los datos que se envían al guardar cambios
        if (campoContrasena) {
            datos.append('contrasena', campoContrasena.value);
        }

        fetch(contextPath + '/EditarUsuarioServlet', {
            method: 'POST',
            headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
            body: datos.toString()
        })
            .then(res => res.json().then(data => ({ ok: res.ok, data: data })))
            .then(resultado => {
                if (!resultado.ok || !resultado.data.success) {
                    Swal.fire({ icon: 'error', title: 'Error', text: resultado.data.message || 'Error al actualizar', confirmButtonColor: '#00847b' });
                    return;
                }

                Swal.fire({
                    icon: 'success',
                    title: '¡Docente actualizado con éxito!',
                    text: resultado.data.message || 'Los cambios se guardaron correctamente.',
                    confirmButtonColor: '#00847b'
                }).then(() => {
                    window.location.href = 'gestion_docente_co.jsp';
                });
            })
            .catch(err => {
                console.error('Error al guardar:', err);
                Swal.fire({ icon: 'error', title: 'Error de conexión', text: 'Hubo un problema de conexión al guardar.', confirmButtonColor: '#00847b' });
            });
    });
}