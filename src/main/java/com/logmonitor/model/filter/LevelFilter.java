package com.logmonitor.model.filter;

import com.logmonitor.model.LogEntry;
import com.logmonitor.model.LogLevel;

public final class LevelFilter implements LogFilter {
    private final LogLevel minLevel;

    public LevelFilter(LogLevel minLevel) { this.minLevel = minLevel; }

    @Override
    public boolean matches(LogEntry entry) {
        return entry.getLevel().isAtLeast(minLevel);
    }
}
