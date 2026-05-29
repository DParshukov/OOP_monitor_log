package com.logmonitor.view;

import com.logmonitor.controller.LogController;
import com.logmonitor.i18n.LocaleManager;
import com.logmonitor.model.LogLevel;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import java.awt.FlowLayout;

public final class FilterPanel extends JPanel {

    private final LogController controller;
    private final LocaleManager i18n = LocaleManager.getInstance();

    private final JLabel levelLabel = new JLabel();
    private final JLabel keywordLabel = new JLabel();
    private final JComboBox<Object> levelBox = new JComboBox<>();
    private final JTextField keywordField = new JTextField(20);
    private final JButton applyBtn = new JButton();
    private final JButton resetBtn = new JButton();

    public FilterPanel(LogController controller) {
        this.controller = controller;
        setLayout(new FlowLayout(FlowLayout.LEFT, 8, 6));
        setBorder(BorderFactory.createTitledBorder(i18n.t("filter.title")));

        rebuildLevelBox();
        add(levelLabel);
        add(levelBox);
        add(keywordLabel);
        add(keywordField);
        add(applyBtn);
        add(resetBtn);

        applyBtn.addActionListener(e -> apply());
        resetBtn.addActionListener(e -> reset());

        retranslate();
        i18n.addLocaleChangeListener(this::retranslate);
    }

    private void rebuildLevelBox() {
        Object selected = levelBox.getSelectedItem();
        levelBox.removeAllItems();
        levelBox.addItem(i18n.t("filter.any"));
        for (LogLevel l : LogLevel.values()) levelBox.addItem(l);
        if (selected != null) levelBox.setSelectedItem(selected);
    }

    private void apply() {
        Object sel = levelBox.getSelectedItem();
        controller.getCriteria().setMinLevel(sel instanceof LogLevel ll ? ll : null);
        controller.getCriteria().setKeyword(keywordField.getText());
        controller.refreshView();
    }

    private void reset() {
        levelBox.setSelectedIndex(0);
        keywordField.setText("");
        controller.getCriteria().setMinLevel(null);
        controller.getCriteria().setKeyword("");
        controller.refreshView();
    }

    private void retranslate() {
        levelLabel.setText(i18n.t("filter.level") + ":");
        keywordLabel.setText(i18n.t("filter.keyword") + ":");
        applyBtn.setText(i18n.t("filter.apply"));
        resetBtn.setText(i18n.t("filter.reset"));
        setBorder(BorderFactory.createTitledBorder(i18n.t("filter.title")));
        rebuildLevelBox();
        revalidate();
        repaint();
    }
}
