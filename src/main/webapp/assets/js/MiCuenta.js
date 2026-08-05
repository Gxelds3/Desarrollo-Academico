const contextPath = window.contextPath || '';
const formMiCuenta = document.getElementById('formMiCuenta');

if (formMiCuenta) {
    formMiCuenta.addEventListener('submit', function (e) {
        e.preventDefault();

        const inputActual = document.getElementById('passActual');
        const inputNueva = document.getElementById('passNueva');
        const inputConfirm = document.getElementById('passConfirm');

        const passActualVal = inputActual ? inputActual.value.trim() : '';
        const novaPasswordVal = inputNueva ? inputNueva.value.trim() : '';
        const passConfirmVal = inputConfirm ? inputConfirm.value.trim() : '';

        // 1. Validaciones locales
        if (!passActualVal || !novaPasswordVal || !passConfirmVal) {
            Swal.fire({
                icon: 'warning',
                title: 'Campos vacíos',
                text: 'Por favor, llena todos los campos de contraseña.',
                confirmButtonColor: '#00847b'
            });
            return;
        }

        if (novaPasswordVal !== passConfirmVal) {
            Swal.fire({
                icon: 'error',
                title: 'Contraseñas no coinciden',
                text: 'La nueva contraseña y su confirmación deben ser iguales.',
                confirmButtonColor: '#00847b'
            });
            return;
        }

        if (novaPasswordVal.length < 12 || novaPasswordVal.length > 15) {
            Swal.fire({
                icon: 'error',
                title: 'Contraseña Inválida',
                text: 'La contraseña debe tener entre 12 y 15 caracteres.',
                confirmButtonColor: '#00847b'
            });
            return;
        }

        const btnGuardar = formMiCuenta.querySelector('button[type="submit"]');
        if (btnGuardar) btnGuardar.disabled = true;

        const formData = new URLSearchParams();
        formData.append('passActual', passActualVal);
        formData.append('passNueva', novaPasswordVal);

        // 2. Envío al Servlet
        fetch(contextPath + '/CambiarPasswordServlet', {
            method: 'POST',
            headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
            body: formData.toString()
        })
            .then(res => res.json().then(data => ({ ok: res.ok, data: data })))
            .then(resultado => {
                if (resultado.ok && resultado.data.success) {
                    // Al tener éxito, actualizamos 'Contraseña Actual' con la nueva y limpiamos las otras
                    if (inputActual) inputActual.value = novaPasswordVal;
                    if (inputNueva) inputNueva.value = '';
                    if (inputConfirm) inputConfirm.value = '';

                    Swal.fire({
                        icon: 'success',
                        title: '¡Actualizado con Éxito!',
                        text: resultado.data.message || 'Tu contraseña ha sido actualizada.',
                        confirmButtonColor: '#00847b'
                    });
                } else {
                    Swal.fire({
                        icon: 'error',
                        title: 'No se pudo actualizar',
                        text: resultado.data.message || 'Error al cambiar contraseña.',
                        confirmButtonColor: '#00847b'
                    });
                }
            })
            .catch(err => {
                console.error('Error:', err);
                Swal.fire({
                    icon: 'error',
                    title: 'Error de conexión',
                    text: 'No fue posible comunicarse con el servidor.',
                    confirmButtonColor: '#00847b'
                });
            })
            .finally(() => {
                if (btnGuardar) btnGuardar.disabled = false;
            });
    });
}