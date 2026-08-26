/**
 * coordinador.js
 *
 * Comportamiento del panel/sidebar del layout de Coordinador: resaltado del enlace activo y colapso del menú lateral al salir el cursor.
 */

document.addEventListener('DOMContentLoaded', function() {
    const sidebar = document.querySelector('.sidebar-hover');
    if (!sidebar) return;

    /**
     * Indica si el viewport actual corresponde a un tamaño móvil/tablet (<= 991px).
     * @returns {boolean}
     */
    function isMobile() {
        return window.innerWidth <= 991;
    }

    // Restaura el estado "fijo" (locked) del menú lateral guardado en localStorage,
    // solo aplica en escritorio.
    if (!isMobile() && localStorage.getItem('menuAbierto') === 'true') {
        sidebar.classList.add('locked');
    }

    // Al hacer clic en un enlace del menú (en escritorio), se fija el sidebar abierto
    // y se recuerda la preferencia en localStorage.
    const links = document.querySelectorAll('.sidebar-item');
    links.forEach(link => {
        link.addEventListener('click', () => {
            if (!isMobile()) {
                localStorage.setItem('menuAbierto', 'true');
                sidebar.classList.add('locked');
            }
        });
    });

    // Al salir el cursor del sidebar (en escritorio), se libera el estado "fijo".
    sidebar.addEventListener('mouseleave', () => {
        if (!isMobile()) {
            localStorage.setItem('menuAbierto', 'false');
            sidebar.classList.remove('locked');
        }
    });

    // En móvil, un clic en el sidebar expande/contrae el menú; un clic sobre un
    // enlace concreto lo contrae de nuevo tras la navegación.
    sidebar.addEventListener('click', function(e) {
        if (isMobile()) {
            if (e.target.closest('.sidebar-item')) {
                sidebar.classList.remove('mobile-expanded');
                return;
            }
            sidebar.classList.toggle('mobile-expanded');
        }
    });
});
