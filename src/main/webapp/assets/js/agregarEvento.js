// =====================================================================
// agregarEvento.js – Lógica para agregar evento con asignación de docentes
// =====================================================================

const contextPath = window.contextPath || '';
const form = document.getElementById('formAgregarEvento');
const btnGuardar = document.getElementById('btnGuardar');

// ---- Estado de docentes ----
let todosLosUsuarios = [];        // Todos los usuarios (docentes/coordinadores)
let docentesAsignados = [];       // Lista local de docentes a asignar (aún no hay evento en BD)
let filtroParticipante = '';

// ---- Helpers ----
function getIdUsuario(u) { return u.idUsuario || u.id; }
function getCorreoUsuario(u) { return u.correoInstitucional || u.correo || ''; }
function escHtml(t) { return String(t || '').replace(/&/g,'&amp;').replace(/</g,'&lt;').replace(/>/g,'&gt;'); }
function normStr(t) { return String(t||'').toLowerCase().normalize('NFD').replace(/[\u0300-\u036f]/g,''); }
function getDivisionName(id) {
    if (!id) return 'N/A';
    switch(Number(id)) {
        case 1: return 'DATID';
        case 2: return 'DACEA';
        case 3: return 'DATEFI';
        case 4: return 'DAMI';
        case 5: return 'General';
        default: return 'N/A';
    }
}
function getNombreCompleto(u) {
    return [u.nombre, u.apellidoPaterno, u.apellidoMaterno].filter(Boolean).join(' ');
}

// ---- Tabla de docentes asignados (local, aún sin evento guardado) ----
const tbodyParticipantes = document.getElementById('tablaParticipantesBody');
const inputBuscarParticipante = document.getElementById('buscarParticipante');
const inputBuscarDocente = document.getElementById('inputBuscarDocente');
const tbodyBusquedaDocentes = document.getElementById('tbodyBusquedaDocentes');
const hiddenContainer = document.getElementById('hiddenDocentesContainer');

function renderTablaAsignados() {
    if (!tbodyParticipantes) return;
    var filtro = normStr(filtroParticipante);
    var lista = docentesAsignados.filter(function(u) {
        return filtro === '' || normStr(getNombreCompleto(u)).indexOf(filtro) !== -1 || normStr(getCorreoUsuario(u)).indexOf(filtro) !== -1;
    });

    if (!lista.length) {
        tbodyParticipantes.innerHTML = '<tr><td colspan="4" class="text-center text-muted py-4">Aún no hay docentes asignados.</td></tr>';
    } else {
        tbodyParticipantes.innerHTML = '';
        lista.forEach(function(u) {
            var userId = getIdUsuario(u);
            var iniciales = ((u.nombre||'').charAt(0)+(u.apellidoPaterno||'').charAt(0)).toUpperCase();
            var estadoColor = u.activo === 1 ? '#28a745' : '#d32f2f';
            var estadoTexto = u.activo === 1 ? 'Activo' : 'Inactivo';
            var tr = document.createElement('tr');
            tr.innerHTML =
                '<td><div class="docente-name-container">' +
                    '<div class="avatar-circle">' + iniciales + '</div>' +
                    '<div class="docente-name">' + escHtml(getNombreCompleto(u)) + '</div>' +
                '</div></td>' +
                '<td>' + escHtml(getCorreoUsuario(u)) + '</td>' +
                '<td><span class="badge" style="background-color:' + estadoColor + ';">' + estadoTexto + '</span></td>' +
                '<td style="white-space: nowrap;">' +
                    '<a href="#" class="action-btn delete btn-quitar" data-id="' + userId + '" title="Quitar">' +
                        '<i class="bi bi-trash"></i>' +
                    '</a>' +
                '</td>';
            tbodyParticipantes.appendChild(tr);
        });
    }

    // Actualizar inputs ocultos para el submit
    if (hiddenContainer) {
        hiddenContainer.innerHTML = '';
        docentesAsignados.forEach(function(u) {
            var inp = document.createElement('input');
            inp.type = 'hidden';
            inp.name = 'docentesSeleccionados';
            inp.value = getIdUsuario(u);
            hiddenContainer.appendChild(inp);
        });
    }
}

if (inputBuscarParticipante) {
    inputBuscarParticipante.addEventListener('input', function() {
        filtroParticipante = this.value;
        renderTablaAsignados();
    });
}

// Quitar docente de la lista local
if (tbodyParticipantes) {
    tbodyParticipantes.addEventListener('click', function(e) {
        var btn = e.target.closest('.btn-quitar');
        if (!btn) return;
        e.preventDefault();
        var idUsuario = Number(btn.getAttribute('data-id'));
        Swal.fire({
            icon: 'warning',
            title: '¿Quitar docente?',
            text: 'Se eliminará de la lista de asignados.',
            showCancelButton: true,
            confirmButtonColor: '#d33',
            cancelButtonColor: '#aaa',
            confirmButtonText: 'Sí, quitar',
            cancelButtonText: 'Cancelar'
        }).then(function(res) {
            if (res.isConfirmed) {
                docentesAsignados = docentesAsignados.filter(function(u) { return Number(getIdUsuario(u)) !== idUsuario; });
                renderTablaAsignados();
                renderBusquedaDocentes(inputBuscarDocente ? inputBuscarDocente.value : '');
            }
        });
    });
}

// ---- Modal buscador ----
function cargarTodosLosUsuarios() {
    fetch(contextPath + '/ListarUsuariosServlet?t=' + Date.now(), { credentials: 'same-origin' })
        .then(function(res) { return res.json(); })
        .then(function(data) { todosLosUsuarios = data || []; })
        .catch(function(err) { console.error('Error al cargar usuarios:', err); });
}

function renderBusquedaDocentes(filtro) {
    if (!tbodyBusquedaDocentes) return;
    var texto = normStr(filtro);
    var asignadosIds = docentesAsignados.map(function(u) { return Number(getIdUsuario(u)); });
    var disponibles = todosLosUsuarios.filter(function(u) {
        return u.activo === 1 && asignadosIds.indexOf(Number(getIdUsuario(u))) === -1;
    });
    var resultados = texto === '' ? disponibles : disponibles.filter(function(u) {
        return normStr(getNombreCompleto(u)).indexOf(texto) !== -1 ||
               normStr(getCorreoUsuario(u)).indexOf(texto) !== -1 ||
               normStr(u.numeroEmpleado||'').indexOf(texto) !== -1;
    });

    if (!resultados.length) {
        tbodyBusquedaDocentes.innerHTML = '<tr><td colspan="4" class="text-center text-muted py-3">No se encontraron docentes disponibles.</td></tr>';
        return;
    }

    var mostrar = resultados.slice(0, 20);
    tbodyBusquedaDocentes.innerHTML = '';
    mostrar.forEach(function(u) {
        var tr = document.createElement('tr');
        var userId = getIdUsuario(u);
        var rol = u.rol || '';
        var rolDisplay = rol.charAt(0).toUpperCase() + rol.slice(1);
        tr.innerHTML =
            '<td>' + escHtml(getNombreCompleto(u)) + '</td>' +
            '<td>' + escHtml(getCorreoUsuario(u)) + '</td>' +
            '<td><span class="badge bg-secondary">' + escHtml(rolDisplay) + '</span></td>' +
            '<td><button type="button" class="btn btn-sm btn-outline-success btn-asignar" data-id="' + userId + '">' +
            '<i class="bi bi-plus-circle me-1"></i>Añadir</button></td>';
        tbodyBusquedaDocentes.appendChild(tr);
    });

    if (resultados.length > 20) {
        var trMore = document.createElement('tr');
        trMore.innerHTML = '<td colspan="4" class="text-center text-muted small py-2">Mostrando 20 de ' + resultados.length + '. Refina tu búsqueda.</td>';
        tbodyBusquedaDocentes.appendChild(trMore);
    }
}

if (inputBuscarDocente) {
    inputBuscarDocente.addEventListener('input', function() { renderBusquedaDocentes(this.value); });
}

var modalAsignar = document.getElementById('modalAsignarDocente');
if (modalAsignar) {
    modalAsignar.addEventListener('shown.bs.modal', function() {
        if (inputBuscarDocente) { inputBuscarDocente.value = ''; inputBuscarDocente.focus(); }
        renderBusquedaDocentes('');
    });
}

// Click en "Añadir" dentro del modal
if (tbodyBusquedaDocentes) {
    tbodyBusquedaDocentes.addEventListener('click', function(e) {
        var btn = e.target.closest('.btn-asignar');
        if (!btn) return;
        e.preventDefault();
        var idUsuario = Number(btn.getAttribute('data-id'));
        var usuario = todosLosUsuarios.find(function(u) { return Number(getIdUsuario(u)) === idUsuario; });
        if (!usuario) return;

        // Agregar a la lista local
        docentesAsignados.push(usuario);
        renderTablaAsignados();
        renderBusquedaDocentes(inputBuscarDocente ? inputBuscarDocente.value : '');
        Swal.fire({ icon: 'success', title: 'Docente agregado', timer: 1000, showConfirmButton: false });
    });
}

// =====================================================================
// SUBMIT DEL FORMULARIO
// =====================================================================
if (form) {
    form.addEventListener('submit', function (e) {
        e.preventDefault();

        const fechaInicioVal = form.querySelector('[name="fechaInicio"]')?.value;
        const fechaFinVal = form.querySelector('[name="fechaFin"]')?.value;

        if (fechaInicioVal && fechaFinVal && fechaFinVal <= fechaInicioVal) {
            Swal.fire({ icon: 'warning', title: 'Fechas inválidas', text: 'La fecha de fin debe ser posterior a la fecha de inicio.', confirmButtonColor: '#00847b' });
            return;
        }

        if (btnGuardar) btnGuardar.disabled = true;

        let porcentaje = 0;
        let timerCarga;

        Swal.fire({
            title: 'Guardando evento...',
            html: '<div style="font-size: 1.5rem; font-weight: bold; color: #00847b; margin-top: 10px;" id="lblPorcentajeEvento">0%</div>',
            allowOutsideClick: false,
            allowEscapeKey: false,
            showConfirmButton: false,
            didOpen: () => {
                Swal.showLoading();
                timerCarga = setInterval(() => {
                    if (porcentaje < 90) {
                        porcentaje += 10;
                        const el = document.getElementById('lblPorcentajeEvento');
                        if (el) el.textContent = porcentaje + '%';
                    }
                }, 80);
            }
        });

        // Armar FormData (incluye los hidden inputs de docentesSeleccionados)
        const formData = new FormData(form);
        const datos = new URLSearchParams();
        for (const pair of formData) {
            datos.append(pair[0], pair[1]);
        }

        fetch(contextPath + '/AgregarEventoCO', {
            method: 'POST',
            headers: { 'Content-Type': 'application/x-www-form-urlencoded; charset=UTF-8' },
            body: datos.toString(),
            credentials: 'same-origin'
        })
        .then(function (response) {
            return response.json().then(function (data) {
                return { ok: response.ok, data: data };
            });
        })
        .then(function (resultado) {
            clearInterval(timerCarga);
            const el = document.getElementById('lblPorcentajeEvento');
            if (el) el.textContent = '100%';

            setTimeout(function () {
                if (resultado.ok && resultado.data.success) {
                    Swal.fire({
                        icon: 'success',
                        title: '¡Evento Registrado con Éxito!',
                        text: resultado.data.message || 'El evento se ha guardado correctamente.',
                        confirmButtonColor: '#00847b',
                        confirmButtonText: 'Aceptar'
                    }).then(function (result) {
                        if (result.isConfirmed) {
                            window.location.href = window.location.pathname.includes('_de') ? 'gestion_eventos_de.jsp' : 'gestion_evento_co.jsp';
                        }
                    });
                } else {
                    Swal.fire({ icon: 'error', title: 'No se pudo guardar el evento', text: resultado.data.message || 'Ocurrió un error al conectar con la base de datos.', confirmButtonColor: '#00847b' });
                    if (btnGuardar) btnGuardar.disabled = false;
                }
            }, 300);
        })
        .catch(function (error) {
            clearInterval(timerCarga);
            console.error('Error al registrar el evento:', error);
            Swal.fire({ icon: 'error', title: 'Error de conexión', text: 'No fue posible comunicarse con el servidor.', confirmButtonColor: '#00847b' });
            if (btnGuardar) btnGuardar.disabled = false;
        });
    });
}

// Cargar usuarios al inicio
cargarTodosLosUsuarios();