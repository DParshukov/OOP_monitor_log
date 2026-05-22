package com.logmonitor.service;

import com.logmonitor.model.LogEntry;
import java.util.List;

/** Observer: получает уведомления о новых порциях логов. */
@FunctionalInterface
public interface LogSourceListener {
    void onNewEntries(List<LogEntry> entries);
}
