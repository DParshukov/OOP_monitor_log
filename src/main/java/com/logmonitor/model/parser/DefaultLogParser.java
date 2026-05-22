package com.logmonitor.model.parser;

import com.logmonitor.model.LogEntry;
import com.logmonitor.model.LogLevel;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Формат строки:
 *   2026-05-22 12:34:56 [INFO ] com.acme.Module - message text
 */
public final class DefaultLogParser implements LogParser {

    private static final DateTimeFormatter TS_FMT =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private static final Pattern LINE = Pattern.compile(
            "^(\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2})\\s+" +
            "\\[\\s*(TRACE|DEBUG|INFO|WARN|ERROR|FATAL)\\s*\\]\\s+" +
            "(\\S+)\\s+-\\s+(.*)$"
    );

    @Override
    public Optional<LogEntry> parse(String line) {
        if (line == null || line.isBlank()) return Optional.empty();
        Matcher m = LINE.matcher(line);
        if (!m.matches()) return Optional.empty();

        LocalDateTime ts;
        try {
            ts = LocalDateTime.parse(m.group(1), TS_FMT);
        } catch (DateTimeParseException e) {
            return Optional.empty();
        }
        return Optional.of(LogEntry.builder()
                .timestamp(ts)
                .level(LogLevel.fromString(m.group(2)))
                .source(m.group(3))
                .message(m.group(4))
                .build());
    }
}
