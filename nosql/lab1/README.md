# Лабораторная работа №1 — Etcd

Spring Boot-приложение для работы с событиями библиотеки. Etcd используется как единственное хранилище данных.

## Что демонстрирует проект

- хранение событий и заказов в формате JSON;
- временные заявки через Etcd lease и TTL;
- cache-aside для количества мест по умолчанию в заявке менеджера;
- атомарный счётчик просмотров через CAS;
- транзакционное создание заказа и удаление заявки;
- сравнение blind write и CAS при конкурентной записи;
- сохранение данных между перезапусками благодаря Podman volume.

## Структура

```text
src/main/java/ru/bright
├── config       Spring-конфигурация
├── controller   REST-контроллеры
├── dto          запросы и ответы API
├── exception    исключения и обработчик ошибок
├── model        модели предметной области
├── repository   работа с Etcd и JSON
└── service      сценарии приложения
```

Поток запроса: `Controller → Service → EtcdGateway → Etcd`.

## Запуск через Podman

Нужны установленные Podman и Podman Compose. На macOS один раз создайте машину:

```bash
podman machine init
```

Затем из каталога проекта, где лежит `compose.yaml`:

```bash
podman machine start
podman compose up --build -d
```

Откройте **http://localhost:18080/** — это интерфейс для проверки API. На Linux команда `podman machine start` не нужна.

Остановить лабораторную:

```bash
podman compose down
```

## Сохранение и восстановление

Именованный volume `library-etcd-data` сохраняет данные между перезапусками контейнеров.
Для создания переносимого snapshot запустите:

```bash
./scripts/snapshot-save.sh library.db
```

Для восстановления остановите приложение и замените содержимое volume данными из snapshot:

```bash
./scripts/snapshot-restore.sh library.db
```

Скрипт восстановления сам останавливает Compose, пересоздаёт только volume
`library-etcd-data`, восстанавливает его через `etcdutl` и запускает сервисы снова.
Файлы `*.db` исключены из репозитория. Для Docker вместо Podman используйте
`CONTAINER_ENGINE=docker`.

Запустить тесты:

```bash
./gradlew test
```

## Основной сценарий

Создать событие:

```bash
curl -sS -X POST http://localhost:18080/api/events \
  -H 'Content-Type: application/json' \
  -d '{"title":"Презентация книги","type":"PRESENTATION","startsAt":"2026-10-01T16:00:00Z","capacity":80}'
```

Создать временную заявку, подставив идентификатор события:

```bash
curl -sS -X POST http://localhost:18080/api/drafts \
  -H 'Content-Type: application/json' \
  -d '{"eventId":"EVENT_ID","managerId":"manager-1","quantity":2,"comment":"Первый ряд","ttlSeconds":30}'
```

Поле `quantity` можно не передавать: тогда берётся настройка менеджера или `2`, если её нет. Настроить значение:

```bash
curl -sS -X PUT http://localhost:18080/api/managers/manager-1/settings \
  -H 'Content-Type: application/json' \
  -d '{"defaultQuantity":4}'
```

Оформить заявку:

```bash
curl -sS -X POST http://localhost:18080/api/drafts/DRAFT_ID/submit
```

## API

- `POST /api/events`, `GET /api/events`, `GET /api/events/{id}`;
- `POST /api/events/{id}/views`, `GET /api/events/{id}/views`;
- `POST /api/drafts`, `GET /api/drafts/{id}`;
- `POST /api/drafts/{id}/submit`, `GET /api/orders/{id}`;
- `PUT /api/managers/{managerId}/settings`, `GET /api/managers/{managerId}/settings`;
- `POST /api/experiments/write-conflict?writers=24`.

Ошибки валидации возвращают 400, отсутствие данных — 404, конфликт — 409, недоступность Etcd — 503.
