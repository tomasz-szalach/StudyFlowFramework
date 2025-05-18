/* ──────────────────────────────────────────────────────────────
   StudyFlow – widok listy zadań (homepage)
   ────────────────────────────────────────────────────────────── */
document.addEventListener('DOMContentLoaded', () => {

    /* ========== DOM & zmienne globalne ========== */
    const pageTitle   = document.querySelector('.page-title');
    const taskDisplay = document.querySelector('.task-display');
    const taskLinks   = document.querySelectorAll('.TASKLIST');
    const searchInput = document.getElementById('search');

    /* sort – zapamiętujemy w localStorage */
    let sortField      = localStorage.getItem('sf') || 'due';   // due | priority | created | alpha
    let sortDir        = localStorage.getItem('sd') || 'asc';   // asc | desc
    let currentListId  = null;                                  // aktualnie wybrana lista

    /* elementy UI sortowania */
    const sortBtn   = document.querySelector('.sort-btn');
    const sortLabel = sortBtn.querySelector('.sort-label');
    const sortDirEl = sortBtn.querySelector('.sort-dir');
    const sortMenu  = document.querySelector('.sort-menu');

    /* ========== helpers ========== */
    const DAY     = 86_400_000;
    const MapPr   = { HIGH:0, MEDIUM:1, LOW:2 };                // pomoc do sortowania po priorytecie
    const mapText = { due:'Termin wykonania', priority:'Priorytet',
        created:'Data utworzenia', alpha:'Alfabetycznie' };
    const truncate = (str, n = 140) =>
        str && str.length > n ? str.slice(0, n).trimEnd() + '…' : str;

    function deadlineLabel(d){
        if(!d) return null;
        const diff = Math.round((new Date(d).setHours(0,0,0,0) -
            new Date().setHours(0,0,0,0)) / DAY);
        if(diff < 0)  return {cls:'deadline-red',        txt:'PO TERMINIE'};
        if(diff === 0)return {cls:'deadline-orange-dark',txt:'< 24 H'};
        if(diff === 1)return {cls:'deadline-orange-dark',txt:'1 DZIEŃ'};
        if(diff <= 3)return {cls:'deadline-orange',      txt:`${diff} DNI`};
        if(diff <= 7)return {cls:'deadline-yellow',      txt:`${diff} DNI`};
        return          {cls:'deadline-green',           txt:'PONAD TYDZIEŃ'};
    }

    /* ─ priorytet → klasa + tekst ─ */
    function priorityLabel(prio){
        const k = (prio || 'LOW').toUpperCase();
        switch(k){
            case 'HIGH':   return { cls:'prio-red',    txt:'WYSOKI'  };
            case 'MEDIUM': return { cls:'prio-orange', txt:'ŚREDNI'  };
            default:       return { cls:'prio-green',  txt:'NISKI'   };
        }
    }

    /* ========== SORT – UI ========== */
    function updateSortUI(){
        const map={due:'terminu',priority:'priorytetu',created:'daty utworzenia',alpha:'kolejności alfabetycznej'};
        sortLabel.textContent='Sortuj ↑↓';        // stały napis
        // info-bar
        const info=document.querySelector('.sort-info');
        info.innerHTML=`posortowane według <b>${map[sortField]}</b>
        <button class="rev">${sortDir==='asc'?'▲':'▼'}</button>
        <button class="close">×</button>`;
        info.classList.add('show');
        wireSortInfoBtns();
    }


    /* show / hide menu */
    sortBtn.addEventListener('click', e=>{
        e.stopPropagation();
        sortMenu.classList.toggle('show');
    });
    document.addEventListener('click', ()=>sortMenu.classList.remove('show'));

    /* wybór pola sortowania */
    sortMenu.querySelectorAll('button').forEach(btn=>{
        btn.addEventListener('click', ()=>{
            sortField = btn.dataset.field;
            localStorage.setItem('sf', sortField);
            sortMenu.classList.remove('show');
            updateSortUI();
            fetchCurrent();                       // przeładuj z nowym sortem
        });
    });


    /* ───────── sort-info (▼ / ×) ───────── */
    function wireSortInfoBtns(){
        const info = document.querySelector('.sort-info');
        info.querySelector('.rev' )?.addEventListener('click', ()=>{     // ▲/▼
            sortDir = sortDir==='asc' ? 'desc' : 'asc';
            localStorage.setItem('sd',sortDir);
            updateSortUI(); fetchCurrent();
        });
        info.querySelector('.close')?.addEventListener('click', ()=>{    // ×
            info.classList.remove('show');        // chowamy pasek
            sortField='due'; sortDir='asc';       // wracamy do domyślnego
            localStorage.setItem('sf',sortField);
            localStorage.setItem('sd',sortDir);
            fetchCurrent();
        });
    }


    /* ========== ŁADOWANIE LIST Z SIDEBARU ========== */
    taskLinks.forEach(link=>{
        link.addEventListener('click', e=>{
            e.preventDefault();
            taskLinks.forEach(l=>l.classList.remove('selected'));
            link.classList.add('selected');

            currentListId = link.dataset.tasklistId;
            pageTitle.textContent = 'Zadania ' + link.querySelector('.name').textContent;

            fetchCurrent();
        });
    });
    taskLinks[0]?.click();                       // auto-select pierwszej listy

    /* live-search */
    searchInput?.addEventListener('input', ()=>fetchCurrent());

    /* helper – pobierz listę w aktualnym widoku (sort + filtr) */
    async function fetchCurrent(){
        if(!currentListId) return;
        const base = `/api/tasks/tasklists/${currentListId}`;
        const q    = searchInput?.value?.trim();
        const url  = q ? `${base}/search?query=${encodeURIComponent(q)}` : `${base}/tasks`;
        fetchList(url);
    }

    /* pobierz i renderuj */
    async function fetchList(url){
        const res = await fetch(url);
        if(res.ok){
            const tasks = await res.json();
            renderTasks(tasks);
        }
    }

    /* ========== SORTOWANIE & RENDER ========== */
    function sortTasks(arr){
        const mult = sortDir === 'asc' ? 1 : -1;
        return arr.slice().sort((a,b)=>{
            let x,y;
            switch(sortField){
                case 'due':
                    x = a.dueDate||a.due_date||''; y = b.dueDate||b.due_date||'';
                    return (x>y?1:x<y?-1:0)*mult;
                case 'priority':
                    x = MapPr[(a.priority||'LOW').toUpperCase()];
                    y = MapPr[(b.priority||'LOW').toUpperCase()];
                    return (x-y)*mult;
                case 'created':
                    return (a.id-b.id)*mult;
                default: // alpha
                    return a.name.localeCompare(b.name,'pl')*mult;
            }
        });
    }

    function renderTasks(tasks){
        taskDisplay.innerHTML = '';

        const sorted = sortTasks(tasks);
        const active = sorted.filter(t=>t.status!=='completed');
        const done   = sorted.filter(t=>t.status==='completed');

        active.forEach(t=>taskDisplay.append(createTaskItem(t,false)));

        if(done.length){
            const {wrap,body} = createCompletedSection();
            done.forEach(t=>body.append(createTaskItem(t,true)));
            refreshCount(wrap);
            taskDisplay.append(wrap);
        }
    }

    /* ========== SEKCJA „Wykonane” ========== */
    function createCompletedSection(){
        const wrap = document.createElement('div'); wrap.className='completed-section expanded';
        const head = document.createElement('div'); head.className='completed-header';
        head.innerHTML = '<span class="arrow">▾</span> Wykonane (<span class="done-count">0</span>)';
        const body = document.createElement('div'); body.className='completed-body';

        head.addEventListener('click', ()=>{
            wrap.classList.toggle('expanded');
            head.querySelector('.arrow').textContent = wrap.classList.contains('expanded') ? '▾' : '▸';
        });

        wrap.append(head,body);
        return {wrap,body};
    }
    const refreshCount = s =>{
        const n = s.querySelectorAll('.task-item').length;
        if(!n)  s.remove();
        else    s.querySelector('.done-count').textContent = n;
    };

    /* ========== TASK ITEM ========== */
    function createTaskItem(t, completed){
        const item  = document.createElement('div'); item.className='task-item';
        if(completed) item.classList.add('completed');
        item.dataset.taskId = t.id;

        /* header */
        const header = document.createElement('div'); header.className='task-header';

        const cb = document.createElement('input'); cb.type='checkbox'; cb.className='task-checkbox'; cb.checked=completed;
        const name = document.createElement('div'); name.className='name'; name.textContent=t.name;
        if(completed) name.classList.add('completed');

        const dueRaw = t.dueDate || t.due_date;
        const dueDiv = document.createElement('div'); dueDiv.className='due-date';
        if(dueRaw){ dueDiv.textContent=`Termin wykonania zadania: ${dueRaw}`; dueDiv.dataset.raw=dueRaw; }

        /* box: status + badge + priority */
        const box = document.createElement('div'); box.className='status-box';

        const st  = document.createElement('div'); st.className='status';
        st.textContent = completed ? 'Zakończone' : 'Do zrobienia';
        box.append(st);

        const info = deadlineLabel(dueRaw);
        if(info && !completed){
            const badge = document.createElement('span');
            badge.className = `deadline-info ${info.cls}`; badge.textContent=info.txt;
            box.append(badge);
        }

        const pri = priorityLabel(t.priority);
        const p   = document.createElement('span');
        p.className = `priority-info ${pri.cls}`; p.textContent = pri.txt;
        box.append(p);

        const del = document.createElement('button'); del.className='delete-task-button'; del.textContent='🗑️';

        header.append(cb,name,dueDiv,box,del);
        const desc = document.createElement('div'); desc.className='description'; desc.textContent = truncate(t.description);
        item.append(header,desc);

        cb .addEventListener('change', ()=>toggleStatus(item));
        del.addEventListener('click',   ()=>deleteTask(item));

        item.addEventListener('click', e=>{
            // żeby klik na 🗑 lub checkbox nie otwierał strony
            if(e.target.closest('.task-checkbox, .delete-task-button')) return;
            window.location.href = `/task/${t.id}`;
        });

        return item;
    }

    /* ========== TOGGLE STATUS ========== */
    async function toggleStatus(item){
        const id = item.dataset.taskId;
        const cb = item.querySelector('.task-checkbox');
        const res = await fetch(`/api/tasks/${id}/toggle`, {method:'PATCH'});
        if(!res.ok){ cb.checked=!cb.checked; return; }

        const toDone = (await res.text()) === 'completed';
        const name = item.querySelector('.name');
        const box  = item.querySelector('.status-box');
        const st   = box.querySelector('.status');

        name.classList.toggle('completed', toDone);
        item.classList.toggle('completed', toDone);
        st.textContent = toDone ? 'Zakończone' : 'Do zrobienia';

        box.querySelectorAll('.deadline-info').forEach(b=>b.remove());
        if(!toDone){
            const info = deadlineLabel(item.querySelector('.due-date').dataset.raw);
            if(info){
                const badge=document.createElement('span');
                badge.className=`deadline-info ${info.cls}`; badge.textContent=info.txt;
                box.append(badge);
            }
        }
        moveItem(item,toDone);
    }

    function moveItem(item,toDone){
        let section = taskDisplay.querySelector('.completed-section');

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
        item.addEventListener('animationend', ()=>item.classList.remove('anim-in'), {once:true});
    }

    /* ========== DELETE TASK (korzysta z showConfirm z modal.js) ========== */
    async function deleteTask(item){
        const ok = await showConfirm('Usuń zadanie','Czy na pewno chcesz usunąć to zadanie?');
        if(!ok) return;
        const id  = item.dataset.taskId;
        const res = await fetch(`/api/tasks/${id}`, {method:'DELETE'});
        if(res.ok){
            const section = item.closest('.completed-section');
            item.remove(); if(section) refreshCount(section);
        }else await showConfirm('Błąd','Nie udało się usunąć zadania','OK');
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
