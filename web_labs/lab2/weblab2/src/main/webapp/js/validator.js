document.addEventListener('DOMContentLoaded', () => {

    const form = document.getElementById('check-form');
    const xInput = document.getElementById('x-input');
    const yHidden = document.getElementById('y-hidden');
    const rHidden = document.getElementById('r-hidden');
    const yButtons = document.getElementById('y-buttons');
    const rButtons = document.getElementById('r-buttons');
    const xError = document.getElementById('x-error');
    const yError = document.getElementById('y-error');
    const rError = document.getElementById('r-error');


    form.addEventListener('submit', (event) => {
        event.preventDefault();

        const isFormValid = validateForm();
        if (isFormValid) {
            form.submit();
        }
    });

    function validateForm() {
        clearAllErrors();
        let isValid = true;

        const xValueStr = xInput.value.trim().replace(',', '.');

        if (xValueStr === '') {
            showError(xInput, xError, 'Поле X не может быть пустым.');
            isValid = false;
        } else if (isNaN(Number(xValueStr))) {
            showError(xInput, xError, 'Значение X должно быть числом.');
            isValid = false;
        } else {
            const xNum = parseFloat(xValueStr);
            if (xNum < -5 || xNum > 5) {
                showError(xInput, xError, 'Значение X должно лежать в отрезке [-5; 5].');
                isValid = false;
            }
        }

        if (yHidden.value === '') {
            showError(yButtons, yError, 'Y не может быть пустым.');
            isValid = false;
        }

        if (rHidden.value === '') {
            showError(rButtons, rError, 'R не может быть пустым.');
            isValid = false;
        }

        return isValid;
    }

    function showError(field, errorSpan, message) {
        errorSpan.textContent = message;
    }

    function clearError(errorSpan) {
        errorSpan.textContent = '';
    }

    function clearAllErrors() {
        clearError(xError);
        clearError(yError);
        clearError(rError);
    }
});

