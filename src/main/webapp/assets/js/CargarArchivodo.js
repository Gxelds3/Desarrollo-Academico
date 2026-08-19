const params = new URLSearchParams(window.location.search);
const idEvento = params.get('id');
let constanciaIdActual = null;

// Límite máximo en bytes (10 MB)
const TAMANO_MAX_BYTES = 10 * 1024 * 1024;

if (idEvento) {
    document.getElementById('hiddenIdEvento').value = idEvento;
} else {
    Swal.fire('Error', 'No se especificó un evento válido', 'error').then(() => {
        window.location.href = 'mis_eventos_do.jsp';
    });
}

function aFechaVisible(iso) {
    if (!iso) return '';
    const partes = iso.split('-');
    if (partes.length !== 3) return iso;
    return partes[2] + '/' + partes[1] + '/' + partes[0].slice(2);
}

function capitalizar(texto) {
    if (!texto) return '';
    return texto.charAt(0).toUpperCase() + texto.slice(1);
}

function mostrarFormulario() {
    document.getElementById('formCargaArchivo').style.display = '';
    document.getElementById('constanciaCard').style.display = 'none';
}

function mostrarConstancia(c, estaBloqueado) {
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

    if (estaBloqueado) {
        document.getElementById('vencidoBanner').style.display = '';
        document.getElementById('btnCancelarEntrega').disabled = true;
        document.getElementById('btnCancelarEntrega').title = 'El periodo de recepción de constancias está cerrado';
    }
}

function bloquearFormularioCarga(mensaje) {
    document.getElementById('formCargaArchivo').querySelectorAll('input, button[type="submit"]').forEach(el => el.disabled = true);
    document.getElementById('uploadZone').style.opacity = '0.4';
    document.getElementById('uploadZone').style.pointerEvents = 'none';

    // Evitar duplicar el banner de advertencia si ya existe
    if (!document.getElementById('bannerPeriodoCerrado')) {
        const warn = document.createElement('div');
        warn.id = 'bannerPeriodoCerrado';
        warn.className = 'vencido-banner mt-3';
        warn.innerHTML = `<i class="bi bi-lock-fill me-2"></i><strong>Carga Inhabilitada.</strong> ${mensaje}`;
        document.getElementById('formCargaArchivo').querySelector('.data-card').appendChild(warn);
    }
}

async function inicializarPagina() {
    let estaBloqueado = false;
    let motivoBloqueo = "El periodo de recepción de constancias no está activo.";

    try {
        // 1. Obtener los datos del evento
        const resEvento = await fetch(contextPath + '/EditarEventoServlet?id=' + encodeURIComponent(idEvento) + '&t=' + Date.now());
        const dataEvento = await resEvento.json();

        if (dataEvento.success) {
            document.getElementById('tituloEvento').textContent = (dataEvento.nombre || '').toUpperCase();
            document.getElementById('campoTipo').textContent = capitalizar(dataEvento.tipo);
            document.getElementById('campoLugar').textContent = dataEvento.lugar || '';
            document.getElementById('campoInstitucion').textContent = dataEvento.institucion || '';
            document.getElementById('campoDescripcion').textContent = dataEvento.descripcion || '';
            document.getElementById('campoFechaInicio').textContent = aFechaVisible(dataEvento.fechaInicio);
            document.getElementById('campoFechaFin').textContent = aFechaVisible(dataEvento.fechaFin);
            document.getElementById('campoModalidad').textContent = capitalizar(dataEvento.modalidad);
        }

        // 2. VALIDACIÓN DE PERIODOS: General + División
        // Nota: Asegúrate de tener este Servlet (o ajusta la URL al Servlet que devuelva el estado de tus periodos)
        const resPeriodo = await fetch(contextPath + '/VerificarPeriodoCargaServlet?idEvento=' + encodeURIComponent(idEvento) + '&t=' + Date.now());
        const dataPeriodo = await resPeriodo.json();

        // Se requiere estricta confirmación de que AMBOS están prendidos/activos
        const periodoGeneralActivo = dataPeriodo.periodoGeneralActivo === true;
        const periodoDivisionActivo = dataPeriodo.periodoDivisionActivo === true;

        if (!periodoGeneralActivo || !periodoDivisionActivo) {
            estaBloqueado = true;

            if (!periodoGeneralActivo && !periodoDivisionActivo) {
                motivoBloqueo = "El periodo general y el de tu división están cerrados.";
            } else if (!periodoGeneralActivo) {
                motivoBloqueo = "El periodo general de carga de constancias está cerrado.";
            } else {
                motivoBloqueo = "El periodo de carga para tu división no está habilitado.";
            }
        }

        // 3. Consultar si el docente ya subió una constancia para este evento
        const resConst = await fetch(contextPath + '/ObtenerConstanciaServlet?idEvento=' + encodeURIComponent(idEvento) + '&t=' + Date.now());
        const result = await resConst.json();

        if (result && result.success && result.constancia) {
            mostrarConstancia(result.constancia, estaBloqueado);
        } else {
            mostrarFormulario();
            if (estaBloqueado) {
                bloquearFormularioCarga(motivoBloqueo);
            }
        }

    } catch (err) {
        console.error('Error al inicializar la página:', err);
        mostrarFormulario();
    }
}

inicializarPagina();

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

const vigenciaSi = document.getElementById('vigenciaSi');
const vigenciaNo = document.getElementById('vigenciaNo');
const fechaVencimiento = document.getElementById('fechaVencimiento');

vigenciaSi.addEventListener('change', () => {
    document.getElementById('bloqueFechaVigencia').classList.remove('d-none');
    fechaVencimiento.required = true;
});

vigenciaNo.addEventListener('change', () => {
    document.getElementById('bloqueFechaVigencia').classList.add('d-none');
    fechaVencimiento.required = false;
    fechaVencimiento.value = '';
});

const archivoPdf = document.getElementById('archivoPdf');
const uploadZone = document.getElementById('uploadZone');
const archivoSeleccionadoInfo = document.getElementById('archivoSeleccionadoInfo');
const archivoSeleccionadoNombre = document.getElementById('archivoSeleccionadoNombre');

// Selección de archivo con validación de 10 MB
archivoPdf.addEventListener('change', (e) => {
    if (e.target.files.length > 0) {
        const archivo = e.target.files[0];

        if (archivo.size > TAMANO_MAX_BYTES) {
            Swal.fire('Error', 'El archivo excede el tamaño máximo permitido de 10 MB.', 'error');
            e.target.value = ''; // Limpia la selección
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
        Swal.fire('Error', 'El archivo no puede pesar más de 10 MB.', 'error');
        return;
    }

    if (vigenciaSi.checked && !fechaVencimiento.value) {
        Swal.fire('Advertencia', 'Debes elegir una fecha de vigencia', 'warning');
        return;
    }

    const formData = new FormData(this);

    Swal.fire({
        title: 'Subiendo archivo...',
        allowOutsideClick: false,
        didOpen: () => Swal.showLoading()
    });

    fetch(contextPath + '/SubirConstanciaServlet', {
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