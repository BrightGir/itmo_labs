
document.addEventListener('DOMContentLoaded', () => {
    const yButtonsContainer = document.getElementById('y-buttons');
    const yHiddenInput = document.getElementById('y-hidden');
    const rButtonsContainer = document.getElementById('r-buttons');
    const rHiddenInput = document.getElementById('r-hidden');
    setupButtonGroup(yButtonsContainer, yHiddenInput);
    setupButtonGroup(rButtonsContainer, rHiddenInput);

    function setupButtonGroup(buttonContainer, hiddenInput) {
        buttonContainer.addEventListener('click', (event) => {
            const clickedElement = event.target;
            if (clickedElement.tagName !== 'BUTTON') return;
            const isSelected = clickedElement.classList.contains("selected");
            if(!isSelected) {
                hiddenInput.value = clickedElement.dataset.value;
            } else {
                hiddenInput.value = '';
            }
            const allButtons = buttonContainer.querySelectorAll('button');
            allButtons.forEach(button => button.classList.remove('selected'));
            if(!isSelected) {
                clickedElement.classList.add('selected');
            }
        }, true);
    }
});