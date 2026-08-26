/**
 * GestionDesarrolladores.js
 *
 * Lógica de la vista de gestión (listado) de desarrolladores: búsqueda, activar/desactivar y eliminación permanente.
 */

const contextPath = window.contextPath || '';
const tbody = document.getElementById('tablaDesarrolladoresBody');
const inputBuscar = document.getElementById('buscarDesarrollador');

const DIVISIONES = {
    1: 'Datid',
    2: 'Dacea',
    3: 'Datefi',
    4: 'Dami',
    5: 'General'
};

// "Lista maestra" con todos los desarrolladores que trae el servidor.
let desarrolladoresOriginales = [];
let filtroTexto = '';

// Helper para alertas SweetAlert2
/**
 * Muestra una alerta emergente (SweetAlert2) al usuario; si SweetAlert2 no está disponible, recurre a `alert()` nativo como respaldo.
 * @param {*} titulo
 * @param {*} mensaje
 * @param {*} [icono='warning']
 */
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

// ------------------------------------------------------------------
//  VALIDACIONES DE FRONTEND (TODAS LAS REGLAS REQUERIDAS)
// ------------------------------------------------------------------
/**
 * Valida en el frontend los campos obligatorios y el formato de los datos de un docente antes de enviarlos al servidor.
 * @param {*} datos
 */
function validarFormularioDocente(datos) {
    const { nombre, apeP, apeM, division, numEmp, tel, correo, pass, confirmPass } = datos;

    // 1. CAMPOS INCOMPLETOS / OBLIGATORIOS
    if (!nombre || !apeP || !apeM || !numEmp || !tel || !correo || !pass || !confirmPass) {
        mostrarAlerta('Campos incompletos', 'Por favor, llena todos los campos obligatorios del formulario.');
        return false;
    }

    // 2. SELECCIÃ“N DE DIVISIÃ“N ACADÃ‰MICA
    if (!division) {
        mostrarAlerta('División requerida', 'Por favor selecciona una División Académica.');
        return false;
    }

    // 3. NÃšMERO DE EMPLEADO ÃšNICAMENTE CON DÍGITOS
    if (!/^\d+$/.test(numEmp)) {
        mostrarAlerta('Número de Empleado inválido', 'El número de empleado solo debe contener dígitos numéricos.');
        return false;
    }

    // 4. TELÃ‰FONO ESTRICTAMENTE DE 10 DÍGITOS
    if (!/^\d{10}$/.test(tel)) {
        mostrarAlerta('Teléfono inválido', 'El teléfono debe ser de exactamente 10 dígitos numéricos.');
        return false;
    }

    // 5. CORREO INSTITUCIONAL MÁXIMO 50 CARACTERES
    if (correo.length > 50) {
        mostrarAlerta('Correo demasiado largo', 'El correo institucional no debe exceder los 50 caracteres.');
        return false;
    }

    // 6. CORREO ESTRICTAMENTE TERMINADO EN @utez.edu.mx
    if (!correo.toLowerCase().endsWith('@utez.edu.mx')) {
        mostrarAlerta('Correo no institucional', 'El correo debe terminar estrictamente en @utez.edu.mx');
        return false;
    }

    // 7. CONTRASEÃ‘A ENTRE 12 Y 15 CARACTERES
    if (pass.length < 12 || pass.length > 15) {
        mostrarAlerta('Contraseña inválida', 'La contraseña debe tener entre 12 y 15 caracteres.');
        return false;
    }

    // 8. COINCIDENCIA EXACTA DE CONTRASEÃ‘AS
    if (pass !== confirmPass) {
        mostrarAlerta('Las contraseñas no coinciden', 'Asegúrate de escribir exactamente la misma contraseña en ambos campos.');
        return false;
    }

    return true;
}

/**
 * Escapa los caracteres especiales de HTML (&, <, >) de un texto para insertarlo de forma segura en el DOM y prevenir inyección de HTML/XSS.
 * @param {*} texto
 */
function escapeHtml(texto) {
    if (texto === null || texto === undefined) return '';
    return String(texto)
        .replace(/&/g, '&amp;')
        .replace(/</g, '&lt;')
        .replace(/>/g, '&gt;'); // CORREGIDO: > en lugar de $
}

/**
 * Normaliza un texto a minúsculas y sin acentos/diacríticos, para poder comparar cadenas de forma insensible a mayúsculas y tildes (usado en buscadores/filtros).
 * @param {*} texto
 */
function normalizar(texto) {
    return String(texto || '')
        .toLowerCase()
        .normalize('NFD')
        .replace(/[\u0300-\u036f]/g, ''); // quita acentos para que "búsqueda" == "busqueda"
}

/**
 * Concatena nombre y apellidos de un registro en una sola cadena de nombre completo, ignorando los campos vacíos.
 * @param {*} dev
 */
function nombreCompleto(dev) {
    return [dev.nombre, dev.apellidoPaterno, dev.apellidoMaterno].filter(Boolean).join(' ');
}

// Función helper para obtener las primeras dos iniciales
/**
 * Obtiene las iniciales (hasta dos letras) a partir de un nombre completo, usadas para mostrar un avatar/placeholder visual.
 * @param {*} nombreStr
 */
function obtenerIniciales(nombreStr) {
    if (!nombreStr) return '';
    const palabras = nombreStr.trim().split(/\s+/);
    if (palabras.length === 1) {
        return palabras[0].substring(0, 2).toUpperCase();
    }
    return (palabras[0][0] + palabras[1][0]).toUpperCase();
}

/**
 * Devuelve la lista de desarrolladores que coinciden con el texto de búsqueda actual (filtrado en memoria sobre la lista maestra).
 */
function obtenerDesarrolladoresFiltrados() {
    const texto = normalizar(filtroTexto);
    if (texto === '') return desarrolladoresOriginales;

    return desarrolladoresOriginales.filter(function (dev) {
        return normalizar(nombreCompleto(dev)).includes(texto) ||
            normalizar(dev.correo).includes(texto);
    });
}

/**
 * Renderiza en el DOM la tabla de desarrolladores a partir de la lista recibida.
 * @param {*} lista
 */
function renderDesarrolladores(lista) {
    if (!tbody) return;

    if (!lista || !lista.length) {
        tbody.innerHTML = '<tr><td colspan="6" class="text-center text-muted py-4">No se encontraron desarrolladores.</td></tr>';
        return;
    }

    tbody.innerHTML = '';
    lista.forEach(function (dev) {
        const activo = Number(dev.activo) === 1;
        const iconoEstado = activo ? 'bi-toggle-on text-success' : 'bi-toggle-off text-danger';
        const divisionNombre = DIVISIONES[dev.idDivision] || dev.division || '';

        const completo = nombreCompleto(dev);
        const iniciales = obtenerIniciales(completo);

        const fila = document.createElement('tr');
        fila.setAttribute('data-id', dev.id);
        fila.innerHTML =
            '<td class="text-start">' +
            '<div class="docente-name-container">' +
            '<div class="avatar-circle" style="flex-shrink:0;">' + escapeHtml(iniciales) + '</div>' +
            '<div class="docente-name" style="overflow:hidden; text-overflow:ellipsis; white-space:nowrap;">' +
            escapeHtml(completo) +
            '</div>' +
            '</div>' +
            '</td>' +
            '<td>' + escapeHtml(dev.correo) + '</td>' +
            '<td>' + escapeHtml(dev.numeroEmpleado) + '</td>' +
            '<td>' +
            '<i class="bi ' + iconoEstado + ' fs-4 toggle-estado" style="cursor:pointer;" data-id="' + dev.id + '" data-activo="' + (activo ? 1 : 0) + '"></i>' +
            '</td>' +
            '<td class="acciones-cell" style="white-space: nowrap;">' +

            /* Enlaces directos para redirigir a las vistas completas de Editar y Ver */
            '<a href="' + contextPath + '/EditarDesarrollador?id=' + dev.id + '" class="action-btn" title="Editar"><i class="bi bi-pencil"></i></a>' +
            '<a href="' + contextPath + '/VerDesarrollador?id=' + dev.id + '" class="action-btn" title="Ver"><i class="bi bi-eye"></i></a>' +

            '<a href="#" class="action-btn delete" title="Eliminar" data-id="' + dev.id + '"><i class="bi bi-trash"></i></a>' +
            '</td>';
        tbody.appendChild(fila);
    });
}

/**
 * Aplica el filtro de búsqueda vigente sobre la lista maestra y vuelve a renderizar la tabla con el resultado.
 */
function aplicarFiltro() {
    window.renderPaginator(obtenerDesarrolladoresFiltrados(), 20, 'paginationContainer', renderDesarrolladores);
}

/**
 * Obtiene del servidor la lista de desarrolladores y la muestra en la tabla, aplicando el filtro de búsqueda vigente.
 */
function cargarDesarrolladores() {
    if (!tbody) return;
    tbody.innerHTML = '<tr><td colspan="6" class="text-center text-muted py-4">Cargando...</td></tr>';

    fetch(contextPath + '/ListarDesarrollador', { credentials: 'same-origin' })
        .then(function (response) {
            if (response.redirected || (response.url && response.url.includes('login.jsp'))) {
                window.location.href = 'login.jsp';
                return null;
            }
            return response.json();
        })
        .then(function (desarrolladores) {
            if (!desarrolladores) return;
            desarrolladoresOriginales = desarrolladores || [];
            aplicarFiltro();
        })
        .catch(function (error) {
            console.error('Error al cargar desarrolladores:', error);
            tbody.innerHTML = '<tr><td colspan="6" class="text-center text-danger py-4">No se pudieron cargar los desarrolladores.</td></tr>';
        });
}

//  FUNCIÃ“N PARA EL SWITCH: Cambiar estado Activo/Inactivo
/**
 * Envía al servidor la petición para activar/desactivar (o cambiar de estado) el registro indicado.
 * @param {*} id
 * @param {*} nuevoEstado
 */
function cambiarEstado(id, nuevoEstado) {
    const datos = new URLSearchParams();
    datos.append('id', id);
    datos.append('estado', nuevoEstado);

    return fetch(contextPath + '/CambiarEstadoUsuarioServlet', {
        method: 'POST',
        headers: { 'Content-Type': 'application/x-www-form-urlencoded; charset=UTF-8' },
        body: datos.toString(),
        credentials: 'same-origin'
    }).then(function (response) {
        return response.json().then(function (data) {
            return { ok: response.ok, data: data };
        });
    });
}

// FUNCIÃ“N PARA LA PAPELERA: Eliminar definitivamente de BD
/**
 * Solicita confirmación al usuario y, si acepta, envía al servidor la petición para eliminar de forma permanente al desarrollador indicado.
 * @param {*} id
 */
function eliminarDesarrolladorPermanente(id) {
    const datos = new URLSearchParams();
    datos.append('idUsuario', id);

    return fetch(contextPath + '/EliminarDocente', {
        method: 'POST',
        headers: { 'Content-Type': 'application/x-www-form-urlencoded; charset=UTF-8' },
        body: datos.toString(),
        credentials: 'same-origin'
    }).then(function (response) {
        return response.json().then(function (data) {
            return { ok: response.ok, data: data };
        });
    });
}

// ------------------------------------------------------------------
//  EVENTOS Y RESTRICCIONES EN TIEMPO REAL
// ------------------------------------------------------------------
document.addEventListener("DOMContentLoaded", function () {
    // 1. RESTRICCIÃ“N EN TIEMPO REAL: Solo letras y espacios en Nombre y Apellidos
    const inputsTexto = document.querySelectorAll('#campoNombre, #campoApellidoP, #campoApellidoM, #nombre, #apellidoPaterno, #apellidoMaterno');
    inputsTexto.forEach(function (input) {
        input.addEventListener('input', function () {
            this.value = this.value.replace(/[^a-zA-ZáéíóúÁÃ‰ÍÃ“ÃšñÃ‘\s]/g, '');
        });
    });

    // 2. RESTRICCIÃ“N EN TIEMPO REAL: Solo dígitos en Num. Empleado y Teléfono
    const inputsNumericos = document.querySelectorAll('#campoNumEmpleado, #campoTelefono, #numeroEmpleado, #telefono');
    inputsNumericos.forEach(function (input) {
        input.addEventListener('input', function () {
            this.value = this.value.replace(/\D/g, '');
        });
    });

    // Buscador en vivo
    if (inputBuscar) {
        inputBuscar.addEventListener('input', function () {
            filtroTexto = inputBuscar.value.trim();
            aplicarFiltro();
        });
    }

    // Delegación de eventos para la tabla
    if (tbody) {
        tbody.addEventListener('click', function (e) {
            // Interruptor de estado
            const toggle = e.target.closest('.toggle-estado');
            if (toggle) {
                const id = toggle.getAttribute('data-id');
                const activoActual = toggle.getAttribute('data-activo') === '1';
                const nuevoEstado = activoActual ? 0 : 1;
                const accion = activoActual ? 'desactivar' : 'activar';

                Swal.fire({
                    icon: 'question',
                    title: '¿Deseas ' + accion + ' a este desarrollador?',
                    text: activoActual
                        ? 'El desarrollador no podrá iniciar sesión mientras esté inactivo.'
                        : 'El desarrollador podrá volver a iniciar sesión.',
                    showCancelButton: true,
                    confirmButtonColor: '#00847b',
                    cancelButtonColor: '#aaaaaa',
                    confirmButtonText: 'Sí, ' + accion,
                    cancelButtonText: 'Cancelar'
                }).then(function (result) {
                    if (!result.isConfirmed) return;

                    // --- PRELOADER CON PORCENTAJE PARA CAMBIAR ESTADO ---
                    let porcentaje = 0;
                    let timerCarga;

                    Swal.fire({
                        title: 'Actualizando estado...',
                        html: '<div style="font-size: 1.5rem; font-weight: bold; color: #00847b; margin-top: 10px;" id="lblPorcentajeDev">0%</div>',
                        allowOutsideClick: false,
                        allowEscapeKey: false,
                        showConfirmButton: false,
                        didOpen: () => {
                            Swal.showLoading();
                            timerCarga = setInterval(() => {
                                if (porcentaje < 90) {
                                    porcentaje += 10;
                                    const el = document.getElementById('lblPorcentajeDev');
                                    if (el) el.textContent = porcentaje + '%';
                                }
                            }, 80);
                        }
                    });

                    cambiarEstado(id, nuevoEstado)
                        .then(function (resultado) {
                            clearInterval(timerCarga);
                            const el = document.getElementById('lblPorcentajeDev');
                            if (el) el.textContent = '100%';

                            setTimeout(function () {
                                if (resultado.ok && resultado.data.success) {
                                    Swal.close();
                                    cargarDesarrolladores();
                                } else {
                                    mostrarAlerta('No se pudo actualizar el estado', resultado.data.message || 'Ocurrió un error al conectar con la base de datos.', 'error');
                                }
                            }, 300);
                        })
                        .catch(function (error) {
                            clearInterval(timerCarga);
                            console.error('Error al cambiar el estado:', error);
                            mostrarAlerta('Error de conexión', 'No fue posible comunicarse con el servidor.', 'error');
                        });
                });
                return;
            }

            // Eliminar registro
            const botonEliminar = e.target.closest('.action-btn.delete');
            if (botonEliminar) {
                e.preventDefault();

                const id = botonEliminar.getAttribute('data-id');

                Swal.fire({
                    icon: 'warning',
                    title: '¿Deseas eliminar este desarrollador?',
                    text: 'Esta acción borrará al desarrollador de la base de datos.',
                    showCancelButton: true,
                    confirmButtonColor: '#dc3545',
                    cancelButtonColor: '#aaaaaa',
                    confirmButtonText: 'Sí, eliminar',
                    cancelButtonText: 'Cancelar'
                }).then(function (result) {
                    if (!result.isConfirmed) return;

                    // --- PRELOADER CON PORCENTAJE PARA ELIMINAR ---
                    let porcentaje = 0;
                    let timerCarga;

                    Swal.fire({
                        title: 'Eliminando desarrollador...',
                        html: '<div style="font-size: 1.5rem; font-weight: bold; color: #dc3545; margin-top: 10px;" id="lblPorcentajeDev">0%</div>',
                        allowOutsideClick: false,
                        allowEscapeKey: false,
                        showConfirmButton: false,
                        didOpen: () => {
                            Swal.showLoading();
                            timerCarga = setInterval(() => {
                                if (porcentaje < 90) {
                                    porcentaje += 10;
                                    const el = document.getElementById('lblPorcentajeDev');
                                    if (el) el.textContent = porcentaje + '%';
                                }
                            }, 80);
                        }
                    });

                    eliminarDesarrolladorPermanente(id)
                        .then(function (resultado) {
                            clearInterval(timerCarga);
                            const el = document.getElementById('lblPorcentajeDev');
                            if (el) el.textContent = '100%';

                            setTimeout(function () {
                                if (resultado.ok && resultado.data.success) {
                                    Swal.fire({
                                        icon: 'success',
                                        title: '¡Eliminado!',
                                        text: resultado.data.message || 'El desarrollador fue eliminado correctamente.',
                                        confirmButtonColor: '#00847b'
                                    });
                                    cargarDesarrolladores();
                                } else {
                                    mostrarAlerta('No se pudo eliminar', resultado.data.message || 'Ocurrió un error al eliminar el desarrollador.', 'error');
                                }
                            }, 300);
                        })
                        .catch(function (error) {
                            clearInterval(timerCarga);
                            console.error('Error al eliminar el desarrollador:', error);
                            mostrarAlerta('Error de conexión', 'No fue posible comunicarse con el servidor.', 'error');
                        });
                });
                return;
            }
        });
    }

    cargarDesarrolladores();
});
