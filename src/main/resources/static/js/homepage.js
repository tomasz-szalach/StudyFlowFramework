/* ===============================================
   homepage.js – StudyFlow  |  pełna wersja
   =============================================== */
document.addEventListener('DOMContentLoaded', () => {
    const pageTitle   = document.querySelector('.page-title');
    const taskDisplay = document.querySelector('.task-display');
    const taskLinks   = document.querySelectorAll('.TASKLIST');
    const searchInput = document.getElementById('search');

    /* ========== helpers ========== */
    const DAY = 86_400_000;
    const deadlineLabel = d=>{
        if(!d) return null;
        const diff=Math.round((new Date(d).setHours(0,0,0,0)-new Date().setHours(0,0,0,0))/DAY);
        if(diff<0)  return {cls:'deadline-red',        txt:'PO TERMINIE'};
        if(diff===0)return {cls:'deadline-orange-dark',txt:'< 24 H'};
        if(diff===1)return {cls:'deadline-orange-dark',txt:'1 DZIEŃ'};
        if(diff<=3) return {cls:'deadline-orange',     txt:`${diff} DNI`};
        if(diff<=7) return {cls:'deadline-yellow',     txt:`${diff} DNI`};
        return        {cls:'deadline-green',           txt:'PONAD TYDZIEŃ'};
    };

    /* === uniwersalne okno „Czy na pewno?” === */
    function showConfirm(title, msg, confirmTxt = 'Usuń') {
        return new Promise(resolve => {
            const back = document.createElement('div'); back.className = 'modal-backdrop';

            const box  = document.createElement('div'); box.className  = 'modal-box';
            box.innerHTML = `
      <h3>${title}</h3>
      <p style="margin:0">${msg}</p>

      <div class="modal-actions">
        <button class="cancel"  type="button">Anuluj</button>
        <button class="confirm" type="button">${confirmTxt}</button>
      </div>`;

            back.append(box); document.body.append(back);

            const close = ok => { back.remove(); resolve(ok); };

            box.querySelector('.confirm').addEventListener('click', () => close(true));
            box.querySelector('.cancel' ).addEventListener('click', () => close(false));

            /* klik w tło też zamyka */
            back.addEventListener('click', e=>{
                if(e.target === back) close(false);
            });
        });
    }


    /* ========== sekcja Wykonane ========== */
    const createCompletedSection=()=>{
        const wrap=document.createElement('div'); wrap.className='completed-section expanded';
        const head=document.createElement('div'); head.className='completed-header';
        head.innerHTML='<span class="arrow">▾</span> Wykonane (<span class="done-count">0</span>)';
        const body=document.createElement('div'); body.className='completed-body';
        head.addEventListener('click',()=>{
            wrap.classList.toggle('expanded');
            head.querySelector('.arrow').textContent = wrap.classList.contains('expanded') ? '▾' : '▸';
        });
        wrap.append(head,body); return {wrap,body};
    };
    const refreshCount=s=>{
        const n=s.querySelectorAll('.task-item').length;
        const span=s.querySelector('.done-count');
        if(!span) return;
        if(n===0) s.remove(); else span.textContent=n;
    };

    /* ========== ładowanie list ========== */
    taskLinks.forEach(link=>{
        link.addEventListener('click',e=>{
            e.preventDefault();
            taskLinks.forEach(l=>l.classList.remove('selected'));
            link.classList.add('selected');
            pageTitle.textContent='Zadania '+link.querySelector('.name').textContent;
            fetchList(`/api/tasks/tasklists/${link.dataset.tasklistId}/tasks`);
        });
    });
    searchInput?.addEventListener('input',()=>{
        const sel=document.querySelector('.TASKLIST.selected'); if(!sel) return;
        fetchList(`/api/tasks/tasklists/${sel.dataset.tasklistId}/search?query=${encodeURIComponent(searchInput.value)}`);
    });
    taskLinks[0]?.click();

    async function fetchList(url){
        const res=await fetch(url); if(!res.ok) return;
        renderTasks(await res.json());
    }

    /* ========== render ========== */
    function renderTasks(tasks){
        taskDisplay.innerHTML='';
        const active=tasks.filter(t=>t.status!=='completed');
        const done  =tasks.filter(t=>t.status==='completed');

        active.forEach(t=>taskDisplay.append(createTaskItem(t,false)));
        if(done.length){
            const {wrap,body}=createCompletedSection();
            done.forEach(t=>body.append(createTaskItem(t,true)));
            refreshCount(wrap);
            taskDisplay.append(wrap);
        }
    }

    function createTaskItem(t,completed){
        const item=document.createElement('div'); item.className='task-item'; if(completed) item.classList.add('completed');
        item.dataset.taskId=t.id;

        const header=document.createElement('div'); header.className='task-header';
        const cb=document.createElement('input');  cb.type='checkbox'; cb.className='task-checkbox'; cb.checked=completed;
        const name=document.createElement('div');  name.className='name'; name.textContent=t.name;
        if(completed) name.classList.add('completed');
        const dueRaw=t.dueDate||t.due_date;
        const dueDiv=document.createElement('div'); dueDiv.className='due-date';
        if(dueRaw){ dueDiv.textContent=`Termin wykonania zadania: ${dueRaw}`; dueDiv.dataset.raw=dueRaw; }

        const box=document.createElement('div'); box.className='status-box';
        const st=document.createElement('div');  st.className='status'; st.textContent=completed?'Zakończone':'Do zrobienia';
        box.append(st);
        const info=deadlineLabel(dueRaw);
        if(info && !completed){
            const badge=document.createElement('span');
            badge.className=`deadline-info ${info.cls}`; badge.textContent=info.txt;
            box.append(badge);
        }

        const del=document.createElement('button'); del.className='delete-task-button'; del.textContent='🗑️';

        header.append(cb,name,dueDiv,box,del);
        const desc=document.createElement('div'); desc.className='description'; desc.textContent=t.description||'';
        item.append(header,desc);

        cb.addEventListener('change',()=>toggleStatus(item));
        del.addEventListener('click',()=>deleteTask(item));
        return item;
    }

    /* ========== toggle status ========== */
    async function toggleStatus(item){
        const id=item.dataset.taskId;
        const cb=item.querySelector('.task-checkbox');
        const res=await fetch(`/api/tasks/${id}/toggle`,{method:'PATCH'});
        if(!res.ok){ cb.checked=!cb.checked; return; }

        const toDone=(await res.text())==='completed';
        const name=item.querySelector('.name');
        const box =item.querySelector('.status-box');
        const st  =box.querySelector('.status');

        name.classList.toggle('completed',toDone);
        item.classList.toggle('completed',toDone);
        st.textContent=toDone?'Zakończone':'Do zrobienia';

        box.querySelectorAll('.deadline-info').forEach(b=>b.remove());
        if(!toDone){
            const info=deadlineLabel(item.querySelector('.due-date').dataset.raw);
            if(info){
                const badge=document.createElement('span');
                badge.className=`deadline-info ${info.cls}`; badge.textContent=info.txt;
                box.append(badge);
            }
        }

        moveItem(item,toDone);
    }

    function moveItem(item,toDone){
        let section=taskDisplay.querySelector('.completed-section');

        if(toDone){
            item.classList.add('anim-out');
            setTimeout(()=>{
                item.classList.remove('anim-out');
                if(!section){ const s=createCompletedSection(); taskDisplay.append(s.wrap); section=s.wrap; }
                section.querySelector('.completed-body').prepend(item);
                item.classList.add('anim-in'); refreshCount(section);
            },250);
        }else{
            item.classList.add('anim-out');
            setTimeout(()=>{
                item.classList.remove('anim-out');
                taskDisplay.insertBefore(item, taskDisplay.firstChild);
                item.classList.add('anim-in'); if(section) refreshCount(section);
            },250);
        }
        item.addEventListener('animationend',()=>item.classList.remove('anim-in'),{once:true});
    }

    /* ========== delete task ========== */
    async function deleteTask(item){
        const ok = await showConfirm('Usuń zadanie',
            'Czy na pewno chcesz usunąć to zadanie?');
        if(!ok) return;

        const id  = item.dataset.taskId;
        const res = await fetch(`/api/tasks/${id}`, { method:'DELETE' });

        if(res.ok){
            const section = item.closest('.completed-section');
            item.remove();
            if(section) refreshCount(section);
        }else{
            await showConfirm('Błąd', 'Nie udało się usunąć zadania', 'OK');
        }
    }


    /* ========== menu ⋮ w sidebarze ========== */
    let currentMenu=null;
    document.addEventListener('click', showContext, true);   // capture

    function showContext(e){
        const btn=e.target.closest('.list-menu-btn');
        if(!btn) return;

        e.preventDefault(); e.stopPropagation();
        currentMenu?.remove(); currentMenu=null;

        const link = btn.closest('.TASKLIST');
        const id   = btn.dataset.id;
        const name = link.querySelector('.name').textContent;

        /* ---- tworzymy modal (helper) ---- */
        const modal = (title,body,onOK)=>{
            const back=document.createElement('div'); back.className='modal-backdrop';
            const box =document.createElement('div'); box.className='modal-box';
            box.innerHTML=`<h3>${title}</h3>${body}<div class="modal-actions">
                              <button class="modal-secondary">Anuluj</button>
                              <button class="modal-primary">OK</button></div>`;
            back.append(box); document.body.append(back);
            const [cancel,ok]=box.querySelectorAll('button');

            cancel.onclick=()=>back.remove();
            ok.onclick=async ()=>{
                ok.disabled=true;
                const val=box.querySelector('input')?.value;
                const res=await onOK(val);
                if(res!==false) back.remove(); else ok.disabled=false;
            };
        };

        /* ---- menu ---- */
        const menu=document.createElement('div'); menu.className='list-context';
        menu.innerHTML=`<button data-act="rename">Zmień nazwę</button>
                        <button data-act="delete">Usuń listę</button>`;
        document.body.append(menu); currentMenu=menu;

        const r=btn.getBoundingClientRect();
        menu.style.top=`${r.bottom+4}px`; menu.style.left=`${r.left}px`;

        menu.onclick=ev=>{
            const act=ev.target.dataset.act;
            if(!act) return;

            /* --- Zmień nazwę --- */
            if(act==='rename'){
                modal('Zmień nazwę',
                    `<input type="text" value="${name}" autofocus>`,
                    async val=>{
                        if(!val || val.trim()===name) return false;
                        const res=await fetch(`/api/tasklists/${id}`,{      // <<< poprawiony URL
                            method:'PUT',
                            headers:{'Content-Type':'application/json'/*,'X-CSRF-TOKEN':token*/},
                            body:JSON.stringify({name:val.trim()})
                        });
                        if(!res.ok){ alert('Błąd zmiany nazwy'); return false; }
                        link.querySelector('.name').textContent=val.trim();
                        if(link.classList.contains('selected'))
                            pageTitle.textContent='Zadania '+val.trim();
                    });
            }

            /* --- Usuń listę --- */
            if(act==='delete'){
                modal('Usuń listę',
                    `<p>Czy na pewno usunąć listę „<b>${name}</b>”?</p>`,
                    async ()=>{
                        const res=await fetch(`/api/tasklists/${id}`,{     // <<< poprawiony URL
                            method:'DELETE'/*,'X-CSRF-TOKEN':token*/});
                        if(!res.ok){ alert('Błąd usuwania'); return false; }
                        const wasSel=link.classList.contains('selected');
                        link.remove();
                        if(wasSel){
                            document.querySelector('.TASKLIST')?.click();
                            if(!document.querySelector('.TASKLIST')) taskDisplay.innerHTML='';
                        }
                    });
            }

            menu.remove();
        };

        document.addEventListener('click',ev=>{
            if(!ev.target.closest('.list-context')) menu.remove();
        },{once:true,capture:true});
    }
});
