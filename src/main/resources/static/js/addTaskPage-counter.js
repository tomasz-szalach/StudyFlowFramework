document.addEventListener('DOMContentLoaded', () => {
    const ta  = document.getElementById('task-desc');     // textarea
    const cnt = document.getElementById('descCounter');   // licznik
    const MAX = 1000;

    const update = () => {
        const len = ta.value.length;
        cnt.textContent = `${len} / ${MAX}`;
        cnt.classList.toggle('limit', len > MAX);           // czerwony, gdy > MAX
    };

    ta.addEventListener('input', update);
    update();                                             // stan startowy
});
