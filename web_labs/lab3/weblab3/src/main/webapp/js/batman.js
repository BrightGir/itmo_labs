
let R = 4, ONE_UNIT_PX = 47, centerX = 350, centerY = 350;
let ellipseA = 7;
let ellipseB = 3;
let toSvgX = (x) => centerX + x * (R/4) * ONE_UNIT_PX;
let toSvgY = (y) => centerY - y * (R/4) * ONE_UNIT_PX;
const s = (x) => x === 0 ? 0 : Math.sqrt(Math.abs(x) / x);
const bottomF = (x) => Math.abs(x/2) - (3*Math.sqrt(33)-7)/112 * x*x - 3 +
    Math.sqrt(1 - Math.pow(Math.abs(Math.abs(x)-2)-1, 2));
const wingF = (x) => (6*Math.sqrt(10))/7 +
    (1.5 - 0.5*Math.abs(x)) * s(Math.abs(x) - 1) -
    (6*Math.sqrt(10))/14 * Math.sqrt(Math.max(0, 4 - Math.pow(Math.abs(x) - 1, 2)));


export function getBatmanPath(R1, ONE_UNIT_PX1, centerX1, centerY1) {
    R = R1;
    ONE_UNIT_PX = ONE_UNIT_PX1;
    centerX = centerX1;
    centerY = centerY1;
    toSvgX = (x) => centerX + x * (R/4) * ONE_UNIT_PX;
    toSvgY = (y) => centerY - y * (R/4) * ONE_UNIT_PX;
    return getLeftEllipsePath()+getBottomPath()+getRightEllipsePath()+getRightWing()+getHead()+getLeftWing();
}

function getLeftEllipsePath() {
    const xEnd = -4;
    const yEnd = -2.46196;
    const xStart = -3;
    const yStart = 2.71052;
    const phiStart = Math.atan2(yStart/ellipseB, xStart/ellipseA);
    const phiEnd = Math.atan2(yEnd/ellipseB, xEnd/ellipseA);
    return getEllipsePath(phiStart, phiEnd, true, true);
}

function getRightEllipsePath() {
    const xEnd = 3;
    const yEnd = 2.71052;
    const xStart = 4;
    const yStart = -2.46196;
    const phiStart = Math.atan2(yStart/ellipseB, xStart/ellipseA);
    const phiEnd = Math.atan2(yEnd/ellipseB, xEnd/ellipseA);
    return getEllipsePath(phiStart, phiEnd, false, false);
}


function getEllipsePath(phiStart, phiEnd, start, left) {
    let path = ''
    const a = ellipseA;
    const b = ellipseB;
    const step = 0.01;
    console.log(phiStart, phiEnd)
    if(start) {
        path += `M ${toSvgX(a*Math.cos(phiStart))} ${toSvgY(b*Math.sin(phiStart))}`
    }
    if(left) {
        if(phiStart < phiEnd) {
            phiStart += 2 * Math.PI;
        } else {
            phiEnd += 2*Math.PI;
        }
    }
    if(phiEnd < phiStart) {
        let temp = phiStart;
        phiStart = phiEnd;
        phiEnd = temp;
    }
    for (let t = phiStart + step; t < phiEnd; t += step) {
        const x = a * Math.cos(t);
        const y = b * Math.sin(t);
        path += ` L ${toSvgX(x)} ${toSvgY(y)}`;
    }
    path += ` L ${toSvgX(a*Math.cos(phiEnd))} ${toSvgY(b*Math.sin(phiEnd))}`;
    return path;
}

function getBottomPath() {
    let path = ''
    for(let x = -4; x <= 4; x += 0.1) {
        path += ` L ${toSvgX(x)} ${toSvgY(bottomF(x))}`
    }
    return path;
}

function getRightWing() {
    let path = '';
    for(let x = 3; x >= 1; x -= 0.1) {
        path += ` L ${toSvgX(x)} ${toSvgY(wingF(x))}`
    }
    return path;
}

function getLeftWing() {
    let path = '';
    for(let x = -1.01; x >= -3; x -= 0.01) {
        path += ` L ${toSvgX(x)} ${toSvgY(wingF(x))}`
    }
    return path;
}

function getHead() {
    let path = '';
    path += ` L ${toSvgX(1)} ${toSvgY(1)}`
    path += ` L ${toSvgX(0.75)} ${toSvgY(3)}`
    path += ` L ${toSvgX(0.5)} ${toSvgY(2.25)}`
    path += ` L ${toSvgX(-0.5)} ${toSvgY(2.25)}`
    path += ` L ${toSvgX(-0.75)} ${toSvgY(3)}`
    path += ` L ${toSvgX(-1)} ${toSvgY(1)}`
    return path;
}

