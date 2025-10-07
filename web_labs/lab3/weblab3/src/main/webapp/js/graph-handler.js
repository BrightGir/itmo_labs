import { getBatmanPath } from "./batman.js";

let labelsGroup, rect, polygon, path, svg, form, currentRElement;

const centerX = 350;
const centerY = 350;
const ONE_UNIT_PX = 47;
let labels;
export function updateGraph() {
    const rInput = document.getElementById('check-form:r-input_input');
    const currentR = rInput ? Number((rInput.value).replace(',','.')) : 0;
    drawShapes(currentR);
    updateLabels(currentR);
    drawHistoryPoints(currentR);
}

function updateGraphWithValue(R) {
    if(isNaN(R)) return;
    const currentR = R;
    drawShapes(currentR);
    updateLabels(currentR);
    drawHistoryPoints(currentR);
}

window.updateGraph = updateGraph;
window.updateGraphWithValue = updateGraphWithValue;
window.setGraphClickValues = setGraphClickValues;
window.setFormTimestamp = setFormTimestamp;


document.addEventListener('DOMContentLoaded', () => {
    // Инициализируем все элементы после загрузки DOM
    labelsGroup = document.getElementById('labels');
    rect = document.getElementById('graph-rect');
    polygon = document.getElementById('graph-polygon');
    path = document.getElementById('graph-path');
    svg = document.getElementById("graph-svg");
    form = document.getElementById("check-form");

    labels = {
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

    updateGraph();
});



function drawShapes(R) {
    if(R <= 0) {
        path.setAttribute('visibility', 'hidden');
        Object.values(labels.x).forEach(label => {label.textContent=''})
        Object.values(labels.y).forEach(label => {label.textContent=''})
        return;
    }
    path.setAttribute('visibility', 'visible');
    const pathData = getBatmanPath(R, ONE_UNIT_PX, centerX, centerY);
    path.setAttribute('d', pathData);
}




function drawHistoryPoints(R) {
    const historyPointsContainer = document.getElementById('history-points');
    historyPointsContainer.innerHTML = '';
    if(R <= 0) {
        return;
    }
    let points = [];
    const resultsJsonElement = document.getElementById('hiddenForm:resultsJson');
    if (resultsJsonElement) {
        const jsonData = resultsJsonElement.textContent;
        console.log("json data - " + jsonData);
        if (jsonData) {
            try {
                points = JSON.parse(jsonData);
            } catch (e) {
                console.error("Error parsing results JSON:", e);
            }
        }
    }
    points.forEach(point => {
        const x = parseFloat((point.x));
        const y = parseFloat((point.y));
        const r = parseFloat((point.r));
        let cx = 0;
        let cy = 0;
        let prop = R/r;
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
        labels.x.r.textContent=``;
        labels.x.rHalf.textContent=``;
        labels.x.negR.textContent='';
        labels.x.negRHalf.textContent=``;

        labels.y.r.textContent=``;
        labels.y.rHalf.textContent=``;
        labels.y.negR.textContent=``;
        labels.y.negRHalf.textContent=``;

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

    const rPx = R * ONE_UNIT_PX;
    const rHalfPx = (R / 2) * ONE_UNIT_PX;

    labels.x.r.setAttribute('x', centerX + rPx);
    labels.x.r.setAttribute('y', centerY + 15);
    labels.x.rHalf.setAttribute('x', centerX + rHalfPx);
    labels.x.rHalf.setAttribute('y', centerY + 15);
    labels.x.negRHalf.setAttribute('x', centerX - rHalfPx);
    labels.x.negRHalf.setAttribute('y', centerY + 15);
    labels.x.negR.setAttribute('x', centerX - rPx);
    labels.x.negR.setAttribute('y', centerY + 15);


    labels.y.r.setAttribute('y', centerY - rPx);
    labels.y.r.setAttribute('x', centerX + 15);
    labels.y.rHalf.setAttribute('y', centerY - rHalfPx);
    labels.y.rHalf.setAttribute('x', centerX + 15);
    labels.y.negRHalf.setAttribute('y', centerY + rHalfPx);
    labels.y.negRHalf.setAttribute('x', centerX + 15);
    labels.y.negR.setAttribute('y', centerY + rPx);
    labels.y.negR.setAttribute('x', centerX + 15);
}


function setGraphClickValues(event) {
    const svgRect = svg.getBoundingClientRect();
    const clickX_px = event.clientX - svgRect.left;
    const clickY_px = event.clientY - svgRect.top;

    const mathX = (clickX_px - centerX) / ONE_UNIT_PX;
    const mathY = (centerY - clickY_px) / ONE_UNIT_PX;

    const rInput = document.getElementById('check-form:r-input_input');
    const currentR = rInput ? Number((rInput.value).replace(',','.')) : 0;

    if (currentR <= 0) {
        return false;
    }

    const graphXField = document.getElementById('submit-from-graph-form:graphX');
    const graphYField = document.getElementById('submit-from-graph-form:graphY');
    const graphRField = document.getElementById('submit-from-graph-form:graphR');
    const clientTimeStamp = document.getElementById('submit-from-graph-form:timeStamp');
    const clientTimeZone = document.getElementById('submit-from-graph-form:timeZone');

    if (graphXField) graphXField.value = mathX.toFixed(3);
    if (graphYField) graphYField.value = mathY.toFixed(3);
    if (graphRField) graphRField.value = currentR;
    if(clientTimeStamp) clientTimeStamp.value = Date.now();
    if(clientTimeZone) clientTimeZone.value = Intl.DateTimeFormat().resolvedOptions().timeZone;
    return true;
}

function setFormTimestamp() {
    const clientTimeStamp = document.getElementById('check-form:formClientTimeStamp');
    const clientTimeZone = document.getElementById('check-form:formClientTimeZone');
    if(clientTimeStamp) clientTimeStamp.value = Date.now();
    if(clientTimeZone) clientTimeZone.value = Intl.DateTimeFormat().resolvedOptions().timeZone;
}