/* animacja strzałki <select> */
document.addEventListener('DOMContentLoaded', () => {
    document.querySelectorAll('.task-form select').forEach(sel => {
        /* focus/blur działa w każdym browserze */
        sel.addEventListener('focus', () => sel.classList.add('open'));
        sel.addEventListener('blur',  () => sel.classList.remove('open'));
    });
});

/* inicjalizacja Choices.js po załadowaniu biblioteki */
window.addEventListener('load', () => {
    document.querySelectorAll('[data-choices]').forEach(sel => {
        new Choices(sel, {
            searchEnabled: false,      // bez pola wyszukiwania
            itemSelectText: '',        // brak napisu “Press to select”
            shouldSort: false,
            classNames: { containerOuter: 'choices myChoices' } // własna klasa do stylu
        });
    });
});
