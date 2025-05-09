# Система управления банковскими картами

REST API на Spring Boot для управления банковскими картами, с аутентификацией на JWT, ролевой защитой (USER / ADMIN) и базой данных PostgreSQL.

---

## Возможности

### Пользователи (ROLE\_USER)

* Регистрация / Вход
* Создание своей карты
* Просмотр своих карт (c фильтрацией по статусу)
* Перевод средств между своими картами
* Блокировка своих карт

### Админы (ROLE\_ADMIN)

* Просмотр всех пользователей
* Просмотр карт юзера
* Создание карт для юзера
* Блокировка любой карты
* Удаление карт или пользователей

---

## Docker запуск

### 1. Собрать jar:

```bash
mvn clean package
```

### 2. Запустить Docker Compose:

```bash
docker-compose up --build
```

### 3. Открыть:

* API: [http://localhost:8080](http://localhost:8080)
* Swagger UI: [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)

---

## Переменные окружения

```env
SPRING_DATASOURCE_URL=jdbc:postgresql://postgres:5432/bankcardsystem
SPRING_DATASOURCE_USERNAME=postgres
SPRING_DATASOURCE_PASSWORD=postgres
JWT_SECRET=eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJBdXRoIFNlcnZlciIsInN1YiI6ImF1dGgiLCJleHAiOjE1MDU0Njc3NTY4NjksImlhdCI6MTUwNTQ2NzE1MjA2OSwidXNlciI6MX0.9VPGwNXYfXnNFWH3VsKwhFJ0MazwmNvjSSRZ1vf3ZUU
JWT_EXPIRATION=3600000
```

---

## JWT-аутентификация

* Регистрация: `POST /api/auth/register`
* Вход: `POST /api/auth/login`
* Header: `Authorization: Bearer <token>`

---

## Эндпоинты API

### Авторизация

| Метод | Путь                 | Описание     |
| ----- | -------------------- | ------------ |
| POST  | `/api/auth/register` | Регистрация  |
| POST  | `/api/auth/login`    | Вход и токен |

### Карты (User)

| Метод | Путь                    | Описание              |
| ----- | ----------------------- | --------------------- |
| GET   | `/api/cards`            | Список своих карт     |
| POST  | `/api/cards`            | Создать карту         |
| POST  | `/api/cards/transfer`   | Перевод между картами |
| PATCH | `/api/cards/{id}/block` | Блокировать карту     |

### Админ

| Метод  | Путь                          | Описание         |
| ------ | ----------------------------- | ---------------- |
| GET    | `/api/admin/users`            | Список юзеров    |
| GET    | `/api/admin/users/{id}/cards` | Карты юзера      |
| POST   | `/api/admin/users/{id}/cards` | Создать карту    |
| PATCH  | `/api/admin/cards/{id}/block` | Блокировка карты |
| DELETE | `/api/admin/users/{id}`       | Удалить юзера    |
| DELETE | `/api/admin/cards/{id}`       | Удалить карту    |

---

## Примеры (через Postman)

### Регистрация

```
POST /api/auth/register
{
  "email": "user@example.com",
  "password": "password123"
}
```

### Вход

```
POST /api/auth/login
{
  "email": "user@example.com",
  "password": "password123"
}
```

### Создать карту

```
POST /api/cards
Authorization: Bearer <token>
{
  "cardNumber": "1234 5678 9012 3456",
  "expirationDate": "2026-12-31",
  "initialBalance": 1000
}
```

---

## Схема БД

### User

* id
* email
* password
* role (USER / ADMIN)

### Card

* id
* cardNumber (encrypted)
* expirationDate
* balance
* status (ACTIVE / BLOCKED)
* user\_id (FK)

---

## Swagger UI

Swagger доступен по адресу:
[http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)

