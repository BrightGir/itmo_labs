package ru.bright.gui;

import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;
import ru.bright.Response;
import ru.bright.ResponseStatus;
import ru.bright.UserRequest;
import ru.bright.model.Flat;
import ru.bright.util.CustomTextField;
import ru.bright.utils.ElementPosition;
import ru.bright.utils.HistoryCommand;

import java.io.IOException;
import java.net.ConnectException;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.concurrent.CompletableFuture;

public class RemoveByIdCommandWindow extends Window {

    private Stage stage;
    private CustomTextField field;
    private Button doneButton;
    private MainWindow mainWindow;
    private Label label;

    public RemoveByIdCommandWindow(MainApp app, MainWindow mainWindow) {
        super(app);
        this.stage = new Stage();
        this.mainWindow = mainWindow;
    }

    @Override
    protected Stage getStage() {
        return stage;
    }

    @Override
    public ResourceBundle getBundle() {
        return getApp().bundle;
    }

    @Override
    void initUI() {
        this.label = new Label(bundle.getString("label.type_id_for_remove"));
        this.doneButton = new Button(bundle.getString("button.delete"));
        this.field = new CustomTextField( this, 175,30,
                180,20,30);
        MainApp.createStyledGreenButton(doneButton, Font.font("Ubuntu-regular",13),5,5);
        doneButton.setOnAction(event -> {
            if(field.getText().isEmpty()) {
                field.setStatus(Color.RED,"error.empty_credentials");
                return;
            }
            if(!field.isTextInt()) {
                field.setStatus(Color.RED,"error.must_be_int");
                return;
            }
            long id = Long.parseLong(field.getText());
            Optional<Flat> fl = mainWindow.getActualFlats().stream().filter(f -> f.getId().equals(id)).findFirst();
            if(fl.isEmpty()) {
                field.setStatus(Color.RED,"error.flat_with_id_not_exists");
                return;
            }
            if(!fl.get().getOwnerLogin().equals(getClient().getUser().getLogin())) {
                field.setStatus(Color.RED,"error.flat_remove_access_denied");
                return;
            }

            try {
                CompletableFuture<Response> resp = getClient().requestToServer(new UserRequest(getClient().getUser(),
                        "remove_by_id " + id,null));
                if(resp == null) {
                    RemoveByIdCommandWindow.this.throwConnectingErrorAlert();
                    return;
                }
                resp.thenAcceptAsync(r -> {
                    if(r.getStatus() == ResponseStatus.OK) {
                        Platform.runLater(() -> {
                            if(r.getStatus() == ResponseStatus.OK) {
                                Stage st = MainApp.showCustomAlert(bundle.getString("button.ok"),bundle.getString("title.success"),
                                        bundle.getString("info.was_removed_successful")
                                                .replaceAll("\\{id}",String.valueOf(id)),
                                        Font.font("Ubuntu-regular",13),null);
                                getApp().addCommand(new HistoryCommand(getApp(),"command.remove","("+id+")"));
                                stage.close();
                             //   st.setOnCloseRequest(q -> stage.close());
                            } else {
                                showErrorToConsole("Response status for remove flat not OK: " + r);
                                throwErrorAlert();
                            }
                        });
                    }
                });
            } catch (ConnectException e1) {
                showErrorToConsole("Error ocurred add command: " + e1.getMessage());
                RemoveByIdCommandWindow.this.throwConnectingErrorAlert();
            } catch (Exception e2) {
                showErrorToConsole("Error ocurred add command: " + e2.getMessage());
                RemoveByIdCommandWindow.this.throwErrorAlert();
            }
        });
    }

    @Override
    protected void updateUITexts() {
        field.update();
        doneButton.setText(bundle.getString("button.delete"));
        label.setText(bundle.getString("label.type_id_for_remove"));
    }


    @Override
    Pane createLayout() {
        Pane pane = new Pane();
        pane.getChildren().addAll(doneButton,label);
        field.init(pane);
        field.getStatusLabel().setLayoutX(0);
        field.getStatusLabel().setPrefWidth(530);
        field.getStatusLabel().setAlignment(Pos.CENTER);
       // field.getStatusLabel().setTextAlignment(TextAlignment.TOP_LEFT);
        return pane;
    }

    @Override
    void pullOriginalPositions() {
        putPosition(doneButton,new ElementPosition(211,84,100,10));
        doneButton.setPrefSize(100,10);
        putPosition(label,new ElementPosition(0,5,getWidth(),13));
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
    }
}
