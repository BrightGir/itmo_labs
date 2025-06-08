package ru.bright.util;

import javafx.collections.ObservableList;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Control;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.util.Callback;
import ru.bright.gui.Window;
import ru.bright.model.Furnish;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class CustomComboBoxField<T> extends CustomField {

    private ComboBox<T> comboBox;
    private Collection<T> currentItems;

    /**
     * @param labelText
     * @param window
     * @param layoutY      - layout for TextField
     * @param layoutX      - layout for TextField
     * @param width
     * @param height
     * @param labelOffset
     * @param statusOffset
     */
    public CustomComboBoxField(String labelBundleText, Window window, double layoutX, double layoutY, double width, double height, int labelOffset, int statusOffset) {
        super(labelBundleText, window, layoutX, layoutY, width, height, labelOffset, statusOffset);
    }

    @Override
    public Control createControl() {
        comboBox = new ComboBox<>();
        return comboBox;
    }

    @Override
    public void update() {
        super.update();
        T selected = comboBox.getValue();
        List<T> itemsCopy = new ArrayList<>(comboBox.getItems());
        comboBox.getItems().clear();
        comboBox.getItems().addAll(itemsCopy);
        if (selected != null && comboBox.getItems().contains(selected)) {
            comboBox.setValue(selected);
        }
        // T selected = comboBox.getValue();
        // ObservableList<T> currentItems = comboBox.getItems();
        // setItems(null);
        // setItems(currentItems);
        // comboBox.setValue(selected);
    }

    public void setDefault(T item) {
        comboBox.setValue(item);
    }

    public void setItems(Collection<T> items) {
        if(items == null) {
            comboBox.getItems().clear();
            return;
        }
        comboBox.getItems().setAll(items);
    }

    public void setView(Callback<ListView<T>, ListCell<T>> cell, ListCell<T> buttonCell) {
        comboBox.setButtonCell(buttonCell);
        comboBox.setCellFactory(cell);
    }
}
