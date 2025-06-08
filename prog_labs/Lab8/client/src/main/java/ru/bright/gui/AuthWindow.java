package ru.bright.gui;


import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import ru.bright.*;
import ru.bright.model.Flat;
import ru.bright.model.Furnish;
import ru.bright.model.House;
import ru.bright.util.Console;
import ru.bright.utils.AppAction;
import ru.bright.utils.Config;
import ru.bright.utils.ElementPosition;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class AuthWindow extends Window {

    protected Label usernameLabel, passwordLabel, languageLabel;
    protected TextField usernameField;
    protected PasswordField passwordField;
    protected Button loginButton, registerButton;
    protected Label statusLabel;
    protected ComboBox<String> languageComboBox;
    protected Label titleLabel;
    private Stage stage;

    public AuthWindow(MainApp app, Stage primaryStage) {
        super(app);
        this.stage = primaryStage;
        this.bundle = app.bundle;
    }


    @Override
    void initUI() {
        usernameLabel= new Label(bundle.getString("label.username"));
        titleLabel = new Label(bundle.getString("label.auth_title"));
        languageLabel = new Label(bundle.getString("label.language"));
        passwordLabel = new Label(bundle.getString("label.password"));
        loginButton = new Button(bundle.getString("button.login"));
        registerButton = new Button(bundle.getString("button.register"));
        passwordField = new PasswordField();
        passwordField.setPromptText(bundle.getString("prompt.password"));
        usernameField = new TextField();
        usernameField.setPromptText(bundle.getString("prompt.username"));
        statusLabel = new Label();
        Map<String, Locale> locales = new HashMap<>();
        locales.put("Русский", new Locale("ru"));
        locales.put("English", new Locale("en"));
        locales.put("Česky", new Locale("cs"));
        locales.put("Polski", new Locale("pl"));
        languageComboBox = new ComboBox<>();
        languageComboBox.setItems(FXCollections.observableArrayList(locales.keySet()));

        Optional<String> currentDisplayName = locales.entrySet().stream()
                .filter(entry -> entry.getValue().equals(getBundle().getLocale()))
                .map(Map.Entry::getKey)
                .findFirst();

        if (currentDisplayName.isPresent()) {
            languageComboBox.setValue(currentDisplayName.get());
        }

        languageComboBox.setOnAction(event -> {
            String selectedDisplayName = languageComboBox.getValue();
            if (selectedDisplayName != null) {
                Locale selectedLocale = locales.get(selectedDisplayName);
                if (selectedLocale != null) {
                    getApp().switchLanguage(selectedLocale);
                }
            }
        });
        loginButton.setOnAction(e -> handleAuthAction("auth"));
        registerButton.setOnAction(e -> handleAuthAction("register"));
        stage.setTitle(bundle.getString("window.title"));
        registerListening(AppAction.SWITCH_LANGUAGE,() -> {
            updateUITexts();
        });
    }


    @Override
    protected void updateUITexts() {
        this.bundle = getApp().bundle;
        stage.setTitle(bundle.getString("window.title"));
        languageLabel.setText(bundle.getString("label.language"));
        usernameLabel.setText(bundle.getString("label.username"));
        passwordLabel.setText(bundle.getString("label.password"));
        loginButton.setText(bundle.getString("button.login"));
        registerButton.setText(bundle.getString("button.register"));
        usernameField.setPromptText(bundle.getString("prompt.username"));
        passwordField.setPromptText(bundle.getString("prompt.password"));
        titleLabel.setText(bundle.getString("label.auth_title"));
    }


    @Override
    void pullOriginalPositions() {
       putPosition(titleLabel,new ElementPosition(0,0,1140,97));
       putPosition(languageComboBox, new ElementPosition(54,30,130.48,24.8));
       putPosition(languageLabel, new ElementPosition(55, 00, 130, 30));
       putPosition(usernameLabel, new ElementPosition(409, 260, 118.6, 24.8));
       putPosition(usernameField, new ElementPosition(409, 297, 320.2, 24.8));
       putPosition(passwordLabel, new ElementPosition(410, 372, 320.2, 24.8));
       putPosition(passwordField, new ElementPosition(409.8, 409.4, 320.2, 24.8));
       passwordField.setPrefSize(320.2, 24.8);
       putPosition(loginButton, new ElementPosition(409.8, 471.48, 130.48, 24.8));
       putPosition(registerButton, new ElementPosition(552.45, 471.48, 177.68, 24.8));
       putPosition(statusLabel, new ElementPosition(410, 545.9, 320, 24.8));
       statusLabel.setPrefSize(320, 24.8);
       statusLabel.setAlignment(Pos.CENTER);
       titleLabel.setAlignment(Pos.CENTER);
       languageLabel.setAlignment(Pos.CENTER);
    }


    @Override
    Pane createLayout() {
        Pane root = new Pane();
        root.setPrefSize(getWidth(), getHeight());

        Font ubuntuFont = Font.loadFont(
                AuthWindow.class.getResourceAsStream("/fonts/Ubuntu-Regular.ttf"), 14);


        String labelTextStyle = "-fx-font-family: '" + ubuntuFont.getName() + "'; -fx-font-size: 21px;";
        String inputFieldStyle = "-fx-font-family: '" + ubuntuFont.getName() + "'; -fx-font-size: 16px; -fx-padding: 10px;";

        languageComboBox.setStyle("-fx-font-family: '" + ubuntuFont.getName() + "'; -fx-font-size: 12px;");
        languageLabel.setStyle("-fx-font-family: '" + ubuntuFont.getName() + "'; -fx-font-size: 15px;");

        titleLabel.setTextFill(Color.BLACK);
        titleLabel.setFont(Font.font(ubuntuFont.getName(), 30));

        // === Логин ===
        usernameLabel.setPrefSize(118.6,24.8);
        usernameLabel.setStyle(labelTextStyle);

        usernameField.setPrefSize(320.2, 24.8);
        usernameField.setStyle(inputFieldStyle);

        passwordLabel.setStyle(labelTextStyle);
        passwordField.setStyle(inputFieldStyle);
        //passwordField

        MainApp.createStyledButton(loginButton, ubuntuFont);
        MainApp.createStyledButton(registerButton, ubuntuFont);
        statusLabel.setTextFill(Color.RED);
        statusLabel.setFont(Font.font(ubuntuFont.getName(), 16));


        root.getChildren().addAll(
                titleLabel,
                usernameField, usernameLabel,
                passwordLabel, passwordField,
                loginButton, registerButton,
                statusLabel,  languageLabel,languageComboBox
        );
        return root;
    }



    private void updateStatusLabel(String text, Color color) {
        statusLabel.setText(text);
        statusLabel.setTextFill(color);
        repositionElements(positions);
    }


    @Override
    protected void fullCentering() {
   //     Platform.runLater(() -> {
   //         center(languageLabel,languageComboBox,6);
   //     });
    //   Platform.runLater(() -> {
    //       center(statusLabel,usernameField,-300);
    //   });
    }

    @Override
    public double getWidth() {
        return 1140;
    }

    @Override
    public double getHeight() {
        return 670;
    }


    @Override
    protected Stage getStage() {
        return stage;
    }

    public ResourceBundle getBundle() {
        return bundle;
    }


    private void handleAuthAction(String type) {
        statusLabel.setText("");
        String username = usernameField.getText();
        String password = passwordField.getText();
        if(username == null || password == null || username.isEmpty() || password.isEmpty()) {
            updateStatusLabel(bundle.getString("error.empty_credentials"),Color.RED);
            return;
        }
        CompletableFuture<Response> response1 = sendAuthRequest(type,username,password);
        if(response1 == null) {
            updateStatusLabel(bundle.getString("error.server_sleep"),Color.RED);
            return;
        }
        //Response response = null;
        try {
            response1.thenAcceptAsync(response -> {
                Platform.runLater(() -> {
                if(!getClient().isAvailable()) {
                    updateStatusLabel(bundle.getString("error.server_sleep"),Color.RED);
                    return;
                }
                if(response == null) {
                    updateStatusLabel(bundle.getString("error.server_sleep"),Color.RED);
                    return;
                }
                if(response.getStatus() == ResponseStatus.ERROR) {
                    updateStatusLabel(bundle.getString("error.unknown"),Color.RED);
                    return;
                }
                if(response.getStatus() == ResponseStatus.LOGIN_BUSY) {
                    updateStatusLabel(bundle.getString("error.login_busy"),Color.RED);
                    return;
                }
                if(response.getStatus() == ResponseStatus.AUTH_FAILED) {
                    updateStatusLabel(bundle.getString("error.wrong_credentials"),Color.RED);
                    return;
                }
                if(response.getStatus() == ResponseStatus.REGISTER_SUCCESSFUL) {
                    updateStatusLabel(bundle.getString("label.register_successful"),Color.GREEN);
                    return;
                }
                if(response.getStatus() == ResponseStatus.AUTH_PASSED) {
                        this.hide(true);
                        getClient().setUser(new User(username, password));
                        MainWindow mainWindow = new MainWindow(getApp(),stage);

                        mainWindow.showWindow();
                        mainWindow.addElement(languageLabel, new ElementPosition(languageLabel.getLayoutX(), languageLabel.getLayoutY(),
                                languageLabel.getWidth(), languageLabel.getHeight()));
                        mainWindow.addElement(languageComboBox, new ElementPosition(languageComboBox.getLayoutX(), languageComboBox.getLayoutY(),
                                languageComboBox.getWidth(), languageComboBox.getHeight()));
                        return;

                }
                });
            });


        } catch (Exception e) {
            e.printStackTrace();
        }
        //   response1.thenAcceptAsync(response -> {
      //      Platform.runLater(() -> {
      //
      //      });
      //  });


    }


  //  public void showErrorToConsole(String text) {
  //      getClient().getConsole().printErr(text);
  //  }

    public CompletableFuture<Response> sendAuthRequest(String type, String login, String password) {
        try {
            CompletableFuture<Response> resp =  getClient().requestToServer(
                    new UserRequest(null, type + " " + login + " " + password,null));
            return resp;
        } catch (Exception e) {
            statusLabel.setStyle("-fx-fill: red;");
            statusLabel.setText(bundle.getString("error.server_sleep"));
        }
        return null;
    }


}
