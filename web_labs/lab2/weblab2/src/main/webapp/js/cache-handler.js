
import {updateGraph, updateHistory1, updateNowHistory} from './graph-handler.js';


window.addEventListener('pageshow', (event) => {
    if (event.persisted) {
        requestApi();
    }
});

export function requestApi() {
    fetch(`${window.contextPath}/checkServlet/history`).then(response => {
        if (!response.ok) {
            return;
        }
        return response.json();
    }).then(respJson => {
        updateNowHistory(respJson);
        updateTable(respJson);
        updateHistory1(respJson);
    }).catch(error => {

    });
}

function updateTable(history) {
    const tableBody = document.querySelector(".table-container table tbody");


    tableBody.innerHTML = '';
    history.forEach(result => {
        const newRow = tableBody.insertRow(0);
        newRow.insertCell(0).textContent = result.x;
        newRow.insertCell(1).textContent = result.y;
        newRow.insertCell(2).textContent = result.r;
        newRow.insertCell(3).textContent = result.hit ? "Попадание" : "Промах";
        newRow.insertCell(4).textContent = result.currentTime;
        newRow.insertCell(5).textContent = result.executionTime;
    });
}