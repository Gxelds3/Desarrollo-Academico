/* historial_evento_do.js
 * Carga el historial de eventos del docente autenticado desde /ListarMisEventosServlet
 * Renderiza tabla con 6 columnas: Título, Tipo, Institución, Modalidad, Fecha, Acciones
 * Incluye filtro por tipo (pills) + buscador en tiempo real + paginación
 */

document.addEventListener('DOMContentLoaded', function () {
    const contextPath = window.contextPath || (window.location.pathname.substring(0, window.location.pathname.indexOf("/", 1))) || '';
    const tbody       = document.getElementById('tablaEventosBody');
    const inputBuscar = document.getElementById('buscarEvento');
    const contenedorFiltros    = document.getElementById('contenedorFiltrosTipo');
    const contenedorPaginacion = document.getElementById('paginacionContainer');

    let eventosOriginales = [];
    let filtroTexto = '';
    let filtroTipo  = 'todos';
    let paginaActual = 1;
    const EVENTOS_POR_PAGINA = 8;

    // ── Utilidades ──────────────────────────────────────────────────────────────

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

    /**
     * Convierte "2026-08-20" → "20/08/26"
     */
    function formatearFecha(iso) {
        if (!iso) return '';
        const p = iso.split('-');
        if (p.length !== 3) return iso;
        return p[2] + '/' + p[1] + '/' + p[0].slice(2);
    }

    /**
     * Une las fechas de inicio y fin en un rango legible
     */
    function formatearFechas(fechaInicio, fechaFin) {
        if (!fechaInicio) return 'Sin fecha';
        const fInicio = formatearFecha(fechaInicio);
        if (!fechaFin) return escapeHtml(fInicio);
        const fFin = formatearFecha(fechaFin);
        return `${escapeHtml(fInicio)} - ${escapeHtml(fFin)}`;
    }

    // ── Filtrado ─────────────────────────────────────────────────────────────────

    function obtenerEventosFiltrados() {
        const texto = normalizar(filtroTexto);
        const tipoFiltroNormalizado = normalizar(filtroTipo);

        return eventosOriginales.filter(function (ev) {
            const coincideTexto = texto === '' ||
                normalizar(ev.nombre || ev.titulo).includes(texto) ||
                normalizar(ev.institucion).includes(texto) ||
                normalizar(ev.descripcion || ev.subtitulo).includes(texto);

            const tipoEventoNormalizado = normalizar(ev.tipo || ev.tipoEvento);
            const coincideTipo = (filtroTipo === 'todos') || (tipoEventoNormalizado === tipoFiltroNormalizado);

            return coincideTexto && coincideTipo;
        });
    }

    // ── Renderizado de tabla ──────────────────────────────────────────────────────

    function renderEventos(lista) {
        if (!tbody) return;

        if (!lista || !lista.length) {
            tbody.innerHTML = '<tr><td colspan="6" class="text-center text-muted py-4">No se encontraron eventos registrados.</td></tr>';
            return;
        }

        const sufijoRol = window.sufijoRol || 'do';
        tbody.innerHTML = '';

        lista.forEach(function (evt) {
            const idEvento = evt.id || evt.idEvento;
            const titulo = escapeHtml(evt.titulo || evt.nombre);
            const subtitulo = escapeHtml(evt.subtitulo || evt.descripcion || evt.descripcionCorta || '');
            const tipo = escapeHtml(evt.tipo || evt.tipoEvento || 'Sin especificación');
            const institucion = escapeHtml(evt.institucion || 'N/A');
            const modalidad = escapeHtml(evt.modalidad || 'Presencial');
            const fechas = formatearFechas(evt.fechaInicio, evt.fechaFin);

            const fila = document.createElement('tr');
            fila.setAttribute('data-id', idEvento);

            fila.innerHTML = `
                <td class="text-start">
                    <div class="fw-semibold">${titulo}</div>
                    ${subtitulo ? `<div class="small text-muted">${subtitulo}</div>` : ''}
                </td>
                <td>${tipo}</td>
                <td>${institucion}</td>
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

    // ── Paginación dinámica ───────────────────────────────────────────────────────

    function renderPaginacion(totalPaginas) {
        if (!contenedorPaginacion) return;
        contenedorPaginacion.innerHTML = '';
        if (totalPaginas <= 1) return;

        function crearBtn(contenido, activo, deshabilitado, onClick) {
            var a = document.createElement('a');
            a.href = '#';
            a.className = 'page-btn' + (activo ? ' active' : '') + (deshabilitado ? ' disabled' : '');
            a.innerHTML = contenido;
            if (!deshabilitado) {
                a.addEventListener('click', function (e) {
                    e.preventDefault();
                    onClick();
                });
            }
            return a;
        }

        contenedorPaginacion.appendChild(
            crearBtn('<i class="bi bi-chevron-left"></i>', false, paginaActual === 1, function () {
                paginaActual--;
                aplicarFiltros();
            })
        );

        for (var i = 1; i <= totalPaginas; i++) {
            var mostrar = (i === 1 || i === totalPaginas ||
                (i >= paginaActual - 1 && i <= paginaActual + 1));
            var esDots  = ((i === 2 && paginaActual > 3) ||
                (i === totalPaginas - 1 && paginaActual < totalPaginas - 2));

            if (mostrar) {
                (function (p) {
                    contenedorPaginacion.appendChild(
                        crearBtn(p, p === paginaActual, false, function () {
                            paginaActual = p;
                            aplicarFiltros();
                        })
                    );
                })(i);
            } else if (esDots) {
                var span = document.createElement('span');
                span.className = 'page-btn dots';
                span.textContent = '...';
                contenedorPaginacion.appendChild(span);
            }
        }

        contenedorPaginacion.appendChild(
            crearBtn('<i class="bi bi-chevron-right"></i>', false, paginaActual === totalPaginas, function () {
                paginaActual++;
                aplicarFiltros();
            })
        );
    }

    // ── Aplicar filtros, paginación y rerender ───────────────────────────────────

    function aplicarFiltros() {
        const filtrados = obtenerEventosFiltrados();
        const totalPaginas = Math.ceil(filtrados.length / EVENTOS_POR_PAGINA);

        // Ajustar número de página si desborda tras filtrar
        if (paginaActual > totalPaginas && totalPaginas > 0) {
            paginaActual = totalPaginas;
        }

        const inicio = (paginaActual - 1) * EVENTOS_POR_PAGINA;
        const fin = inicio + EVENTOS_POR_PAGINA;
        const paginaEventos = filtrados.slice(inicio, fin);

        renderEventos(paginaEventos);
        renderPaginacion(totalPaginas);
    }

    // ── Carga desde servidor ──────────────────────────────────────────────────────

    function cargarMisEventos() {
        if (!tbody) return;
        tbody.innerHTML = '<tr><td colspan="6" class="text-center text-muted py-4">Cargando eventos...</td></tr>';

        fetch(contextPath + '/ListarMisEventosServlet?t=' + Date.now(), { credentials: 'same-origin' })
            .then(function (response) {
                if (response.redirected || (response.url && response.url.includes('login.jsp'))) {
                    window.location.href = 'login.jsp';
                    return null;
                }
                var contentType = response.headers.get('content-type');
                if (contentType && contentType.indexOf('application/json') !== -1) {
                    return response.json();
                }
                throw new Error('Respuesta no es JSON');
            })
            .then(function (data) {
                if (!data) return;
                eventosOriginales = data || [];
                paginaActual = 1;
                aplicarFiltros();
            })
            .catch(function (error) {
                console.error('Error al cargar historial de eventos:', error);
                if (tbody) {
                    tbody.innerHTML = '<tr><td colspan="6" class="text-center text-danger py-4">No se pudieron cargar los eventos.</td></tr>';
                }
            });
    }

    // ── Event listeners ───────────────────────────────────────────────────────────

    if (inputBuscar) {
        inputBuscar.addEventListener('input', function () {
            filtroTexto  = this.value.trim();
            paginaActual = 1;
            aplicarFiltros();
        });
    }

    if (contenedorFiltros) {
        contenedorFiltros.addEventListener('click', function (e) {
            var pill = e.target.closest('.nav-pill');
            if (!pill) return;
            e.preventDefault();

            contenedorFiltros.querySelectorAll('.nav-pill').forEach(function (p) {
                p.classList.remove('active');
            });
            pill.classList.add('active');

            filtroTipo   = pill.getAttribute('data-tipo') || 'todos';
            paginaActual = 1;
            aplicarFiltros();
        });
    }

    cargarMisEventos();
});