package ru.bright.gui;

import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.geometry.Pos;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;
import ru.bright.Response;
import ru.bright.UserRequest;
import ru.bright.model.Flat;
import ru.bright.model.Furnish;
import ru.bright.utils.AppAction;
import ru.bright.utils.ElementPosition;

import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.CompletableFuture;

public class MainWindow extends Window {



    private Stage stage;
    private TableView<Flat> flatTableView = new TableView<>();
    private TableColumn<Flat, Long> idCol;
    private TableColumn<Flat, String> nameCol;
    private TableColumn<Flat, Integer> areaCol;
    private TableColumn<Flat, Integer> roomsCol;
    private TableColumn<Flat, Integer> metroCol;
    private TableColumn<Flat, Boolean> heatingCol;
    private TableColumn<Flat, Furnish> furnishCol;
    private TableColumn<Flat, String> houseNameCol;
    private TableColumn<Flat, String> houseYearCol;
    private TableColumn<Flat, String> houseFlatsCol;
    private TableColumn<Flat, String> ownerCol;
    private TableColumn<Flat, String> flatXCol;
    private TableColumn<Flat, String> flatYCol;
    private TableColumn<Flat, String> flatCreationTimeCol;
    private Canvas canvas;
    protected final double CANVAS_WIDTH = 462;
    protected final double CANVAS_HEIGHT = 430;
    private Set<Flat> actualFlats;
    private Button commandAddButton;
    private Button commandAddIfMaxButton;
    private Button commandUpdateButton;
    private Button commandRemoveButton;
    private Button commandRemoveLowerButton;
    private AnimationManager animationManager;
    private Button resetTableButton;
    private Button ascTableButton;
    private Button descTableButton;
    private Button filterPrefixTableButton;
    private Button commandHistoryButton;
    private final int BUTTONS_OFFSET = 20;
    private Button exitButton;
    private Label userLabel;

    public MainWindow(MainApp app, Stage stage) {
        super(app);
        this.stage = stage;
        this.actualFlats = new HashSet<>();
        this.animationManager = new AnimationManager(this);
        updateActualFlats();
    }


    @Override
    void initUI() {
        registerListening(AppAction.SWITCH_LANGUAGE,() -> {
            updateUITexts();
        });
        registerListening(AppAction.UPDATE,() -> {
            showErrorToConsole("IN UPDATE WINDOW");
            update();
        });
        idCol = new TableColumn<>(bundle.getString("table.flat_id"));
        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        flatTableView.getColumns().add(idCol);

        nameCol = new TableColumn<>(bundle.getString("table.flat_name"));
        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        flatTableView.getColumns().add(nameCol);

        areaCol = new TableColumn<>(bundle.getString("table.flat_area"));
        areaCol.setCellValueFactory(new PropertyValueFactory<>("area"));
        flatTableView.getColumns().add(areaCol);

        flatXCol = new TableColumn<>(bundle.getString("table.flat_x"));
        flatXCol.setCellValueFactory(cd -> new SimpleStringProperty(String.valueOf(cd.getValue().getCoordinates().getX())));
        flatTableView.getColumns().add(flatXCol);

        flatYCol = new TableColumn<>(bundle.getString("table.flat_y"));
        flatYCol.setCellValueFactory(cd -> new SimpleStringProperty(String.valueOf(cd.getValue().getCoordinates().getY())));
        flatTableView.getColumns().add(flatYCol);

        roomsCol = new TableColumn<>(bundle.getString("table.flat_rooms_number"));
        roomsCol.setCellValueFactory(new PropertyValueFactory<>("numberOfRooms"));
        flatTableView.getColumns().add(roomsCol);

        metroCol = new TableColumn<>(bundle.getString("table.flat_metro_time"));
        metroCol.setCellValueFactory(new PropertyValueFactory<>("timeToMetroOnFoot"));
        flatTableView.getColumns().add(metroCol);

        heatingCol = new TableColumn<>(bundle.getString("table.flat_central_heating"));
        heatingCol.setCellValueFactory(new PropertyValueFactory<>("centralHeating"));
        flatTableView.getColumns().add(heatingCol);

        furnishCol = new TableColumn<>(bundle.getString("table.flat_furnish"));
        furnishCol.setCellValueFactory(new PropertyValueFactory<>("furnish"));
        furnishCol.setCellFactory(col -> new TableCell<Flat, Furnish>() {
            @Override
            protected void updateItem(Furnish item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(bundle.getString("furnish." + item.name().toLowerCase()));
                }
            }
        });
        flatTableView.getColumns().add(furnishCol);



        houseNameCol = new TableColumn<>(bundle.getString("table.house_name"));
        houseNameCol.setCellValueFactory(cd -> new SimpleStringProperty(cd.getValue().getHouse().getName()));
        flatTableView.getColumns().add(houseNameCol);

        houseYearCol = new TableColumn<>(bundle.getString("table.house_years"));
        houseYearCol.setCellValueFactory(cd -> new SimpleStringProperty(String.valueOf(cd.getValue().getHouse().getYear())));
        flatTableView.getColumns().add(houseYearCol);

        houseFlatsCol = new TableColumn<>(bundle.getString("table.house_flats_on_floor"));
        houseFlatsCol.setCellValueFactory(cd
                -> new SimpleStringProperty(String.valueOf(cd.getValue().getHouse().getNumberOfFlatsOnFloor())));
        flatTableView.getColumns().add(houseFlatsCol);

        ownerCol = new TableColumn<>(bundle.getString("table.flat_owner"));
        ownerCol.setCellValueFactory(new PropertyValueFactory<>("ownerLogin"));
        flatTableView.getColumns().add(ownerCol);




        flatCreationTimeCol = new TableColumn<>(bundle.getString("table.flat_timestamp"));
        flatCreationTimeCol.setCellValueFactory(cd -> {
            if(cd.getValue().getCreationDate() == null) {
                return new SimpleStringProperty("");
            }
            return new SimpleStringProperty(cd.getValue().getCreationDate().format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss")));
        });
        flatTableView.getColumns().add(flatCreationTimeCol);





        canvas = animationManager.createInteractiveCanvas();

        commandAddButton = new Button(bundle.getString("button.add_command"));
        commandAddButton.setOnAction(e -> {
            AddCommandWindow addCommandWindow = new AddCommandWindow(getApp());
            addCommandWindow.showWindow();
        });
        MainApp.createStyledButton(commandAddButton,Font.font("Ubuntu-Regular",10));

        commandAddIfMaxButton = new Button(bundle.getString("button.add_if_max_command"));
        commandAddIfMaxButton.setOnAction(e -> {
            AddIfMaxCommandWindow addIfMaxCommandWindow = new AddIfMaxCommandWindow(getApp(),this);
            addIfMaxCommandWindow.showWindow();
        });
        MainApp.createStyledButton(commandAddIfMaxButton,Font.font("Ubuntu-Regular",10));

        commandUpdateButton = new Button(bundle.getString("button.update"));
        commandUpdateButton.setOnAction(e -> {
            UpdateByIdWindow updateByIdWindow = new UpdateByIdWindow(getApp(),this);
            updateByIdWindow.showWindow();
        });
        MainApp.createStyledButton(commandUpdateButton,Font.font("Ubuntu-Regular",10));


        commandRemoveButton = new Button(bundle.getString("button.remove_by_id_command"));
        commandRemoveButton.setOnAction(e -> {
            RemoveByIdCommandWindow removeByIdCommandWindow = new RemoveByIdCommandWindow(getApp(),this);
            removeByIdCommandWindow.showWindow();
        });
        MainApp.createStyledButton(commandRemoveButton,Font.font("Ubuntu-Regular",10));

        commandRemoveLowerButton = new Button(bundle.getString("button.remove_lower"));
        commandRemoveLowerButton.setOnAction(e -> {
            RemoveLowerCommandWindow commandRemoveCommandWindow = new RemoveLowerCommandWindow(getApp(),this);
            commandRemoveCommandWindow.showWindow();
        });
        MainApp.createStyledButton(commandRemoveLowerButton,Font.font("Ubuntu-Regular",10));

        commandHistoryButton = new Button(bundle.getString("button.history"));
        commandHistoryButton.setOnAction(e -> {
            HistoryWindow historyWindow = new HistoryWindow(getApp());
            historyWindow.showWindow();
        });
        MainApp.createStyledButton(commandHistoryButton,Font.font("Ubuntu-Regular",10));

        ascTableButton = new Button(bundle.getString("button.table_asc"));
        ascTableButton.setOnAction(e -> {
            flatTableView.setItems(FXCollections
                    .observableArrayList(getActualFlats().stream().sorted().toList()));
        });
        MainApp.createStyledButton(ascTableButton,Font.font("Ubuntu-Regular",10));

        descTableButton = new Button(bundle.getString("button.table_desc"));
        descTableButton.setOnAction(e -> {
            flatTableView.setItems(FXCollections
                    .observableArrayList(getActualFlats().stream()
                            .sorted(Comparator.reverseOrder()).toList()));
        });
        MainApp.createStyledButton(descTableButton,Font.font("Ubuntu-Regular",10));

        resetTableButton = new Button(bundle.getString("button.reset"));
        resetTableButton.setOnAction(e -> {
            flatTableView.setItems(FXCollections
                    .observableArrayList(getActualFlats()));
        });
        MainApp.createStyledButton(resetTableButton,Font.font("Ubuntu-Regular",10));

        filterPrefixTableButton = new Button(bundle.getString("button.filter_prefix"));
        filterPrefixTableButton.setOnAction(e -> {
            TypePrefixWindow prefixWindow = new TypePrefixWindow(getApp(),this);
            prefixWindow.showWindow();
        });
        MainApp.createStyledButton(filterPrefixTableButton,Font.font("Ubuntu-Regular",10));


        exitButton = new Button(bundle.getString("button.exit"));
        exitButton.setOnAction(e -> {
            MainWindow.this.hide(true);
            AuthWindow auth = new AuthWindow(getApp(),stage);
            auth.showWindow();
        });
        MainApp.createStyledRedButton(exitButton,Font.font("Ubuntu-Regular",10));

        userLabel = new Label(bundle.getString("label.user") + ": " + getClient().getUser().getLogin());
        userLabel.setFont(Font.font("Ubuntu-Regular",14));
        userLabel.setTextFill(Color.GRAY);

        flatTableView.setOnMouseClicked(e -> {
            if(e.getClickCount() == 2) {
                Flat flat = flatTableView.getSelectionModel().getSelectedItem();
                if(flat.getOwnerLogin().equals(getClient().getUser().getLogin())) {
                    new UpdateWindow(getApp(), this,
                            flat.getId()).showWindow();
                }
            }
        });
       // update();prefix_sort
    }

    public TableView<Flat> getFlatTableView() {
        return flatTableView;
    }

    @Override
    public void check() {
        userLabel.toFront();
        userLabel.setVisible(true);
        userLabel.setTextFill(Color.RED);
        userLabel.setText("TEST");
        System.out.println("user x:" + userLabel.getLayoutX());
        System.out.println("user y:" + userLabel.getLayoutY());
        System.out.println("user w:" + userLabel.getWidth());
        System.out.println("user h:" + userLabel.getHeight());
        System.out.println("user text:" + userLabel.getText());
    }

    @Override
    Pane createLayout() {
        Pane pane = new Pane();
        pane.setPrefSize(1140.0,670);
        pane.getChildren().add(canvas);
        pane.getChildren().add(flatTableView);
        pane.getChildren().add(commandAddButton);
        pane.getChildren().add(commandAddIfMaxButton);
        pane.getChildren().add(commandRemoveButton);
        pane.getChildren().add(commandUpdateButton);
        pane.getChildren().add(commandRemoveLowerButton);
        pane.getChildren().add(commandHistoryButton);
        pane.getChildren().add(filterPrefixTableButton);
        pane.getChildren().add(resetTableButton);
        pane.getChildren().add(descTableButton);
        pane.getChildren().add(ascTableButton);
        pane.getChildren().add(exitButton);
        pane.getChildren().add(userLabel);
        return pane;
    }

    @Override
    void pullOriginalPositions() {
        putPosition(flatTableView,new ElementPosition(650,110,469,430));
        putPosition(canvas,new ElementPosition(170,110,CANVAS_WIDTH,CANVAS_HEIGHT));
        canvas.setWidth(CANVAS_WIDTH);
        canvas.setHeight(CANVAS_HEIGHT);

        putPosition(commandAddButton,new ElementPosition(10,200,152,25)); //КНОПКИ -15 -30
        commandAddButton.setAlignment(Pos.CENTER);

        putPosition(commandAddIfMaxButton,new ElementPosition(10,240,-1,-1)); //КНОПКИ -15 -30
        commandAddIfMaxButton.setWrapText(true);
        commandAddIfMaxButton.setPrefWidth(152);
        commandAddIfMaxButton.setAlignment(Pos.CENTER);
        commandAddIfMaxButton.setTextAlignment(TextAlignment.CENTER);

        putPosition(commandUpdateButton,new ElementPosition(10,295,152,25)); //КНОПКИ -15 -30
        commandUpdateButton.setAlignment(Pos.CENTER);

        putPosition(commandRemoveButton,new ElementPosition(10,335,152,25)); //КНОПКИ -15 -30
        commandRemoveButton.setAlignment(Pos.CENTER);

        putPosition(commandRemoveLowerButton,new ElementPosition(10,375,-1,-1)); //КНОПКИ -15 -30
        commandRemoveLowerButton.setWrapText(true);
        commandRemoveLowerButton.setPrefWidth(152);
        commandRemoveLowerButton.setAlignment(Pos.CENTER);
        commandRemoveLowerButton.setTextAlignment(TextAlignment.CENTER);

        putPosition(commandHistoryButton,new ElementPosition(10,415,-1,-1)); //КНОПКИ -15 -30
        commandHistoryButton.setWrapText(true);
        commandHistoryButton.setPrefWidth(152);
        commandHistoryButton.setAlignment(Pos.CENTER);
        commandHistoryButton.setTextAlignment(TextAlignment.CENTER);

        putPosition(descTableButton,new ElementPosition(685,556,194,25)); // -25
        descTableButton.setAlignment(Pos.CENTER);
        descTableButton.setTextAlignment(TextAlignment.CENTER);
        putPosition(ascTableButton,new ElementPosition(891,556,194,25));
        ascTableButton.setAlignment(Pos.CENTER);
        ascTableButton.setTextAlignment(TextAlignment.CENTER);

        putPosition(filterPrefixTableButton,new ElementPosition(685,596,194,25)); // -25
        filterPrefixTableButton.setAlignment(Pos.CENTER);
        filterPrefixTableButton.setTextAlignment(TextAlignment.CENTER);

        putPosition(resetTableButton,new ElementPosition(891,596,194,25));
        resetTableButton.setAlignment(Pos.CENTER);
        resetTableButton.setTextAlignment(TextAlignment.CENTER);

        putPosition(userLabel,new ElementPosition(20,640,250,15)); // -25
        userLabel.setAlignment(Pos.BASELINE_LEFT);

        putPosition(exitButton,new ElementPosition(1024,10,102,20));
        exitButton.setAlignment(Pos.CENTER);
        exitButton.setTextAlignment(TextAlignment.CENTER);
    }




    @Override
    void update() {
        updateActualFlats();
        flatTableView.setItems(FXCollections.observableArrayList(getActualFlats()));
        super.update();
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
    protected void updateUITexts() {
        this.bundle = getApp().bundle;
        idCol.setText(bundle.getString("table.flat_id"));
        nameCol.setText(bundle.getString("table.flat_name"));
        areaCol.setText(bundle.getString("table.flat_area"));
        roomsCol.setText(bundle.getString("table.flat_rooms_number"));
        metroCol.setText(bundle.getString("table.flat_metro_time"));
        heatingCol.setText(bundle.getString("table.flat_central_heating"));
        furnishCol.setText(bundle.getString("table.flat_furnish"));
        houseNameCol.setText(bundle.getString("table.house_name"));
        houseYearCol.setText(bundle.getString("table.house_years"));
        houseFlatsCol.setText(bundle.getString("table.house_flats_on_floor"));
        ownerCol.setText(bundle.getString("table.flat_owner"));
        commandAddButton.setText(bundle.getString("button.add_command"));
        commandAddIfMaxButton.setText(bundle.getString("button.add_if_max_command"));
        commandUpdateButton.setText(bundle.getString("button.update"));
        commandRemoveButton.setText(bundle.getString("button.remove_by_id_command"));
        commandRemoveLowerButton.setText(bundle.getString("button.remove_lower"));
        commandHistoryButton.setText(bundle.getString("button.history"));
        ascTableButton.setText(bundle.getString("button.table_asc"));
        descTableButton.setText(bundle.getString("button.table_desc"));
        resetTableButton.setText(bundle.getString("button.reset"));
        filterPrefixTableButton.setText(bundle.getString("button.filter_prefix"));
        exitButton.setText(bundle.getString("button.exit"));
        userLabel.setText(bundle.getString("label.user") + ": " + getClient().getUser().getLogin());

    }


    @Override
    void fullCentering() {


    }

    @Override
    public double getWidth() {
        return 1140;
    }

    @Override
    public double getHeight() {
        return 670;
    }

    public Set<Flat> getActualFlats() {
        return actualFlats;
    }

    private void updateActualFlats() {

        Set<Flat> previousFlatsState = new HashSet<>(this.actualFlats); // Сохраняем копию текущего состояния

        CompletableFuture<Response> resp = null;
        try {
            resp = getClient().requestToServer(
                    new UserRequest(getClient().getUser(), "show", null));
        } catch (IOException e) {
            e.printStackTrace();
        }

      //  System.out.println(actualFlats.size() + " gone to response");

        resp.thenAccept(response -> {
         //   System.out.println(actualFlats.size() + " gone to response");
            Platform.runLater(() -> {
                Set<Flat> serverFlats = (Set<Flat>) response.getData();
                if (serverFlats == null) {
                    serverFlats = new HashSet<>();
                }
            //    System.out.println(actualFlats.size() + " gget data");
                List<Flat> newlyAddedFlats = new ArrayList<>();
                for(Flat flat : serverFlats) {
                    Optional<Flat> oldFlat = previousFlatsState.stream().filter(f -> Objects.equals(f.getId(), flat.getId())).findFirst();
                    if(oldFlat.isPresent()) {
                        if(!oldFlat.get().getCoordinates().equals(flat.getCoordinates())) {
                            newlyAddedFlats.add(flat);
                        }
                    } else {
                        newlyAddedFlats.add(flat);
                    }
                }



                this.actualFlats = serverFlats;
                if (!newlyAddedFlats.isEmpty()) {
                    animationManager.animateNewWaveOfFlatsAppearance(newlyAddedFlats);
                }
                animationManager.forceRedraw();
             //   System.out.println(actualFlats.size() + " its size");

                flatTableView.setItems(FXCollections.observableArrayList(actualFlats));
            });
        }).exceptionally(ex -> {
            ex.printStackTrace();
            Platform.runLater(this::throwErrorAlert);
            return null;
        });


    }

    public Canvas getCanvas() {
        return canvas;
    }


}
