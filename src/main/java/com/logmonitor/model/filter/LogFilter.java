package com.logmonitor.model.filter;

import com.logmonitor.model.LogEntry;

/** Strategy: каждый конкретный фильтр — самостоятельная стратегия отбора записей. */
@FunctionalInterface
public interface LogFilter {
    boolean matches(LogEntry entry);

    default LogFilter and(LogFilter other) {
        return e -> this.matches(e) && other.matches(e);
    }

    default LogFilter or(LogFilter other) {
        return e -> this.matches(e) || other.matches(e);
    }

    static LogFilter acceptAll() { return e -> true; }
}
