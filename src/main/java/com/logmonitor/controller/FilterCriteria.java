package com.logmonitor.controller;

import com.logmonitor.model.LogLevel;
import com.logmonitor.model.filter.CompositeFilter;
import com.logmonitor.model.filter.KeywordFilter;
import com.logmonitor.model.filter.LevelFilter;
import com.logmonitor.model.filter.LogFilter;

public final class FilterCriteria {
    private LogLevel minLevel;
    private String keyword = "";

    public LogLevel getMinLevel() { return minLevel; }
    public void setMinLevel(LogLevel minLevel) { this.minLevel = minLevel; }

    public String getKeyword() { return keyword; }
    public void setKeyword(String keyword) { this.keyword = keyword == null ? "" : keyword; }

    public LogFilter toFilter() {
        CompositeFilter f = new CompositeFilter();
        if (minLevel != null) f.add(new LevelFilter(minLevel));
        if (!keyword.isBlank()) f.add(new KeywordFilter(keyword));
        return f;
    }
}
