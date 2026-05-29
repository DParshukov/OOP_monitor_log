package com.logmonitor.model;

public enum LogLevel {
    TRACE, DEBUG, INFO, WARN, ERROR, FATAL;

    public static LogLevel fromString(String raw) {
        if (raw == null) return INFO;
        try {
            return LogLevel.valueOf(raw.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            return INFO;
        }
    }

    public boolean isAtLeast(LogLevel other) {
        return this.ordinal() >= other.ordinal();
    }
}
