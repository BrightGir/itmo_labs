package ru.bright.gui;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Modality;
import javafx.stage.Stage;
import ru.bright.Client;
import ru.bright.utils.AppAction;
import ru.bright.utils.Config;
import ru.bright.utils.HistoryCommand;

import java.util.*;

public class MainApp extends Application {

    private static final String RESOURCE_BUNDLE_BASE_NAME = "messages";
    protected static Client client;
    protected static Config config;
    public ResourceBundle bundle;
    protected Locale currentLocale;
    private Stage primaryStage;
    private static Map<AppAction, List<Window>> listeners;
    private List<HistoryCommand> cmds;


    @Override
    public void init() throws Exception {
        this.currentLocale = config.getApplicationLocale();
        loadResourceBundle(currentLocale);
        this.listeners = new HashMap<>();
        this.cmds = new ArrayList<>();
        super.init();
    }

    public void addCommand(HistoryCommand cmd) {
        this.cmds.add(cmd);
        if(cmds.size() == 14) {
            cmds.remove(0);
        }
    }

    public List<HistoryCommand> getCommands() {
        return cmds;
    }

    @Override
    public void start(Stage stage) throws Exception {
        this.primaryStage = stage;
        stage.setOnCloseRequest(e -> {

            Platform.exit();
        });
        AuthWindow authWindow = new AuthWindow(this,stage);
        authWindow.showWindow();
    }

    public static void registerListener(AppAction action, Window window) {
        if(!listeners.containsKey(action)) {
            listeners.put(action, new ArrayList<>());
        }
        listeners.get(action).add(window);
    }

    public static void notify(AppAction action) {
        if(listeners != null && listeners.containsKey(action)) {
            listeners.get(action).forEach(w -> w.accept(action));
        }
    }

    public Client getClient() {
        return client;
    }

    public static void setClient(Client client2) {
        client = client2;
    }

    public static void setConfig(Config config2) {
        config = config2;
    }

    private void loadResourceBundle(Locale locale) {
        try {
            this.bundle = ResourceBundle.getBundle(RESOURCE_BUNDLE_BASE_NAME, locale);
            this.currentLocale = locale;
        } catch (java.util.MissingResourceException e) {
            client.getConsole().printErr("Unknown ResourceBundle for Locale: " + locale);
            this.bundle = ResourceBundle.getBundle(RESOURCE_BUNDLE_BASE_NAME, config.getDefaultLanguage());
            this.currentLocale =  config.getDefaultLanguage();
        }
    }

    public void switchLanguage(Locale newLocale) {
        if (!newLocale.equals(currentLocale)) {
            loadResourceBundle(newLocale);
            if (bundle != null) {
                config.setApplicationLocale(newLocale);
                notify(AppAction.SWITCH_LANGUAGE);
            }
        }
    }

    static Button createStyledButton(Button button, Font font) {
        button.setFont(font);
        button.setStyle(
                "-fx-background-color: #4a90e2;" +
                        "-fx-text-fill: white;" +
                        "-fx-font-size: " + font.getSize() + "px;" +
                        "-fx-padding: 10px 20px;" +
                        "-fx-border-radius: 8px;" +
                        "-fx-background-radius: 8px;"
        );
        //   button.setPrefSize(120, 40);

        // Эффект при наведении
        button.setOnMouseEntered(e -> button.setStyle(
                "-fx-background-color: #357ABD;" +
                        "-fx-text-fill: white;" +
                        "-fx-font-size: " + font.getSize() + "px;" +
                        "-fx-padding: 10px 20px;" +
                        "-fx-border-radius: 8px;" +
                        "-fx-background-radius: 8px;"
        ));

        button.setOnMouseExited(e -> button.setStyle(
                "-fx-background-color: #4a90e2;" +
                        "-fx-text-fill: white;" +
                        "-fx-font-size: " + font.getSize() + "px;" +
                        "-fx-padding: 10px 20px;" +
                        "-fx-border-radius: 8px;" +
                        "-fx-background-radius: 8px;"
        ));

        return button;
    }

    public static Stage showCustomAlert(String buttonName, String title, String message, Font font, Stage owner) {
        Stage dialog = new Stage();
        dialog.setTitle(title);
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.setResizable(false);
        if (owner != null) dialog.initOwner(owner);

        Label label = new Label(message);
        label.setFont(font);
        label.setWrapText(true);

        Button okButton = new Button("ОК");
        MainApp.createStyledGreenButton(okButton, font);

        okButton.setOnAction(e -> dialog.close());

        VBox content = new VBox(15, label, okButton);
        content.setAlignment(Pos.CENTER);
        content.setPadding(new Insets(20));

        Scene scene = new Scene(content, 400, 150);
        dialog.setScene(scene);
        dialog.showAndWait();
        return dialog;
    }

    public static Stage showAccessError(ResourceBundle bundle) {
        Stage dialog = new Stage();
        dialog.setTitle(bundle.getString("window.access_error"));
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.setResizable(false);

        Label label = new Label(bundle.getString("label.access_error"));
        Font font = Font.font("Ubuntu-Regular",15);
        label.setFont(font);
        label.setWrapText(true);

        Button okButton = new Button(bundle.getString("button.back"));
        MainApp.createStyledGreenButton(okButton, font);

        okButton.setOnAction(e -> dialog.close());

        VBox content = new VBox(15, label, okButton);
        content.setAlignment(Pos.CENTER);
        content.setPadding(new Insets(20));

        Scene scene = new Scene(content, 400, 150);
        dialog.setScene(scene);
        dialog.showAndWait();
        return dialog;
    }


    static Button createStyledGreenButton(Button button, Font font) {
        button.setFont(font);
        button.setStyle(
                "-fx-background-color: #77dd77;" +  // светло-зелёный
                        "-fx-text-fill: white;" +
                        "-fx-font-size: " + font.getSize() + "px;" +
                        "-fx-padding: 10px 20px;" +
                        "-fx-border-radius: 8px;" +
                        "-fx-background-radius: 8px;"
        );

        // Эффект при наведении
        button.setOnMouseEntered(e -> button.setStyle(
                "-fx-background-color: #66cdaa;" +  // более тёмкий зелёный при наведении
                        "-fx-text-fill: white;" +
                        "-fx-font-size: " + font.getSize() + "px;" +
                        "-fx-padding: 10px 20px;" +
                        "-fx-border-radius: 8px;" +
                        "-fx-background-radius: 8px;"
        ));

        button.setOnMouseExited(e -> button.setStyle(
                "-fx-background-color: #77dd77;" +
                        "-fx-text-fill: white;" +
                        "-fx-font-size: " + font.getSize() + "px;" +
                        "-fx-padding: 10px 20px;" +
                        "-fx-border-radius: 8px;" +
                        "-fx-background-radius: 8px;"
        ));

        return button;
    }

    static Button createStyledRedButton(Button button, Font font) {
        button.setFont(font);
        button.setStyle(
                "-fx-background-color: #ff6347;" +  // светло-красный (цвет томата)
                        "-fx-text-fill: white;" +
                        "-fx-font-size: " + font.getSize() + "px;" +
                        "-fx-padding: 10px 20px;" +
                        "-fx-border-radius: 8px;" +
                        "-fx-background-radius: 8px;"
        );

        // Эффект при наведении
        button.setOnMouseEntered(e -> button.setStyle(
                "-fx-background-color: #ff4500;" +  // более тёмный красный при наведении
                        "-fx-text-fill: white;" +
                        "-fx-font-size: " + font.getSize() + "px;" +
                        "-fx-padding: 10px 20px;" +
                        "-fx-border-radius: 8px;" +
                        "-fx-background-radius: 8px;"
        ));

        button.setOnMouseExited(e -> button.setStyle(
                "-fx-background-color: #ff6347;" +
                        "-fx-text-fill: white;" +
                        "-fx-font-size: " + font.getSize() + "px;" +
                        "-fx-padding: 10px 20px;" +
                        "-fx-border-radius: 8px;" +
                        "-fx-background-radius: 8px;"
        ));

        return button;
    }

    public static Button createStyledGreenButton(Button button, Font font, int yPadding, int xPadding) {
        button.setFont(font);
        button.setFont(font);
        button.setStyle(
                "-fx-background-color: #77dd77;" +  // светло-зелёный
                        "-fx-text-fill: white;" +
                        "-fx-font-size: " + font.getSize() + "px;" +
                        "-fx-padding: "+ yPadding + "px " + xPadding+ "px;" +
                        "-fx-border-radius: 8px;" +
                        "-fx-background-radius: 8px;"
        );

        // Эффект при наведении
        button.setOnMouseEntered(e -> button.setStyle(
                "-fx-background-color: #66cdaa;" +  // более тёмкий зелёный при наведении
                        "-fx-text-fill: white;" +
                        "-fx-font-size: " + font.getSize() + "px;" +
                        "-fx-padding: "+ yPadding + "px " + xPadding+ "px;" +
                        "-fx-border-radius: 8px;" +
                        "-fx-background-radius: 8px;"
        ));

        button.setOnMouseExited(e -> button.setStyle(
                "-fx-background-color: #77dd77;" +
                        "-fx-text-fill: white;" +
                        "-fx-font-size: " + font.getSize() + "px;" +
                        "-fx-padding: "+ yPadding + "px " + xPadding+ "px;" +
                        "-fx-border-radius: 8px;" +
                        "-fx-background-radius: 8px;"
        ));


        return button;
    }


}
