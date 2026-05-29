# Log Monitor

Java/Swing GUI-клиент для просмотра логов с фильтрацией, сортировкой,
многопоточным чтением файлов и встроенным генератором тестовых логов.

## Требования
- JDK 17+
- Maven 3.8+

## Запуск
```
mvn -q package
java -jar target/log-monitor-1.0.0.jar
```

## Возможности
- Загрузка `.log` файлов (формат `YYYY-MM-DD HH:MM:SS [LEVEL] source - message`)
- Встроенный генератор тестовых логов (отдельный поток)
- Фильтрация по минимальному уровню и поисковой подстроке
- Сортировка по любой колонке (клик по заголовку)
- Цветовая подсветка уровней
- Локализация RU/EN со сменой языка на лету

## Архитектура

| Слой         | Пакет                              |
|--------------|------------------------------------|
| Model        | `com.logmonitor.model.*`           |
| Filters      | `com.logmonitor.model.filter.*`    |
| Parsers      | `com.logmonitor.model.parser.*`    |
| Services     | `com.logmonitor.service.*`         |
| Controller   | `com.logmonitor.controller.*`      |
| View (Swing) | `com.logmonitor.view.*`            |
| i18n         | `com.logmonitor.i18n.*`            |

### Паттерны
- **Strategy + Composite** — `LogFilter` и его реализации, `CompositeFilter`
- **Observer** — `ObservableLogSource` + `LogSourceListener`, смена локали
- **Singleton** — `LocaleManager`
- **Builder** — `LogEntry.Builder`
- **MVC** — `LogController` зависит только от `LogView` (интерфейс)

### SOLID
- **SRP/OCP/LSP/ISP/DIP** — соблюдены через интерфейсы `LogRepository`,
  `LogFilter`, `LogParser`, `LogView` и инъекцию зависимостей в конструкторы.

### Многопоточность
- `LogFileReaderService` — `ExecutorService` для чтения файла вне EDT
- `LogGeneratorService` — `ScheduledExecutorService`
- `InMemoryLogRepository` — `ReentrantReadWriteLock`
- UI-обновления через `SwingUtilities.invokeLater`
