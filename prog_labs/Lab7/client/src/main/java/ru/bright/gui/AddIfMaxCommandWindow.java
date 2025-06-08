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
import java.util.Collection;
import java.util.Comparator;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.concurrent.CompletableFuture;

public class AddIfMaxCommandWindow extends BaseFlatWindow{

    private ResourceBundle bundle;
    private MainWindow mainWindow;

    public AddIfMaxCommandWindow(MainApp app, MainWindow mainWindow) {
        super(app);
        this.bundle = app.bundle;
        this.mainWindow = mainWindow;
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

                    Optional<Flat> maxFlat =  mainWindow.getActualFlats().stream().max(Comparator.naturalOrder());
                    if(maxFlat.isEmpty() || maxFlat.get().compareTo(flat) < 0) {
                        CompletableFuture<Response> resp =  getClient().requestToServer(new UserRequest(getClient().getUser(), "add", flat));
                        if(resp == null) {
                            AddIfMaxCommandWindow.this.throwConnectingErrorAlert();
                            return;
                        }
                        resp.thenAcceptAsync(r -> {
                            Platform.runLater(() -> {
                                if(r.getStatus() == ResponseStatus.OK) {
                                    MainApp.showCustomAlert(
                                            bundle.getString("button.ok"),bundle.getString("title.success"),
                                            bundle.getString("info.was_added_successful"),
                                            Font.font("Ubuntu-regular",15),null);
                                    getApp().addCommand(new HistoryCommand(getApp(),"command.add_if_max",""));
                                    clearAllFields();
                                } else {
                                    showErrorToConsole("Response status for addIfMax flat not OK: " + r);
                                    throwErrorAlert();
                                }
                            });
                        });
                    } else {
                        MainApp.showCustomAlert(bundle.getString("button.ok"),bundle.getString("title.success"),bundle.getString("info.not_added_if_max"),
                                Font.font("Ubuntu-regular",15),null);
                    }
                } catch (ConnectException e1) {
                    showErrorToConsole("Error ocurred add command: " + e1.getMessage());
                    AddIfMaxCommandWindow.this.throwConnectingErrorAlert();
                } catch (Exception e2) {
                    showErrorToConsole("Error ocurred add command: " + e2.getMessage());
                    AddIfMaxCommandWindow.this.throwErrorAlert();
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
        return bundle.getString("title.add_if_max_window");
    }

    @Override
    String getButtonName() {
        return bundle.getString("button.add");
    }
}
