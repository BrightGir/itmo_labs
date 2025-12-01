
# Конфигурация JSF: FacesServlet и faces-config.xml

Правильная конфигурация — это фундамент JSF-приложения. Она определяет, как веб-сервер распознает JSF-запросы и как фреймворк должен себя вести. В центре этого процесса находятся два ключевых элемента: сервлет `FacesServlet` и файл `faces-config.xml`.

---

## 1. Класс `FacesServlet` — Сердце и мозг JSF

**`FacesServlet`** — это стандартный Java Servlet, который является **единственной точкой входа** для всех HTTP-запросов к JSF-приложению. Он реализует паттерн проектирования **Front Controller (Единый контроллер)**.

### Его ключевые задачи:

1.  **Перехват запросов**: Он настроен так, чтобы перехватывать все запросы, которые соответствуют определенному URL-шаблону (например, все, что заканчивается на `.xhtml`).
2.  **Создание `FacesContext`**: Для каждого входящего запроса `FacesServlet` создает объект `FacesContext`. Этот объект — это "контейнер", который хранит всю информацию, необходимую для обработки данного запроса: дерево компонентов, сообщения об ошибках, доступ к HTTP-запросу и ответу и т.д.
3.  **Запуск Жизненного Цикла JSF**: Получив `FacesContext`, сервлет запускает и последовательно выполняет все фазы жизненного цикла JSF (Restore View, Apply Request Values, Process Validations, и т.д.) для обработки запроса.
4.  **Рендеринг ответа**: После завершения жизненного цикла `FacesServlet` использует результат (сгенерированный HTML) для отправки ответа обратно в браузер.

### Конфигурация `FacesServlet` в `web.xml`

Чтобы веб-контейнер (например, Tomcat или WildFly) знал о `FacesServlet` и о том, какие запросы ему отправлять, его необходимо сконфигурировать в главном дескрипторе развертывания веб-приложения — файле `WEB-INF/web.xml`.

**Пример стандартной конфигурации в `web.xml`:**
```xml
<?xml version="1.0" encoding="UTF-8"?>
<web-app xmlns="http://xmlns.jcp.org/xml/ns/javaee"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://xmlns.jcp.org/xml/ns/javaee http://xmlns.jcp.org/xml/ns/javaee/web-app_4_0.xsd"
         version="4.0">

    <!-- 1. Определение сервлета -->
    <servlet>
        <servlet-name>Faces Servlet</servlet-name>
        <servlet-class>javax.faces.webapp.FacesServlet</servlet-class>
        <!-- Загружать сервлет при старте приложения -->
        <load-on-startup>1</load-on-startup>
    </servlet>

    <!-- 2. Маппинг URL-шаблона на сервлет -->
    <servlet-mapping>
        <servlet-name>Faces Servlet</servlet-name>
        <!-- Все запросы, заканчивающиеся на .xhtml, будут обработаны этим сервлетом -->
        <url-pattern>*.xhtml</url-pattern> 
    </servlet-mapping>
    
    <!-- 3. (Опционально) Установка стадии проекта -->
    <context-param>
        <param-name>javax.faces.PROJECT_STAGE</param-name>
        <param-value>Development</param-value> <!-- Development, Production, UnitTest, SystemTest -->
    </context-param>

</web-app>
````

- *.xhtml — современный стандартный паттерн. В старых проектах можно встретить *.jsf или /faces/*.
    

---

## 2. Файл faces-config.xml — Правила игры для JSF

Если FacesServlet — это исполнитель, то faces-config.xml — это свод правил, по которым он играет. Этот файл находится в WEB-INF/faces-config.xml и содержит специфичные для JSF настройки.

**Важно:** Роль этого файла сильно изменилась с появлением аннотаций в JSF 2.0.

### Историческая роль (устаревшие задачи, теперь решаются аннотациями)

В JSF 1.x в этом файле делалось **почти всё**:
- **Регистрация Managed Beans**:
    
    ```xml
    <managed-bean>
        <managed-bean-name>userBean</managed-bean-name>
        <managed-bean-class>com.example.UserBean</managed-bean-class>
        <managed-bean-scope>session</managed-bean-scope>
    </managed-bean>
    ```
    
- **Определение правил навигации**: Жестко задавались переходы между страницами.
    
    ```xml
    <navigation-rule>
        <from-view-id>/login.xhtml</from-view-id>
        <navigation-case>
            <from-action>#{loginBean.login}</from-action>
            <from-outcome>success</from-outcome>
            <to-view-id>/welcome.xhtml</to-view-id>
        </navigation-case>
    </navigation-rule>
    ```
    
- **Регистрация кастомных конвертеров, валидаторов, компонентов**.
    

### Современная роль (актуальные задачи)

Сегодня, благодаря аннотациям (@Named, @FacesConverter и др.), faces-config.xml часто бывает почти пустым. Однако он по-прежнему используется для **глобальных конфигураций**, которые неудобно или невозможно задать через аннотации:

1. **Регистрация ResourceBundle для интернационализации (i18n)**:
    
    ```xml
    <application>
        <resource-bundle>
            <base-name>com.example.messages.messages</base-name>
            <var>msg</var>
        </resource-bundle>
    </application>
    ```
    
2. **Регистрация глобальных PhaseListener**: Слушателей, которые выполняют код до или после определенных фаз жизненного цикла для всех страниц.
    
    ```xml
    <lifecycle>
        <phase-listener>com.example.listeners.MyPhaseListener</phase-listener>
    </lifecycle>
    ```
    
3. **Переопределение стандартных фабрик JSF**: Например, для замены стандартной логики обработки исключений (ExceptionHandlerFactory).
    
4. **Включение специфических функций**: Например, активация EL-резолвера для CDI.
    

---

### Итог: Как это работает вместе

1. Пользователь запрашивает в браузере http://.../myapp/index.xhtml.
    
2. Веб-контейнер (Tomcat) смотрит в web.xml и видит, что URL-паттерн *.xhtml соответствует сервлету Faces Servlet.
    
3. Контейнер передает запрос на обработку экземпляру FacesServlet.
    
4. FacesServlet инициализирует JSF-окружение. При этом он читает faces-config.xml, чтобы узнать о глобальных настройках (например, о доступных ResourceBundle).
    
5. FacesServlet создает FacesContext и запускает жизненный цикл JSF для обработки запроса.
    
6. В ходе жизненного цикла JSF использует аннотации для поиска бинов, валидаторов и т.д.
    
7. FacesServlet получает готовый HTML и отправляет его обратно пользователю.