package ru.bright.gui;

import javafx.collections.FXCollections;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import ru.bright.util.CustomTextField;
import ru.bright.utils.ElementPosition;

import java.util.ResourceBundle;

public class TypePrefixWindow extends Window {


    private Stage stage;
    private CustomTextField field;
    private Button doneButton;
    private MainWindow mainWindow;
    private Label label;

    public TypePrefixWindow(MainApp app, MainWindow mainWindow) {
        super(app);
        this.stage = new Stage();
        this.mainWindow = mainWindow;
        stage.setResizable(false);
    }

    @Override
    public Stage getStage() {
        return stage;
    }

    @Override
    public ResourceBundle getBundle() {
        return getApp().bundle;
    }

    @Override
    void initUI() {
        this.label = new Label(bundle.getString("label.type_prefix_for_sort"));
        this.doneButton = new Button(bundle.getString("button.ok"));
        this.field = new CustomTextField( this, 175,30,
                180,20,30);
        MainApp.createStyledGreenButton(doneButton, Font.font("Ubuntu-regular",13),5,5);
        doneButton.setOnAction(event -> {
            if(field.getText().isEmpty()) {
                field.setStatus(Color.RED,"error.empty_credentials");
                return;
            }
            mainWindow.getFlatTableView().setItems(FXCollections.observableArrayList(
                    mainWindow.getActualFlats().stream().filter(f -> f.getName().startsWith(field.getText())).toList()));
            stage.close();
        });
        label.setFont(Font.font("Ubuntu-regular",15));
    }

    @Override
    public void updateUITexts() {
        this.bundle = getApp().bundle;
        field.update();
        doneButton.setText(bundle.getString("button.ok"));
        label.setText(bundle.getString("label.type_prefix_for_sort"));
    }


    @Override
    Pane createLayout() {
        Pane pane = new Pane();
        pane.getChildren().addAll(doneButton, label);
        field.init(pane);
        field.getStatusLabel().setPrefWidth(530);
        field.getStatusLabel().setAlignment(Pos.TOP_LEFT);
        return pane;
    }

    @Override
    void pullOriginalPositions() {
        putPosition(doneButton,new ElementPosition(211,84,100,10));
        doneButton.setPrefSize(100,10);
        putPosition(label,new ElementPosition(0,5,getWidth(),15));
        label.setAlignment(Pos.CENTER);
    }

    @Override
    void fullCentering() {

    }

    @Override
    public double getWidth() {
        return 530;
    }

    @Override
    public double getHeight() {
        return 150;
    }}
