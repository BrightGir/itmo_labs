package ru.bright.gui;

import javafx.application.Platform;
import javafx.scene.text.Font;
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
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class RemoveLowerCommandWindow extends BaseFlatWindow {

    private MainWindow mainWindow;

    public RemoveLowerCommandWindow(MainApp app, MainWindow mainWindow) {
        super(app);
        this.mainWindow = mainWindow;;
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
                    Collection<Flat> collection = mainWindow.getActualFlats().stream().filter(f ->
                                    (f.getOwnerLogin().equals(getClient().getUser().getLogin())
                                    && f.compareTo(flat) < 0))
                            .collect(Collectors.toList());
                    AtomicInteger k = new AtomicInteger(0);
                    if(collection.size() == 0) {
                        MainApp.showCustomAlert(bundle.getString("button.ok"), bundle.getString("title.success"),
                                bundle.getString("info.was_removed_lower").replaceAll("\\{n}",String.valueOf(0)),
                                Font.font("Ubuntu-regular",15),null);
                        getStage().close();
                        return;
                    }
                    for (Flat f : collection) {
                        CompletableFuture<Response> resp = getClient().requestToServer(new UserRequest(getClient().getUser(),
                                "remove_by_id " + f.getId(), null));
                        if(resp == null) {
                            RemoveLowerCommandWindow.this.throwConnectingErrorAlert();
                            break;
                        }
                        resp.thenAcceptAsync(r -> {
                            Platform.runLater(() -> {
                                if(r.getStatus() == ResponseStatus.OK) {
                                    k.addAndGet(1);
                                } else {
                                    showErrorToConsole("Response status for remove lower flat not OK: " + r);
                                    throwErrorAlert();
                                    return;
                                }
                                if(k.get() == collection.size()) {
                                    getApp().addCommand(new HistoryCommand(getApp(),"command.remove_lower",""));
                                    MainApp.showCustomAlert(bundle.getString("button.ok"), bundle.getString("title.success"),
                                            bundle.getString("info.was_removed_lower").replaceAll("\\{n}",String.valueOf(k.get())),
                                            Font.font("Ubuntu-regular",15),null);
                                    getStage().close();
                                }
                            });
                        });
                    }

                } catch (ConnectException e1) {
                    showErrorToConsole("Error ocurred add command: " + e1.getMessage());
                    RemoveLowerCommandWindow.this.throwConnectingErrorAlert();
                } catch (Exception e2) {
                    showErrorToConsole("Error ocurred add command: " + e2.getMessage());
                    RemoveLowerCommandWindow.this.throwErrorAlert();
                }
            }
        });
    }


    @Override
    String getTitle() {
        return bundle.getString("title.remove_lower");
    }

    @Override
    String getButtonName() {
        return bundle.getString("button.remove");
    }

    @Override
    public double getWidth() {
        return 657;
    }

    @Override
    public double getHeight() {
        return 360;
    }
}
