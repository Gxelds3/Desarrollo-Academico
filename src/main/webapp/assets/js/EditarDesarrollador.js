document.addEventListener('DOMContentLoaded', function () {
    const contextPath = window.contextPath || '';
    const params = new URLSearchParams(window.location.search);
    const idDesarrollador = params.get('id');

    const form = document.getElementById('formEditarDesarrollador');
    const btnGuardar = document.getElementById('btnGuardar');

    function cargarDesarrollador() {
        if (!idDesarrollador) {
            Swal.fire({
                icon: 'error',
                title: 'Falta el id del desarrollador',
                text: 'Entra a esta página desde "Gestión de Desarrolladores" para poder editar.',
                confirmButtonColor: '#00847b'
            }).then(function () {
                window.location.href = 'gestion_desarrolladores_de.jsp';
            });
            return;
        }

        console.log("Cargando desarrollador ID:", idDesarrollador);

        fetch(contextPath + '/EditarDesarrollador?id=' + encodeURIComponent(idDesarrollador))
            .then(function (response) {
                return response.json();
            })
            .then(function (data) {
                console.log("Datos obtenidos de la BD:", data);

                if (!data.success) {
                    Swal.fire({
                        icon: 'error',
                        title: 'No se pudo cargar el desarrollador',
                        text: data.message || 'Ocurrió un error al obtener los datos.',
                        confirmButtonColor: '#00847b'
                    });
                    return;
                }

                // Llenado dinámico garantizado de cada campo por su ID
                const campoId = document.getElementById('campoId');
                const campoNombre = document.getElementById('campoNombre');
                const campoApellidoPaterno = document.getElementById('campoApellidoPaterno');
                const campoApellidoMaterno = document.getElementById('campoApellidoMaterno');
                const campoDivision = document.getElementById('campoDivision');
                const campoNumeroEmpleado = document.getElementById('campoNumeroEmpleado');
                const campoTelefono = document.getElementById('campoTelefono');
                const campoCorreo = document.getElementById('campoCorreo');

                if (campoId) campoId.value = data.id || '';
                if (campoNombre) campoNombre.value = data.nombre || '';
                if (campoApellidoPaterno) campoApellidoPaterno.value = data.apellidoPaterno || '';
                if (campoApellidoMaterno) campoApellidoMaterno.value = data.apellidoMaterno || '';
                if (campoNumeroEmpleado) campoNumeroEmpleado.value = data.numeroEmpleado || '';
                if (campoTelefono) campoTelefono.value = data.telefono || '';
                if (campoCorreo) campoCorreo.value = data.correo || '';

                if (campoDivision && data.idDivision !== undefined && data.idDivision !== null) {
                    campoDivision.value = String(data.idDivision);
                }
            })
            .catch(function (error) {
                console.error('Error al cargar el desarrollador:', error);
                Swal.fire({
                    icon: 'error',
                    title: 'Error de conexión',
                    text: 'No fue posible comunicarse con el servidor.',
                    confirmButtonColor: '#00847b'
                });
            });
    }

    // Alternar visibilidad de contraseña (Ojito)
    function setupTogglePassword(btnId, inputName) {
        const btn = document.getElementById(btnId);
        const input = form ? form.querySelector('[name="' + inputName + '"]') : null;
        if (!btn || !input) return;

        btn.addEventListener('click', function () {
            const icon = btn.querySelector('i');
            if (input.type === 'password') {
                input.type = 'text';
                icon.classList.replace('bi-eye', 'bi-eye-slash');
            } else {
                input.type = 'password';
                icon.classList.replace('bi-eye-slash', 'bi-eye');
            }
        });
    }

    setupTogglePassword('btnTogglePass', 'contrasena');
    setupTogglePassword('btnToggleConfirmPass', 'confirmar_contrasena');

    // Envío del formulario
    if (form) {
        form.addEventListener('submit', function (e) {
            e.preventDefault();

            const contrasenaVal = form.querySelector('[name="contrasena"]').value.trim();
            const confirmarVal = form.querySelector('[name="confirmar_contrasena"]').value.trim();

            if (contrasenaVal.length > 0) {
                if (contrasenaVal.length < 8) {
                    Swal.fire({
                        icon: 'warning',
                        title: 'Contraseña demasiado corta',
                        text: 'La contraseña debe tener al menos 8 caracteres.',
                        confirmButtonColor: '#00847b'
                    });
                    return;
                }

                if (contrasenaVal !== confirmarVal) {
                    Swal.fire({
                        icon: 'warning',
                        title: 'Las contraseñas no coinciden',
                        text: 'Verifica que ambas contraseñas sean iguales.',
                        confirmButtonColor: '#00847b'
                    });
                    return;
                }
            }

            Swal.fire({
                icon: 'question',
                title: '¿Deseas actualizar este desarrollador?',
                text: 'Se guardarán los cambios realizados en el formulario.',
                showCancelButton: true,
                confirmButtonColor: '#00847b',
                cancelButtonColor: '#6c757d',
                confirmButtonText: 'Sí, actualizar',
                cancelButtonText: 'Cancelar'
            }).then(function (confirmacion) {
                if (!confirmacion.isConfirmed) return;

                if (btnGuardar) btnGuardar.disabled = true;

                const datosForm = new FormData(form);

                fetch(contextPath + '/EditarDesarrollador', {
                    method: 'POST',
                    body: datosForm
                })
                    .then(function (response) {
                        return response.json().then(function (data) {
                            return { ok: response.ok, data: data };
                        });
                    })
                    .then(function (resultado) {
                        if (resultado.ok && resultado.data.success) {
                            Swal.fire({
                                icon: 'success',
                                title: '¡Desarrollador Actualizado con Éxito!',
                                text: 'Los cambios se guardaron correctamente.',
                                confirmButtonColor: '#00847b',
                                confirmButtonText: 'Aceptar'
                            }).then(function (result) {
                                if (result.isConfirmed) {
                                    window.location.href = 'gestion_desarrolladores_de.jsp';
                                }
                            });
                        } else {
                            Swal.fire({
                                icon: 'error',
                                title: 'No se pudo actualizar el desarrollador',
                                text: resultado.data.message || 'Ocurrió un error al conectar con la base de datos.',
                                confirmButtonColor: '#00847b'
                            });
                            if (btnGuardar) btnGuardar.disabled = false;
                        }
                    })
                    .catch(function (error) {
                        console.error('Error al actualizar:', error);
                        Swal.fire({
                            icon: 'error',
                            title: 'Error de conexión',
                            text: 'No fue posible comunicarse con el servidor.',
                            confirmButtonColor: '#00847b'
                        });
                        if (btnGuardar) btnGuardar.disabled = false;
                    });
            });
        });
    }

    // Ejecutar la carga de datos al iniciar
    cargarDesarrollador();
});