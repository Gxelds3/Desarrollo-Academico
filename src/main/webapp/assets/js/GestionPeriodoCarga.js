/**
 * GestionPeriodoCarga.js
 *
 * Lógica de la vista de gestión de periodos de carga: listado, resaltado de vigencia y cambio de estado (activar/desactivar) de cada periodo.
 */

document.addEventListener("DOMContentLoaded", function() {
    cargarPeriodos();

    document.getElementById("inputBuscar").addEventListener("keyup", function() {
        const valor = this.value.toLowerCase();
        const filas = document.querySelectorAll("#tablaPeriodosBody tr");

        filas.forEach(fila => {
            const texto = fila.innerText.toLowerCase();
            fila.style.display = texto.includes(valor) ? "" : "none";
        });
    });
});

/**
 * Obtiene del servidor la lista de periodos de carga y los muestra en la tabla.
 */
function cargarPeriodos() {
    const tbody = document.getElementById("tablaPeriodosBody");
    tbody.innerHTML = `<tr><td colspan="5" class="py-4 text-muted">Cargando periodos...</td></tr>`;

    fetch("ListarPeriodosServlet")
        .then(response => {
            if (!response.ok) throw new Error("Error al consultar el servidor");
            return response.json();
        })
        .then(data => {
            tbody.innerHTML = "";

            if (!data || data.length === 0) {
                tbody.innerHTML = `
                    <tr>
                        <td colspan="5" class="py-4 text-muted">
                            No hay periodos de carga registrados.
                        </td>
                    </tr>`;
                return;
            }

            data.forEach(periodo => {
                const tr = document.createElement("tr");

                const id = periodo.idPeriodo || periodo.id;
                const esActivo = periodo.activo;

                tr.innerHTML = `
                    <td class="text-start">
                        <div class="d-flex align-items-center">
                            <div class="avatar-circle" style="flex-shrink:0;"></div>
                            <span class="fw-semibold ms-2">${periodo.division}</span>
                        </div>
                    </td>
                    <td>${periodo.fechaInicio}</td>
                    <td>${periodo.fechaFin}</td>
                    <td>
                        <button type="button" class="btn btn-link p-0 text-decoration-none"
                                title="${esActivo ? 'Desactivar' : 'Activar'}"
                                onclick="confirmarCambioEstado(${id}, ${!esActivo})">
                            <i class="bi ${esActivo ? 'bi-toggle-on text-success' : 'bi-toggle-off text-danger'} fs-4"></i>
                        </button>
                    </td>
                    <td>
                        <a href="editar_periodo_carga_de.jsp?id=${id}" class="action-btn" title="Editar"><i class="bi bi-pencil"></i></a>
                        <a href="ver_periodo_carga_de.jsp?id=${id}" class="action-btn" title="Ver detalle"><i class="bi bi-eye"></i></a>

                    </td>
                `;
                tbody.appendChild(tr);
            });
        })
        .catch(error => {
            console.error("Error:", error);
            tbody.innerHTML = `
                <tr>
                    <td colspan="5" class="py-4 text-danger">
                        Ocurrió un error al cargar la lista de periodos.
                    </td>
                </tr>`;
        });
}

/**
 * Solicita confirmación al usuario y, si acepta, envía al servidor la petición para cambiar el estado del periodo de carga.
 * @param {*} id
 * @param {*} nuevoEstado
 */
function confirmarCambioEstado(id, nuevoEstado) {
    const accionTexto = nuevoEstado ? "activar" : "desactivar";

    Swal.fire({
        title: `¿Deseas ${accionTexto} este periodo?`,
        text: `El periodo pasará a estar ${nuevoEstado ? 'Activo' : 'Inactivo'}.`,
        icon: 'question',
        showCancelButton: true,
        confirmButtonColor: nuevoEstado ? '#198754' : '#ffc107',
        cancelButtonColor: '#6c757d',
        confirmButtonText: `Sí, ${accionTexto}`,
        cancelButtonText: 'Cancelar'
    }).then((result) => {
        if (result.isConfirmed) {
            fetch(`CambiarEstadoServlet?id=${id}&estado=${nuevoEstado}`)
                .then(response => {
                    return response.json().then(data => {
                        if (!response.ok) {
                            throw new Error(data.error || 'Error desconocido');
                        }
                        return data;
                    });
                })
                .then(() => {
                    Swal.fire({
                        title: '¡Actualizado!',
                        text: `El periodo ha sido ${nuevoEstado ? 'activado' : 'desactivado'} con éxito.`,
                        icon: 'success',
                        timer: 1800,
                        showConfirmButton: false
                    });
                    cargarPeriodos();
                })
                .catch(error => {
                    console.error("Error al cambiar estado:", error);
                    Swal.fire({
                        title: 'No se puede activar',
                        text: error.message,
                        icon: 'warning'
                    });
                    cargarPeriodos();
                });
        }
    });
}