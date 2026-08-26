/**
 * EditarDesarrollador.js
 *
 * Lógica de la vista de edición de un desarrollador existente: carga de sus datos, validaciones en tiempo real y envío de los cambios al servidor.
 */

const params = new URLSearchParams(window.location.search);
const idDev  = params.get('id');

// Función global para mostrar/ocultar contraseñas
/**
 * Alterna la visibilidad (texto plano / oculto) del campo de contraseña indicado y actualiza el icono del ojito.
 * @param {*} inputId
 */
function togglePassword(inputId) {
    const input = document.getElementById(inputId);
    const icon  = document.getElementById('icon-' + inputId);

    if (!input) return;

    if (input.type === 'password') {
        input.type = 'text';
        if (icon) {
            icon.classList.remove('bi-eye-fill', 'text-muted');
            icon.classList.add('bi-eye-slash-fill', 'text-primary');
        }
    } else {
        input.type = 'password';
        if (icon) {
            icon.classList.remove('bi-eye-slash-fill', 'text-primary');
            icon.classList.add('bi-eye-fill', 'text-muted');
        }
    }
}

/**
 * Muestra una alerta emergente (SweetAlert2) al usuario; si SweetAlert2 no está disponible, recurre a `alert()` nativo como respaldo.
 * @param {*} titulo
 * @param {*} mensaje
 * @param {*} [icono='warning']
 */
function mostrarAlerta(titulo, mensaje, icono = 'warning') {
    if (typeof Swal !== 'undefined') {
        Swal.fire({ icon: icono, title: titulo, text: mensaje, confirmButtonColor: '#00847b' });
    } else {
        alert(titulo + ': ' + mensaje);
    }
}

/**
 * Rellena los campos del formulario con los datos recibidos desde el servidor para su edición.
 * @param {*} data
 */
function llenarFormulario(data) {
    if (!data) return;

    if (document.getElementById('campoIdUsuario')) {
        document.getElementById('campoIdUsuario').value = data.idUsuario || data.id_usuario || data.id || '';
    }
    if (document.getElementById('campoNombre')) {
        document.getElementById('campoNombre').value = data.nombre || '';
    }
    if (document.getElementById('campoApellidoP')) {
        document.getElementById('campoApellidoP').value = data.apellidoPaterno || data.apellido_paterno || '';
    }
    if (document.getElementById('campoApellidoM')) {
        document.getElementById('campoApellidoM').value = data.apellidoMaterno || data.apellido_materno || '';
    }
    if (document.getElementById('campoNumEmpleado')) {
        document.getElementById('campoNumEmpleado').value = data.numeroEmpleado || data.numero_empleado || '';
    }
    if (document.getElementById('campoTelefono')) {
        document.getElementById('campoTelefono').value = data.telefono || '';
    }
    if (document.getElementById('campoCorreo')) {
        document.getElementById('campoCorreo').value = data.correoInstitucional || data.correo || '';
    }

    const inputPassNueva  = document.getElementById('passNueva');
    const inputPassConfirm = document.getElementById('passConfirm');

    if (inputPassNueva)   inputPassNueva.value   = '';
    if (inputPassConfirm) inputPassConfirm.value = '';

    const divId = data.idDivision || data.division;
    const campoDivision = document.getElementById('campoDivision');
    const campoDivisionHidden = document.getElementById('campoDivisionHidden');

    if (divId && campoDivision) {
        campoDivision.value = divId;
        if (campoDivisionHidden) campoDivisionHidden.value = divId;
    }
}

/**
 * Obtiene del servidor los datos del registro a editar y rellena el formulario correspondiente.
 */
function cargarDatos() {
    if (!idDev) {
        mostrarAlerta('Falta ID', 'Accede a esta página desde la gestión de desarrolladores.', 'error');
        return;
    }
    fetch((window.contextPath || '') + '/ObtenerDocente?id=' + idDev, { credentials: 'same-origin' })
        .then(res => {
            if (res.redirected || (res.url && res.url.includes('login.jsp'))) {
                window.location.href = 'login.jsp';
                return null;
            }
            return res.json();
        })
        .then(data => {
            if (!data) return;
            if (data.success === false) {
                mostrarAlerta('No encontrado', data.message || 'No se encontró el desarrollador.', 'error');
                return;
            }
            llenarFormulario(data);
        })
        .catch(err => {
            console.error('Error al cargar datos:', err);
            mostrarAlerta('Error', 'No se pudo contactar al servidor.', 'error');
        });
}

document.addEventListener('DOMContentLoaded', () => {
    cargarDatos();

    const campoNombre      = document.getElementById('campoNombre');
    const campoApellidoP   = document.getElementById('campoApellidoP');
    const campoApellidoM   = document.getElementById('campoApellidoM');
    const campoNumEmpleado = document.getElementById('campoNumEmpleado');
    const campoTelefono    = document.getElementById('campoTelefono');
    const campoDivision    = document.getElementById('campoDivision');
    const campoDivisionHidden = document.getElementById('campoDivisionHidden');

    if (campoDivision) {
        campoDivision.addEventListener('change', function () {
            if (campoDivisionHidden) campoDivisionHidden.value = campoDivision.value;
        });
    }

    [campoNombre, campoApellidoP, campoApellidoM].filter(Boolean).forEach(inp => {
        inp.addEventListener('input', e => {
            e.target.value = e.target.value.replace(/[^a-zA-ZáéíóúÁÉÍÓÚñÑ\s]/g, '');
        });
    });

    [campoNumEmpleado, campoTelefono].filter(Boolean).forEach(inp => {
        inp.addEventListener('input', e => {
            e.target.value = e.target.value.replace(/\D/g, '');
        });
    });

    const form = document.getElementById('formEditarDesarrollador') || document.querySelector('form');
    if (form) {
        form.addEventListener('submit', function (e) {
            e.preventDefault();
            guardarCambios();
        });
    }
});

/**
 * Recolecta y valida los datos del formulario y envía al servidor la petición para guardar los cambios del registro editado.
 */
function guardarCambios() {
    const campoIdUsuario      = document.getElementById('campoIdUsuario');
    const campoNombre         = document.getElementById('campoNombre');
    const campoApellidoP      = document.getElementById('campoApellidoP');
    const campoApellidoM      = document.getElementById('campoApellidoM');
    const campoDivision       = document.getElementById('campoDivision');
    const campoDivisionHidden = document.getElementById('campoDivisionHidden');
    const campoNumEmpleado    = document.getElementById('campoNumEmpleado');
    const campoTelefono       = document.getElementById('campoTelefono');
    const campoCorreo         = document.getElementById('campoCorreo');

    const passNueva   = document.getElementById('passNueva');
    const passConfirm = document.getElementById('passConfirm');

    const nombreVal    = campoNombre        ? campoNombre.value.trim()        : '';
    const apePVal      = campoApellidoP     ? campoApellidoP.value.trim()     : '';
    const apeMVal      = campoApellidoM     ? campoApellidoM.value.trim()     : '';
    const divisionVal  = campoDivisionHidden ? campoDivisionHidden.value.trim() : (campoDivision ? campoDivision.value.trim() : '');
    const numEmpVal    = campoNumEmpleado   ? campoNumEmpleado.value.trim()   : '';
    const telVal       = campoTelefono      ? campoTelefono.value.trim()      : '';
    const correoVal    = campoCorreo        ? campoCorreo.value.trim()        : '';

    const passNuevaVal  = passNueva   ? passNueva.value.trim()   : '';
    const passConfVal   = passConfirm ? passConfirm.value.trim() : '';

    // Validación de nueva contraseña opcional
    if (passNuevaVal !== '') {
        if (passNuevaVal.length < 12 || passNuevaVal.length > 15) {
            mostrarAlerta('Contraseña inválida', 'La nueva contraseña debe tener entre 12 y 15 caracteres.');
            return;
        }
        if (passNuevaVal !== passConfVal) {
            mostrarAlerta('Las contraseñas no coinciden', 'Verifica que la nueva contraseña y su confirmación sean iguales.');
            return;
        }
    }

    const idVal = campoIdUsuario ? campoIdUsuario.value : idDev;
    const datos = new URLSearchParams();

    datos.append('id',                   idVal);
    datos.append('id_usuario',           idVal);
    datos.append('idUsuario',            idVal);
    datos.append('nombre',               nombreVal);
    datos.append('apellido_paterno',     apePVal);
    datos.append('apellidoPaterno',      apePVal);
    datos.append('apellido_materno',     apeMVal);
    datos.append('apellidoMaterno',      apeMVal);
    datos.append('division',             divisionVal);
    datos.append('idDivision',           divisionVal);
    datos.append('numero_empleado',      numEmpVal);
    datos.append('numeroEmpleado',       numEmpVal);
    datos.append('telefono',             telVal);
    datos.append('correo',               correoVal);
    datos.append('correoInstitucional',  correoVal);

    // Parámetros de contraseña actual y nueva
    datos.append('contrasena',           passNuevaVal);
    datos.append('passNueva',            passNuevaVal);
    datos.append('confirmar_contrasena', passConfVal);

    Swal.fire({
        icon: 'question',
        title: '¿Guardar cambios?',
        text: 'Se actualizarán los datos del desarrollador.',
        showCancelButton: true,
        confirmButtonColor: '#00847b',
        cancelButtonColor: '#aaaaaa',
        confirmButtonText: 'Sí, guardar',
        cancelButtonText: 'Cancelar'
    }).then(res => {
        if (!res.isConfirmed) return;

        let pct = 0;
        let timer;
        Swal.fire({
            title: 'Actualizando desarrollador...',
            html: '<div style="font-size:1.8rem;font-weight:bold;color:#00847b;margin-top:10px;" id="pctDev">0%</div>',
            allowOutsideClick: false,
            allowEscapeKey: false,
            showConfirmButton: false,
            didOpen: () => {
                Swal.showLoading();
                timer = setInterval(() => {
                    if (pct < 90) {
                        pct += 10;
                        const el = document.getElementById('pctDev');
                        if (el) el.textContent = pct + '%';
                    }
                }, 80);
            }
        });

        fetch((window.contextPath || '') + '/EditarDesarrollador', {
            method: 'POST',
            headers: { 'Content-Type': 'application/x-www-form-urlencoded; charset=UTF-8' },
            body: datos.toString(),
            credentials: 'same-origin'
        })
            .then(async resp => {
                const data = await resp.json().catch(() => null);
                clearInterval(timer);
                const el = document.getElementById('pctDev');
                if (el) el.textContent = '100%';

                setTimeout(() => {
                    if (!resp.ok || !data || !data.success) {
                        mostrarAlerta('Error al actualizar', (data && data.message) ? data.message : 'Ocurrió un problema al guardar.', 'error');
                        return;
                    }
                    Swal.fire({
                        icon: 'success',
                        title: '¡Desarrollador actualizado!',
                        text: data.message || 'Los cambios se guardaron correctamente.',
                        confirmButtonColor: '#00847b'
                    }).then(() => {
                        window.location.href = 'gestion_desarrolladores_de.jsp';
                    });
                }, 300);
            })
            .catch(err => {
                clearInterval(timer);
                console.error('Error:', err);
                mostrarAlerta('Error de red', 'No se pudo comunicar con el servidor.', 'error');
            });
    });
}