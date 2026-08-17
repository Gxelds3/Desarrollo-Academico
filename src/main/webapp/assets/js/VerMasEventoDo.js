// Obtiene el contextPath definido dinámicamente desde el HTML/JSP
const contextPath = window.contextPath || (window.location.pathname.substring(0, window.location.pathname.indexOf("/", 1))) || '';
const params = new URLSearchParams(window.location.search);
const idEvento = params.get('id');

const tituloEvento = document.getElementById('tituloEvento');
const campoNombre = document.getElementById('campoNombre');
const campoLugar = document.getElementById('campoLugar');
const campoInstitucion = document.getElementById('campoInstitucion');
const campoTipo = document.getElementById('campoTipo');
const campoDescripcion = document.getElementById('campoDescripcion');
const campoFechaInicio = document.getElementById('campoFechaInicio');
const campoFechaFin = document.getElementById('campoFechaFin');
const campoModalidad = document.getElementById('campoModalidad');

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

function actualizarContadorConstancias(docentes) {
    const elContador = document.getElementById('contadorEntregados');
    const elPorcentaje = document.getElementById('porcentajeEntregados');
    const elBarra = document.getElementById('barraProgreso');

    if (!docentes || docentes.length === 0) {
        if (elContador) elContador.textContent = '0 de 0';
        if (elPorcentaje) elPorcentaje.textContent = '0%';
        if (elBarra) elBarra.style.width = '0%';
        return;
    }

    const total = docentes.length;
    // El backend manda "entregado" como booleano real (calculado con EXISTS sobre CONSTANCIAS)
    const entregados = docentes.filter(function (d) {
        return d.entregado === true;
    }).length;

    const porcentaje = Math.round((entregados / total) * 100);

    if (elContador) elContador.textContent = entregados + ' de ' + total;
    if (elPorcentaje) elPorcentaje.textContent = porcentaje + '%';
    if (elBarra) elBarra.style.width = porcentaje + '%';
}

function renderizarDocentes(docentes) {
    const tbody = document.getElementById('tablaDocentesBody');
    if (!tbody) return;

    if (!docentes || docentes.length === 0) {
        tbody.innerHTML = '<tr><td colspan="4" class="text-center text-muted py-3">No hay docentes asignados a este evento.</td></tr>';
        return;
    }

    tbody.innerHTML = '';
    docentes.forEach(function (docente) {
        const tieneConstancia = docente.entregado === true;
        const nombreCompleto = (docente.nombre || '') + ' ' + (docente.apellidoPaterno || '') + ' ' + (docente.apellidoMaterno || '');
        const iniciales = ((docente.nombre || 'D')[0] + (docente.apellidoPaterno || '')[0]).toUpperCase();

        const activoTexto = Number(docente.activo) === 1 ? 'Activo' : 'Inactivo';

        const fila = document.createElement('tr');
        fila.innerHTML =
            '<td>' +
            '<div class="d-flex align-items-center gap-2">' +
            '<span class="avatar-initials bg-success text-white rounded-circle d-flex align-items-center justify-content-center" style="width: 32px; height: 32px; font-size: 12px;">' + iniciales + '</span>' +
            '<div>' +
            '<div class="fw-semibold">' + (docente.nombre || '') + '</div>' +
            '<div class="small text-muted">' + (docente.apellidoPaterno || '') + ' ' + (docente.apellidoMaterno || '') + '</div>' +
            '</div>' +
            '</div>' +
            '</td>' +
            '<td>' + (docente.correo || '') + '</td>' +
            '<td>' + activoTexto + '</td>' +
            '<td class="text-center">' +
            (tieneConstancia
                ? '<i class="bi bi-eye text-primary" title="Constancia subida"></i>'
                : '<i class="bi bi-eye-slash text-muted" title="Sin constancia"></i>') +
            '</td>';
        tbody.appendChild(fila);
    });
}

function cargarParticipantes() {
    fetch(contextPath + '/ListarParticipantesEventoServlet?id=' + encodeURIComponent(idEvento) + '&t=' + Date.now())
        .then(function (res) { return res.json(); })
        .then(function (docentes) {
            renderizarDocentes(docentes);
            actualizarContadorConstancias(docentes);
        })
        .catch(function (error) {
            console.error('Error al cargar participantes:', error);
        });
}

function cargarEvento() {
    if (!idEvento) {
        Swal.fire({
            icon: 'error',
            title: 'Falta el id del evento',
            text: 'Entra a esta página desde la vista general para poder ver el detalle.',
            confirmButtonColor: '#00847b'
        });
        return;
    }

    fetch(contextPath + '/EditarEventoServlet?id=' + encodeURIComponent(idEvento) + '&t=' + Date.now())
        .then(function (response) { return response.json(); })
        .then(function (data) {
            if (!data.success) {
                Swal.fire({
                    icon: 'error',
                    title: 'No se pudo cargar el evento',
                    text: data.message || 'Ocurrió un error al obtener los datos.',
                    confirmButtonColor: '#00847b'
                });
                return;
            }

            if (tituloEvento) tituloEvento.textContent = (data.nombre || '').toUpperCase();
            if (campoNombre) campoNombre.value = data.nombre || '';
            if (campoLugar) campoLugar.value = data.lugar || '';
            if (campoInstitucion) campoInstitucion.value = data.institucion || '';
            if (campoTipo) campoTipo.value = capitalizar(data.tipo);
            if (campoDescripcion) campoDescripcion.value = data.descripcion || '';
            if (campoFechaInicio) campoFechaInicio.value = aFechaVisible(data.fechaInicio);
            if (campoFechaFin) campoFechaFin.value = aFechaVisible(data.fechaFin);
            if (campoModalidad) campoModalidad.textContent = capitalizar(data.modalidad);

            // Calcular fecha límite (basada en el periodo de carga de la división del evento)
            if (data.fechaLimiteEntrega) {
                const hoy = new Date();
                hoy.setHours(0,0,0,0);
                const partes = data.fechaLimiteEntrega.split('-');
                const limite = new Date(partes[0], partes[1] - 1, partes[2]);

                const difTiempo = limite.getTime() - hoy.getTime();
                const difDias = Math.ceil(difTiempo / (1000 * 3600 * 24));

                const elFechaLimite = document.getElementById('fechaLimiteTexto');
                if (elFechaLimite) elFechaLimite.textContent = aFechaVisible(data.fechaLimiteEntrega);

                const badge = document.getElementById('diasRestantesBadge');
                if (badge) {
                    if (difDias > 0) {
                        badge.textContent = 'Faltan ' + difDias + ' días';
                        badge.className = 'badge-light-green';
                    } else if (difDias === 0) {
                        badge.textContent = 'Vence hoy';
                        badge.className = 'badge text-bg-warning';
                    } else {
                        badge.textContent = 'Vencido hace ' + Math.abs(difDias) + ' días';
                        badge.className = 'badge text-bg-danger';
                    }
                }
            }

            // Los docentes/participantes se cargan aparte (ListarParticipantesEventoServlet)
            cargarParticipantes();
        })
        .catch(function (error) {
            console.error('Error al cargar el evento:', error);
            Swal.fire({
                icon: 'error',
                title: 'Error de conexión',
                text: 'No fue posible comunicarse con el servidor.',
                confirmButtonColor: '#00847b'
            });
        });
}

document.addEventListener('DOMContentLoaded', cargarEvento);