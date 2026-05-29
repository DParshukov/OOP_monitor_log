package com.logmonitor.service;

import com.logmonitor.model.LogEntry;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/** Subject в паттерне Observer. */
public abstract class ObservableLogSource {

    private final List<LogSourceListener> listeners = new CopyOnWriteArrayList<>();

    public final void addListener(LogSourceListener l) {
        if (l != null) listeners.add(l);
    }

    public final void removeListener(LogSourceListener l) {
        listeners.remove(l);
    }

    protected final void publish(List<LogEntry> batch) {
        if (batch == null || batch.isEmpty()) return;
        for (LogSourceListener l : listeners) {
            try { l.onNewEntries(batch); } catch (RuntimeException ignored) { }
        }
    }
}
