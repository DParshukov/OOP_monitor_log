package com.logmonitor.model.filter;

import com.logmonitor.model.LogEntry;

public final class KeywordFilter implements LogFilter {
    private final String keywordLower;

    public KeywordFilter(String keyword) {
        this.keywordLower = keyword == null ? "" : keyword.toLowerCase();
    }

    @Override
    public boolean matches(LogEntry entry) {
        if (keywordLower.isEmpty()) return true;
        return entry.getMessage().toLowerCase().contains(keywordLower)
                || entry.getSource().toLowerCase().contains(keywordLower);
    }
}
