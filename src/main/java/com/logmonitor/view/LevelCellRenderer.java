package com.logmonitor.view;

import com.logmonitor.model.LogLevel;

import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.Color;
import java.awt.Component;

public final class LevelCellRenderer extends DefaultTableCellRenderer {

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value,
                                                   boolean isSelected, boolean hasFocus,
                                                   int row, int column) {
        JLabel c = (JLabel) super.getTableCellRendererComponent(
                table, value, isSelected, hasFocus, row, column);
        try {
            LogLevel lvl = LogLevel.valueOf(String.valueOf(value));
            c.setForeground(colorFor(lvl));
        } catch (IllegalArgumentException ignored) {
            c.setForeground(Color.BLACK);
        }
        return c;
    }

    private Color colorFor(LogLevel l) {
        return switch (l) {
            case FATAL -> new Color(140, 0, 0);
            case ERROR -> new Color(200, 0, 0);
            case WARN  -> new Color(200, 120, 0);
            case INFO  -> new Color(0, 100, 0);
            case DEBUG -> new Color(80, 80, 80);
            case TRACE -> new Color(120, 120, 120);
        };
    }
}
