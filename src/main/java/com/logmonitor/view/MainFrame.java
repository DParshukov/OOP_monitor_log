package com.logmonitor.view;

import com.logmonitor.controller.LogController;
import com.logmonitor.i18n.LocaleManager;
import com.logmonitor.model.LogEntry;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JToolBar;
import javax.swing.RowSorter;
import javax.swing.SortOrder;
import javax.swing.SwingUtilities;
import javax.swing.table.TableRowSorter;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.util.List;
import java.util.Locale;

public final class MainFrame extends JFrame implements LogView {

    private final LocaleManager i18n = LocaleManager.getInstance();
    private final LogTableModel tableModel = new LogTableModel();
    private final JTable table = new JTable(tableModel);
    private final JLabel statusLabel = new JLabel();
    private final JLabel countsLabel = new JLabel();

    private LogController controller;
    private FilterPanel filterPanel;

    private JMenu fileMenu;
    private JMenuItem openItem;
    private JMenuItem exitItem;
    private JMenu langMenu;
    private JMenuItem ruItem;
    private JMenuItem enItem;
    private JButton startGenBtn;
    private JButton stopGenBtn;
    private JButton clearBtn;

    public MainFrame() {
        super();
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(new Dimension(1100, 650));
        setLocationRelativeTo(null);

        buildMenu();
        buildTable();
        setLayout(new BorderLayout());
        add(buildToolBar(), BorderLayout.NORTH);
        add(new JScrollPane(table), BorderLayout.CENTER);
        add(buildStatusBar(), BorderLayout.SOUTH);

        retranslate();
        i18n.addLocaleChangeListener(this::retranslate);
    }

    public void attachController(LogController controller) {
        this.controller = controller;
        filterPanel = new FilterPanel(controller);
        add(filterPanel, BorderLayout.WEST);
        revalidate();
    }

    private void buildMenu() {
        JMenuBar bar = new JMenuBar();
        fileMenu = new JMenu();
        openItem = new JMenuItem();
        exitItem = new JMenuItem();
        openItem.addActionListener(e -> chooseFile());
        exitItem.addActionListener(e -> dispose());
        fileMenu.add(openItem);
        fileMenu.addSeparator();
        fileMenu.add(exitItem);

        langMenu = new JMenu();
        ruItem = new JMenuItem();
        enItem = new JMenuItem();
        ruItem.addActionListener(e -> i18n.setLocale(new Locale("ru")));
        enItem.addActionListener(e -> i18n.setLocale(new Locale("en")));
        langMenu.add(ruItem);
        langMenu.add(enItem);

        bar.add(fileMenu);
        bar.add(langMenu);
        setJMenuBar(bar);
    }

    private JToolBar buildToolBar() {
        JToolBar tb = new JToolBar();
        tb.setFloatable(false);
        startGenBtn = new JButton();
        stopGenBtn  = new JButton();
        clearBtn    = new JButton();
        startGenBtn.addActionListener(e -> {
            if (controller != null) controller.startGenerator(700);
        });
        stopGenBtn.addActionListener(e -> {
            if (controller != null) controller.stopGenerator();
        });
        clearBtn.addActionListener(e -> {
            if (controller != null) controller.clear();
        });
        tb.add(startGenBtn);
        tb.add(stopGenBtn);
        tb.addSeparator();
        tb.add(clearBtn);
        return tb;
    }

    private void buildTable() {
        table.setAutoCreateRowSorter(false);
        TableRowSorter<LogTableModel> sorter = new TableRowSorter<>(tableModel);
        sorter.setSortKeys(List.of(new RowSorter.SortKey(0, SortOrder.DESCENDING)));
        table.setRowSorter(sorter);
        table.getColumnModel().getColumn(1).setCellRenderer(new LevelCellRenderer());
        table.getColumnModel().getColumn(0).setPreferredWidth(150);
        table.getColumnModel().getColumn(1).setPreferredWidth(70);
        table.getColumnModel().getColumn(2).setPreferredWidth(200);
        table.getColumnModel().getColumn(3).setPreferredWidth(600);
    }

    private JPanel buildStatusBar() {
        JPanel p = new JPanel(new BorderLayout());
        p.setBorder(BorderFactory.createEmptyBorder(2, 8, 2, 8));
        p.add(statusLabel, BorderLayout.WEST);
        p.add(countsLabel, BorderLayout.EAST);
        return p;
    }

    private void chooseFile() {
        JFileChooser fc = new JFileChooser();
        if (fc.showOpenDialog(this) == JFileChooser.APPROVE_OPTION && controller != null) {
            var file = fc.getSelectedFile().toPath();
            controller.openFile(file);
            showStatus(i18n.t("status.loaded", file.getFileName().toString()));
        }
    }

    private void retranslate() {
        setTitle(i18n.t("app.title"));
        fileMenu.setText(i18n.t("menu.file"));
        openItem.setText(i18n.t("menu.file.open"));
        exitItem.setText(i18n.t("menu.file.exit"));
        langMenu.setText(i18n.t("menu.language"));
        ruItem.setText(i18n.t("menu.language.ru"));
        enItem.setText(i18n.t("menu.language.en"));
        startGenBtn.setText(i18n.t("generator.start"));
        stopGenBtn.setText(i18n.t("generator.stop"));
        clearBtn.setText(i18n.t("generator.clear"));
        statusLabel.setText(i18n.t("status.ready"));
        tableModel.fireTableStructureChanged();
        buildTable();
    }

    @Override
    public void renderEntries(List<LogEntry> entries) {
        SwingUtilities.invokeLater(() -> {
            tableModel.setRows(entries);
            int total = controller != null ? controller.totalCount() : entries.size();
            countsLabel.setText(i18n.t("status.total", total) + "   |   " + i18n.t("status.shown", entries.size()));
        });
    }

    @Override
    public void showStatus(String message) {
        SwingUtilities.invokeLater(() -> statusLabel.setText(message));
    }
}
