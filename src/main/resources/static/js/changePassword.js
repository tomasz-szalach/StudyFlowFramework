document.addEventListener('DOMContentLoaded', function () {
    const changePasswordButton = document.getElementById('changePasswordButton');

    if (changePasswordButton) {
        changePasswordButton.addEventListener('click', function () {
            window.location.href = '/changePasswordPage';
        });
    }
});
