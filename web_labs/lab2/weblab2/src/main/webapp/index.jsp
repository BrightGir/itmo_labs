<%@ page contentType="text/html; charset=UTF-8" language="java" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<!DOCTYPE html>
<html lang="ru">
<head>
    <meta charset="UTF-8">
    <title>Лабораторная работа</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/index-style.css">
</head>
<body>
    <header class="header">
        <h1>Лабораторная работа №2</h1>
        <p>Фонарева Виктория Сергеевна</p>
        <p>Группа: P3210</p>
    </header>
    <main>
        <div class="form-container">
            <h2>Параметры</h2>
            <form id="check-form" method="GET" action="${pageContext.request.contextPath}">
                <div class="form-group">
                    <label for="x-input">Координата X (от -5 до 5)</label>
                    <input type="text" id="x-input" name="x" placeholder="Введите X">
                    <span class="error-message" id="x-error"></span>
                </div>

                <div class="form-group">
                    <label>Координата Y</label>
                    <div class="button-group" id="y-buttons">
                        <button type="button" data-value="-4">-4</button>
                        <button type="button" data-value="-3">-3</button>
                        <button type="button" data-value="-2">-2</button>
                        <button type="button" data-value="-1">-1</button>
                        <button type="button" data-value="0">0</button>
                        <button type="button" data-value="1">1</button>
                        <button type="button" data-value="2">2</button>
                        <button type="button" data-value="3">3</button>
                        <button type="button" data-value="4">4</button>
                    </div>
                    <input type="hidden" id="y-hidden" name="y">
                    <span class="error-message" id="y-error"></span>
                </div>

                <div class="form-group">
                    <label>Радиус R</label>
                    <div class="button-group" id="r-buttons">
                        <button type="button" data-value="1">1</button>
                        <button type="button" data-value="1.5">1.5</button>
                        <button type="button" data-value="2">2</button>
                        <button type="button" data-value="2.5">2.5</button>
                        <button type="button" data-value="3">3</button>
                    </div>
                    <input type="hidden" id="r-hidden" name="r">
                    <span class="error-message" id="r-error"></span>
                </div>
                <button type="submit" id="submit-button">Проверить</button>
            </form>
        </div>
        <div class="graph-container">
            <h2>График</h2>
            <div class="graph-wrapper">
                <svg id="graph-svg" width="500" height="500" xmlns="http://www.w3.org/2000/svg">
                    <g id="shapes" fill="#48D1CC">
                        <rect id="graph-rect" height="0" width="0"></rect>
                        <polygon id="graph-polygon" points="0,0 0,0 0,0"></polygon>
                        <path id="graph-path" d=""></path>
                    </g>
                    <g id="labels" fill="black" font-size="14">
                        <text x="480" y="240">X</text>
                        <text x="260" y="20">Y</text>

                        <text id="label-x-r" x="445" y="240"></text>
                        <text id="label-x-r-half" x="345" y="240"></text>
                        <text id="label-x-neg-r-half" x="140" y="240"></text>
                        <text id="label-x-neg-r" x="40" y="240"></text>

                        <text id="label-y-r" x="260" y="55"></text>
                        <text id="label-y-r-half" x="260" y="155"></text>
                        <text id="label-y-neg-r-half" x="260" y="355"></text>
                        <text id="label-y-neg-r" x="260" y="455"></text>
                    </g>
                    <g id="axes" stroke="black">
                        <line x1="250" y1="500" x2="250" y2="0"></line>
                        <polygon points="250,0 245,15 255,15" fill="black"></polygon>
                        <line x1="0" y1="250" x2="500" y2="250"></line>
                        <polygon points="500,250 485,245 485,255" fill="black"></polygon>
                    </g>
                    <g id="history-points">

                    </g>
                    <g id="history-points">

                    </g>
                </svg>
            </div>
        </div>
        <div class="results-container">
            <h2>История проверок</h2>
            <div class="table-container">
                <table>
                    <thead>
                        <tr>
                            <th>X</th>
                            <th>Y</th>
                            <th>R</th>
                            <th>Результат</th>
                            <th>Время проверки</th>
                            <th>Время выполнения (с)</th>
                        </tr>
                    </thead>
                    <tbody>
                    <c:forEach var="res" items="${requestScope.resultsReversed}">
                        <tr>
                            <td>${res.x}</td>
                            <td>${res.y}</td>
                            <td>${res.r}</td>
                            <td>${res.hit ? "Попадание" : "Промах"}</td>
                            <td>${res.currentTime}</td>
                            <td>${res.executionTime}</td>
                        </tr>
                    </c:forEach>
                    </tbody>
                </table>
            </div>
        </div>
    </main>
    <script>
        window.historyPoints = ${not empty historyJson ? historyJson : '[]'};
        window.contextPath= '${pageContext.request.contextPath}';
    </script>
    <script type="module" src="${pageContext.request.contextPath}/js/validator.js"></script>
    <script type="module" src="${pageContext.request.contextPath}/js/buttons-selector.js"></script>
    <script type="module" src="${pageContext.request.contextPath}/js/graph-handler.js"></script>
    <script type="module" src="${pageContext.request.contextPath}/js/cache-handler.js"></script>
</body>
</html>