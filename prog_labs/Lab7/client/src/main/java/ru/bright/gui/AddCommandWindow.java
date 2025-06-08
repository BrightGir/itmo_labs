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

public class AddCommandWindow extends BaseFlatWindow {


    public AddCommandWindow(MainApp app) {
        super(app);
    }

    @Override
    public void initUI() {
        super.initUI();
        getButton().setOnAction(e -> {
            if(checkFieldsCorrect()) {
                try {
                    Flat flat = new Flat();
                    flat.setOwnerLogin(getClient().getUser().getLogin());
                    flat.setName(getFlatName());
                    flat.setArea(Float.valueOf(getFlatArea()));
                    flat.setCoordinates(new Coordinates(Float.parseFloat(getFlatX()), Integer.valueOf(getFlatY())));
                    flat.setTimeToMetroOnFoot(Float.valueOf(getFlatMetro()));
                    flat.setNumberOfRooms(Integer.valueOf(getFlatRooms()));
                    flat.setFurnish(getFlatFurnish());
                    flat.setCentralHeating(getFlatHeating());
                    flat.setHouse(new House(getHouseName(), Integer.valueOf(getHouseAge()),
                            Integer.valueOf(getHouseFlats())));
                    flat.setCreationDate(ZonedDateTime.now());
                    CompletableFuture<Response> resp =  getClient().requestToServer(new UserRequest(getClient().getUser(), "add", flat));
                    if(resp == null) {
                        AddCommandWindow.this.throwConnectingErrorAlert();
                        return;
                    }
                    resp.thenAcceptAsync(r -> {
                        Platform.runLater(() -> {
                            if(r.getStatus() == ResponseStatus.OK) {
                                MainApp.showCustomAlert(bundle.getString("button.ok"), bundle.getString("title.success"),bundle.getString("info.was_added_successful"),
                                        Font.font("Ubuntu-regular",15),null);
                                getApp().addCommand(new HistoryCommand(getApp(),"command.add",flat.getName()));
                                clearAllFields();
                            } else {
                                showErrorToConsole("Response status for add flat not OK: " + r);
                                throwErrorAlert();
                            }
                        });
                    });
                } catch (ConnectException e1) {
                    showErrorToConsole("Error ocurred add command (server): " + e1.getMessage());
                    AddCommandWindow.this.throwConnectingErrorAlert();
                } catch (Exception e2) {
                    showErrorToConsole("Error ocurred add command: " + e2.getMessage());
                    AddCommandWindow.this.throwErrorAlert();
                }

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
        return bundle.getString("title.add_command_window");
    }

    @Override
    String getButtonName() {
        return bundle.getString("button.create");
    }
}
