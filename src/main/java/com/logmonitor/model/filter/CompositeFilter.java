package com.logmonitor.model.filter;

import com.logmonitor.model.LogEntry;
import java.util.ArrayList;
import java.util.List;

/** Composite: объединяет произвольное количество стратегий по AND-семантике. */
public final class CompositeFilter implements LogFilter {
    private final List<LogFilter> filters = new ArrayList<>();

    public CompositeFilter add(LogFilter f) {
        if (f != null) filters.add(f);
        return this;
    }

    @Override
    public boolean matches(LogEntry entry) {
        for (LogFilter f : filters) {
            if (!f.matches(entry)) return false;
        }
        return true;
    }
}
