package com.logmonitor.model.parser;

import com.logmonitor.model.LogEntry;
import java.util.Optional;

public interface LogParser {
    Optional<LogEntry> parse(String line);
}
