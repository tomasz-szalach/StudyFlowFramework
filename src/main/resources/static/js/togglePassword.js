function togglePassword(checkbox) {
    const passwordInputs = document.querySelectorAll('.Input_input_password');
    passwordInputs.forEach(input => {
        if (checkbox.checked) {
            input.type = 'text';
        } else {
            input.type = 'password';
        }
    });
}

document.addEventListener('DOMContentLoaded', function () {
    const checkboxes = document.querySelectorAll('input[type="checkbox"][onchange="togglePassword(this)"]');
    checkboxes.forEach(checkbox => {
        checkbox.addEventListener('change', function () {
            togglePassword(this);
        });
    });
});
