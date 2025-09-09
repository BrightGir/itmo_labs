

document.addEventListener('DOMContentLoaded', () => {
    const form = document.getElementById("point-form")
    const xInput = document.getElementById('x-input');
    const rInput = document.getElementById('r-input');
    const pagination = document.getElementById("pagination-container")
    const resultsBody = document.getElementById('results-body');
    const rowsPerPage = 14;
    let currentPage = 1;
    let allResults = [];
    form.addEventListener('submit', (event) => {
        event.preventDefault();
        const isFormValid = validateForm();
        if (!isFormValid) return;
        const xValue = xInput.value.trim().replace(',', '.');
        const yValue = form.querySelector('input[name="y-value"]:checked').value;
        const rValue = rInput.value.trim().replace(',', '.');

        const params = new URLSearchParams();
        params.append("x", xValue)
        params.append("y", yValue)
        params.append("r", rValue)

       // fetch('/cgi-bin/check', {
        fetch('/fcgi-bin/weblab1.jar', {
            method: 'POST',
            body: params
        }).then(response => {
           if(!response.ok) {
               throw new Error(`Сетевая ошибка, сервер ответил со статусом ${response.status}`);
           }
            return response.json();
        }).then(data => {
            if(data.error) {
                alert(`Ошибка сервера: ${data.error}`)
                return;
            }
            addResultToTable(data)
        }).catch(error => {
            alert(`Ошибка ${error.message}`);
        })
    })

    function addResultToTable(data) {
        allResults.unshift(data);
        currentPage = 1;
        displayPage(currentPage);
        setupPagination();
    }



    function validateForm() {
        clearErrors()
        let isValid = true;
        const xValueStr = (xInput.value.trim().replace(',', '.'));
        let xValue = parseFloat(xValueStr);
        if (xValueStr === '') {
            showError(xInput, 'Поле X не может быть пустым.');
            isValid = false;
        } else if (isNaN(xValue) || xValue < -5 || xValue > 5) {
            showError(xInput, 'X должен быть числом от -5 до 5.');
            isValid = false;
        }
        const ySelected = form.querySelector('input[name="y-value"]:checked');
        if (!ySelected) {
            const yFieldset = form.querySelector('fieldset');
            showError(yFieldset, 'Выберите Y');
            isValid = false;
        }
        const rValueStr = (rInput.value.trim().replace(',', '.'));
        let rValue = parseFloat(rValueStr)
        if (rValue === '') {
            showError(rInput, 'Поле R не может быть пустым.');
            isValid = false;
        } else if (isNaN(rValue) || rValue < 1 || rValue > 4) {
            showError(rInput, 'R должен быть числом от 1 до 4.');
            isValid = false;
        }
        return isValid;
    }

    function showError(inputElement, message) {
        const errorSpan = inputElement.nextElementSibling;
        errorSpan.textContent = message;
    }

    function clearErrors() {
        const errorSpans = form.querySelectorAll('.error-message');
        errorSpans.forEach(span => span.textContent = '');
    }

    function setupPagination() {
        pagination.innerHTML = '';
        const pageCount = Math.ceil(allResults.length / rowsPerPage);

        for (let i = 1; i <= pageCount; i++) {
            const btn = createPageButton(i);
            pagination.appendChild(btn);
        }
        updatePaginationButtons();
    }

    function displayPage(page) {
        resultsBody.innerHTML = '';
        currentPage = page;
        const startIndex = (page - 1) * rowsPerPage;
        const endIndex = startIndex + rowsPerPage;
        const paginatedItems = allResults.slice(startIndex, endIndex);

        paginatedItems.forEach(data => {
            const newRow = document.createElement('tr');
            newRow.innerHTML = `
                <td>${parseFloat(data.x).toFixed(3)}</td>
                <td>${parseInt(data.y)}</td>
                <td>${parseFloat(data.r).toFixed(3)}</td>
                <td>${data.isHit ? 'Попадание' : 'Промах'}</td>
                <td>${data.currentTime}</td>
                <td>${data.executionTime}</td>
            `;
            resultsBody.appendChild(newRow);
        });

        updatePaginationButtons();
    }

    function createPageButton(page) {
        const button = document.createElement('button');
        button.innerText = page;

        button.addEventListener('click', () => {
            displayPage(page);
        });

        return button;
    }

    function updatePaginationButtons() {
        const buttons = pagination.querySelectorAll('button');
        buttons.forEach(button => {
            if (parseInt(button.innerText) === currentPage) {
                button.classList.add('active');
            } else {
                button.classList.remove('active');
            }
        });
    }
});

