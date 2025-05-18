/* login – podgląd hasła & pokaz błędu ?error */
document.addEventListener('DOMContentLoaded', () => {

    /* 👁 */
    document.querySelectorAll('.toggle.eye').forEach(btn=>{
        btn.onclick = () =>{
            const inp = btn.previousElementSibling;
            inp.type  = inp.type === 'password' ? 'text' : 'password';
        };
    });

    /* spring przekazuje ?error – pokaż elegancko */
    const url = new URLSearchParams(location.search);
    if (url.has('error')) {
        const p = document.querySelector('.error-msg');
        p.textContent = 'Nieprawidłowy e-mail lub hasło.';
        p.style.display = 'block';
    }
});
