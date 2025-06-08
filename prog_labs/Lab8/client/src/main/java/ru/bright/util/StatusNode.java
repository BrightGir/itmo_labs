package ru.bright.util;

import javafx.scene.control.Label;
import javafx.scene.paint.Color;
import ru.bright.gui.Window;

public class StatusNode {

    private Label label;
    private Window window;
    private String currentStatus;

    public StatusNode(Window window, Label label) {
        this.label = label;
        this.window = window;
        this.currentStatus = "empty";
    }

    public void setStatus(Color color, String bundleStatus) {
        this.currentStatus = bundleStatus;
        if(window.getBundle().containsKey(bundleStatus)) {
            label.setText(window.getBundle().getString(bundleStatus));
            label.setTextFill(color);
        } else {
            label.setText("");
        }
    }

    public void setEmpty() {
        this.currentStatus = "empty";
        label.setTextFill(Color.BLACK);
        label.setText("");
    }

    public void updateBundleStatus() {
        if(window.getBundle().containsKey(currentStatus)) {
            label.setText(window.getBundle().getString(currentStatus));
        } else {
            label.setText("");
        }
    }

    public Label getLabel() {
        return label;
    }



}
