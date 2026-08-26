/**
 * historial_evento.js
 *
 * Lógica de la vista de historial de eventos: búsqueda/filtros, paginación y renderizado de la tabla de eventos pasados.
 */

﻿const contextPath = window.contextPath || '';
const tbody = document.getElementById('tablaEventosBody');
const inputBuscar = document.getElementById('buscarEvento');
const filtrosTipo = document.getElementById('filtrosTipo');
const contenedorPaginacion = document.getElementById('paginacionContainer');

let eventosOriginales = [];
let filtroTexto = '';
let filtroTipo = 'todos';

// ConfiguraciÃ³n de PaginaciÃ³n
let paginaActual = 1;
const eventosPorPagina = 5; // Cambia este valor segÃºn cuÃ¡ntos registros quieras ver por pÃ¡gina

/**
 * Escapa los caracteres especiales de HTML (&, <, >) de un texto para insertarlo de forma segura en el DOM y prevenir inyección de HTML/XSS.
 * @param {*} texto
 */
function escapeHtml(texto) {
    if (texto === null || texto === undefined) return '';
    return String(texto)
        .replace(/&/g, '&amp;')
        .replace(/</g, '&lt;')
        .replace(/>/g, '&gt;');
}

/**
 * Normaliza un texto a minúsculas y sin acentos/diacríticos, para poder comparar cadenas de forma insensible a mayúsculas y tildes (usado en buscadores/filtros).
 * @param {*} texto
 */
function normalizar(texto) {
    return String(texto || '')
        .toLowerCase()
        .normalize('NFD')
        .replace(/[\u0300-\u036f]/g, '');
}

/**
 * Convierte una fecha en formato ISO a un formato de fecha legible para mostrar en la interfaz.
 * @param {*} fechaIso
 */
function formatearFecha(fechaIso) {
    if (!fechaIso) return '';
    const partes = fechaIso.split('-');
    if (partes.length !== 3) return fechaIso;
    return partes[2] + '/' + partes[1] + '/' + partes[0].slice(2);
}

/**
 * Devuelve la lista de eventos que coinciden con el texto de búsqueda / filtros actuales (filtrado en memoria sobre la lista maestra).
 */
function obtenerEventosFiltrados() {
    const texto = normalizar(filtroTexto);

    const filtrados = eventosOriginales.filter(function (ev) {
        const coincideTipo = filtroTipo === 'todos' || normalizar(ev.tipo) === filtroTipo;
        const coincideTexto = texto === '' || normalizar(ev.nombre).includes(texto);
        return coincideTipo && coincideTexto;
    });

    

    return filtrados;
}

// Renderiza los registros correspondientes a la pÃ¡gina activa
/**
 * Renderiza en el DOM la tabla/listado de eventos a partir de la lista recibida.
 * @param {*} eventos
 */
function renderEventos(eventos) {
    if (!eventos.length) {
        tbody.innerHTML = '<tr><td colspan="5" class="text-center text-muted py-4">No se encontraron eventos.</td></tr>';
        renderPaginacion(0);
        return;
    }

    // LÃ³gica para cortar la lista segÃºn la pÃ¡gina actual
    const totalPaginas = Math.ceil(eventos.length / eventosPorPagina);
    if (paginaActual > totalPaginas) paginaActual = 1;

    const inicio = (paginaActual - 1) * eventosPorPagina;
    const fin = inicio + eventosPorPagina;
    const eventosPÃ¡gina = eventos.slice(inicio, fin);

    tbody.innerHTML = '';
    eventosPÃ¡gina.forEach(function (ev) {
        const fila = document.createElement('tr');
        fila.setAttribute('data-id', ev.id);
        fila.innerHTML =
            '<td class="text-start">' +
            '<div class="fw-semibold">' + escapeHtml(ev.nombre) + '</div>' +
            '<div class="small text-muted">' + escapeHtml(ev.descripcion) + '</div>' +
            '</td>' +
            '<td>' + escapeHtml(ev.tipo) + '</td>' +
            '<td>' +
            '<div>' + escapeHtml(ev.institucion) + '</div>' +
            '<div class="small text-muted">' + escapeHtml(ev.lugar) + '</div>' +
            '</td>' +
            '<td>' + formatearFecha(ev.fechaInicio) + ' - ' + formatearFecha(ev.fechaFin) + '</td>' +
            '<td>' +
            '<a href="ver_mas_evento_de.jsp?id=' + ev.id + '" class="action-btn"><i class="bi bi-eye"></i></a>' +
            '</td>';
        tbody.appendChild(fila);
    });

    renderPaginacion(totalPaginas);
}

// Renderiza los botones dinÃ¡micos de paginaciÃ³n
/**
 * Genera y renderiza en el DOM los controles de paginación según el total de registros/páginas.
 * @param {*} totalPaginas
 */
function renderPaginacion(totalPaginas) {
    if (!contenedorPaginacion) return;
    contenedorPaginacion.innerHTML = '';

    if (totalPaginas <= 1) return; // Si es 1 pÃ¡gina o menos, oculta los botones

    // BotÃ³n Anterior
    const btnAnt = document.createElement('a');
    btnAnt.href = '#';
    btnAnt.className = 'page-btn' + (paginaActual === 1 ? ' disabled' : '');
    btnAnt.innerHTML = '<i class="bi bi-chevron-left"></i>';
    btnAnt.addEventListener('click', function (e) {
        e.preventDefault();
        if (paginaActual > 1) {
            paginaActual--;
            aplicarFiltros();
        }
    });
    contenedorPaginacion.appendChild(btnAnt);

    // Renderizado numerado
    for (let i = 1; i <= totalPaginas; i++) {
        if (
            i === 1 ||
            i === totalPaginas ||
            (i >= paginaActual - 1 && i <= paginaActual + 1)
        ) {
            const btnPage = document.createElement('a');
            btnPage.href = '#';
            btnPage.className = 'page-btn' + (i === paginaActual ? ' active' : '');
            btnPage.textContent = i;
            btnPage.addEventListener('click', function (e) {
                e.preventDefault();
                paginaActual = i;
                aplicarFiltros();
            });
            contenedorPaginacion.appendChild(btnPage);
        } else if (
            (i === 2 && paginaActual > 3) ||
            (i === totalPaginas - 1 && paginaActual < totalPaginas - 2)
        ) {
            const dots = document.createElement('span');
            dots.className = 'page-btn dots';
            dots.textContent = '...';
            contenedorPaginacion.appendChild(dots);
        }
    }

    // BotÃ³n Siguiente
    const btnSig = document.createElement('a');
    btnSig.href = '#';
    btnSig.className = 'page-btn' + (paginaActual === totalPaginas ? ' disabled' : '');
    btnSig.innerHTML = '<i class="bi bi-chevron-right"></i>';
    btnSig.addEventListener('click', function (e) {
        e.preventDefault();
        if (paginaActual < totalPaginas) {
            paginaActual++;
            aplicarFiltros();
        }
    });
    contenedorPaginacion.appendChild(btnSig);
}

/**
 * Aplica los filtros de búsqueda vigentes sobre la lista maestra y vuelve a renderizar la tabla con el resultado.
 */
function aplicarFiltros() {
    window.renderPaginator(obtenerEventosFiltrados(), 20, 'paginationContainer', renderEventos);
}

/**
 * Obtiene del servidor la lista de eventos y la muestra en la tabla/listado, aplicando el filtro de búsqueda vigente.
 */
function cargarEventos() {
    let url = contextPath + '/ListarMisEventosServlet';

    fetch(url)
        .then(function (response) {
            const contentType = response.headers.get("content-type");
            if (contentType && contentType.indexOf("application/json") !== -1) {
                return response.json();
            } else {
                throw new Error("El servidor no devolviÃ³ un JSON.");
            }
        })
        .then(function (eventos) {
            eventosOriginales = eventos || [];
            paginaActual = 1;
            aplicarFiltros();
        })
        .catch(function (error) {
            console.error('Error al cargar eventos:', error);
            tbody.innerHTML = '<tr><td colspan="5" class="text-center text-danger py-4">No se pudieron cargar los eventos. Revisa tu servidor.</td></tr>';
        });
}

if (inputBuscar) {
    inputBuscar.addEventListener('input', function () {
        filtroTexto = inputBuscar.value;
        paginaActual = 1; // Reinicia a la pÃ¡gina 1 al buscar
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
        paginaActual = 1; // Reinicia a la pÃ¡gina 1 al cambiar de filtro
        aplicarFiltros();
    });
}

cargarEventos();
