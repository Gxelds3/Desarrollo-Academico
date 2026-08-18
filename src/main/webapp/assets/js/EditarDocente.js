const params = new URLSearchParams(window.location.search);
const idDocente = params.get('id');

// Guarda el rol con el que se cargó originalmente el docente
let rolOriginal = 'docente';

// Función global para mostrar/ocultar contraseñas
function togglePassword(inputId) {
    const input = document.getElementById(inputId);
    const icon = document.getElementById('icon-' + inputId);

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

function mostrarAlerta(titulo, mensaje, icono = 'warning') {
    if (typeof Swal !== 'undefined') {
        Swal.fire({
            icon: icono,
            title: titulo,
            text: mensaje,
            confirmButtonColor: '#00847b'
        });
    } else {
        alert(titulo + ': ' + mensaje);
    }
}

function obtenerPaginaDestino() {
    const pathActual = window.location.pathname;
    if (pathActual.includes('_de.jsp')) {
        return 'gestion_docente_de.jsp';
    } else if (pathActual.includes('_do.jsp')) {
        return 'gestion_docente_do.jsp';
    }
    return 'gestion_docente_co.jsp';
}

/* =========================================================
   MANEJO DEL SELECTOR DE ROL (Docente / Coordinador)
   ========================================================= */

function selectRol(rol) {
    const cardDocente = document.getElementById('cardDocente');
    const cardCoordinador = document.getElementById('cardCoordinador');
    const radioDocente = document.getElementById('radioDocente');
    const radioCoordinador = document.getElementById('radioCoordinador');

    if (!cardDocente || !cardCoordinador) return;

    const iconoDocente = cardDocente.querySelector('i');
    const textoDocente = cardDocente.querySelector('div');
    const iconoCoordinador = cardCoordinador.querySelector('i');
    const textoCoordinador = cardCoordinador.querySelector('div');

    if (rol === 'docente') {
        cardDocente.style.border = '2px solid var(--teal-main, #00847b)';
        if (iconoDocente) iconoDocente.style.color = 'var(--teal-main, #00847b)';
        if (textoDocente) textoDocente.style.color = 'var(--teal-main, #00847b)';

        cardCoordinador.style.border = '2px solid #ccc';
        if (iconoCoordinador) iconoCoordinador.style.color = '#aaa';
        if (textoCoordinador) textoCoordinador.style.color = '#aaa';

        if (radioDocente) radioDocente.checked = true;
        if (radioCoordinador) radioCoordinador.checked = false;
    } else {
        cardCoordinador.style.border = '2px solid var(--teal-main, #00847b)';
        if (iconoCoordinador) iconoCoordinador.style.color = 'var(--teal-main, #00847b)';
        if (textoCoordinador) textoCoordinador.style.color = 'var(--teal-main, #00847b)';

        cardDocente.style.border = '2px solid #ccc';
        if (iconoDocente) iconoDocente.style.color = '#aaa';
        if (textoDocente) textoDocente.style.color = '#aaa';

        if (radioCoordinador) radioCoordinador.checked = true;
        if (radioDocente) radioDocente.checked = false;
    }
}

function obtenerRolSeleccionado() {
    const radioCoordinador = document.getElementById('radioCoordinador');
    return (radioCoordinador && radioCoordinador.checked) ? 'coordinador' : 'docente';
}

/* =========================================================
   CARGA DE DATOS DEL DOCENTE
   ========================================================= */

function llenarFormularioDocente(data) {
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

    // Los campos de contraseña permanecen libres/limpios
    const inputPassActual = document.getElementById('passActual');
    const inputPassNueva = document.getElementById('passNueva');
    const inputPassConfirm = document.getElementById('passConfirm');

    if (inputPassActual) inputPassActual.value = '';
    if (inputPassNueva) inputPassNueva.value = '';
    if (inputPassConfirm) inputPassConfirm.value = '';

    const divisionId = data.idDivision || data.division;
    const campoDivision = document.getElementById('campoDivision');
    const campoDivisionHidden = document.getElementById('campoDivisionHidden');

    if (divisionId && campoDivision) {
        campoDivision.value = divisionId;
        if (campoDivisionHidden) campoDivisionHidden.value = divisionId;
    }

    let rolData = (data.rol || data.tipoUsuario || data.tipo_usuario || '').toString().toLowerCase();

    if (!rolData) {
        rolData = (data.esCoordinador === true || data.es_coordinador === true) ? 'coordinador' : 'docente';
    }

    rolOriginal = (rolData === 'coordinador') ? 'coordinador' : 'docente';
    selectRol(rolOriginal);
}

function cargarDatosDocente() {
    if (!idDocente) {
        mostrarAlerta('Falta ID del usuario', 'Accede a esta página desde la gestión de docentes.', 'error');
        return;
    }

    fetch('ObtenerDocente?id=' + idDocente, { credentials: 'same-origin' })
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
                mostrarAlerta('Docente no encontrado', data.message || 'No se encontraron datos para el ID: ' + idDocente, 'error');
                return;
            }
            llenarFormularioDocente(data);
        })
        .catch(err => {
            console.error('Error al cargar datos:', err);
            mostrarAlerta('Error de servidor', 'No se pudieron cargar los datos del servidor.', 'error');
        });
}

document.addEventListener("DOMContentLoaded", () => {
    cargarDatosDocente();

    const campoNombre = document.getElementById('campoNombre');
    const campoApellidoP = document.getElementById('campoApellidoP');
    const campoApellidoM = document.getElementById('campoApellidoM');
    const campoNumEmpleado = document.getElementById('campoNumEmpleado');
    const campoTelefono = document.getElementById('campoTelefono');

    // Validaciones en tiempo real
    const inputsTexto = [campoNombre, campoApellidoP, campoApellidoM].filter(Boolean);
    inputsTexto.forEach(input => {
        input.addEventListener('input', (e) => {
            e.target.value = e.target.value.replace(/[^a-zA-ZáéíóúÁÉÍÓÚñÑ\s]/g, '');
        });
    });

    const inputsNumericos = [campoNumEmpleado, campoTelefono].filter(Boolean);
    inputsNumericos.forEach(input => {
        input.addEventListener('input', (e) => {
            e.target.value = e.target.value.replace(/\D/g, '');
        });
    });

    const formEditar = document.getElementById('formEditarDocente') || document.querySelector('form');
    if (formEditar) {
        formEditar.addEventListener('submit', function (e) {
            e.preventDefault();
            guardarCambios(e);
        });
    }
});

/* =========================================================
   GUARDADO
   ========================================================= */

function guardarCambios(e) {
    if (e && e.preventDefault) e.preventDefault();

    const campoIdUsuario = document.getElementById('campoIdUsuario');
    const campoNombre = document.getElementById('campoNombre');
    const campoApellidoP = document.getElementById('campoApellidoP');
    const campoApellidoM = document.getElementById('campoApellidoM');
    const campoDivision = document.getElementById('campoDivision');
    const campoNumEmpleado = document.getElementById('campoNumEmpleado');
    const campoTelefono = document.getElementById('campoTelefono');
    const campoCorreo = document.getElementById('campoCorreo');

    const passActual = document.getElementById('passActual');
    const passNueva = document.getElementById('passNueva');
    const passConfirm = document.getElementById('passConfirm');

    const nombreVal = campoNombre ? campoNombre.value.trim() : '';
    const apePVal = campoApellidoP ? campoApellidoP.value.trim() : '';
    const apeMVal = campoApellidoM ? campoApellidoM.value.trim() : '';
    const divisionVal = campoDivision ? campoDivision.value.trim() : '';
    const numEmpVal = campoNumEmpleado ? campoNumEmpleado.value.trim() : '';
    const telVal = campoTelefono ? campoTelefono.value.trim() : '';
    const correoVal = campoCorreo ? campoCorreo.value.trim() : '';

    const passActualVal = passActual ? passActual.value.trim() : '';
    const passNuevaVal = passNueva ? passNueva.value.trim() : '';
    const passConfirmVal = passConfirm ? passConfirm.value.trim() : '';

    const rolSeleccionado = obtenerRolSeleccionado();

    // Validaciones de campos obligatorios generales
    if (!nombreVal || !apePVal || !apeMVal || !numEmpVal || !telVal || !correoVal) {
        mostrarAlerta('Campos incompletos', 'Por favor, llena todos los campos obligatorios (*).');
        return;
    }

    if (!divisionVal) {
        mostrarAlerta('División requerida', 'No se detectó la división académica.');
        return;
    }

    if (!/^\d+$/.test(numEmpVal)) {
        mostrarAlerta('Número de Empleado inválido', 'El número de empleado solo debe contener dígitos numéricos.');
        return;
    }

    if (!/^\d{10}$/.test(telVal)) {
        mostrarAlerta('Teléfono inválido', 'El teléfono debe ser de exactamente 10 dígitos numéricos.');
        return;
    }

    if (correoVal.length > 50) {
        mostrarAlerta('Correo demasiado largo', 'El correo no debe exceder los 50 caracteres.');
        return;
    }

    if (!correoVal.toLowerCase().endsWith('@utez.edu.mx')) {
        mostrarAlerta('Correo no institucional', 'El correo debe terminar estrictamente en @utez.edu.mx');
        return;
    }

    // -------------------------------------------------------------
    // VALIDACIÓN DE CAMBIO DE CONTRASEÑA OPCIONAL
    // Solo si el usuario ingresó algo en la nueva contraseña
    // -------------------------------------------------------------
    if (passNuevaVal !== '') {
        if (!passActualVal) {
            mostrarAlerta('Contraseña requerida', 'Debes ingresar tu contraseña actual para confirmar el cambio de contraseña.');
            return;
        }

        if (passNuevaVal.length < 12 || passNuevaVal.length > 15) {
            mostrarAlerta('Contraseña inválida', 'La nueva contraseña debe tener entre 12 y 15 caracteres.');
            return;
        }

        if (passNuevaVal !== passConfirmVal) {
            mostrarAlerta('Las contraseñas no coinciden', 'Asegúrate de escribir exactamente la misma contraseña en "Nueva Contraseña" y "Confirmar Nueva Contraseña".');
            return;
        }
    }

    const datos = new URLSearchParams();
    const idVal = campoIdUsuario ? campoIdUsuario.value : idDocente;

    datos.append('idUsuario', idVal);
    datos.append('id_usuario', idVal);
    datos.append('id', idVal);

    datos.append('nombre', nombreVal);
    datos.append('apellidoPaterno', apePVal);
    datos.append('apellido_paterno', apePVal);
    datos.append('apellidoMaterno', apeMVal);
    datos.append('apellido_materno', apeMVal);

    datos.append('idDivision', divisionVal);
    datos.append('division', divisionVal);

    datos.append('numeroEmpleado', numEmpVal);
    datos.append('numero_empleado', numEmpVal);

    datos.append('telefono', telVal);

    datos.append('correoInstitucional', correoVal);
    datos.append('correo', correoVal);

    datos.append('passActual', passActualVal);
    datos.append('contrasenaActual', passActualVal);
    datos.append('contrasena_actual', passActualVal);

    datos.append('contrasena', passNuevaVal);
    datos.append('passNueva', passNuevaVal);
    datos.append('contrasenaNueva', passNuevaVal);
    datos.append('contrasena_nueva', passNuevaVal);

    datos.append('confirmarContrasena', passConfirmVal);
    datos.append('confirmar_contrasena', passConfirmVal);
    datos.append('passConfirm', passConfirmVal);

    datos.append('rol', rolSeleccionado);

    const huboCambioDeRol = rolSeleccionado !== rolOriginal;

    if (huboCambioDeRol) {
        const nombreRolNuevo = rolSeleccionado === 'coordinador' ? 'Coordinador' : 'Docente';
        const nombreRolViejo = rolOriginal === 'coordinador' ? 'Coordinador' : 'Docente';

        Swal.fire({
            icon: 'warning',
            title: '¿Deseas modificar el rol?',
            text: `El rol cambiará de "${nombreRolViejo}" a "${nombreRolNuevo}". ¿Deseas continuar?`,
            showCancelButton: true,
            confirmButtonColor: '#00847b',
            cancelButtonColor: '#aaaaaa',
            confirmButtonText: 'Sí, cambiar rol',
            cancelButtonText: 'Cancelar'
        }).then(function (resRol) {
            if (!resRol.isConfirmed) return;
            confirmarYGuardar(datos);
        });
    } else {
        confirmarYGuardar(datos);
    }
}

// Confirmación general de guardado + envío real al servlet
function confirmarYGuardar(datos) {
    Swal.fire({
        icon: 'question',
        title: '¿Deseas guardar los cambios?',
        text: 'Se actualizarán los datos del docente.',
        showCancelButton: true,
        confirmButtonColor: '#00847b',
        cancelButtonColor: '#aaaaaa',
        confirmButtonText: 'Sí, guardar',
        cancelButtonText: 'Cancelar'
    }).then(function (res) {
        if (!res.isConfirmed) return;

        let porcentaje = 0;
        let timerCarga;

        Swal.fire({
            title: 'Actualizando docente...',
            html: '<div style="font-size: 1.8rem; font-weight: bold; color: #00847b; margin-top: 10px;" id="lblPorcentajeEdit">0%</div>',
            allowOutsideClick: false,
            allowEscapeKey: false,
            showConfirmButton: false,
            didOpen: () => {
                Swal.showLoading();
                timerCarga = setInterval(() => {
                    if (porcentaje < 90) {
                        porcentaje += 10;
                        const el = document.getElementById('lblPorcentajeEdit');
                        if (el) el.textContent = porcentaje + '%';
                    }
                }, 80);
            }
        });

        fetch('EditarDocente', {
            method: 'POST',
            headers: { 'Content-Type': 'application/x-www-form-urlencoded; charset=UTF-8' },
            body: datos.toString(),
            credentials: 'same-origin'
        })
            .then(async res => {
                const data = await res.json().catch(() => null);
                if (!res.ok) {
                    throw new Error((data && data.message) ? data.message : 'Error al procesar en servidor');
                }
                return data;
            })
            .then(resultado => {
                clearInterval(timerCarga);
                const el = document.getElementById('lblPorcentajeEdit');
                if (el) el.textContent = '100%';

                setTimeout(() => {
                    if (!resultado || !(resultado.success || resultado.ok)) {
                        mostrarAlerta('Error al actualizar', (resultado && resultado.message) ? resultado.message : 'Ocurrió un problema al guardar los cambios.', 'error');
                        return;
                    }

                    if (typeof Swal !== 'undefined') {
                        Swal.fire({
                            icon: 'success',
                            title: '¡Docente actualizado con éxito!',
                            text: resultado.message || 'Los cambios se guardaron correctamente.',
                            confirmButtonColor: '#00847b'
                        }).then(() => {
                            window.location.href = obtenerPaginaDestino();
                        });
                    } else {
                        alert('¡Docente actualizado con éxito!');
                        window.location.href = obtenerPaginaDestino();
                    }
                }, 300);
            })
            .catch(err => {
                clearInterval(timerCarga);
                console.error('Error al guardar:', err);
                mostrarAlerta('Error al guardar', err.message || 'Hubo un problema al intentar guardar los datos.', 'error');
            });
    });
}