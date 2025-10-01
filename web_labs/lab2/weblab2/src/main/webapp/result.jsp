

<%@ page contentType="text/html; charset=UTF-8" language="java" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="ru">
<head>
    <meta charset="UTF-8">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/result-style.css">
</head>
<body>

<header class="header">

</header>

<main>
    <div class="result-container">
        <h2>Результат проверки</h2>
        <table class="result-table">
            <tr>
                <td>Координата X</td>
                <td>${result.x}</td>
            </tr>
            <tr>
                <td>Координата Y</td>
                <td>${result.y}</td>
            </tr>
            <tr>
                <td>Радиус R</td>
                <td>${result.r}</td>
            </tr>
            <tr>
                <td>Результат</td>
                <td>
                    ${result.hit ? "Попадание" : "Промах"}
                </td>
            </tr>
            <tr>
                <td>Время проверки</td>
                <td>${result.currentTime}</td>
            </tr>
            <tr>
                <td>Время выполнения (с)</td>
                <td>${result.executionTime}</td>
            </tr>
        </table>
        <a href="${pageContext.request.contextPath}/" class="back-button">Вернуться назад</a>
    </div>
</main>
</body>
</html>