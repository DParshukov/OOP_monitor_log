package com.logmonitor.model;

import com.logmonitor.model.filter.LogFilter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Потокобезопасное in-memory хранилище логов.
 * ReadWriteLock даёт лучшую производительность при частом чтении (фильтрация в UI)
 * и относительно редких записях (батчи из ридера/генератора).
 */
public final class InMemoryLogRepository implements LogRepository {

    private final List<LogEntry> entries = new ArrayList<>();
    private final ReadWriteLock lock = new ReentrantReadWriteLock();

    @Override
    public void add(LogEntry entry) {
        lock.writeLock().lock();
        try { entries.add(entry); } finally { lock.writeLock().unlock(); }
    }

    @Override
    public void addAll(List<LogEntry> batch) {
        lock.writeLock().lock();
        try { entries.addAll(batch); } finally { lock.writeLock().unlock(); }
    }

    @Override
    public void clear() {
        lock.writeLock().lock();
        try { entries.clear(); } finally { lock.writeLock().unlock(); }
    }

    @Override
    public int size() {
        lock.readLock().lock();
        try { return entries.size(); } finally { lock.readLock().unlock(); }
    }

    @Override
    public List<LogEntry> findAll() {
        lock.readLock().lock();
        try { return new ArrayList<>(entries); } finally { lock.readLock().unlock(); }
    }

    @Override
    public List<LogEntry> find(LogFilter filter, Comparator<LogEntry> sorter) {
        lock.readLock().lock();
        try {
            List<LogEntry> result = new ArrayList<>(entries.size());
            for (LogEntry e : entries) {
                if (filter == null || filter.matches(e)) result.add(e);
            }
            if (sorter != null) result.sort(sorter);
            return result;
        } finally {
            lock.readLock().unlock();
        }
    }
}
