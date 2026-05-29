package com.logmonitor.model;

import com.logmonitor.model.filter.LogFilter;
import java.util.List;
import java.util.Comparator;

/**
 * DIP: контроллер и view зависят от абстракции, а не от конкретной реализации хранилища.
 */
public interface LogRepository {
    void add(LogEntry entry);
    void addAll(List<LogEntry> entries);
    void clear();
    int size();
    List<LogEntry> findAll();
    List<LogEntry> find(LogFilter filter, Comparator<LogEntry> sorter);
}
