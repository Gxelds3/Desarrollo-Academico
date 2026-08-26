/**
 * MisEventosDe.js
 *
 * Lógica de la vista 'Mis eventos' para el rol Desarrollador: carga, filtrado y renderizado de los eventos asociados al usuario en sesión.
 */

﻿// MisEventos.js - Carga los eventos del usuario autenticado desde la BD
const contextPathMisEventos = window.contextPath || '';
const tbodyMisEventos = document.getElementById('tablaMisEventosBody');
const inputBuscarMisEventos = document.getElementById('buscarMisEventos');

let misEventosOriginales = [];
let filtroTextoMisEventos = '';

/**
 * Escapa los caracteres especiales de HTML (&, <, >) de un texto para insertarlo de forma segura en el DOM y prevenir inyección de HTML/XSS.
 * @param {*} t
 */
function escHtmlEv(t) {
    if (t === null || t === undefined) return '';
    return String(t).replace(/&/g, '&amp;').replace(/</g, '&lt;').replace(/>/g, '&gt;');
}

/**
 * Normaliza un texto a minúsculas y sin acentos/diacríticos, para poder comparar cadenas de forma insensible a mayúsculas y tildes (usado en buscadores/filtros).
 * @param {*} t
 */
function normEv(t) {
    return String(t || '').toLowerCase().normalize('NFD').replace(/[\u0300-\u036f]/g, '');
}

/**
 * Convierte una fecha en formato ISO a un formato de fecha legible para mostrar en la interfaz.
 * @param {*} fechaIso
 */
function formatFechaMisEv(fechaIso) {
    if (!fechaIso) return '';
    const partes = fechaIso.split('-');
    if (partes.length !== 3) return fechaIso;
    return partes[2] + '/' + partes[1] + '/' + partes[0].slice(2);
}

/**
 * Renderiza en el DOM el listado de eventos del usuario en sesión a partir de la lista recibida.
 * @param {*} lista
 */
function renderMisEventos(lista) {
    if (!tbodyMisEventos) return;
    if (!lista.length) {
        tbodyMisEventos.innerHTML = '<tr><td colspan="5" class="text-center text-muted py-4">No tienes eventos asignados.</td></tr>';
        return;
    }

    tbodyMisEventos.innerHTML = '';
    lista.forEach(function (ev) {
        const urlVer = window.urlVerEvento ? window.urlVerEvento + '?id=' + ev.id : '#';
        const urlCargar = window.urlCargarArchivo ? window.urlCargarArchivo + '?id=' + ev.id : '#';

        const tr = document.createElement('tr');
        tr.innerHTML =
            '<td>' +
            '  <div class="fw-semibold">' + escHtmlEv(ev.nombre) + '</div>' +
            '  <div class="small text-muted">' + escHtmlEv(ev.descripcion) + '</div>' +
            '</td>' +
            '<td>' + escHtmlEv(ev.tipo) + '</td>' +
            '<td>' +
            '  <div>' + escHtmlEv(ev.institucion) + '</div>' +
            '  <div class="small text-muted">' + escHtmlEv(ev.lugar) + '</div>' +
            '</td>' +
            '<td>' + formatFechaMisEv(ev.fechaInicio) + ' - ' + formatFechaMisEv(ev.fechaFin) + '</td>' +
            '<td>' +
            '  <a href="' + urlVer + '" class="action-btn"><i class="bi bi-eye"></i></a>' +
            '</td>';
        tbodyMisEventos.appendChild(tr);
    });
}

/**
 * Aplica el filtro de búsqueda vigente sobre la lista de eventos del usuario y vuelve a renderizar el listado.
 */
function aplicarFiltrosMisEventos() {
    const texto = normEv(filtroTextoMisEventos);
    const filtrados = misEventosOriginales.filter(function (ev) {
        return texto === '' || normEv(ev.nombre).includes(texto) || normEv(ev.tipo).includes(texto);
    });
    window.renderPaginator(filtrados, 20, 'paginationContainer', renderMisEventos);
}

/**
 * Obtiene del servidor los eventos asociados al usuario en sesión y los renderiza en la vista.
 */
function cargarMisEventos() {
    fetch(contextPathMisEventos + '/ListarEventosDe')
        .then(function (res) {
            if (!res.ok) throw new Error('Error de servidor: ' + res.status);
            return res.json();
        })
        .then(function (data) {
            misEventosOriginales = data || [];
            aplicarFiltrosMisEventos();
        })
        .catch(function (err) {
            console.error('Error al cargar mis eventos:', err);
            if (tbodyMisEventos) {
                tbodyMisEventos.innerHTML = '<tr><td colspan="5" class="text-center text-danger py-4">No se pudieron cargar los eventos.</td></tr>';
            }
        });
}

if (inputBuscarMisEventos) {
    inputBuscarMisEventos.addEventListener('input', function () {
        filtroTextoMisEventos = inputBuscarMisEventos.value;
        aplicarFiltrosMisEventos();
    });
}

if (tbodyMisEventos) {
    cargarMisEventos();
}
