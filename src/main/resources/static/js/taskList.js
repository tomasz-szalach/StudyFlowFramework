document.addEventListener('DOMContentLoaded', () => {
    const taskLists     = document.querySelectorAll('.TASKLIST');
    const taskContainer = document.querySelector('.task-display');


    /* ----- helpers ----- */
    const DAY=86_400_000;
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
    const addSparkle = item=>{
        const h=item.querySelector('.task-header');
        for(let i=0;i<6;i++){
            const s=document.createElement('div');
            s.className='sparkle-effect';
            Object.assign(s.style,{
                '--x':`${Math.random()*100-50}px`,
                '--y':`${-Math.random()*80}px`,
                '--r':`${Math.random()*720-360}deg`,
                animationDelay:`${i*0.05}s`
            });
            h.append(s); setTimeout(()=>s.remove(),1200);
        }
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


    /* ----- sekcja Wykonane ----- */
    const createCompletedSection = ()=>{
        const wrap=document.createElement('div'); wrap.className='completed-section expanded'; /* ← dodany expanded */
        const head=document.createElement('div'); head.className='completed-header';
        head.innerHTML='<span class="arrow">▾</span> Wykonane (<span class="done-count">0</span>)';
        const body=document.createElement('div'); body.className='completed-body';
        head.addEventListener('click',()=>{
            wrap.classList.toggle('expanded');
            head.querySelector('.arrow').textContent = wrap.classList.contains('expanded') ? '▾' : '▸';
        });
        wrap.append(head,body);
        return {wrap,body};
    };

    const refreshCount = s=>{
        const n=s.querySelectorAll('.task-item').length;
        const span=s.querySelector('.done-count');
        if(!span) return;
        if(n===0) s.remove(); else span.textContent=n;
    };

    /* ----- sidebar click ----- */
    taskLists.forEach(l=>l.addEventListener('click',e=>{
        e.preventDefault(); fetchTasks(l.dataset.tasklistId);
    }));

    async function fetchTasks(id){
        const res=await fetch(`/getTasks?id=${id}`); if(!res.ok) return;
        renderTasks((await res.json()).tasks);
    }

    /* ----- render listy ----- */
    function renderTasks(tasks){
        taskContainer.innerHTML='';
        const active  = tasks.filter(t=>t.status!=='completed');
        const done    = tasks.filter(t=>t.status==='completed');

        active.forEach(t=>taskContainer.append(createTaskItem(t,false)));

        if(done.length){
            const {wrap,body}=createCompletedSection();
            done.forEach(t=>body.append(createTaskItem(t,true)));
            refreshCount(wrap);
            taskContainer.append(wrap);
        }
    }

    /* ----- poj. kartka ----- */
    function createTaskItem(t,done){
        const tpl=document.querySelector('#task-template').content.cloneNode(true);
        const item=tpl.querySelector('.task-item');
        item.dataset.taskId=t.id;
        if(done) item.classList.add('completed');

        const name=tpl.querySelector('.name'); name.textContent=t.name;
        if(done) name.classList.add('completed');

        const due=t.due_date||t.dueDate;
        const dueDiv=tpl.querySelector('.due-date');
        if(due){ dueDiv.textContent=`Termin wykonania zadania: ${due}`; dueDiv.dataset.raw=due; }

        const box=document.createElement('div'); box.className='status-box';
        const st =document.createElement('div'); st.className='status';
        st.textContent=done?'Zakończone':'Do zrobienia'; box.append(st);
        const info=deadlineLabel(due);
        if(info && !done){
            const badge=document.createElement('span');
            badge.className=`deadline-info ${info.cls}`; badge.textContent=info.txt;
            box.append(badge);
        }

        const header=tpl.querySelector('.task-header');
        header.insertBefore(box, header.querySelector('.delete-task-button'));

        const cb=tpl.querySelector('.task-checkbox');
        cb.checked=done; cb.addEventListener('change',()=>toggleStatus(item));

        tpl.querySelector('.delete-task-button')
            .addEventListener('click',()=>deleteTask(t.id,item));

        return tpl;
    }

    /* ----- toggle ----- */
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
        } else addSparkle(item);

        moveItem(item,toDone);
    }

    /* ----- przeniesienie kartki ----- */
    function moveItem(item,toDone){
        let section = taskContainer.querySelector('.completed-section');

        if(toDone){ /* → Wykonane */
            item.classList.add('anim-out');
            setTimeout(()=>{
                item.classList.remove('anim-out');

                if(!section){
                    const s = createCompletedSection();
                    taskContainer.append(s.wrap); section = s.wrap;
                    /* auto-expand przy pierwszym zadaniu */
                    section.classList.add('expanded');
                }
                /* jeśli sekcja była zwinięta – rozwiń na chwilę */
                if(!section.classList.contains('expanded')){
                    section.classList.add('expanded');
                    setTimeout(()=>section.classList.remove('expanded'), 800); // 0.8 s podglądu
                }

                section.querySelector('.completed-body').prepend(item);
                item.classList.add('anim-in');
                refreshCount(section);
            },250);                     // 0.25 s = czas anim-out
        }else{   /* ← Aktywne */
            item.classList.add('anim-out');
            setTimeout(()=>{
                item.classList.remove('anim-out');
                taskContainer.insertBefore(item, taskContainer.firstChild);
                item.classList.add('anim-in');
                if(section) refreshCount(section);
            },250);
        }
        item.addEventListener('animationend',()=>item.classList.remove('anim-in'),{once:true});
    }

    /* ----- delete ----- */
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

});
