package com.logmonitor;

import com.logmonitor.controller.LogController;
import com.logmonitor.model.InMemoryLogRepository;
import com.logmonitor.model.LogRepository;
import com.logmonitor.model.parser.DefaultLogParser;
import com.logmonitor.service.LogFileReaderService;
import com.logmonitor.service.LogGeneratorService;
import com.logmonitor.view.MainFrame;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;

public final class Main {

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) { }

        SwingUtilities.invokeLater(Main::launch);
    }

    private static void launch() {
        LogRepository repo = new InMemoryLogRepository();
        LogFileReaderService reader = new LogFileReaderService(new DefaultLogParser());
        LogGeneratorService generator = new LogGeneratorService();

        MainFrame frame = new MainFrame();
        LogController controller = new LogController(repo, reader, generator, frame);
        frame.attachController(controller);

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            reader.close();
            generator.close();
        }, "shutdown-hook"));

        frame.setVisible(true);
        controller.refreshView();
    }
}
