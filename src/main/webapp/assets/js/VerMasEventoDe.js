/**
 * VerMasEventoDe.js
 *
 * Lógica de la vista de detalle de un evento para el rol Desarrollador: carga del evento y de sus participantes.
 */

const contextPath = window.contextPath || '';
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

// Convierte "yyyy-MM-dd" (formato que maneja el servidor) a "dd/mm/yy" (formato que usa esta vista)
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
 * Obtiene del servidor la información del evento actual (según el id de la URL) y la muestra en la vista.
 */
function cargarEvento() {
    if (!idEvento) {
        Swal.fire({
            icon: 'error',
            title: 'Falta el id del evento',
            text: 'Entra a esta página desde "Gestión de Eventos" para poder ver el detalle.',
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

            tituloEvento.textContent = (data.nombre || '').toUpperCase();
            campoNombre.value = data.nombre || '';
            campoLugar.value = data.lugar || '';
            campoInstitucion.value = data.institucion || '';
            campoTipo.value = capitalizar(data.tipo);
            campoDescripcion.value = data.descripcion || '';
            campoFechaInicio.value = aFechaVisible(data.fechaInicio);
            campoFechaFin.value = aFechaVisible(data.fechaFin);
            campoModalidad.textContent = capitalizar(data.modalidad);

            // Calcular fecha límite (basada en el periodo de carga de la división del evento)
            if (data.fechaLimiteEntrega) {
                const hoy = new Date();
                hoy.setHours(0,0,0,0);
                const partes = data.fechaLimiteEntrega.split('-');
                const limite = new Date(partes[0], partes[1] - 1, partes[2]);

                const difTiempo = limite.getTime() - hoy.getTime();
                const difDias = Math.ceil(difTiempo / (1000 * 3600 * 24));

                document.getElementById('fechaLimiteTexto').textContent = aFechaVisible(data.fechaLimiteEntrega);
                const badge = document.getElementById('diasRestantesBadge');

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

/**
 * Obtiene del servidor la lista de participantes/docentes asociados al evento y los muestra en la vista.
 */
function cargarParticipantes() {
    fetch(contextPath + '/ListarParticipantesEventoServlet?id=' + encodeURIComponent(idEvento) + '&t=' + Date.now())
        .then(res => res.json())
        .then(participantes => {
            const tbody = document.getElementById('tablaDocentesBody');
            tbody.innerHTML = '';

            let entregados = 0;
            let total = participantes.length;

            if (total === 0) {
                tbody.innerHTML = '<tr><td colspan="4">No hay docentes asignados a este evento.</td></tr>';
            } else {
                participantes.forEach(p => {
                    if (p.entregado) entregados++;

                    const tr = document.createElement('tr');
                    const statusClass = p.activo === 1 ? 'status-active' : 'text-danger';
                    const statusText = p.activo === 1 ? 'Activo' : 'Inactivo';
                    const iniciales = (p.nombre.charAt(0) + p.apellidoPaterno.charAt(0)).toUpperCase();
                    let entregadoBtn = `<a href="${contextPath}/cargar_archivo_de.jsp?id=${idEvento}&idUsuarioTarget=${p.id}" class="action-btn" title="Ver espacio del docente"><i class="bi ${p.entregado ? 'bi-eye' : 'bi-eye-slash'}"></i></a>`;
                    tr.innerHTML =
                        '<td class="text-start">' +
                        '<div class="docente-name-container">' +
                        '<div class="avatar-circle">' + iniciales + '</div>' +
                        '<div class="docente-name">' +
                        p.nombre + '<br>' + p.apellidoPaterno + ' ' + (p.apellidoMaterno || '') +
                        '</div>' +
                        '</div>' +
                        '</td>' +
                        '<td>' + (p.correo || '') + '</td>' +
                        '<td class="' + statusClass + '">' + statusText + '</td>' +
                        '<td>' + entregadoBtn + '</td>';
                    tbody.appendChild(tr);
                });
            }

            // Actualizar gráfica circular de porcentaje
            document.getElementById('documentosEntregadosTexto').textContent = entregados + ' de ' + total;
            let porcentaje = total > 0 ? Math.round((entregados / total) * 100) : 0;

            document.getElementById('barraProgresoFill').style.width = porcentaje + '%';
            document.getElementById('porcentajeTexto').textContent = porcentaje + '%';
        })
        .catch(error => console.error("Error al cargar participantes:", error));
}

cargarEvento();