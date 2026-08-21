window.renderPaginator = function(listaOriginal, rowsPerPage, containerId, renderCallback) {
    const container = document.getElementById(containerId);
    if (!container) return;
    
    if (!listaOriginal || !Array.isArray(listaOriginal)) {
        listaOriginal = [];
    }

    let currentPage = 1;
    let totalPages = Math.ceil(listaOriginal.length / rowsPerPage);
    if (totalPages === 0) totalPages = 1;

    function renderPage(page) {
        currentPage = page;
        const start = (page - 1) * rowsPerPage;
        const end = start + rowsPerPage;
        const subLista = listaOriginal.slice(start, end);

        // Llamamos al render con solo la sublista
        renderCallback(subLista);

        renderControls();
    }

    function renderControls() {
        container.innerHTML = '';

        let ul = document.createElement('ul');
        ul.className = 'pagination justify-content-center';
        
        // Prev
        let liPrev = document.createElement('li');
        liPrev.className = 'page-item ' + (currentPage === 1 ? 'disabled' : '');
        let aPrev = document.createElement('a');
        aPrev.className = 'page-link';
        aPrev.href = '#';
        aPrev.innerHTML = '&laquo;';
        aPrev.onclick = function(e) {
            e.preventDefault();
            if(currentPage > 1) renderPage(currentPage - 1);
        };
        liPrev.appendChild(aPrev);
        ul.appendChild(liPrev);

        // Pages
        for (let i = 1; i <= totalPages; i++) {
            let li = document.createElement('li');
            li.className = 'page-item ' + (currentPage === i ? 'active' : '');
            let a = document.createElement('a');
            a.className = 'page-link';
            a.href = '#';
            a.innerText = i;
            if(currentPage === i) {
                a.style.backgroundColor = '#00847b';
                a.style.borderColor = '#00847b';
                a.style.color = '#fff';
            } else {
                a.style.color = '#00847b';
            }
            a.onclick = function(e) {
                e.preventDefault();
                renderPage(i);
            };
            li.appendChild(a);
            ul.appendChild(li);
        }

        // Next
        let liNext = document.createElement('li');
        liNext.className = 'page-item ' + (currentPage === totalPages ? 'disabled' : '');
        let aNext = document.createElement('a');
        aNext.className = 'page-link';
        aNext.href = '#';
        aNext.innerHTML = '&raquo;';
        aNext.onclick = function(e) {
            e.preventDefault();
            if(currentPage < totalPages) renderPage(currentPage + 1);
        };
        liNext.appendChild(aNext);
        ul.appendChild(liNext);

        container.appendChild(ul);
    }

    renderPage(1);
};