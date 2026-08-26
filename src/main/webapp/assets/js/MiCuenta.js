/**
 * MiCuenta.js
 *
 * Lógica de la vista 'Mi cuenta': validación y envío de los cambios de datos personales del usuario en sesión.
 */

const contextPath = window.contextPath || '';
const formMiCuenta = document.getElementById('formMiCuenta');

document.addEventListener("DOMContentLoaded", () => {
    const inputTelefono = document.getElementById('campoTelefono');

    // -------------------------------------------------------------------------
    // 1. RESTRICCIÓN EN TIEMPO REAL: Solo números y máximo 10 dígitos en Teléfono
    // -------------------------------------------------------------------------
    /**
     * Handler del evento 'input' del campo Teléfono: filtra en tiempo real
     * cualquier carácter que no sea un dígito numérico.
     * @param {InputEvent} e
     */
    if (inputTelefono) {
        inputTelefono.addEventListener('input', (e) => {
            // Elimina inmediatamente cualquier caracter que no sea número (0-9)
            e.target.value = e.target.value.replace(/[^0-9]/g, '');
        });
    }

    // -------------------------------------------------------------------------
    // 2. ENVÍO DEL FORMULARIO Y VALIDACIONES
    // -------------------------------------------------------------------------
    /**
     * Handler del evento 'submit' del formulario Mi Cuenta.
     * Valida contraseña actual, teléfono y, si aplica, la nueva contraseña;
     * luego envía los datos (FormData) por POST a ActualizarMiCuentaServlet
     * mostrando una barra de progreso simulada y el resultado con SweetAlert2.
     * @param {SubmitEvent} e
     */
    if (formMiCuenta) {
        formMiCuenta.addEventListener('submit', function (e) {
            e.preventDefault();

            const inputActual = document.getElementById('passActual');
            const inputNueva = document.getElementById('passNueva');
            const inputConfirm = document.getElementById('passConfirm');

            const telefonoVal = inputTelefono ? inputTelefono.value.trim() : '';
            const passActualVal = inputActual ? inputActual.value.trim() : '';
            const novaPasswordVal = inputNueva ? inputNueva.value.trim() : '';
            const passConfirmVal = inputConfirm ? inputConfirm.value.trim() : '';

            // --- A) VALIDACIÓN DE CONTRASEÑA ACTUAL OBLIGATORIA ---
            if (!passActualVal) {
                Swal.fire({
                    icon: 'warning',
                    title: 'Contraseña Actual Requerida',
                    text: 'Debes ingresar tu contraseña actual para confirmar cualquier cambio.',
                    confirmButtonColor: '#00847b',
                    confirmButtonText: 'Aceptar'
                });
                return;
            }

            // --- B) VALIDACIÓN DE TELÉFONO (Exactamente 10 dígitos) ---
            if (!/^\d{10}$/.test(telefonoVal)) {
                Swal.fire({
                    icon: 'warning',
                    title: 'Teléfono inválido',
                    text: 'El número de teléfono debe contener exactamente 10 dígitos numéricos.',
                    confirmButtonColor: '#00847b',
                    confirmButtonText: 'Aceptar'
                });
                return;
            }

            // --- C) VALIDACIÓN DE NUEVA CONTRASEÑA (Solo si intenta cambiarla) ---
            const estaCambiandoPass = novaPasswordVal !== '' || passConfirmVal !== '';

            if (estaCambiandoPass) {
                if (!novaPasswordVal || !passConfirmVal) {
                    Swal.fire({
                        icon: 'warning',
                        title: 'Campos vacíos',
                        text: 'Por favor, llena los campos de la nueva contraseña y su confirmación.',
                        confirmButtonColor: '#00847b',
                        confirmButtonText: 'Aceptar'
                    });
                    return;
                }

                if (novaPasswordVal !== passConfirmVal) {
                    Swal.fire({
                        icon: 'error',
                        title: 'Contraseñas no coinciden',
                        text: 'La nueva contraseña y su confirmación deben ser iguales.',
                        confirmButtonColor: '#00847b',
                        confirmButtonText: 'Aceptar'
                    });
                    return;
                }

                if (novaPasswordVal.length < 12 || novaPasswordVal.length > 15) {
                    Swal.fire({
                        icon: 'error',
                        title: 'Contraseña Inválida',
                        text: 'La nueva contraseña debe tener entre 12 y 15 caracteres.',
                        confirmButtonColor: '#00847b',
                        confirmButtonText: 'Aceptar'
                    });
                    return;
                }
            }

            // --- D) PREPARACIÓN DE DATOS PARA ENVÍO ---
            const btnGuardar = formMiCuenta.querySelector('button[type="submit"]');
            if (btnGuardar) btnGuardar.disabled = true;

            // FormData recopila automáticamente todos los inputs del HTML (incluyendo readonly y hiddens)
            const formData = new FormData(formMiCuenta);

            // Asignación explícita de campos opcionales/sensibles
            formData.set('passActual', passActualVal);
            formData.set('contrasena', novaPasswordVal);
            formData.set('confirmar_contrasena', passConfirmVal);

            // --- E) PRELOADER CON PORCENTAJE ---
            let porcentaje = 0;
            let timerCarga;

            Swal.fire({
                title: 'Actualizando tu información...',
                html: '<div style="font-size: 1.5rem; font-weight: bold; color: #00847b; margin-top: 10px;" id="lblPorcentajeCuenta">0%</div>',
                allowOutsideClick: false,
                allowEscapeKey: false,
                showConfirmButton: false,
                didOpen: () => {
                    Swal.showLoading();
                    timerCarga = setInterval(() => {
                        if (porcentaje < 90) {
                            porcentaje += 10;
                            const el = document.getElementById('lblPorcentajeCuenta');
                            if (el) el.textContent = porcentaje + '%';
                        }
                    }, 80);
                }
            });

            // --- F) ENVÍO AL SERVLET 'EditarDesarrollador' ---
            fetch(contextPath + '/ActualizarMiCuentaServlet', {
                method: 'POST',
                body: formData // Envío compatible con @MultipartConfig en el Servlet
            })
                .then(res => res.json().then(data => ({ ok: res.ok, data: data })))
                .then(resultado => {
                    clearInterval(timerCarga);
                    const el = document.getElementById('lblPorcentajeCuenta');
                    if (el) el.textContent = '100%';

                    setTimeout(() => {
                        if (resultado.ok && resultado.data.success) {
                            // Limpiar campos de contraseña tras éxito
                            if (inputActual) inputActual.value = '';
                            if (inputNueva) inputNueva.value = '';
                            if (inputConfirm) inputConfirm.value = '';

                            Swal.fire({
                                icon: 'success',
                                title: '¡Actualizado con Éxito!',
                                text: resultado.data.message || 'Tus datos han sido actualizados correctamente.',
                                confirmButtonColor: '#00847b',
                                confirmButtonText: 'Aceptar'
                            });
                        } else {
                            Swal.fire({
                                icon: 'error',
                                title: 'No se pudo actualizar',
                                text: resultado.data.message || 'Error al actualizar tus datos.',
                                confirmButtonColor: '#00847b',
                                confirmButtonText: 'Aceptar'
                            });
                        }
                    }, 300);
                })
                .catch(err => {
                    clearInterval(timerCarga);
                    console.error('Error:', err);
                    Swal.fire({
                        icon: 'error',
                        title: 'Error de conexión',
                        text: 'No fue posible comunicarse con el servidor.',
                        confirmButtonColor: '#00847b',
                        confirmButtonText: 'Aceptar'
                    });
                })
                .finally(() => {
                    if (btnGuardar) btnGuardar.disabled = false;
                });
        });
    }
});