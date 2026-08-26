/**
 * login.js
 *
 * Comportamiento del formulario de inicio de sesión: mostrar/ocultar los campos de contraseña y confirmación de contraseña.
 */

// Mostrar / Ocultar Contraseña Principal
const togglePassword = document.querySelector('#togglePassword');
const password = document.querySelector('#txtPassword');
/**
 * Handler de clic del icono "ojito": alterna el campo de contraseña
 * entre tipo 'password' (oculto) y 'text' (visible) e intercambia el icono.
 */
if (togglePassword && password) {
    togglePassword.addEventListener('click', function () {
        const type = password.getAttribute('type') === 'password' ? 'text' : 'password';
        password.setAttribute('type', type);
        this.classList.toggle('bi-eye');
        this.classList.toggle('bi-eye-slash');
    });
}

// Mostrar / Ocultar Confirmar Contraseña
const toggleConfirmPassword = document.querySelector('#toggleConfirmPassword');
const confirmPassword = document.querySelector('#txtConfirmPassword');
/**
 * Handler de clic del icono "ojito": alterna el campo de confirmación de
 * contraseña entre tipo 'password' (oculto) y 'text' (visible) e intercambia el icono.
 */
if (toggleConfirmPassword && confirmPassword) {
    toggleConfirmPassword.addEventListener('click', function () {
        const type = confirmPassword.getAttribute('type') === 'password' ? 'text' : 'password';
        confirmPassword.setAttribute('type', type);
        this.classList.toggle('bi-eye');
        this.classList.toggle('bi-eye-slash');
    });
}