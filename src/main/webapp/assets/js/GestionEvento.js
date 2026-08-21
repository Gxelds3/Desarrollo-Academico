// 1. CORREGIDO: "const" en lugar de "onst"
const contextPath = window.contextPath || '';
const tbody = document.getElementById('tablaEventosBody');
const inputBuscar = document.getElementById('buscarEvento');
const filtrosTipo = document.getElementById('filtrosTipo');

let eventosOriginales = [];
let filtroTexto = '';
let filtroTipo = 'todos';

// Detectar si estamos en vista de desarrollador (_de.jsp) o coordinador (_co.jsp)
const esDesarrollador = window.location.pathname.includes('_de.jsp');
const sufijoRol = esDesarrollador ? '_de.jsp' : '_co.jsp';
// Si es desarrollador son 6 columnas, si es coordinador son 5 columnas
const totalColumnas = esDesarrollador ? 6 : 5;

const COLORES_DIVISION = {
    'DATID': '#007BFF',
    'DATEFI': '#32CD32',
    'DAMI': '#DC143C',
    'DACEA': '#DA70D6',
    'GENERAL': '#6c757d'
};

function obtenerColorDivision(nombreDivision) {
    const clave = (nombreDivision || '').toString().toUpperCase().trim();
    return COLORES_DIVISION[clave] || '#adb5bd';
}

function obtenerColorDivision(nombreDivision) {
    return COLORES_DIVISION[nombreDivision] || '#adb5bd';
}

function obtenerColorTexto(colorFondoHex) {
    const clarosSinContrasteBlanco = ['#CCFF00', '#00FFFF'];
    return clarosSinContrasteBlanco.includes(colorFondoHex) ? '#212529' : '#ffffff';
}

function escapeHtml(texto) {
    if (texto === null || texto === undefined) return '';
    return String(texto)
        .replace(/&/g, '&amp;')
        .replace(/</g, '&lt;')
        .replace(/>/g, '&gt;');
}

function normalizar(texto) {
    return String(texto || '')
        .toLowerCase()
        .normalize('NFD')
        .replace(/[\u0300-\u036f]/g, '');
}

function formatearFecha(fechaIso) {
    if (!fechaIso) return '';
    const partes = fechaIso.split('-');
    if (partes.length !== 3) return fechaIso;
    return partes[2] + '/' + partes[1] + '/' + partes[0].slice(2);
}

function obtenerEventosFiltrados() {
    const texto = normalizar(filtroTexto);

    const filtrados = eventosOriginales.filter(function (ev) {
        const coincideTipo = filtroTipo === 'todos' || normalizar(ev.tipo) === filtroTipo;
        const coincideTexto = texto === '' || normalizar(ev.nombre).includes(texto);
        return coincideTipo && coincideTexto;
    });

    

    return filtrados;
}

function renderEventos(eventos) {
    if (!eventos.length) {
        tbody.innerHTML = '<tr><td colspan="' + totalColumnas + '" class="text-center text-muted py-4">No se encontraron eventos.</td></tr>';
        return;
    }

    tbody.innerHTML = '';
    eventos.forEach(function (ev) {
        const fila = document.createElement('tr');
        fila.setAttribute('data-id', ev.id);

        let celdas =
            '<td>' +
            '<div class="fw-semibold">' + escapeHtml(ev.nombre) + '</div>' +
            '<div class="small text-muted">' + escapeHtml(ev.descripcion) + '</div>' +
            '</td>' +
            '<td>' + escapeHtml(ev.tipo) + '</td>' +
            '<td>' +
            '<div>' + escapeHtml(ev.institucion) + '</div>' +
            '<div class="small text-muted">' + escapeHtml(ev.lugar) + '</div>' +
            '</td>' +
            '<td>' + formatearFecha(ev.fechaInicio) + ' - ' + formatearFecha(ev.fechaFin) + '</td>';

        if (esDesarrollador) {
            const nombreDivision = ev.nombreDivision || 'General';
            const colorDivision = obtenerColorDivision(nombreDivision);
            const colorTextoDivision = obtenerColorTexto(colorDivision);
            celdas += '<td><span class="badge" style="background-color:' + colorDivision + '; color:' + colorTextoDivision + '; padding:5px 12px; border-radius:12px; font-weight:500;">' + escapeHtml(nombreDivision) + '</span></td>';
        }

        celdas +=
            '<td>' +
            '<a href="' + contextPath + '/editar_evento' + sufijoRol + '?id=' + ev.id + '" class="action-btn" title="Editar Evento"><i class="bi bi-pencil"></i></a> ' +
            '<a href="' + contextPath + '/ver_mas_evento' + sufijoRol + '?id=' + ev.id + '" class="action-btn" title="Ver Evento"><i class="bi bi-eye"></i></a> ' +
            '<a href="#" class="action-btn delete btn-eliminar-evento" data-id="' + ev.id + '" title="Eliminar Evento"><i class="bi bi-trash"></i></a>' +
            '</td>';

        fila.innerHTML = celdas;
        tbody.appendChild(fila);
    });
}

function aplicarFiltros() {
    window.renderPaginator(obtenerEventosFiltrados(), 20, 'paginationContainer', renderEventos);
}

function cargarEventos() {
    fetch(contextPath + '/ListarEventosServlet', { credentials: 'same-origin' })
        .then(function (response) {
            if (response.redirected || (response.url && response.url.includes('login.jsp'))) {
                window.location.href = contextPath + '/login.jsp';
                return null;
            }
            const contentType = response.headers.get("content-type");
            if (contentType && contentType.indexOf("application/json") !== -1) {
                return response.json();
            } else {
                throw new Error("El servidor no devolviÃ³ un JSON.");
            }
        })
        .then(function (eventos) {
            if (!eventos) return;
            if (eventos.error) {
                window.location.href = contextPath + '/login.jsp';
                return;
            }
            eventosOriginales = eventos || [];
            aplicarFiltros();
        })
        .catch(function (error) {
            console.error('Error al cargar eventos:', error);
            tbody.innerHTML = '<tr><td colspan="' + totalColumnas + '" class="text-center text-danger py-4">No se pudieron cargar los eventos. Revisa tu servidor.</td></tr>';
        });
}

if (inputBuscar) {
    inputBuscar.addEventListener('input', function () {
        filtroTexto = inputBuscar.value;
        aplicarFiltros();
    });
}

if (filtrosTipo) {
    filtrosTipo.addEventListener('click', function (e) {
        const pill = e.target.closest('.nav-pill');
        if (!pill) return;
        e.preventDefault();

        filtrosTipo.querySelectorAll('.nav-pill').forEach(function (p) {
            p.classList.remove('active');
        });
        pill.classList.add('active');

        filtroTipo = pill.getAttribute('data-tipo') || 'todos';
        aplicarFiltros();
    });
}

if (tbody) {
    tbody.addEventListener('click', function (e) {
        const boton = e.target.closest('.action-btn.delete');
        if (!boton) return;
        e.preventDefault();

        const id = boton.getAttribute('data-id');

        Swal.fire({
            icon: 'warning',
            title: 'Â¿Deseas eliminar este evento?',
            text: 'Esta acciÃ³n no se puede deshacer.',
            showCancelButton: true,
            confirmButtonColor: '#00847b',
            cancelButtonColor: '#aaaaaa',
            confirmButtonText: 'SÃ­, eliminar',
            cancelButtonText: 'Cancelar'
        }).then(function (result) {
            if (!result.isConfirmed) return;

            const datos = new FormData();
            datos.append('id', id);

            fetch(contextPath + '/EliminarEventoServlet', {
                method: 'POST',
                body: datos
            })
                .then(function (response) {
                    const contentType = response.headers.get("content-type");
                    if (contentType && contentType.indexOf("application/json") !== -1) {
                        return response.json().then(function (data) {
                            return { ok: response.ok, data: data };
                        });
                    } else {
                        throw new Error("El servidor devolviÃ³ un error HTML al intentar eliminar.");
                    }
                })
                .then(function (resultado) {
                    if (resultado.ok && resultado.data.success) {
                        Swal.fire({
                            icon: 'success',
                            title: 'Evento eliminado',
                            text: 'El evento se eliminÃ³ correctamente.',
                            confirmButtonColor: '#00847b'
                        });
                        cargarEventos();
                    } else {
                        Swal.fire({
                            icon: 'error',
                            title: 'No se pudo eliminar',
                            text: resultado.data.message || 'OcurriÃ³ un error al eliminar el evento.',
                            confirmButtonColor: '#00847b'
                        });
                    }
                })
                .catch(function (error) {
                    console.error('Error al eliminar el evento:', error);
                    Swal.fire({
                        icon: 'error',
                        title: 'Error de conexiÃ³n',
                        text: 'No fue posible comunicarse con el servidor.',
                        confirmButtonColor: '#00847b'
                    });
                });
        });
    });

    cargarEventos();
}
