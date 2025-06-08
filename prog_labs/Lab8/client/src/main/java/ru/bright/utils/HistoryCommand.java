package ru.bright.utils;

import ru.bright.gui.MainApp;

import java.util.ResourceBundle;

public class HistoryCommand {

    private String bundleString;
    private String options;
    private ResourceBundle bundle;

    public HistoryCommand(MainApp app, String bundle, String options) {
        this.bundleString = bundle;
        this.options = options;
        this.bundle = app.bundle;
    }

    public String getBundleString() {
        return bundleString;
    }

    public String getOptions() {
        return options;
    }

    public String get() {
        return bundle.getString(bundleString) + " " + options;
    }
}
