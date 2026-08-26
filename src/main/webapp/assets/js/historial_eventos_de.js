/**
 * historial_eventos_de.js
 *
 * Lógica de la vista de historial de eventos para el rol Desarrollador.
 */

﻿document.addEventListener("DOMContentLoaded", function () {
    const contextPath = window.contextPath || (window.location.pathname.substring(0, window.location.pathname.indexOf("/", 1))) || '';
    const tbody = document.getElementById('tablaEventosBody');
    const inputBuscar = document.getElementById('buscarEvento');
    const contenedorFiltros = document.getElementById('contenedorFiltrosTipo');

    let eventosOriginales = [];
    let filtroTexto = '';
    let filtroTipo = 'todos';

    // SanitizaciÃ³n contra inyecciÃ³n HTML (XSS)
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

    // Normalizador para busqueda insensible de acentos/mayusculas
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

    // Formateador individual de fecha (YYYY-MM-DD a DD/MM/YY)
    /**
     * Convierte una fecha en formato ISO ("AAAA-MM-DD") a formato corto legible ("DD/MM/AA").
     * @param {*} fechaIso
     */
    function formatearFechaIndividual(fechaIso) {
        if (!fechaIso) return '';
        const partes = fechaIso.split('-');
        if (partes.length !== 3) return fechaIso;
        return partes[2] + '/' + partes[1] + '/' + partes[0].slice(2);
    }

    // Formateador de rangos de fecha
    /**
     * Une la fecha de inicio y (opcionalmente) la de fin en un texto de rango legible.
     * @param {*} fechaInicio
     * @param {*} fechaFin
     */
    function formatearFechas(fechaInicio, fechaFin) {
        if (!fechaInicio) return 'Sin fecha';
        const fInicio = formatearFechaIndividual(fechaInicio);
        if (!fechaFin) return escapeHtml(fInicio);
        const fFin = formatearFechaIndividual(fechaFin);
        return `${escapeHtml(fInicio)} - ${escapeHtml(fFin)}`;
    }

    // Filtrado combinado: Texto de busqueda + Filtro de CategorÃ­a/Pill
    /**
     * Devuelve la lista de eventos que coinciden con el texto de búsqueda / filtros actuales (filtrado en memoria sobre la lista maestra).
     */
    function obtenerEventosFiltrados() {
        const texto = normalizar(filtroTexto);
        const tipoFiltroNormalizado = normalizar(filtroTipo);

        return eventosOriginales.filter(function (evt) {
            const coincideTexto = normalizar(evt.titulo || evt.nombre).includes(texto) ||
                normalizar(evt.subtitulo || evt.descripcion).includes(texto) ||
                normalizar(evt.institucion).includes(texto) ||
                normalizar(evt.modalidad).includes(texto);

            const tipoEventoNormalizado = normalizar(evt.tipo || evt.tipoEvento);
            const coincideTipo = (filtroTipo === 'todos') || (tipoEventoNormalizado === tipoFiltroNormalizado);

            return coincideTexto && coincideTipo;
        });
    }

    // Renderizado dinÃ¡mico de la tabla (6 Columnas)
    /**
     * Renderiza en el DOM la tabla/listado de eventos a partir de la lista recibida.
     * @param {*} lista
     */
    function renderEventos(lista) {
        if (!tbody) return;

        if (!lista || !lista.length) {
            tbody.innerHTML = '<tr><td colspan="6" class="text-center text-muted py-4">No se encontraron eventos registrados.</td></tr>';
            return;
        }

        const sufijoRol = window.sufijoRol || 'de';
        tbody.innerHTML = '';

        lista.forEach(function (evt) {
            const idEvento = evt.id || evt.idEvento;
            const titulo = escapeHtml(evt.titulo || evt.nombre);
            const subtitulo = escapeHtml(evt.subtitulo || evt.descripcion || '');
            const tipo = escapeHtml(evt.tipo || evt.tipoEvento || 'Sin especificaciÃ³n');
            const institucion = escapeHtml(evt.institucion || 'N/A');
            const modalidad = escapeHtml(evt.modalidad || 'Presencial');
            const fechas = formatearFechas(evt.fechaInicio, evt.fechaFin);

            const fila = document.createElement('tr');
            fila.setAttribute('data-id', idEvento);

            // Coincide exactamente con las 6 columnas del <thead> y <colgroup>
            fila.innerHTML = `
                <td class="text-start">
                    <div class="fw-semibold truncate-1-line" title="${titulo}">${titulo}</div>
                    ${subtitulo ? `<div class="small text-muted truncate-1-line" title="${subtitulo}">${subtitulo}</div>` : ''}
                </td>
                <td>${tipo}</td>
                <td>
                    <div class="truncate-1-line" title="${institucion}">${institucion}</div>
                </td>
                <td>${modalidad}</td>
                <td>${fechas}</td>
                <td>
                    <a href="${contextPath}/ver_mas_evento_${sufijoRol}.jsp?id=${idEvento}" class="action-btn" title="Ver detalle">
                        <i class="bi bi-eye"></i>
                    </a>
                </td>
            `;
            tbody.appendChild(fila);
        });
    }

    /**
     * Aplica el filtro de búsqueda vigente sobre la lista maestra y vuelve a renderizar la tabla con el resultado.
     */
    function aplicarFiltro() {
        window.renderPaginator(obtenerEventosFiltrados(), 20, 'paginationContainer', renderEventos);
    }

    /**
     * Obtiene del servidor los eventos asociados al usuario en sesión y los renderiza en la vista.
     */
    function cargarMisEventos() {
        if (!tbody) return;
        tbody.innerHTML = '<tr><td colspan="6" class="text-center text-muted py-4">Cargando eventos...</td></tr>';

        fetch(contextPath + '/ListarMisEventosServlet?t=' + Date.now(), { credentials: 'same-origin' })
            .then(function (response) {
                if (response.redirected || (response.url && response.url.includes('login.jsp'))) {
                    window.location.href = 'login.jsp';
                    return null;
                }

                const contentType = response.headers.get("content-type");
                if (contentType && contentType.indexOf("application/json") !== -1) {
                    return response.json();
                } else {
                    throw new Error("Respuesta del servidor no vÃ¡lida (Se esperaba JSON).");
                }
            })
            .then(function (eventos) {
                if (!eventos) return;
                eventosOriginales = eventos || [];
                aplicarFiltro();
            })
            .catch(function (error) {
                console.error('Error al cargar la lista de eventos:', error);
                tbody.innerHTML = '<tr><td colspan="6" class="text-center text-danger py-4">No se pudieron cargar los eventos. Revisa la conexiÃ³n con el servidor.</td></tr>';
            });
    }

    // Escuchadores de eventos para bÃºsqueda y filtros por Pill
    if (inputBuscar) {
        inputBuscar.addEventListener('input', function () {
            filtroTexto = inputBuscar.value.trim();
            aplicarFiltro();
        });
    }

    if (contenedorFiltros) {
        contenedorFiltros.addEventListener('click', function (e) {
            const pill = e.target.closest('.nav-pill');
            if (pill) {
                e.preventDefault();

                contenedorFiltros.querySelectorAll('.nav-pill').forEach(p => p.classList.remove('active'));
                pill.classList.add('active');

                filtroTipo = pill.getAttribute('data-tipo') || 'todos';
                aplicarFiltro();
            }
        });
    }

    // Carga inicial
    cargarMisEventos();
});
