package com.logmonitor.model;

import java.time.LocalDateTime;
import java.util.Objects;

public final class LogEntry {
    private final LocalDateTime timestamp;
    private final LogLevel level;
    private final String source;
    private final String message;

    private LogEntry(Builder b) {
        this.timestamp = Objects.requireNonNull(b.timestamp);
        this.level = Objects.requireNonNull(b.level);
        this.source = b.source == null ? "" : b.source;
        this.message = b.message == null ? "" : b.message;
    }

    public LocalDateTime getTimestamp() { return timestamp; }
    public LogLevel getLevel() { return level; }
    public String getSource() { return source; }
    public String getMessage() { return message; }

    @Override
    public String toString() {
        return timestamp + " [" + level + "] " + source + " - " + message;
    }

    public static Builder builder() { return new Builder(); }

    public static final class Builder {
        private LocalDateTime timestamp = LocalDateTime.now();
        private LogLevel level = LogLevel.INFO;
        private String source;
        private String message;

        public Builder timestamp(LocalDateTime t) { this.timestamp = t; return this; }
        public Builder level(LogLevel l) { this.level = l; return this; }
        public Builder source(String s) { this.source = s; return this; }
        public Builder message(String m) { this.message = m; return this; }
        public LogEntry build() { return new LogEntry(this); }
    }
}
