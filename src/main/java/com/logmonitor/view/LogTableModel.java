package com.logmonitor.view;

import com.logmonitor.i18n.LocaleManager;
import com.logmonitor.model.LogEntry;

import javax.swing.table.AbstractTableModel;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public final class LogTableModel extends AbstractTableModel {

    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private List<LogEntry> rows = new ArrayList<>();
    private final LocaleManager i18n = LocaleManager.getInstance();

    public void setRows(List<LogEntry> rows) {
        this.rows = rows;
        fireTableDataChanged();
    }

    public LogEntry getRow(int row) { return rows.get(row); }

    @Override public int getRowCount() { return rows.size(); }
    @Override public int getColumnCount() { return 4; }

    @Override
    public String getColumnName(int column) {
        return switch (column) {
            case 0 -> i18n.t("table.timestamp");
            case 1 -> i18n.t("table.level");
            case 2 -> i18n.t("table.source");
            case 3 -> i18n.t("table.message");
            default -> "";
        };
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        LogEntry e = rows.get(rowIndex);
        return switch (columnIndex) {
            case 0 -> e.getTimestamp().format(FMT);
            case 1 -> e.getLevel().name();
            case 2 -> e.getSource();
            case 3 -> e.getMessage();
            default -> "";
        };
    }
}
