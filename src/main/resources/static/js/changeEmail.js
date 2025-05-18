document.addEventListener('DOMContentLoaded',()=>{

    const form   = document.getElementById('emailForm'),
        pass   = form.password,
        mail1  = form.newEmail,
        mail2  = form.newEmail2,
        errSame= document.getElementById('errSame');

    /* oko */
    form.querySelector('.toggle').onclick = ()=>{
        pass.type = pass.type==='password'?'text':'password';
    };

    /* zgodność maili */
    const same = ()=> {
        const ok = mail1.value === mail2.value && mail1.value !== '';
        errSame.style.display = ok ? 'none' : 'block';
        return ok;
    };
    mail1.addEventListener('input', same);
    mail2.addEventListener('input', same);

    /* submit → request /email/request  */
    form.addEventListener('submit', async e=>{
        e.preventDefault();
        if(!same()) return;

        const data = new URLSearchParams(new FormData(form));

        const r = await fetch(form.action,{
            method : 'POST',
            headers: {'Content-Type':'application/x-www-form-urlencoded'},
            body   : data
        });

        if(r.ok){
            const {token} = await r.json();   // zwracamy tymczasowy identyfikator sesji zmiany
            askCode(token);
        }else{
            showConfirm('Błąd','Nieprawidłowe hasło.','OK');
        }
    });

    /* -------- krok 2 – kod -------- */
    async function askCode(token){
        const ok = await showConfirm('Wprowadź kod',
            '<input id="codeInput" placeholder="6-cyfrowy kod" style="width:100%;padding:.6rem;font-size:1.4rem">',
            'Potwierdź','Anuluj');

        if(!ok) return;
        const code = document.getElementById('codeInput').value.trim();

        const r = await fetch('/api/account/email/confirm',{
            method :'POST',
            headers:{'Content-Type':'application/json'},
            body   : JSON.stringify({token, code})
        });

        if(r.ok){
            await showConfirm('Sukces','Adres e-mail został zmieniony.','OK');
            location.href='/account';
        }else{
            await showConfirm('Błąd','Niepoprawny kod.','OK');
        }
    }
});
