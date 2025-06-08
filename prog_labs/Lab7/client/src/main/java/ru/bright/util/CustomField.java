package ru.bright.util;

import javafx.geometry.Pos;
import javafx.scene.control.Control;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import ru.bright.gui.Window;
import ru.bright.utils.ElementPosition;

public abstract class CustomField {

    private Label label;


    private StatusNode status;
    private boolean labelOff;

    private Window window;
    private double width;
    private double height;
    private int labelOffset, statusOffset;
    private double layoutX, layoutY;
    private Control control;
    private String labelBundleText;

    /**
     * @param layoutX - layout for TextField
     * @param layoutY - layout for TextField
     */
    public CustomField(String labelBundleText, Window window, double layoutX, double layoutY, double width, double height, int labelOffset, int statusOffset) {
        this.window = window;
        this.width = width;
        this.height = height;
        this.layoutX = layoutX;
        this.layoutY = layoutY;
        this.labelOffset = labelOffset;
        this.statusOffset = statusOffset;
        this.labelBundleText = labelBundleText;
        this.label = new Label(window.getBundle().getString(labelBundleText));
        this.status = new StatusNode(window, new Label());
        this.control = createControl();
    }

    public CustomField(Window window, double layoutX, double layoutY, double width, double height, int statusOffset) {
        this.window = window;
        this.width = width;
        this.height = height;
        this.layoutX = layoutX;
        this.layoutY = layoutY;
        this.statusOffset = statusOffset;
        this.status = new StatusNode(window, new Label());
        this.control = createControl();
        this.labelOff = true;
    }

    public abstract Control createControl();

    public void init(Pane pane) {
        window.putPosition(control,new ElementPosition(layoutX, layoutY,width,height));
        window.putPosition(status.getLabel(),new ElementPosition(layoutX,layoutY+statusOffset,width,height));
        status.getLabel().setAlignment(Pos.CENTER);
        if(!labelOff) {
            label.setAlignment(Pos.CENTER);
            window.putPosition(label,new ElementPosition(layoutX,layoutY-labelOffset,width,height));
            pane.getChildren().addAll(label,status.getLabel(),control);
        } else {
            pane.getChildren().addAll(status.getLabel(),control);
        }
    }

    public Label getStatusLabel() {
        return status.getLabel();
    }

    public void setLabelOff(boolean labelOff) {
        this.labelOff = labelOff;
    }

    public Label getLabel() {
        return label;
    }

    public void update() {
        if(!labelOff) {
            label.setText(window.getBundle().getString(labelBundleText));
        }
        status.updateBundleStatus();
    }

    public void updateStatus() {
        status.updateBundleStatus();
    }

    public void setStatus(Color color, String bundleStatus) {
        status.setStatus(color, bundleStatus);
    }

    public void setEmptyStatus() {
        status.setEmpty();
    }

    public void styleControlUbuntuFont(int pt) {
        Font font = Font.font("Ubuntu-Regular",pt);
        label.setFont(font);
    }

    public void styleStatusUbuntuFont(int pt) {
        Font font = Font.font("Ubuntu-Regular",pt);
        status.getLabel().setFont(font);
    }

    protected Window getWindow() {
        return window;
    }

    public Control getControl() {
        return control;
    }



}
