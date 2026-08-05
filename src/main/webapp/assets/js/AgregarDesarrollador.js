const contextPath = window.contextPath || '';
const form = document.getElementById('formAgregarDesarrollador');
const btnGuardar = document.getElementById('btnGuardar');

form.addEventListener('submit', function (e) {
    e.preventDefault();

    const contrasenaVal = form.querySelector('[name="contrasena"]').value;
    const confirmarVal = form.querySelector('[name="confirmar_contrasena"]').value;

    if (contrasenaVal !== confirmarVal) {
        Swal.fire({
            icon: 'warning',
            title: 'Las contraseñas no coinciden',
            text: 'Verifica que ambas contraseñas sean iguales.',
            confirmButtonColor: '#00847b'
        });
        return;
    }


    btnGuardar.disabled = true;

    const datosForm = new FormData(form);

    fetch(contextPath + '/AgregarDesarrolladorServlet', {
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
                    title: '¡Desarrollador Registrado con Éxito!',
                    text: 'El desarrollador se ha guardado correctamente en la base de datos.',
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
                    title: 'No se pudo guardar el desarrollador',
                    text: resultado.data.message || 'Ocurrió un error al conectar con la base de datos.',
                    confirmButtonColor: '#00847b'
                });
                btnGuardar.disabled = false;
            }
        })
        .catch(function (error) {
            console.error('Error al registrar el desarrollador:', error);
            Swal.fire({
                icon: 'error',
                title: 'Error de conexión',
                text: 'No fue posible comunicarse con el servidor.',
                confirmButtonColor: '#00847b'
            });
            btnGuardar.disabled = false;
        });



        // 4. Verificar si el correo existe en el sistema vía AJAX (Servlet)


    function validarPaso1() {
        const inputCorreo = document.getElementById('campoCorreo'); // Asegúrate de que el ID coincida con tu HTML/JSP
        const correo = inputCorreo.value.trim();

        // 1. Si el campo se deja vacío al enviar
        if (correo === '') {
            Swal.fire({
                icon: 'warning',
                title: 'Campos incompletos',
                text: 'Aún faltan campos',
                confirmButtonColor: '#00847b'
            });
            return false;
        }

        // 2. Si excede los 50 caracteres
        if (correo.length > 50) {
            Swal.fire({
                icon: 'warning',
                title: 'Límite excedido',
                text: 'Correo muy largo',
                confirmButtonColor: '#00847b'
            });
            return false;
        }

        // 3. Si no cuenta con el dominio oficial (@utez.edu.mx)
        if (!correo.toLowerCase().endsWith('@utez.edu.mx')) {
            Swal.fire({
                icon: 'warning',
                title: 'Dominio no permitido',
                text: 'El correo debe ser institucional (@utez.edu.mx)',
                confirmButtonColor: '#00847b'
            });
            return false;
        }

        // 4. Verificar si el correo existe en el sistema vía AJAX (Servlet)
        return verificarExistenciaCorreo(correo);
    }

//
//
// Función AJAX para validar si el correo ya está registrado en la BD
    function verificarExistenciaCorreo(correo) {
        const formData = new FormData();
        formData.append('correo', correo);

        // Ajusta la URL de tu servlet de verificación si tiene otro nombre
        return fetch(window.contextPath + '/VerificarCorreoServlet', {
            method: 'POST',
            body: formData
        })
            .then(response => response.json())
            .then(data => {
                // Asumiendo que el servlet responde { existe: true/false }
                if (!data.existe) {
                    Swal.fire({
                        icon: 'error',
                        title: 'Correo no encontrado',
                        text: 'El correo ingresado no está registrado',
                        confirmButtonColor: '#00847b'
                    });
                    return false;
                }
                return true; // El correo existe, puede avanzar al Paso 2
            })
            .catch(error => {
                console.error('Error al verificar el correo:', error);
                Swal.fire({
                    icon: 'error',
                    title: 'Error de conexión',
                    text: 'No se pudo verificar el correo con el servidor.',
                    confirmButtonColor: '#00847b'
                });
                return false;
            });
    }



    function verificarExistenciaCorreo(correo) {
        const formData = new FormData();
        formData.append('correo', correo);

        // Ajusta la URL de tu servlet de verificación si tiene otro nombre
        return fetch(window.contextPath + '/VerificarCorreoServlet', {
            method: 'POST',
            body: formData
        })
            .then(response => response.json())
            .then(data => {
                // Asumiendo que el servlet responde { existe: true/false }
                if (!data.existe) {
                    Swal.fire({
                        icon: 'error',
                        title: 'Correo no encontrado',
                        text: 'El correo ingresado no está registrado',
                        confirmButtonColor: '#00847b'
                    });
                    return false;
                }
                return true; // El correo existe, puede avanzar al Paso 2
            })
            .catch(error => {
                console.error('Error al verificar el correo:', error);
                Swal.fire({
                    icon: 'error',
                    title: 'Error de conexión',
                    text: 'No se pudo verificar el correo con el servidor.',
                    confirmButtonColor: '#00847b'
                });
                return false;
            });
    }
});