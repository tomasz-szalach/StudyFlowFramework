/* ===== PUT / DELETE + walidacja opisu & toast ===== */
document.addEventListener('DOMContentLoaded',()=>{

    const form      = document.getElementById('editForm');
    const deleteBtn = document.getElementById('deleteBtn');
    const toast     = document.getElementById('toast');

    /* ---- licznik opisu ---- */
    const desc    = document.getElementById('desc');
    const counter = document.getElementById('counter');

    const updateCount = () =>{
        const len = desc.value.length;
        counter.textContent = `${len} / 1000`;
        const overflow = len > 1000;
        counter.classList.toggle('over',  overflow);
        desc   .classList.toggle('error', overflow);
        return !overflow;
    };
    updateCount();
    desc.addEventListener('input', updateCount);

    /* ---- ZAPIS ---- */
    form.addEventListener('submit', async e=>{
        e.preventDefault();
        if(!updateCount()) return;          // zbyt długi opis

        const url  = form.getAttribute('action');
        const data = Object.fromEntries(new FormData(form).entries());

        const res = await fetch(url,{
            method:'PUT',
            headers:{'Content-Type':'application/json'},
            body:JSON.stringify(data)
        });

        if(res.ok){
            showToast('Zapisano zmiany');
            setTimeout(()=>location.href='/home',1400);
        }else showToast('Nie udało się zapisać');
    });

    /* ---- USUŃ ---- */
    deleteBtn.addEventListener('click', async ()=>{
        const ok = await showConfirm('Usuń zadanie',
            'Czy na pewno chcesz usunąć to zadanie?');
        if(!ok) return;

        const url  = form.getAttribute('action');
        const res  = await fetch(url,{method:'DELETE'});
        if(res.ok) location.href='/home';
        else       showToast('Nie udało się usunąć');
    });

    /* ---- toast helper ---- */
    function showToast(msg){
        toast.textContent = msg;
        toast.classList.add('show');
        setTimeout(()=>toast.classList.remove('show'), 2200);
    }
});
