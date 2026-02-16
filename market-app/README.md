# market_app# My Market Application

Веб-приложение интернет-магазина с использованием Spring Boot, Spring WebFlux.

## Функциональность

- Просмотр товаров с пагинацией, поиском и сортировкой
- Добавление товаров в корзину
- Просмотр корзины и оформление заказа
- Просмотр истории заказов

## Технологии

- Java 21
- Spring Boot 3.2.2
- Spring WebFlux (Reactive Stack)
- H2 Database (in-memory)
- Thymeleaf
- Maven
- Spring Data R2DBC (Reactive Relational Database Connectivity)


## Сборка и запуск

### Предварительные требования

- JDK 21
- Maven 3.6+
- Docker (опционально)

### Сборка проекта

```bash
mvn clean package
```

### Запуск приложения

```bash
java -jar target/my-market-app-0.0.1-SNAPSHOT.jar
```
Приложение будет доступно по адресу: http://localhost:8080

### Запуск тестов

```bash
mvn test
```

### Запуск в Docker

```bash
docker build -t my-market-app .
docker run -p 8080:8080 my-market-app

```