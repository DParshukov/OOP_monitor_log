package com.logmonitor.controller;

import com.logmonitor.model.LogEntry;
import com.logmonitor.model.LogRepository;
import com.logmonitor.service.LogFileReaderService;
import com.logmonitor.service.LogGeneratorService;
import com.logmonitor.service.LogSourceListener;
import com.logmonitor.view.LogView;

import java.nio.file.Path;
import java.util.Comparator;
import java.util.List;

public final class LogController {

    private final LogRepository repository;
    private final LogFileReaderService fileReader;
    private final LogGeneratorService generator;
    private final LogView view;

    private final FilterCriteria criteria = new FilterCriteria();
    private Comparator<LogEntry> sorter = Comparator.comparing(LogEntry::getTimestamp);

    public LogController(LogRepository repository,
                         LogFileReaderService fileReader,
                         LogGeneratorService generator,
                         LogView view) {
        this.repository = repository;
        this.fileReader = fileReader;
        this.generator = generator;
        this.view = view;

        LogSourceListener appender = batch -> {
            repository.addAll(batch);
            refreshView();
        };
        fileReader.addListener(appender);
        generator.addListener(appender);
    }

    public FilterCriteria getCriteria() { return criteria; }

    public void setSorter(Comparator<LogEntry> sorter) {
        this.sorter = sorter;
        refreshView();
    }

    public void openFile(Path file) {
        fileReader.loadAsync(file);
    }

    public void startGenerator(long periodMillis) { generator.start(periodMillis); }
    public void stopGenerator() { generator.stop(); }
    public boolean isGeneratorRunning() { return generator.isRunning(); }

    public void clear() {
        repository.clear();
        refreshView();
    }

    public void refreshView() {
        List<LogEntry> filtered = repository.find(criteria.toFilter(), sorter);
        view.renderEntries(filtered);
    }

    public int totalCount() { return repository.size(); }
}
