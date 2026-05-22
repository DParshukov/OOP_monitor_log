package com.logmonitor.service;

import com.logmonitor.model.LogEntry;
import com.logmonitor.model.LogLevel;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Генерирует тестовые логи в отдельном демоническом потоке.
 * Демонстрация работы с многопоточностью + Observer.
 */
public final class LogGeneratorService extends ObservableLogSource implements AutoCloseable {

    private static final String[] SOURCES = {
            "auth.LoginService", "db.ConnectionPool", "api.UserController",
            "cache.RedisClient", "scheduler.JobRunner", "payment.Gateway"
    };
    private static final String[] MESSAGES = {
            "Запрос выполнен", "Превышено время ожидания",
            "Не удалось подключиться к БД", "Кэш промах",
            "Пользователь авторизован", "Неверный токен",
            "Платёж подтверждён", "Внутренняя ошибка сервиса"
    };
    private static final LogLevel[] LEVELS = LogLevel.values();

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
        return LogEntry.builder()
                .timestamp(LocalDateTime.now())
                .level(weightedLevel())
                .source(SOURCES[random.nextInt(SOURCES.length)])
                .message(MESSAGES[random.nextInt(MESSAGES.length)])
                .build();
    }

    private LogLevel weightedLevel() {
        int r = random.nextInt(100);
        if (r < 5) return LogLevel.FATAL;
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
