# slf4sql
slf4sql is a library for SQL logging using slf4j.

## slf4sql in brief

This is how it works:
- slf4sql provides the statement interceptor at the `Driver` level, which is much more convenient for Java applications because the `DataSource` is provided by the application server.

![slf4sql Diagram](slf4sql-diagram.png)

## Prerequisites
* JDK 8 or later
* [SLF4J](https://www.slf4j.org/) - Simple Logging Facade for Java


## Usage

- Add slf4sql-x.x.x.jar into your project.
- Replace driver class name with `sb.slf4sql.ProxyDrive`.
- Change url to jdbc:`slf4sql:`oracle:thin:@HOST_IP:HOST_PORT:DATABASE.

```diff
+ sb.slf4sql.ProxyDriver
- oracle.jdbc.OracleDriver
+ jdbc:slf4sql:oracle:thin:@HOST_IP:HOST_PORT:DATABASE
- jdbc:oracle:thin:@HOST_IP:HOST_PORT:DATABASE
```

- Add SQLLogger with debug level

```xml
<!-- SQL Logger -->
<logger name="sb.slf4sql.SQLLogger" level="debug" additivity="false">
          <appender-ref ref="CONSOLE"/>
</logger>
```

### Spring Boot
```
spring.datasource.driver-class-name=sb.slf4sql.ProxyDriver
spring.datasource.url=jdbc:slf4sql:oracle:thin:@HOST_IP:HOST_PORT:DATABASE
spring.datasource.username=USERNAME
spring.datasource.password=PASSWORD
```

### MyBatis 2.0
```xml
<property name="JDBC.Driver" value="sb.slf4sql.ProxyDriver" />
<property name="JDBC.ConnectionURL" value="jdbc:slf4sql:oracle:thin:@HOST_IP:HOST_PORT:DATABASE" />
<property name="JDBC.Username" value="USERNAME" />
<property name="JDBC.Password" value="PASSWORD" />
```

### MyBatis 3.0
```xml
<property name="driver" value="sb.slf4sql.ProxyDriver" />
<property name="url" value="jdbc:slf4sql:oracle:thin:@HOST_IP:HOST_PORT:DATABASE" />
<property name="username" value="USERNAME" />
<property name="password" value="PASSWORD" />
```
          
### Stand-Alone Code in Java
```java
String dbDriver = "sb.slf4sql.ProxyDriver";
String dbUrl = "jdbc:slf4sql:oracle:thin:@HOST_IP:HOST_PORT:DATABASE";
String dbUser = "USERNAME";
String dbPassword = "PASSWORD";

Class<?> clazz = Class.forName(dbDriver);
DriverManager.registerDriver((Driver) clazz.newInstance());
Connection connection = DriverManager.getConnection(dbUrl, dbUser, dbPassword);
```

```html
<logger name="org.hibernate.SQL" level="debug"/>
```
