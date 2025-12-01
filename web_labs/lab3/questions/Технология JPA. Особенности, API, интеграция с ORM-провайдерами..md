# Технология JPA (Jakarta Persistence API)

**JPA (Jakarta Persistence API)**, ранее известная как Java Persistence API, — это **стандартная спецификация** Java, которая описывает, как управлять реляционными данными в Java-приложениях.

**Самое важное, что нужно понять**: JPA — это **не библиотека и не фреймворк**. Это набор интерфейсов, аннотаций и правил (контракт). Конкретные ORM-фреймворки (такие как Hibernate или EclipseLink) **реализуют** этот контракт.

Использование JPA позволяет писать код, который не зависит от конкретного ORM-провайдера. Вы можете поменять Hibernate на EclipseLink, практически не меняя свой код.

---

## 1. Особенности и ключевые концепции

### 1.1. Стандартизация и переносимость
Главное преимущество JPA — это создание единого стандарта. Ваш код, написанный с использованием JPA API, будет работать с любым провайдером, который поддерживает эту версию JPA. Это дает свободу выбора и защищает от привязки к одному поставщику (vendor lock-in).

### 1.2. Object-Relational Mapping (ORM) на основе аннотаций
JPA использует аннотации для "связывания" обычных Java-классов (POJO) с таблицами в базе данных.

-   **Сущность (`Entity`)**: Java-класс, помеченный аннотацией `@Entity`, представляет собой строку в таблице.
-   **Маппинг**: Аннотации `@Table`, `@Column`, `@Id` и другие используются для точной настройки того, как поля класса соответствуют столбцам таблицы.

### 1.3. JPQL (Jakarta Persistence Query Language)
JPA определяет свой собственный, объектно-ориентированный язык запросов, похожий на SQL. Ключевое отличие: **JPQL оперирует сущностями и их полями, а не таблицами и столбцами**.

-   **SQL**: `SELECT u.first_name FROM users u WHERE u.age > 18`
-   **JPQL**: `SELECT u.firstName FROM User u WHERE u.age > 18`

ORM-провайдер сам транслирует JPQL-запрос в нативный SQL, специфичный для используемой СУБД.

### 1.4. Контекст персистентности (Persistence Context)
Это, возможно, самая важная и сложная концепция в JPA. Контекст персистентности — это, по сути, **кэш первого уровня (L1 Cache)** или **сессия**, управляемая `EntityManager`.

-   Когда вы загружаете сущность из БД, она помещается в этот контекст.
-   Все изменения, которые вы вносите в объекты внутри контекста, отслеживаются.
-   При завершении транзакции (`commit`) JPA автоматически находит все измененные ("грязные") объекты и генерирует для них `UPDATE`-запросы.
-   Если вы дважды запрашиваете один и тот же объект по ID в рамках одной транзакции, второй раз запрос в БД не пойдет — объект будет взят из контекста.

---

## 2. Основные элементы API

JPA определяет несколько ключевых интерфейсов, с которыми работает разработчик.

-   **`@Entity`**: Аннотация, превращающая POJO в управляемую сущность.
-   **`EntityManagerFactory`**: "Тяжеловесный", потокобезопасный объект-фабрика. Создается один раз на все приложение. Его задача — создавать экземпляры `EntityManager`.
-   **`EntityManager`**: Основной интерфейс для взаимодействия с базой данных. Он управляет контекстом персистентности.
    -   **Не потокобезопасен!** Обычно создается на одну транзакцию или один запрос.
    -   Ключевые методы:
        -   `persist(entity)`: Делает новый объект управляемым (сохраняет в БД).
        -   `find(Entity.class, primaryKey)`: Ищет сущность по первичному ключу.
        -   `merge(entity)`: Обновляет состояние сущности, сливая изменения из отсоединенного объекта.
        -   `remove(entity)`: Помечает сущность на удаление.
-   **`EntityTransaction`**: Интерфейс для управления транзакциями.
    -   `begin()`: Начать транзакцию.
    -   `commit()`: Подтвердить изменения.
    -   `rollback()`: Откатить изменения.
-   **`Query` / `TypedQuery`**: Интерфейсы для выполнения JPQL-запросов.

### Пример кода

```java
// 1. Создание EntityManagerFactory (обычно делается один раз при старте)
EntityManagerFactory emf = Persistence.createEntityManagerFactory("my-persistence-unit");

// 2. Создание EntityManager для одной операции
EntityManager em = emf.createEntityManager();
EntityTransaction tx = em.getTransaction();

try {
    // 3. Начало транзакции
    tx.begin();

    // 4. Создание и сохранение новой сущности
    User newUser = new User("John Doe", "john.doe@example.com");
    em.persist(newUser);

    // 5. Поиск и обновление существующей сущности
    User foundUser = em.find(User.class, 1L);
    if (foundUser != null) {
        foundUser.setName("John Smith"); // Изменение отслеживается контекстом!
    }

    // 6. Подтверждение транзакции (Hibernate сгенерирует INSERT и UPDATE)
    tx.commit();
} catch (Exception e) {
    if (tx != null && tx.isActive()) {
        tx.rollback(); // Откат в случае ошибки
    }
    e.printStackTrace();
} finally {
    // 7. Закрытие EntityManager
    em.close();
}
// 8. Закрытие фабрики (при остановке приложения)
emf.close();
````

---

## 3. Интеграция с ORM-провайдерами

JPA и ORM-провайдеры работают вместе по четко определенной схеме.

**Схема взаимодействия:**  
Ваше приложение → JPA API → ORM-провайдер (Hibernate/EclipseLink) → JDBC → База данных

**Файл persistence.xml**:  
Это стандартный конфигурационный файл JPA, который находится в META-INF/persistence.xml. В нем вы указываете, какой провайдер использовать, и передаете ему все необходимые настройки.

```xml
<persistence xmlns="http://xmlns.jcp.org/xml/ns/persistence" version="2.1">
    <persistence-unit name="my-persistence-unit" transaction-type="RESOURCE_LOCAL">
        
        <!-- 1. Указываем, какой JPA-провайдер использовать -->
        <provider>org.hibernate.jpa.HibernatePersistenceProvider</provider>
        
        <properties>
            <!-- 2. Передаем JDBC-настройки провайдеру -->
            <property name="javax.persistence.jdbc.driver" value="org.postgresql.Driver"/>
            <property name="javax.persistence.jdbc.url" value="jdbc:postgresql://localhost:5432/mydatabase"/>
            <property name="javax.persistence.jdbc.user" value="postgres"/>
            <property name="javax.persistence.jdbc.password" value="password"/>
            
            <!-- 3. Передаем специфичные для провайдера настройки -->
            <property name="hibernate.dialect" value="org.hibernate.dialect.PostgreSQLDialect"/>
            <property name="hibernate.hbm2ddl.auto" value="update"/>
            <property name="hibernate.show_sql" value="true"/>
        </properties>

    </persistence-unit>
</persistence>
```

1. **provider**: Здесь вы явно говорите JPA, какую реализацию (Hibernate, EclipseLink и т.д.) нужно загрузить.
    
2. **javax.persistence.***: Это стандартные свойства JPA для конфигурации JDBC-соединения.
    
3. **hibernate.* / eclipselink.***: Здесь вы можете указывать специфичные для провайдера настройки, которые выходят за рамки стандарта JPA.
    

Таким образом, JPA предоставляет **стандартный фасад**, а ORM-провайдер — **конкретную реализацию**, которая выполняет всю "грязную" работу по генерации SQL и взаимодействию с JDBC.