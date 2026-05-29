package com.logmonitor.i18n;

import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.List;

public final class LocaleManager {

    private static final LocaleManager INSTANCE = new LocaleManager();
    private static final String BUNDLE_BASE = "i18n.messages";

    public static LocaleManager getInstance() { return INSTANCE; }

    private final List<Runnable> listeners = new CopyOnWriteArrayList<>();
    private volatile Locale locale = new Locale("ru");
    private volatile ResourceBundle bundle = loadBundle(locale);

    private LocaleManager() { }

    public Locale getLocale() { return locale; }

    public void setLocale(Locale newLocale) {
        this.locale = newLocale;
        this.bundle = loadBundle(newLocale);
        for (Runnable r : listeners) {
            try { r.run(); } catch (RuntimeException ignored) { }
        }
    }

    public void addLocaleChangeListener(Runnable r) { listeners.add(r); }

    public String t(String key) {
        try {
            return bundle.getString(key);
        } catch (MissingResourceException e) {
            return key;
        }
    }

    public String t(String key, Object... args) {
        return String.format(t(key), args);
    }

    private static ResourceBundle loadBundle(Locale loc) {
        return ResourceBundle.getBundle(BUNDLE_BASE, loc);
    }
}
