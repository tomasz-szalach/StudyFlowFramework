const form = document.querySelector("form");
const passwordInput = form.querySelector('input[name="new_pass"]');
const passwordInput2 = form.querySelector('input[name="new_pass2"]');

function arePasswordSame(password, pass2){
    return password===pass2;
}

function markValidation(element, condition) {
    !condition ? element.classList.add("no-valid") : element.classList.remove("no-valid");
}

function validatePassword2(){
    setTimeout(
        function(){
            markValidation(passwordInput2, arePasswordSame(
                passwordInput.value,
                passwordInput2.value
            ));
        },
        1000);
}

passwordInput2.addEventListener('keyup', validatePassword2);
