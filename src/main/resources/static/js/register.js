/* rejestracja – siła hasła, zgodność, 👁 */
document.addEventListener('DOMContentLoaded', () => {

    const form = document.getElementById('regForm');
    const p1   = form.password;
    const p2   = form.password2;
    const same = document.getElementById('errSame');
    const met  = document.getElementById('pwdStrength');

    /* 👁 */
    document.querySelectorAll('.toggle.eye').forEach(btn=>{
        btn.onclick = ()=>{
            const inp = btn.previousElementSibling;
            inp.type  = inp.type === 'password' ? 'text' : 'password';
        };
    });

    /* siła */
    const rule=/^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)(?=.*[^\w\s]).+$/;
    const power=s=>{
        const l=s.length;
        if(l===0)           return{cls:'',txt:''};
        if(l<8||!rule.test(s))return{cls:'weak',txt:'za krótkie / brak złożoności'};
        if(l<=9)            return{cls:'good',txt:'dobre'};
        if(l<=12)           return{cls:'strong',txt:'mocne'};
        return                 {cls:'vstrong',txt:'bardzo mocne!'};
    };
    const upd=()=>{
        const{cls,txt}=power(p1.value);
        met.className='strength '+cls;
        met.textContent=txt;
    };
    p1.addEventListener('input',upd); upd();

    /* zgodność – komunikat dopiero PO dotknięciu pola 2 */
    let touched=false;
    const chk=()=>{
        if(!touched)return;
        const ok=p1.value===p2.value;
        p2.classList.toggle('no-valid',!ok);
        same.style.display=ok?'none':'block';
    };
    p2.addEventListener('input',()=>{touched=true;chk();});
    p1.addEventListener('input',chk);

});
