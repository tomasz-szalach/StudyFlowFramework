/* ====================================================================
   changePassword.js  –  StudyFlow
   ==================================================================== */
document.addEventListener('DOMContentLoaded', () => {

    /* ---------- DOM ---------- */
    const form      = document.getElementById('pwdForm'),
        oldIn     = form.old_pass,
        pass1     = form.new_pass,
        pass2     = form.new_pass2,
        sameErr   = document.getElementById('errSame'),
        strength  = document.getElementById('pwdStrength'),
        toast     = document.getElementById('toast');

    /* ---------- oko 👁 ---------- */
    form.querySelectorAll('.toggle').forEach(btn => {
        btn.onclick = () => {
            const inp = btn.previousElementSibling;
            inp.type  = inp.type === 'password' ? 'text' : 'password';
        };
    });

    /* ---------- siła + walidacja treści ---------- */
    const rule = /^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)(?=.*[^\w\s]).+$/;

    /*
        0-7 znaków → za krótkie        (czerwony)
        8-9        → dobre             (good – zielony)
        10-12      → mocne             (strong – jasno-zielony)
        13+        → bardzo mocne!     (vstrong – jasny niebieski)
    */
    function meter(pwd){
        const l = pwd.length;

        if(l === 0)        return {txt:'',                  cls:'',        ok:false};
        if(l < 8)          return {txt:'za krótkie',        cls:'weak',    ok:false};
        if(!rule.test(pwd))return {txt:'nie spełnia wymogów',cls:'weak',    ok:false};
        if(l <= 9)         return {txt:'dobre',             cls:'good',    ok:true };
        if(l <= 12)        return {txt:'mocne',             cls:'strong',  ok:true };
        return               {txt:'bardzo mocne!',         cls:'vstrong', ok:true };
    }

    const updateStrength = () => {
        const {txt, cls, ok} = meter(pass1.value);
        strength.textContent = txt;
        strength.className   = 'strength ' + cls;
        pass1.classList.toggle('no-valid', !ok && pass1.value.length > 0);
    };
    pass1.addEventListener('input', updateStrength);
    updateStrength();

    /* ---------- powtórzone hasła ---------- */
    const validateSame = () => {
        const ok = pass1.value === pass2.value && pass2.value !== '';
        pass2.classList.toggle('no-valid', !ok);
        sameErr.style.display = ok ? 'none' : 'block';
        return ok;
    };
    pass1.addEventListener('input', validateSame);
    pass2.addEventListener('input', validateSame);

    /* ---------- wysyłka ---------- */
    form.addEventListener('submit', async e => {
        e.preventDefault();

        /* siła + zgodność */
        const m = meter(pass1.value);
        if(!m.ok || !validateSame()){
            await showConfirm(
                'Błąd walidacji',
                `Hasło musi mieć min. 8 znaków i zawierać:\n• małą literę\n• wielką literę\n• cyfrę\n• znak specjalny`,
                'OK'
            );
            return;
        }

        /* wysyłamy jako x-www-form-urlencoded (Spring Security form-login) */
        const data = new URLSearchParams(new FormData(form));

        const res = await fetch(form.action, {
            method : 'POST',
            headers: {'Content-Type':'application/x-www-form-urlencoded'},
            body   : data
        });

        if(res.ok){
            showToast('Hasło zmienione ✔︎');
            setTimeout(() => location.href = '/home', 2000);
        }else{
            await showConfirm('Błąd',
                'Nie udało się zmienić hasła (sprawdź hasło pierwotne).',
                'OK');
        }
    });

    /* ---------- toast ---------- */
    function showToast(msg){
        toast.textContent = msg;
        toast.classList.add('show');
        setTimeout(() => toast.classList.remove('show'), 1800);
    }

});
