/**
 * CargarArchivoDe.js
 *
 * Lógica de la vista de carga de constancia para el rol Desarrollador: validación del periodo de carga vigente, control de vigencia del archivo y envío de la entrega.
 */

const params = new URLSearchParams(window.location.search);
const idEvento = params.get('id');
const idUsuarioTarget = params.get('idUsuarioTarget');
let constanciaIdActual = null;
let eventoFechaFin = null;

// Límite máximo en bytes (25 MB)
const TAMANO_MAX_BYTES = 25 * 1024 * 1024;

if (idEvento) {
    document.getElementById('hiddenIdEvento').value = idEvento;
    if (idUsuarioTarget) {
        document.getElementById('hiddenIdUsuarioTarget').value = idUsuarioTarget;
        document.querySelectorAll('.btn-volver-dinamico').forEach(btn => {
            btn.href = 'editar_evento_de.jsp?id=' + idEvento;
        });
    }
} else {
    Swal.fire('Error', 'No se especificó un evento válido', 'error').then(() => {
        window.location.href = 'mi_evento_co.jsp';
    });
}

/**
 * Convierte una fecha en formato ISO a un formato de fecha legible para mostrar en la interfaz.
 * @param {*} iso
 */
function aFechaVisible(iso) {
    if (!iso) return '';
    const partes = iso.split('-');
    if (partes.length !== 3) return iso;
    return partes[2] + '/' + partes[1] + '/' + partes[0].slice(2);
}

/**
 * Devuelve el texto recibido con la primera letra en mayúscula.
 * @param {*} texto
 */
function capitalizar(texto) {
    if (!texto) return '';
    return texto.charAt(0).toUpperCase() + texto.slice(1);
}

/**
 * Muestra el formulario de carga/entrega, ocultando otros estados de la vista (cargando, error, etc.).
 */
function mostrarFormulario() {
    document.getElementById('formCargaArchivo').style.display = '';
    document.getElementById('constanciaCard').style.display = 'none';
}

/**
 * Muestra en la vista los datos de la constancia ya entregada, indicando si se encuentra vencida.
 * @param {*} c
 * @param {*} estaVencido
 */
function mostrarConstancia(c, estaVencido) {
    document.getElementById('formCargaArchivo').style.display = 'none';
    const card = document.getElementById('constanciaCard');
    card.style.display = '';

    constanciaIdActual = c.idConstancia;
    document.getElementById('constanciaNombre').textContent = c.nombreArchivo;
    document.getElementById('constanciaFechaSubida').textContent = c.fechaSubida;

    if (c.tieneVigencia === 1 && c.fechaVencimiento) {
        document.getElementById('constanciaVigenciaWrap').style.display = '';
        document.getElementById('constanciaVigencia').textContent = c.fechaVencimiento;
    }

    document.getElementById('btnVerArchivo').href = contextPath + '/DescargarConstanciaServlet?idConstancia=' + c.idConstancia;

    if (estaVencido) {
        document.getElementById('btnCancelarEntrega').disabled = true;
        document.getElementById('btnCancelarEntrega').title = 'El plazo del evento ha vencido';
    }
}

// ------------------------------------------------------------------
// Validación de Periodos de Carga (General + División)
// ------------------------------------------------------------------
// Validación de Periodos de Carga (lógica corregida)
// ------------------------------------------------------------------
/**
 * Consulta al servidor si existe un periodo de carga vigente para la división indicada y determina si el formulario de entrega debe habilitarse.
 * @param {*} idDivisionDocente
 * @returns {Promise<void>}
 */
async function validarPeriodosCarga(idDivisionDocente) {
    try {
        const res = await fetch(contextPath + '/ListarPeriodosServlet?t=' + Date.now());
        const periodos = await res.json();

        if (!periodos || !Array.isArray(periodos)) {
            return { permitido: true };
        }

        const hoy = new Date();
        hoy.setHours(0, 0, 0, 0);

        // Periodo General
        const periodoGeneral = periodos.find(p => String(p.division || '').toLowerCase() === 'general' || Number(p.idDivision) === 5);

        // Periodo de la división específica del docente
        const periodoDivision = periodos.find(p => Number(p.idDivision) === Number(idDivisionDocente) || String(p.division || '').toLowerCase() === String(idDivisionDocente || '').toLowerCase());

        // ── MODO SINCRONIZADO: General ACTIVO ───────────────────────────
        // Cuando General está activo, todas las divisiones siguen sus fechas.
        // Solo hay que verificar que las fechas de General no hayan vencido.
        if (periodoGeneral && Number(periodoGeneral.activo) === 1) {
            const fechaFin = periodoGeneral.fechaFin ? new Date(periodoGeneral.fechaFin) : null;
            if (fechaFin) {
                fechaFin.setHours(23, 59, 59, 999);
                if (hoy > fechaFin) {
                    return {
                        permitido: false,
                        titulo: 'Fecha de entrega ya pasó',
                        mensaje: 'El periodo General de carga ha vencido. Ya no es posible subir constancias.'
                    };
                }
            }
            // General activo y dentro de fechas → subida permitida para todos
            return { permitido: true };
        }

        // ── MODO AUTÓNOMO: General INACTIVO ─────────────────────────────
        // Cada división es independiente. Solo revisamos la división del docente.
        if (periodoDivision) {
            if (Number(periodoDivision.activo) === 0) {
                return {
                    permitido: false,
                    titulo: 'Periodo de carga deshabilitado',
                    mensaje: `El periodo de carga para tu división se encuentra cerrado en este momento.`
                };
            }
            // División activa → verificar que las fechas no hayan vencido
            const fechaFin = periodoDivision.fechaFin ? new Date(periodoDivision.fechaFin) : null;
            if (fechaFin) {
                fechaFin.setHours(23, 59, 59, 999);
                if (hoy > fechaFin) {
                    return {
                        permitido: false,
                        titulo: 'Fecha de entrega ya pasó',
                        mensaje: 'El periodo de carga para tu división ha vencido. Ya no es posible subir constancias.'
                    };
                }
            }
            return { permitido: true };
        }

        // Sin periodo configurado para esta división
        return {
            permitido: false,
            titulo: 'Periodo de carga no configurado',
            mensaje: 'No hay periodo de carga configurado para tu división.'
        };

    } catch (err) {
        console.error('Error al validar periodos de carga:', err);
        return { permitido: true }; // Ante error de red, no bloquear
    }
}

// Oculta el formulario de subida y muestra el bloque "Periodo deshabilitado"
/**
 * Deshabilita el formulario de entrega de constancia y muestra el mensaje indicado al usuario.
 * @param {*} mensaje
 * @param {*} titulo
 */
function deshabilitarFormularioSubida(mensaje, titulo) {
    // Ocultar completamente el data-card de subida dentro del form
    const form = document.getElementById('formCargaArchivo');
    if (form) {
        const dataCard = form.querySelector('.data-card');
        if (dataCard) dataCard.style.display = 'none';
    }
    // Mostrar el bloque de periodo deshabilitado
    const bloqueDisabled = document.getElementById('periodoDeshabilitadoBloque');
    if (bloqueDisabled) {
        bloqueDisabled.style.display = '';
        const tituloEl = document.getElementById('tituloPeriodoDeshabilitado');
        const msgEl = document.getElementById('mensajePeriodoDeshabilitado');
        if (tituloEl && titulo) tituloEl.textContent = titulo;
        if (msgEl) msgEl.textContent = mensaje;
    }
}

// Carga de datos iniciales
/**
 * Inicializa la página: valida el periodo de carga vigente, carga los datos del evento/constancia y configura los listeners del formulario.
 * @returns {Promise<void>}
 */
async function inicializarPagina() {
    let divisionDocente = null;

    try {
        const resEvento = await fetch(contextPath + '/EditarEventoServlet?id=' + encodeURIComponent(idEvento) + '&t=' + Date.now());
        const dataEvento = await resEvento.json();

        if (dataEvento) {
            document.getElementById('tituloEvento').textContent = (dataEvento.nombre || '').toUpperCase();
            document.getElementById('campoTipo').textContent = capitalizar(dataEvento.tipo);
            document.getElementById('campoLugar').textContent = dataEvento.lugar || '';
            document.getElementById('campoInstitucion').textContent = dataEvento.institucion || '';
            document.getElementById('campoDescripcion').textContent = dataEvento.descripcion || '';
            document.getElementById('campoFechaInicio').textContent = aFechaVisible(dataEvento.fechaInicio);
            document.getElementById('campoFechaFin').textContent = aFechaVisible(dataEvento.fechaFin);
            document.getElementById('campoModalidad').textContent = capitalizar(dataEvento.modalidad);

            // Guardamos la división del usuario obtenida del Servlet
            divisionDocente = dataEvento.idDivisionDocente || dataEvento.idDivision || dataEvento.division;

            if (dataEvento.fechaFin) {
                const p = dataEvento.fechaFin.split('-');
                eventoFechaFin = new Date(p[0], p[1] - 1, p[2]);
                eventoFechaFin.setHours(23, 59, 59);
            }
        }
    } catch (err) {
        console.error('Error al cargar datos del evento:', err);
    }

    // Consultar constancia existente
    try {
        let urlConstancia = contextPath + '/ObtenerConstanciaServlet?idEvento=' + encodeURIComponent(idEvento);
        if (idUsuarioTarget) {
            urlConstancia += '&idUsuarioTarget=' + encodeURIComponent(idUsuarioTarget);
        }
        urlConstancia += '&t=' + Date.now();

        const resConst = await fetch(urlConstancia);
        const result = await resConst.json();
        const estaVencido = eventoFechaFin ? new Date() > eventoFechaFin : false;

        if (result && result.success && result.constancia) {
            mostrarConstancia(result.constancia, estaVencido);
        } else {
            mostrarFormulario();

            // 1. Validar expiración propia del Evento
            if (estaVencido) {
                deshabilitarFormularioSubida('La fecha límite de entrega ya pasó. Ya no es posible subir constancias para este evento.', 'Fecha de entrega ya pasó');
            } else {
                // 2. Validar Estado de Periodos General y por División
                const estadoPeriodos = await validarPeriodosCarga(divisionDocente);
                if (!estadoPeriodos.permitido) {
                    deshabilitarFormularioSubida(estadoPeriodos.mensaje);
                }
            }
        }
    } catch (err) {
        console.error('Error al verificar constancia:', err);
        mostrarFormulario();
    }
}

inicializarPagina();

// Cancelar Entrega
document.getElementById('btnCancelarEntrega').addEventListener('click', () => {
    if (!constanciaIdActual) return;
    Swal.fire({
        title: '¿Cancelar entrega?',
        text: 'Se eliminará el archivo que subiste. Esta acción no se puede deshacer.',
        icon: 'warning',
        showCancelButton: true,
        confirmButtonColor: '#e74c3c',
        cancelButtonColor: '#6c757d',
        confirmButtonText: 'Sí, cancelar entrega',
        cancelButtonText: 'No, mantener'
    }).then(result => {
        if (!result.isConfirmed) return;
        const fd = new FormData();
        fd.append('idConstancia', constanciaIdActual);
        fd.append('idEvento', idEvento);
        if (idUsuarioTarget) {
            fd.append('idUsuarioTarget', idUsuarioTarget);
        }
        fetch(contextPath + '/CancelarConstanciaServlet', { method: 'POST', body: fd })
            .then(res => res.json())
            .then(data => {
                if (data.success) {
                    Swal.fire('¡Cancelado!', data.message, 'success').then(() => location.reload());
                } else {
                    Swal.fire('Error', data.message || 'No se pudo cancelar', 'error');
                }
            })
            .catch(() => Swal.fire('Error', 'Problema de conexión', 'error'));
    });
});

// Lógica de fecha de vigencia
const vigenciaSi = document.getElementById('vigenciaSi');
const vigenciaNo = document.getElementById('vigenciaNo');
const fechaVencimiento = document.getElementById('fechaVencimiento');

vigenciaSi.addEventListener('change', () => {
    document.getElementById('bloqueFechaVigencia').classList.remove('d-none');
    fechaVencimiento.required = true;
    // Bloquear fechas pasadas en el calendario
    const hoyMin = new Date();
    hoyMin.setHours(0, 0, 0, 0);
    const yyyy = hoyMin.getFullYear();
    const mm = String(hoyMin.getMonth() + 1).padStart(2, '0');
    const dd = String(hoyMin.getDate()).padStart(2, '0');
    fechaVencimiento.setAttribute('min', yyyy + '-' + mm + '-' + dd);
});

vigenciaNo.addEventListener('change', () => {
    document.getElementById('bloqueFechaVigencia').classList.add('d-none');
    fechaVencimiento.required = false;
    fechaVencimiento.value = '';
});

// Subida e interacción Drag & Drop / Input File (con validación de 25 MB)
const archivoPdf = document.getElementById('archivoPdf');
const uploadZone = document.getElementById('uploadZone');
const archivoSeleccionadoInfo = document.getElementById('archivoSeleccionadoInfo');
const archivoSeleccionadoNombre = document.getElementById('archivoSeleccionadoNombre');

archivoPdf.addEventListener('change', (e) => {
    if (e.target.files.length > 0) {
        const archivo = e.target.files[0];

        if (archivo.size > TAMANO_MAX_BYTES) {
            Swal.fire('Error', 'El archivo excede el tamaño máximo permitido de 25 MB.', 'error');
            e.target.value = '';
            return;
        }

        uploadZone.style.display = 'none';
        archivoSeleccionadoNombre.textContent = archivo.name;
        archivoSeleccionadoInfo.style.display = 'flex';
    }
});

document.getElementById('btnQuitarArchivo').addEventListener('click', () => {
    archivoPdf.value = '';
    uploadZone.style.display = '';
    archivoSeleccionadoInfo.style.display = 'none';
});

// Envío del formulario vía AJAX
document.getElementById('formCargaArchivo').addEventListener('submit', function(e) {
    e.preventDefault();

    if (archivoPdf.files.length === 0) {
        Swal.fire('Advertencia', 'Debes seleccionar un archivo (PDF, PNG o JPG)', 'warning');
        return;
    }

    const archivo = archivoPdf.files[0];

    if (archivo.size > TAMANO_MAX_BYTES) {
        Swal.fire('Error', 'El archivo no puede pesar más de 25 MB.', 'error');
        return;
    }

    if (vigenciaSi.checked && !fechaVencimiento.value) {
        Swal.fire('Advertencia', 'Debes elegir una fecha de vigencia', 'warning');
        return;
    }

    if (vigenciaSi.checked && fechaVencimiento.value) {
        const hoy = new Date();
        hoy.setHours(0, 0, 0, 0);
        const partes = fechaVencimiento.value.split('-');
        const fechaSeleccionada = new Date(partes[0], partes[1] - 1, partes[2]);
        if (fechaSeleccionada < hoy) {
            Swal.fire('Fecha inválida', 'La fecha de vigencia no puede ser una fecha que ya expiró. Elige una fecha de hoy en adelante.', 'warning');
            return;
        }
    }

    const formData = new FormData(this);

    Swal.fire({
        title: 'Subiendo archivo...',
        allowOutsideClick: false,
        didOpen: () => Swal.showLoading()
    });

    fetch(contextPath + '/SubirConstanciaServlet1', {
        method: 'POST',
        body: formData
    })
        .then(res => res.json())
        .then(data => {
            if (data.success) {
                Swal.fire('¡Éxito!', data.message, 'success').then(() => location.reload());
            } else {
                Swal.fire('Error', data.message || 'Ocurrió un error', 'error');
            }
        })
        .catch(err => {
            console.error(err);
            Swal.fire('Error', 'Problema de conexión al subir', 'error');
        });
});