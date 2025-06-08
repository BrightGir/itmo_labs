package ru.bright.gui;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Modality;
import javafx.stage.Stage;
import ru.bright.Response;
import ru.bright.ResponseStatus;
import ru.bright.UserRequest;
import ru.bright.model.Coordinates;
import ru.bright.model.Flat;
import ru.bright.model.House;
import ru.bright.utils.HistoryCommand;

import java.net.ConnectException;
import java.time.ZonedDateTime;
import java.util.concurrent.CompletableFuture;

public class UpdateWindow extends BaseFlatWindow {

    private Flat flat;
    private MainWindow mainWindow;
    private long id;

    public UpdateWindow(MainApp app, MainWindow mainWindow, long id) {
        super(app);
        this.mainWindow = mainWindow;
        this.id = id;
    }

    @Override
    public void initUI() {
        super.initUI();
        try {
            flat = mainWindow.getActualFlats().stream().filter(f -> f.getId() == id).findFirst().get();
        } catch (Exception e) {
            showErrorToConsole("Error getting flat for update window " + e.getMessage());
            UpdateWindow.this.throwErrorAlert();
            return;
        }
        setFields(flat);
        getButton().setOnAction(e -> {
            try {
                if(!checkFieldsCorrect()) return;
                flat.setName(getFlatName());
                flat.setArea(Float.valueOf(getFlatArea()));
                flat.setCoordinates(new Coordinates(Float.parseFloat(getFlatX()), Integer.valueOf(getFlatY())));
                flat.setTimeToMetroOnFoot(Float.valueOf(getFlatMetro()));
                flat.setNumberOfRooms(Integer.valueOf(getFlatRooms()));
                flat.setFurnish(getFlatFurnish());
                flat.setCentralHeating(getFlatHeating());
                flat.setHouse(new House(getHouseName(), Integer.valueOf(getHouseAge()),
                        Integer.valueOf(getHouseFlats())));
                CompletableFuture<Response> resp = getClient().requestToServer(new UserRequest(getClient().getUser(),
                        "update " + id, flat));
                if(resp == null) {
                    UpdateWindow.this.throwConnectingErrorAlert();
                    return;
                }
                resp.thenAcceptAsync(r -> {
                    Platform.runLater(() -> {
                        if(r.getStatus() == ResponseStatus.OK) {
                            Stage d = MainApp.showCustomAlert(bundle.getString("button.ok"), bundle.getString("title.success"),
                                    bundle.getString("info.was_updated_successful"),
                                    Font.font("Ubuntu-regular",15),null);
                            getApp().addCommand(new HistoryCommand(getApp(),"command.update","(" + id + ")"));
                         //  d.setOnCloseRequest(ev -> {
                         //      getStage().close();
                         //  });
                            getStage().close();
                        } else {
                            showErrorToConsole("Response status for update flat not OK: " + r);
                            throwErrorAlert();
                        }
                    });
                });
            }catch (ConnectException e1) {
                showErrorToConsole("Error ocurred add command: " + e1.getMessage());
                UpdateWindow.this.throwConnectingErrorAlert();
            } catch (Exception e2) {
                showErrorToConsole("Error ocurred add command: " + e2.getMessage());
                UpdateWindow.this.throwErrorAlert();
            }
        });


    }


    @Override
    public double getWidth() {
        return 657;
    }

    @Override
    public double getHeight() {
        return 360;
    }

    @Override
    String getTitle() {
        return (bundle.getString("title.update_window") + " " + String.valueOf(id));
    }

    @Override
    String getButtonName() {
        return bundle.getString("button.update");
    }
}
