# SLF4SQL
SLF4SQL is a library for SQL logging using slf4j.

## SLF4SQL in brief

This is how it works:
- SLF4SQL provides the statement interceptor at the `Driver` level, which is much more convenient for Java applications because the `DataSource` is provided by the application server.

## Usage

- Add slf4sql-x.x.x.jar into your project.
- Change driver class name to `sb.slf4sql.ProxyDrive`.
- Change driver url to `sb.slf4sql.ProxyDrive`.

```html
<logger name="org.hibernate.SQL" level="debug"/>
```

## Prerequisites

- JDK 8
- SLF4J

```
Give examples
```
