package com.logmonitor.model.filter;

import com.logmonitor.model.LogEntry;
import java.time.LocalDateTime;

public final class TimeRangeFilter implements LogFilter {
    private final LocalDateTime from;
    private final LocalDateTime to;

    public TimeRangeFilter(LocalDateTime from, LocalDateTime to) {
        this.from = from;
        this.to = to;
    }

    @Override
    public boolean matches(LogEntry entry) {
        LocalDateTime t = entry.getTimestamp();
        if (from != null && t.isBefore(from)) return false;
        if (to != null && t.isAfter(to)) return false;
        return true;
    }
}
