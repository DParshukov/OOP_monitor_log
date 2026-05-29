package com.logmonitor.view;

import com.logmonitor.model.LogEntry;
import java.util.List;

public interface LogView {
    void renderEntries(List<LogEntry> entries);
    void showStatus(String message);
}
