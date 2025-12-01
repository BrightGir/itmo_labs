Нужно изменить конфиг, чтобы создать соединение с базой данных 
Добавить в `<subsystem xmlns="urn:jboss:domain:datasources:6.0">` (standalone.xml)

persistense.xml
```xml 
		
				<datasource jndi-name="java:jboss/datasources/PostgresDS" pool-name="PostgresqlPool" enabled="true" use-java-context="true">
                    <!-- URL для подключения. ЗАМЕНИТЕ 'weblab3_db' на имя вашей базы данных -->
                    <connection-url>jdbc:postgresql://localhost:5432/weblab3_db</connection-url>
                    
                    <!-- Имя драйвера, который мы определим ниже -->
                    <driver>postgresql</driver>
                    
                    <security>
                        <!-- ЗАМЕНИТЕ 'postgres' и 'password' на ваши логин и пароль от БД -->
                        <user-name>postgres</user-name>
                        <password>postgres</password>
                    </security>
                </datasource>
```
и в drivers postgresql driver, модуль отделньый также для драйвера (modules/system/layers/org/postgresql/main/module.xml и jarnik сюда же)

module.xml
```xml
<?xml version="1.0" ?>
<module xmlns="urn:jboss:module:1.3" name="org.postgresql">

    <resources>
        <resource-root path="postgresql-42.7.7.jar"/>
    </resources>

    <dependencies>
        <module name="javax.api"/>
        <module name="javax.transaction.api"/>
    </dependencies>
</module>
```
Итоговая конфигурация:

standalone.xml
```xml
    <subsystem xmlns="urn:jboss:domain:datasources:6.0">
            <datasources>
                <datasource jndi-name="java:jboss/datasources/ExampleDS" pool-name="ExampleDS" enabled="true" use-java-context="true" statistics-enabled="${wildfly.datasources.statistics-enabled:${wildfly.statistics-enabled:false}}">
                    <connection-url>jdbc:h2:mem:test;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE</connection-url>
                    <driver>h2</driver>
                    <security>
                        <user-name>sa</user-name>
                        <password>sa</password>
                    </security>
                </datasource>
                <datasource jndi-name="java:jboss/datasources/PostgresDS" pool-name="PostgresqlPool" enabled="true" use-java-context="true">
                    <connection-url>jdbc:postgresql://localhost:5432/weblab3_db</connection-url>
                    <driver>postgresql</driver>
                    <security>
                        <user-name>postgres</user-name>
                        <password>postgres</password>
                    </security>
                </datasource>
                <drivers>
                    <driver name="h2" module="com.h2database.h2">
                        <xa-datasource-class>org.h2.jdbcx.JdbcDataSource</xa-datasource-class>
                    </driver>
                    <driver name="postgresql" module="org.postgresql">
                        <driver-class>org.postgresql.Driver</driver-class>
                    </driver>
                </drivers>
            </datasources>
        </subsystem>
```