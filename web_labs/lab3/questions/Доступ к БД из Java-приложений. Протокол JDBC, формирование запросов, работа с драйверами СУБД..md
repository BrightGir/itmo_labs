# Доступ к БД из Java-приложений: JDBC

**JDBC (Java Database Connectivity)** — это стандартный Java API для взаимодействия с реляционными базами данных. По сути, это "мост" между Java-приложением и практически любой СУБД (PostgreSQL, MySQL, Oracle, MS SQL Server и др.).

JDBC предоставляет набор интерфейсов и классов, которые позволяют выполнять следующие задачи:
-   Устанавливать соединение с базой данных.
-   Отправлять SQL-запросы.
-   Обрабатывать результаты запросов.
-   Управлять транзакциями.

---

## 1. Протокол и архитектура JDBC

JDBC — это именно **API (Application Programming Interface)**, а не протокол. Он определяет, **как** Java-код должен взаимодействовать с драйвером, но не то, **как** драйвер общается с базой данных.

Архитектура состоит из двух основных слоев:
1.  **JDBC API**: Предоставляет интерфейсы для разработчика (`Connection`, `Statement`, `ResultSet` и др.). Ваш код работает только с этим слоем.
2.  **JDBC Driver**: Конкретная реализация JDBC API для определенной СУБД. Именно драйвер "переводит" вызовы Java-методв в команды, понятные конкретной базе данных.

**Ключевой элемент — `DriverManager`**: Это класс-фабрика, который управляет списком доступных драйверов и устанавливает соединение с БД на основе URL-адреса.

---

## 2. Работа с драйверами СУБД

**Драйвер** — это библиотека (обычно один `.jar` файл), которую необходимо добавить в ваш проект. Без него ваше приложение не будет знать, как "разговаривать" с вашей базой данных.

**Как получить и подключить драйвер:**
Самый распространенный способ — добавить зависимость в систему сборки (Maven или Gradle).

**Пример для Maven (подключение драйвера PostgreSQL):**
```xml
<!-- pom.xml -->
<dependency>
    <groupId>org.postgresql</groupId>
    <artifactId>postgresql</artifactId>
    <version>42.7.1</version> <!-- Используйте актуальную версию -->
</dependency>
````

**Загрузка драйвера в приложении:**  
В современных версиях JDBC (4.0 и выше) драйвер загружается **автоматически**, как только он появляется в classpath проекта. Раньше требовался явный вызов:  
Class.forName("org.postgresql.Driver"); // сейчас это делать не нужно.

---

## 3. Основные шаги работы с JDBC

Любая операция с БД через JDBC следует четкой последовательности шагов.

**Пример: получение данных из таблицы users**


```java
import java.sql.*;

public class JdbcExample {

    // 1. Параметры подключения к БД
    private static final String URL = "jdbc:postgresql://localhost:5432/mydatabase";
    private static final String USER = "postgres";
    private static final String PASSWORD = "password";

    public static void main(String[] args) {
        // 7. Используем try-with-resources для автоматического закрытия ресурсов
        try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
             Statement statement = connection.createStatement()) {
            
            System.out.println("Соединение с БД установлено!");

            // 4. Выполнение запроса
            String sqlQuery = "SELECT id, name, email FROM users";
            ResultSet resultSet = statement.executeQuery(sqlQuery);
            
            System.out.println("Пользователи:");

            // 5. Обработка результата
            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String name = resultSet.getString("name");
                String email = resultSet.getString("email");
                System.out.printf("ID: %d, Имя: %s, Email: %s\n", id, name, email);
            }
            
        } catch (SQLException e) {
            System.err.println("Ошибка при работе с БД!");
            e.printStackTrace();
        }
        // Ресурсы (connection, statement, resultSet) будут закрыты автоматически
    }
}
```

### Разбор шагов:

1. **Загрузка драйвера**: Происходит автоматически.
    
2. **Установка соединения (Connection)**: DriverManager.getConnection(url, user, password) возвращает объект Connection, представляющий сессию с БД. URL имеет строго определенный формат: jdbc:<вендор>://<хост>:<порт>/<имя_бд>.
    
3. **Создание объекта запроса (Statement)**: connection.createStatement() создает объект, который будет нести наш SQL-запрос к БД.
    
4. **Выполнение запроса**:
    
    - statement.executeQuery(sql): для SELECT-запросов. Возвращает объект ResultSet.
        
    - statement.executeUpdate(sql): для INSERT, UPDATE, DELETE. Возвращает int — количество измененных строк.
        
5. **Обработка результата (ResultSet)**: ResultSet — это итератор, указывающий на строки, полученные из БД.
    
    - resultSet.next(): перемещает курсор на следующую строку. Возвращает false, если строк больше нет.
        
    - resultSet.getString("column_name"), resultSet.getInt(1): методы для получения данных из столбцов текущей строки по имени или по номеру (начиная с 1).
        
6. **Закрытие ресурсов**: **Критически важный шаг!** Всегда необходимо закрывать ResultSet, Statement и Connection, чтобы освободить ресурсы в приложении и в БД.
    

**Современный подход — try-with-resources (как в примере выше)**. Он гарантирует, что все ресурсы, объявленные в скобках try(), будут автоматически закрыты, даже если произойдет исключение.

---

## 4. Формирование запросов: Statement vs PreparedStatement

Это фундаментальное различие, которое напрямую влияет на **безопасность** и **производительность**.

### Statement

- **Что это**: Простой объект для выполнения статичных SQL-запросов.
    
- **Проблема**: Уязвим для **SQL-инъекций**. Если вы вставляете в строку запроса данные от пользователя напрямую, злоумышленник может "сломать" ваш запрос и выполнить произвольный код.
    
- **Пример уязвимости:**
    
    ```java
    String userInput = "1' OR '1'='1"; // Ввод злоумышленника
    String sql = "SELECT * FROM users WHERE id = '" + userInput + "'";
    // Итоговый запрос: SELECT * FROM users WHERE id = '1' OR '1'='1'
    // Этот запрос вернет ВСЕХ пользователей, а не одного!
    ```
    

### PreparedStatement (Предпочтительный способ)

- **Что это**: Предкомпилированный SQL-запрос, в котором места для данных заменены на плейсхолдеры (?).
    
- **Как работает**: Сначала в БД отправляется "шаблон" запроса. Он компилируется и кэшируется. Затем, отдельным вызовом, передаются данные для плейсхолдеров. Драйвер сам заботится о корректном экранировании данных.
    
- **Преимущества**:
    
    1. **Защита от SQL-инъекций**: Данные передаются отдельно от команды, их невозможно интерпретировать как часть SQL-кода.
        
    2. **Производительность**: Если один и тот же запрос выполняется много раз с разными параметрами, БД не нужно компилировать его каждый раз заново.
        

**Пример использования PreparedStatement:**

```java
String sql = "INSERT INTO users (name, email) VALUES (?, ?)";

try (Connection conn = ...;
     PreparedStatement pstmt = conn.prepareStatement(sql)) {

    // Устанавливаем значения для плейсхолдеров
    pstmt.setString(1, "John Doe");  // Первый '?'
    pstmt.setString(2, "john.doe@example.com"); // Второй '?'
    
    int rowsAffected = pstmt.executeUpdate();
    System.out.println("Добавлено строк: " + rowsAffected);
} catch (SQLException e) {
    e.printStackTrace();
}
```

**Вывод**: **Всегда используйте PreparedStatement**, когда в запрос нужно подставить данные, особенно полученные от пользователя. Используйте Statement только для абсолютно статичных запросов без параметров.