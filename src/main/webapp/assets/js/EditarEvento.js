const contextPath = window.contextPath || '';
const params = new URLSearchParams(window.location.search);
const idEvento = params.get('id');

const form = document.getElementById('formEditarEvento');
const campoNombre = document.getElementById('campoNombre');
const campoLugar = document.getElementById('campoLugar');
const campoInstitucion = document.getElementById('campoInstitucion');
const campoTipo = document.getElementById('campoTipo');
const campoDescripcion = document.getElementById('campoDescripcion');
const campoFechaInicio = document.getElementById('campoFechaInicio');
const campoFechaFin = document.getElementById('campoFechaFin');

// Elementos de Participantes
const tbodyParticipantes = document.getElementById('tablaParticipantesBody');
const inputBuscarParticipante = document.getElementById('buscarParticipante');

// Elementos del modal de búsqueda
const inputBuscarDocente = document.getElementById('inputBuscarDocente');
const tbodyBusquedaDocentes = document.getElementById('tbodyBusquedaDocentes');

let participantesOriginales = [];
let todosLosUsuarios = [];
let filtroParticipante = '';

function getIdUsuario(u) { return u.idUsuario || u.id; }
function getCorreoUsuario(u) { return u.correoInstitucional || u.correo || ''; }
function escHtml(texto) {
    if (!texto) return '';
    return String(texto).replace(/&/g, '&amp;').replace(/</g, '&lt;').replace(/>/g, '&gt;');
}
function normString(t) {
    return String(t || '').toLowerCase().normalize('NFD').replace(/[\u0300-\u036f]/g, '');
}
function getNombreCompleto(u) {
    return [u.nombre, u.apellidoPaterno, u.apellidoMaterno].filter(Boolean).join(' ');
}

// ---- CARGA Y GUARDADO DE EVENTO ----
function cargarEvento() {
    if (!idEvento) {
        Swal.fire({ icon: 'error', title: 'Falta el id del evento', text: 'Entra a esta página desde "Gestión de Eventos".', confirmButtonColor: '#00847b' });
        return;
    }

    fetch(contextPath + '/EditarEventoServlet?id=' + encodeURIComponent(idEvento) + '&t=' + Date.now())
        .then(function(res) { return res.json(); })
        .then(function(data) {
            if (!data.success) {
                Swal.fire({ icon: 'error', title: 'Error', text: data.message || 'No se pudo cargar', confirmButtonColor: '#00847b' });
                return;
            }
            if (campoNombre) campoNombre.value = data.nombre || '';
            if (campoLugar) campoLugar.value = data.lugar || '';
            if (campoInstitucion) campoInstitucion.value = data.institucion || '';
            if (campoDescripcion) campoDescripcion.value = data.descripcion || '';
            if (campoFechaInicio) campoFechaInicio.value = data.fechaInicio || '';
            if (campoFechaFin) campoFechaFin.value = data.fechaFin || '';

            // Tipo de evento
            if (campoTipo && data.tipo) {
                var opciones = campoTipo.querySelectorAll('option');
                for (var i = 0; i < opciones.length; i++) {
                    if (opciones[i].value.toLowerCase() === data.tipo.toLowerCase()) {
                        campoTipo.value = opciones[i].value;
                        break;
                    }
                }
            }

            // Modalidad (radio buttons)
            var radios = document.querySelectorAll('input[name="modalidad"]');
            radios.forEach(function(r) {
                r.checked = (r.value === data.modalidad);
            });

            cargarParticipantes();
            cargarTodosLosUsuarios();
        })
        .catch(function(err) { console.error('Error al cargar evento:', err); });
}

if (form) {
    form.addEventListener('submit', function (e) {
        e.preventDefault();

        var modalidadSeleccionada = document.querySelector('input[name="modalidad"]:checked');
        var datos = new URLSearchParams();
        datos.append('id', idEvento);
        datos.append('nombre', campoNombre ? campoNombre.value : '');
        datos.append('lugar', campoLugar ? campoLugar.value : '');
        datos.append('institucion', campoInstitucion ? campoInstitucion.value : '');
        datos.append('tipo', campoTipo ? campoTipo.value : '');
        datos.append('descripcion', campoDescripcion ? campoDescripcion.value : '');
        datos.append('fechaInicio', campoFechaInicio ? campoFechaInicio.value : '');
        datos.append('fechaFin', campoFechaFin ? campoFechaFin.value : '');
        datos.append('modalidad', modalidadSeleccionada ? modalidadSeleccionada.value : '');

        var porcentaje = 0;
        var timerCarga;

        Swal.fire({
            title: 'Actualizando evento...',
            html: '<div style="font-size: 1.5rem; font-weight: bold; color: #00847b; margin-top: 10px;" id="lblPorcentajeEditEvento">0%</div>',
            allowOutsideClick: false,
            allowEscapeKey: false,
            showConfirmButton: false,
            didOpen: function() {
                Swal.showLoading();
                timerCarga = setInterval(function() {
                    if (porcentaje < 90) {
                        porcentaje += 10;
                        var el = document.getElementById('lblPorcentajeEditEvento');
                        if (el) el.textContent = porcentaje + '%';
                    }
                }, 80);
            }
        });

        fetch(contextPath + '/EditarEventoServlet', {
            method: 'POST',
            headers: { 'Content-Type': 'application/x-www-form-urlencoded; charset=UTF-8' },
            body: datos.toString()
        })
        .then(function(res) { return res.json().then(function(data) { return { ok: res.ok, data: data }; }); })
        .then(function(resultado) {
            clearInterval(timerCarga);
            var el = document.getElementById('lblPorcentajeEditEvento');
            if (el) el.textContent = '100%';

            setTimeout(function() {
                if (resultado.ok && resultado.data.success) {
                    Swal.fire({
                        icon: 'success',
                        title: '¡Evento actualizado con éxito!',
                        text: resultado.data.message || 'Los cambios se guardaron correctamente.',
                        confirmButtonColor: '#00847b'
                    }).then(function(r) {
                        if (r.isConfirmed) {
                            var esDesarrollador = window.location.pathname.indexOf('_de.jsp') !== -1;
                            window.location.href = esDesarrollador ? 'gestion_eventos_de.jsp' : 'gestion_evento_co.jsp';
                        }
                    });
                } else {
                    Swal.fire({ icon: 'error', title: 'Error', text: resultado.data.message || 'No se pudo actualizar.', confirmButtonColor: '#00847b' });
                }
            }, 300);
        })
        .catch(function(err) {
            clearInterval(timerCarga);
            console.error(err);
            Swal.fire({ icon: 'error', title: 'Error de conexión', text: 'No fue posible comunicarse con el servidor.', confirmButtonColor: '#00847b' });
        });
    });
}

// ---- PARTICIPANTES ----
function cargarParticipantes() {
    return fetch(contextPath + '/ListarParticipantesEventoServlet?id=' + encodeURIComponent(idEvento) + '&t=' + Date.now())
        .then(function(res) {
            if (!res.ok) throw new Error('HTTP error ' + res.status);
            return res.json();
        })
        .then(function(data) {
            participantesOriginales = data || [];
            aplicarFiltrosParticipantes();
        })
        .catch(function(err) {
            console.error('Error al cargar participantes:', err);
            if (tbodyParticipantes) {
                tbodyParticipantes.innerHTML = '<tr><td colspan="4" class="text-center text-danger py-4">Error al cargar datos.</td></tr>';
            }
        });
}

function aplicarFiltrosParticipantes() {
    var texto = normString(filtroParticipante);
    var filtrados = participantesOriginales.filter(function(u) {
        var correo = getCorreoUsuario(u);
        return texto === '' || normString(getNombreCompleto(u)).indexOf(texto) !== -1 || normString(correo).indexOf(texto) !== -1;
    });
    renderParticipantes(filtrados);
}

function renderParticipantes(lista) {
    if (!tbodyParticipantes) return;
    if (!lista.length) {
        tbodyParticipantes.innerHTML = '<tr><td colspan="4" class="text-center text-muted py-4">No hay docentes asignados a este evento.</td></tr>';
        return;
    }

    tbodyParticipantes.innerHTML = '';
    lista.forEach(function(u) {
        var tr = document.createElement('tr');
        var userId = getIdUsuario(u);
        var userCorreo = getCorreoUsuario(u);
        var estadoColor = u.activo === 1 ? '#28a745' : '#d32f2f';
        var estadoTexto = u.activo === 1 ? 'Activo' : 'Inactivo';
        var iniciales = ((u.nombre || '').charAt(0) + (u.apellidoPaterno || '').charAt(0)).toUpperCase();

        var rolSuffix = window.location.pathname.includes('_co.jsp') ? 'co' : 'de';
        tr.innerHTML =
            '<td>' +
                '<div class="docente-name-container">' +
                    '<div class="avatar-circle">' + iniciales + '</div>' +
                    '<div class="docente-name">' + escHtml(getNombreCompleto(u)) + '</div>' +
                '</div>' +
            '</td>' +
            '<td>' + escHtml(userCorreo) + '</td>' +
            '<td style="color:' + estadoColor + '; font-weight:600;">' + estadoTexto + '</td>' +
            '<td style="white-space: nowrap;">' +
                '<a href="' + contextPath + '/verDocente?id=' + userId + '" class="action-btn" title="Ver detalles"><i class="bi bi-eye"></i></a>' +
                '<a href="' + contextPath + '/cargar_archivo_' + rolSuffix + '.jsp?id=' + idEvento + '&idUsuarioTarget=' + userId + '" class="action-btn" title="Subir archivo"><i class="bi bi-cloud-upload"></i></a>' +
                '<a href="#" class="action-btn delete btn-remover-participante" data-id="' + userId + '" title="Remover del evento"><i class="bi bi-trash"></i></a>' +
            '</td>';
        tbodyParticipantes.appendChild(tr);
    });
}

if (inputBuscarParticipante) {
    inputBuscarParticipante.addEventListener('input', function () {
        filtroParticipante = this.value;
        aplicarFiltrosParticipantes();
    });
}

// REMOVER PARTICIPANTE
if (tbodyParticipantes) {
    tbodyParticipantes.addEventListener('click', function (e) {
        var btn = e.target.closest('.btn-remover-participante');
        if (!btn) return;
        e.preventDefault();

        var idUsuario = btn.getAttribute('data-id');
        Swal.fire({
            icon: 'warning',
            title: '¿Remover a este docente?',
            text: 'Se quitará su asignación al evento.',
            showCancelButton: true,
            confirmButtonColor: '#d33',
            cancelButtonColor: '#aaa',
            confirmButtonText: 'Sí, remover'
        }).then(function(res) {
            if (res.isConfirmed) {
                var formData = new URLSearchParams();
                formData.append('idEvento', idEvento);
                formData.append('idUsuario', idUsuario);

                fetch(contextPath + '/RemoverDocenteEventoServlet', {
                    method: 'POST',
                    headers: { 'Content-Type': 'application/x-www-form-urlencoded; charset=UTF-8' },
                    body: formData.toString()
                }).then(function(r) { return r.json(); }).then(function(data) {
                    if (data.success) {
                        Swal.fire({ icon: 'success', title: 'Removido', timer: 1500, showConfirmButton: false });
                        cargarParticipantes();
                    } else {
                        Swal.fire({ icon: 'error', title: 'Error', text: data.message });
                    }
                });
            }
        });
    });
}

// ---- MODAL BUSCADOR PARA ASIGNAR DOCENTES ----
function cargarTodosLosUsuarios() {
    fetch(contextPath + '/ListarUsuariosServlet?t=' + Date.now())
        .then(function(res) {
            if (!res.ok) throw new Error('HTTP error ' + res.status);
            return res.json();
        })
        .then(function(data) {
            todosLosUsuarios = data || [];
        })
        .catch(function(err) { console.error('Error al cargar usuarios:', err); });
}

function getNoAsignados() {
    var asignadosIds = participantesOriginales.map(function(p) { return Number(getIdUsuario(p)); });
    return todosLosUsuarios.filter(function(u) {
        // Solo mostrar activos y no ya asignados
        return u.activo === 1 && asignadosIds.indexOf(Number(getIdUsuario(u))) === -1;
    });
}

function renderBusquedaDocentes(filtro) {
    if (!tbodyBusquedaDocentes) return;
    var texto = normString(filtro);
    var noAsignados = getNoAsignados();
    var resultados = noAsignados;

    if (texto !== '') {
        resultados = noAsignados.filter(function(u) {
            return normString(getNombreCompleto(u)).indexOf(texto) !== -1 ||
                   normString(getCorreoUsuario(u)).indexOf(texto) !== -1 ||
                   normString(u.numeroEmpleado || '').indexOf(texto) !== -1;
        });
    }

    if (resultados.length === 0) {
        tbodyBusquedaDocentes.innerHTML = '<tr><td colspan="4" class="text-center text-muted py-3">No se encontraron docentes disponibles.</td></tr>';
        return;
    }

    // Limitar a 20 resultados para no sobrecargar
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
            '<td><button type="button" class="btn btn-sm btn-outline-success btn-asignar-docente" data-id="' + userId + '"><i class="bi bi-plus-circle me-1"></i>Añadir</button></td>';
        tbodyBusquedaDocentes.appendChild(tr);
    });

    if (resultados.length > 20) {
        var trMore = document.createElement('tr');
        trMore.innerHTML = '<td colspan="4" class="text-center text-muted small py-2">Mostrando 20 de ' + resultados.length + '. Refina tu búsqueda.</td>';
        tbodyBusquedaDocentes.appendChild(trMore);
    }
}

if (inputBuscarDocente) {
    inputBuscarDocente.addEventListener('input', function() {
        renderBusquedaDocentes(this.value);
    });
}

// Al abrir el modal, resetear búsqueda y mostrar todos
var modalAsignar = document.getElementById('modalAsignarDocente');
if (modalAsignar) {
    modalAsignar.addEventListener('shown.bs.modal', function() {
        if (inputBuscarDocente) {
            inputBuscarDocente.value = '';
            inputBuscarDocente.focus();
        }
        renderBusquedaDocentes('');
    });
}

// Asignar docente desde el buscador
if (tbodyBusquedaDocentes) {
    tbodyBusquedaDocentes.addEventListener('click', function(e) {
        var btn = e.target.closest('.btn-asignar-docente');
        if (!btn) return;
        e.preventDefault();

        var idUsuario = btn.getAttribute('data-id');
        btn.disabled = true;
        btn.innerHTML = '<i class="bi bi-hourglass-split"></i>';

        var formData = new URLSearchParams();
        formData.append('idEvento', idEvento);
        formData.append('idUsuario', idUsuario);

        fetch(contextPath + '/AsignarDocenteEventoServlet', {
            method: 'POST',
            headers: { 'Content-Type': 'application/x-www-form-urlencoded; charset=UTF-8' },
            body: formData.toString()
        }).then(function(r) { return r.json(); }).then(function(data) {
            if (data.success) {
                Swal.fire({ icon: 'success', title: 'Docente asignado', timer: 1200, showConfirmButton: false });
                cargarParticipantes().then(function() {
                    renderBusquedaDocentes(inputBuscarDocente ? inputBuscarDocente.value : '');
                });
            } else {
                Swal.fire({ icon: 'error', title: 'Error', text: data.message });
                btn.disabled = false;
                btn.innerHTML = '<i class="bi bi-plus-circle me-1"></i>Añadir';
            }
        }).catch(function() {
            btn.disabled = false;
            btn.innerHTML = '<i class="bi bi-plus-circle me-1"></i>Añadir';
        });
    });
}

// Iniciar
cargarEvento();