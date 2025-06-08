package ru.bright.gui;

import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import ru.bright.utils.ElementPosition;
import ru.bright.utils.HistoryCommand;

import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class HistoryWindow extends Window {
    private List<Label> labels;
    private Stage stage;
    private Label title;
    private MainApp app;

    public HistoryWindow(MainApp app) {
        super(app);
        this.labels = new ArrayList<>();
        this.bundle = app.bundle;
        this.app = app;
    }

    @Override
    protected Stage getStage() {
        this.stage = new Stage();
        stage.setTitle(bundle.getString("window.history"));
        return stage;
    }

    @Override
    public ResourceBundle getBundle() {
        return bundle;
    }


    @Override
    void initUI() {
        title = new Label(bundle.getString("title.history"));
        for(int i = 0; i < 13; i++) {
            labels.add(new Label());
        }
        title.setFont(Font.font("Ubuntu-Regular",19));
    }

    @Override
    protected void updateUITexts() {
        this.bundle = getApp().bundle;
        title.setText(bundle.getString("title.history"));
        List<HistoryCommand> l = app.getCommands();
        for(int i = 0; i < 13; i++) {
            String s = (i+1) + ". ";
            if(l.size() > i) {
                s += (l.get(i) == null ? "" : l.get(i).get());
            }
            labels.get(i).setText(s);
        }
    }

    @Override
    Pane createLayout() {
        Pane pane = new Pane();
        pane.getChildren().addAll(labels);
        pane.getChildren().add(title);
        return pane;
    }

    @Override
    void pullOriginalPositions() {
        putPosition(title,new ElementPosition(0,0,308,20));
        title.setAlignment(Pos.CENTER);
        int y0 = 30;
        for(int i = 0; i < 13; i++) {
            labels.get(i).setFont(Font.font("Ubuntu-Regular",14));
            putPosition(labels.get(i),new ElementPosition(17,y0,141,20));
            y0 += 20;
        }

    }

    @Override
    void fullCentering() {

    }

    @Override
    public double getWidth() {
        return 308;
    }

    @Override
    public double getHeight() {
        return 330;
    }
}
