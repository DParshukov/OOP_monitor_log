package com.logmonitor.service;

import com.logmonitor.model.LogEntry;
import com.logmonitor.model.parser.LogParser;

import java.io.BufferedReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Загружает лог-файл в фоновом пуле потоков, чтобы UI не блокировался.
 * Парсинг идёт построчно, накопленные записи публикуются батчами слушателям.
 */
public final class LogFileReaderService extends ObservableLogSource implements AutoCloseable {

    private static final int BATCH_SIZE = 200;

    private final LogParser parser;
    private final ExecutorService executor;

    public LogFileReaderService(LogParser parser) {
        this.parser = parser;
        this.executor = Executors.newSingleThreadExecutor(namedThreadFactory("log-reader"));
    }

    public void loadAsync(Path file) {
        executor.submit(() -> readFile(file));
    }

    private void readFile(Path file) {
        try (BufferedReader br = Files.newBufferedReader(file, StandardCharsets.UTF_8)) {
            List<LogEntry> batch = new ArrayList<>(BATCH_SIZE);
            String line;
            while ((line = br.readLine()) != null) {
                Optional<LogEntry> parsed = parser.parse(line);
                parsed.ifPresent(batch::add);
                if (batch.size() >= BATCH_SIZE) {
                    publish(batch);
                    batch = new ArrayList<>(BATCH_SIZE);
                }
            }
            publish(batch);
        } catch (Exception e) {
            System.err.println("Не удалось прочитать файл " + file + ": " + e.getMessage());
        }
    }

    @Override
    public void close() {
        executor.shutdownNow();
    }

    private static ThreadFactory namedThreadFactory(String prefix) {
        AtomicInteger n = new AtomicInteger(1);
        return r -> {
            Thread t = new Thread(r, prefix + "-" + n.getAndIncrement());
            t.setDaemon(true);
            return t;
        };
    }
}
