package ru.bright.util;

import javafx.geometry.Pos;
import javafx.scene.control.Control;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import ru.bright.gui.Window;
import ru.bright.utils.ElementPosition;

import java.util.concurrent.Callable;
import java.util.function.Function;

public class CustomTextField extends CustomField {

    private TextField textField;
    private Callable<Boolean> correctCheckout;

    /**
     * @param window
     * @param layoutY      - layout for TextField
     * @param layoutX      - layout for TextField
     * @param width
     * @param height
     * @param labelOffset
     * @param statusOffset
     */
    public CustomTextField(String labelBundleText, Window window, double layoutX, double layoutY, double width, double height, int labelOffset, int statusOffset) {
        super(labelBundleText, window, layoutX, layoutY, width, height, labelOffset, statusOffset);
    }

    public CustomTextField(Window window, double layoutX, double layoutY, double width, double height, int statusOffset) {
        super(window, layoutX, layoutY, width, height, statusOffset);
    }

    @Override
    public Control createControl() {
        textField = new TextField();
        return textField;
    }

    public boolean isTextInt() {
        try {
            Integer.parseInt(textField.getText());
        } catch (NumberFormatException e) {
            return false;
        }
        return true;
    }

    public String getText() {
        return textField.getText();
    }

    public void setCorrectCheckout(Callable<Boolean> correctCheckout) {
        this.correctCheckout = correctCheckout;

    }

    public boolean isCorrectField() {
        if(correctCheckout == null) {
            return false;
        }
        try {
            return correctCheckout.call();
        } catch (Exception e) {
            getWindow().throwErrorAlert();
            getWindow().getClient().getConsole().printErr("Error call checkout: " + e.getMessage());
            return false;
        }
    }


    public void setText(String text) {
        textField.setText(text);
    }

    @Override
    public void init(Pane pane) {
        super.init(pane);
        ((TextField) getControl()).setAlignment(Pos.CENTER);
    }
}
