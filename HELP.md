# What is Vix Telegram Bot

Vix Telegram Bot is a Spring Boot based app that imitate service of ticket ordering
of imagine circus.

### Features

- Registering and changing your data after that
- Looking through all available performances
- Choosing passed one and getting PDF file of it
- Chatting with the operator

### Used libraries

* [Spring Boot Starter Web](https://mvnrepository.com/artifact/org.springframework.boot/spring-boot-starter-web/)
* [Spring Boot Starter Data JPA](https://mvnrepository.com/artifact/org.springframework.boot/spring-boot-starter-data-jpa/)
* [IText Core](https://mvnrepository.com/artifact/com.itextpdf/itextpdf/)
* [Project Lombok](https://mvnrepository.com/artifact/org.projectlombok/lombok/)
* [PostgreSQL JDBC Driver](https://mvnrepository.com/artifact/org.postgresql/postgresql/)
* [Flyway Core](https://mvnrepository.com/artifact/org.flywaydb/flyway-core/)
* [Telegram Bots](https://mvnrepository.com/artifact/org.telegram/telegrambots/)

### How to start

This bot uses PostgreSQL, so it's required.
Also check if you have enough rights. 
The application will create a directory for storing tickets and will store all requested files in it.

Everything else will be made automatically. Enjoy.

