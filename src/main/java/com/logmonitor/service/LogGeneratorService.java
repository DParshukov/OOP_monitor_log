package com.logmonitor.service;

import com.logmonitor.model.LogEntry;
import com.logmonitor.model.LogLevel;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Генерирует правдоподобные тестовые логи в отдельном демоническом потоке.
 * Сообщение и источник выбираются согласованно с уровнем — FATAL не выдаст
 * «Платёж подтверждён», а INFO не выдаст «Потеря связи с БД».
 */
public final class LogGeneratorService extends ObservableLogSource implements AutoCloseable {

    private record Scenario(String source, String message) { }

    private static final Map<LogLevel, Scenario[]> SCENARIOS = Map.of(
            LogLevel.TRACE, new Scenario[] {
                    new Scenario("api.UserController", "Вход в метод getUser(id=%d)"),
                    new Scenario("db.ConnectionPool",  "acquire(): пул=%d/%d"),
                    new Scenario("cache.RedisClient",  "GET key=user:%d")
            },
            LogLevel.DEBUG, new Scenario[] {
                    new Scenario("api.UserController", "Десериализация payload, %d байт"),
                    new Scenario("scheduler.JobRunner","Следующий запуск через %d мс"),
                    new Scenario("cache.RedisClient",  "Кэш промах для ключа user:%d")
            },
            LogLevel.INFO, new Scenario[] {
                    new Scenario("auth.LoginService",  "Пользователь user%d успешно авторизован"),
                    new Scenario("api.UserController", "GET /api/users/%d -> 200"),
                    new Scenario("payment.Gateway",    "Платёж #%d подтверждён"),
                    new Scenario("scheduler.JobRunner","Запущена задача nightly-report-%d")
            },
            LogLevel.WARN, new Scenario[] {
                    new Scenario("cache.RedisClient",  "Высокая задержка ответа: %d мс"),
                    new Scenario("auth.LoginService",  "Неудачная попытка входа для user%d"),
                    new Scenario("db.ConnectionPool",  "Пул соединений почти исчерпан: занято %d"),
                    new Scenario("api.UserController", "Повторная отправка запроса, попытка %d")
            },
            LogLevel.ERROR, new Scenario[] {
                    new Scenario("payment.Gateway",    "Платёж %d отклонён банком"),
                    new Scenario("api.UserController", "NullPointerException в UserMapper.map (req=%d)"),
                    new Scenario("auth.LoginService",  "Неверный токен сессии (user=%d)"),
                    new Scenario("cache.RedisClient",  "Превышено время ожидания ответа Redis (%d мс)")
            },
            LogLevel.FATAL, new Scenario[] {
                    new Scenario("db.ConnectionPool",  "Потеря связи с master-нодой (try=%d)"),
                    new Scenario("scheduler.JobRunner","Падение планировщика, код %d"),
                    new Scenario("payment.Gateway",    "Шлюз недоступен более %d секунд")
            }
    );

    private final ScheduledExecutorService scheduler;
    private final Random random = new Random();
    private volatile boolean running = false;

    public LogGeneratorService() {
        this.scheduler = Executors.newSingleThreadScheduledExecutor(r -> {
            Thread t = new Thread(r, "log-generator");
            t.setDaemon(true);
            return t;
        });
    }

    public synchronized void start(long periodMillis) {
        if (running) return;
        running = true;
        scheduler.scheduleAtFixedRate(this::generateBatch, 0, periodMillis, TimeUnit.MILLISECONDS);
    }

    public synchronized void stop() {
        running = false;
    }

    public boolean isRunning() { return running; }

    private void generateBatch() {
        if (!running) return;
        int n = 1 + random.nextInt(5);
        LogEntry[] arr = new LogEntry[n];
        for (int i = 0; i < n; i++) arr[i] = randomEntry();
        publish(List.of(arr));
    }

    private LogEntry randomEntry() {
        LogLevel level = weightedLevel();
        Scenario[] pool = SCENARIOS.get(level);
        Scenario s = pool[random.nextInt(pool.length)];
        String msg = String.format(s.message(), 1 + random.nextInt(999));
        return LogEntry.builder()
                .timestamp(LocalDateTime.now())
                .level(level)
                .source(s.source())
                .message(msg)
                .build();
    }

    private LogLevel weightedLevel() {
        int r = random.nextInt(100);
        if (r < 5)  return LogLevel.FATAL;
        if (r < 20) return LogLevel.ERROR;
        if (r < 40) return LogLevel.WARN;
        if (r < 75) return LogLevel.INFO;
        if (r < 95) return LogLevel.DEBUG;
        return LogLevel.TRACE;
    }

    @Override
    public void close() {
        running = false;
        scheduler.shutdownNow();
    }
}
