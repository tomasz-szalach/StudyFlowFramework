/* lekki modal confirm */
window.showConfirm = (title, msg, okText = 'OK', cancelText = 'Anuluj') =>
    new Promise(res => {
        const back = document.createElement('div');
        back.className = 'modal-backdrop';

        back.innerHTML = `
      <div class="modal-box">
        <h3>${title}</h3>
        <p>${msg}</p>
        <div class="modal-actions">
          <button class="cancel">${cancelText}</button>
          <button class="confirm">${okText}</button>
        </div>
      </div>`;
        document.body.append(back);

        back.querySelector('.cancel').onclick = () => {back.remove(); res(false);};
        back.querySelector('.confirm').onclick = () => {back.remove(); res(true);};
    });
