# slf4sql
slf4sql is a library for SQL logging using slf4j.

## slf4sql in brief

This is how it works:
- slf4sql provides the statement interceptor at the `Driver` level, which is much more convenient for Java applications because the `DataSource` is provided by the application server.

![slf4sql Diagram](slf4sql-diagram.png)

## Usage

- Add slf4sql-x.x.x.jar into your project.
- Change driver class name to `sb.slf4sql.ProxyDrive`.
- Change driver url to `sb.slf4sql.ProxyDrive`.

### Spring Boot
```diff
+ sb.slf4sql.ProxyDriver
- oracle.jdbc.OracleDriver
+ jdbc:slf4sql:oracle:thin:@HOST_IP:HOST_PORT:DATABASE
- jdbc:oracle:thin:@HOST_IP:HOST_PORT:DATABASE
```

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
          

### Java
```
Example
```

```html
<logger name="org.hibernate.SQL" level="debug"/>
```

## Prerequisites
* JDK 8 or later
* [SLF4J](https://www.slf4j.org/) - Simple Logging Facade for Java


```
Give examples
```
