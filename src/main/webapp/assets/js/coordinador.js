document.addEventListener('DOMContentLoaded', function() {
    const sidebar = document.querySelector('.sidebar-hover');
    if (!sidebar) return;

    function isMobile() {
        return window.innerWidth <= 991;
    }

    if (!isMobile() && localStorage.getItem('menuAbierto') === 'true') {
        sidebar.classList.add('locked');
    }

    const links = document.querySelectorAll('.sidebar-item');
    links.forEach(link => {
        link.addEventListener('click', () => {
            if (!isMobile()) {
                localStorage.setItem('menuAbierto', 'true');
                sidebar.classList.add('locked');
            }
        });
    });

    sidebar.addEventListener('mouseleave', () => {
        if (!isMobile()) {
            localStorage.setItem('menuAbierto', 'false');
            sidebar.classList.remove('locked');
        }
    });

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
