
import { requestApi } from './cache-handler.js';


const labelsGroup = document.getElementById('labels');
const rect = document.getElementById('graph-rect');
const polygon = document.getElementById('graph-polygon');
const path = document.getElementById('graph-path');
const svg = document.getElementById("graph-svg");
const form = document.getElementById("check-form");
const centerX = 250;
const centerY = 250;
let selectedR = 0;
const ONE_UNIT_PX = 47;
let currentR = 0;
const labels = {
    x: {
        r: document.getElementById('label-x-r'),
        rHalf: document.getElementById('label-x-r-half'),
        negRHalf: document.getElementById('label-x-neg-r-half'),
        negR: document.getElementById('label-x-neg-r'),
    },
    y: {
        r: document.getElementById('label-y-r'),
        rHalf: document.getElementById('label-y-r-half'),
        negRHalf: document.getElementById('label-y-neg-r-half'),
        negR: document.getElementById('label-y-neg-r'),
    }
};
document.addEventListener('DOMContentLoaded', () => {
    selectedR = getLastHistoryR();
    updateGraph(getLastHistoryR());
    drawHistoryPoints(window.historyPoints, getLastHistoryR());
    setupDynamicR();
    setupGraphInteractive();
});

export function updateGraph(R) {
    if(isNaN(Number(R))) return;
    currentR = Number(R);
    drawShapes(currentR);
    updateLabels(currentR);
}

export function updateHistory(history, R) {
    drawHistoryPoints(history, Number(R));
}


export function updateHistory1(history) {
    drawHistoryPoints(history, getSelectedR());
}


export function updateNowHistory(history) {
    window.historyPoints = history;
}

function getLastHistoryR() {
    let R = 0;
    if(historyPoints != null && historyPoints.length > 0) {
        R = parseFloat((historyPoints[historyPoints.length-1].r).replace(',', '.'));
    }
    return R;
}


function getLastHistoryY() {
    let R = 0;
    if(historyPoints != null && historyPoints.length > 0) {
        R = parseFloat((historyPoints[historyPoints.length-1].x).replace(',', '.'));
    }
    return R;
}

function getLastHistoryX() {
    let R = 0;
    if(historyPoints != null && historyPoints.length > 0) {
        R = parseFloat((historyPoints[historyPoints.length-1].y).replace(',', '.'));
    }
    return R;
}

function drawShapes(R) {
    if(R <= 0) {
        rect.setAttribute('visibility', 'hidden');
        polygon.setAttribute('visibility', 'hidden');
        path.setAttribute('visibility', 'hidden');
        Object.values(labels.x).forEach(label => {label.textContent=''})
        Object.values(labels.y).forEach(label => {label.textContent=''})
        return;
    }
    rect.setAttribute('visibility', 'visible');
    polygon.setAttribute('visibility', 'visible');
    path.setAttribute('visibility', 'visible');
    //прямоугольник
    rect.setAttribute('x', centerX - (R / 2) * ONE_UNIT_PX);
    rect.setAttribute('y', centerY - R * ONE_UNIT_PX);
    rect.setAttribute('width', (R / 2) * ONE_UNIT_PX);
    rect.setAttribute('height', R * ONE_UNIT_PX);

    //треугольник
    const p1 = `${centerX},${centerY}`; // (0,0)
    const p2 = `${centerX - R * ONE_UNIT_PX},${centerY}`; // (-R, 0)
    const p3 = `${centerX},${centerY + (R / 2) * ONE_UNIT_PX}`; // (0, -R/2)
    polygon.setAttribute('points', `${p1} ${p2} ${p3}`);
    //дуга
    const radiusInPixels = (R / 2) * ONE_UNIT_PX;
    // move to line to A Z
    const pathData = `M ${centerX},${centerY} L ${centerX},${centerY - radiusInPixels} A ${radiusInPixels},${radiusInPixels} 0 0,1 ${centerX + radiusInPixels},${centerY} Z`;
    path.setAttribute('d', pathData);
}

function drawHistoryPoints(history, R) {
    const historyPointsContainer = document.getElementById('history-points');
    historyPointsContainer.innerHTML = '';
    if(R <= 0) {
        return;
    }
    history.forEach(point => {
        const Rad = R/3;
        const x = parseFloat((point.x).replace(',', '.'));
        const y = parseFloat((point.y).replace(',', '.'));
        const r = parseFloat((point.r).replace(',', '.'));
        let cx = 0;
        let cy = 0;
        let prop = 0;
        if(getLastHistoryR() === 0) {
            prop = 1;
        } else {
            prop = R/getLastHistoryR();
        }
        cx = (centerX + x * ONE_UNIT_PX*prop);
        cy = (centerY - y * ONE_UNIT_PX*prop);

        const circle = document.createElementNS("http://www.w3.org/2000/svg", "circle");
        circle.setAttribute("cx", cx);
        circle.setAttribute("cy", cy);
        circle.setAttribute("r", "3.5");

        if(point.hit) {
            circle.setAttribute("fill", "#9ACD32");
        } else {
            circle.setAttribute("fill", "#FF0000");
        }


        circle.setAttribute("stroke", "white");
        circle.setAttribute("stroke-width", "1");
        historyPointsContainer.appendChild(circle);
    });
}

function updateLabels(R) {
    if(R <= 0) {
        updateLabels(3);
        return;
    }
    labels.x.r.textContent=`${R}`;
    labels.x.rHalf.textContent=`${R/2}`;
    labels.x.negR.textContent=`${-R}`;
    labels.x.negRHalf.textContent=`${-R/2}`;

    labels.y.r.textContent=`${R}`;
    labels.y.rHalf.textContent=`${R/2}`;
    labels.y.negR.textContent=`${-R}`;
    labels.y.negRHalf.textContent=`${-R/2}`;

    const r_px = R * ONE_UNIT_PX;
    const r_half_px = (R / 2) * ONE_UNIT_PX;

    labels.x.r.setAttribute('x', centerX + r_px - 5);
    labels.x.rHalf.setAttribute('x', centerX + r_half_px - 10);
    labels.x.negRHalf.setAttribute('x', centerX - r_half_px - 20);
    labels.x.negR.setAttribute('x', centerX - r_px - 15);

    labels.y.r.setAttribute('y', centerY - r_px + 5);
    labels.y.rHalf.setAttribute('y', centerY - r_half_px + 5);
    labels.y.negRHalf.setAttribute('y', centerY + r_half_px + 5);
    labels.y.negR.setAttribute('y', centerY + r_px + 5);
}

function setupDynamicR(){
    document.getElementById("r-buttons")
        .addEventListener('click', (event) => {
            const clickedElement = event.target;
            if (clickedElement.tagName !== 'BUTTON') return;
            const isSelected = clickedElement.classList.contains("selected");
            if(isSelected) {
                let R = parseFloat((clickedElement.dataset.value).replace('.',','));
                updateHistory(window.historyPoints, R);
                updateGraph(R);
                selectedR = R;
            } else {
                updateGraph(0);
                updateHistory(window.historyPoints, 0);
                selectedR = 0;
            }
        }, true);
}

function getSelectedR() {
    return selectedR;
}

function setupGraphInteractive() {
    svg.addEventListener('click', even => {
        if(currentR <= 0) return;
        const svgRect = svg.getBoundingClientRect();
        const clickX_px = event.clientX - svgRect.left;
        const clickY_px = event.clientY - svgRect.top;

        const mathX = (clickX_px - centerX) / ONE_UNIT_PX;
        const mathY = (centerY - clickY_px) / ONE_UNIT_PX;


        const fullUrl = new URL(form.getAttribute("action"), window.location.origin);
        if(mathX >= -5 && mathX <= 5 && mathY >= -4 && mathY <= 4) {
            fullUrl.searchParams.set('x', mathX);
            fullUrl.searchParams.set('y', mathY);
            fullUrl.searchParams.set('r', currentR);
            window.location.href = fullUrl.toString();
        }
    });
}