package ru.bright.gui;

import javafx.application.Platform;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Alert;
import javafx.scene.control.Control;
import javafx.scene.control.Label;
import javafx.scene.control.Labeled;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.scene.text.Font;
import javafx.scene.transform.Scale;
import javafx.scene.transform.Transform;
import javafx.stage.Stage;
import ru.bright.Client;
import ru.bright.utils.AppAction;
import ru.bright.utils.ElementPosition;

import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

public abstract class Window {

    protected Map<Node, ElementPosition> positions;
    private Stage stage;
    protected ResourceBundle bundle;
    private boolean isInitialized = false;
    private Pane layout;
    private MainApp app;
    private Map<AppAction, Runnable> actionsToListen;
    private boolean scaleInitialized;

    public Window(MainApp app) {
        this.app = app;
        this.actionsToListen = new HashMap<>();
    }

    public void registerListening(AppAction action, Runnable runnable) {
        actionsToListen.put(action, runnable);
        MainApp.registerListener(action,this);
    }

    public void accept(AppAction action) {
        if(actionsToListen.containsKey(action)) {
            actionsToListen.get(action).run();
        }
    }

    public Client getClient() {
        return app.getClient();
    }


    public MainApp getApp() {
        return app;
    }

    public void showWindow() {
        fullInit();
        Scene scene = new Scene(layout,getWidth(),getHeight());
        stage.setScene(scene);
        if(!stage.isShowing()) {
            stage.show();
        }
        Platform.runLater(() -> {
            fullCentering();
        });

    }

    void showWindowAndWait() {
        fullInit();
        Scene scene = new Scene(layout);
        stage.setScene(scene);
        if(!stage.isShowing()) {
            stage.showAndWait();
        }
        Platform.runLater(() -> {
            fullCentering();
        });
    }


    public void addElement(Node element, ElementPosition position) {
        if(layout != null && layout.getChildren() != null) {
            layout.getChildren().add(element);
            positions.put(element, position);
        }
    }

    public void hide(boolean onlyLayout) {
        layout.getChildren().clear();
        if(!onlyLayout) {
            stage.hide();
        }
    }

    void fullInit() {
        positions = new HashMap<>();
        bundle = getBundle();
        initUI();
        pullOriginalPositions();
        this.stage = getStage();
        layout = createLayout();
        Scale scale = new Scale(1, 1); // начальный масштаб 1x
        layout.getTransforms().add(scale);
        /*
        Runnable updateScale = () -> {
            if (stage.getScene() == null) return;

            double currentSceneWidth = stage.getScene().getWidth();
            double currentSceneHeight = stage.getScene().getHeight();
            double baseDesignWidth = getWidth();   // 1140
            double baseDesignHeight = getHeight(); // 670

            if (baseDesignWidth <= 0 || baseDesignHeight <= 0 || currentSceneWidth <= 0 || currentSceneHeight <= 0) {
                scale.setX(1.0);
                scale.setY(1.0);
                layout.setTranslateX(0); // Сброс смещения
                layout.setTranslateY(0); // Сброс смещения
                return;
            }

            double scaleFactorX = currentSceneWidth / baseDesignWidth;
            double scaleFactorY = currentSceneHeight / baseDesignHeight;

            // --- РАВНОМЕРНОЕ МАСШТАБИРОВАНИЕ ---
            double uniformScaleFactor = Math.min(scaleFactorX, scaleFactorY);
            scale.setX(uniformScaleFactor);
            scale.setY(uniformScaleFactor);

            // --- ЦЕНТРИРОВАНИЕ МАСШТАБИРОВАННОГО Pane ---
            // Рассчитываем реальные размеры Pane после масштабирования
            double scaledContentWidth = baseDesignWidth * uniformScaleFactor;
            double scaledContentHeight = baseDesignHeight * uniformScaleFactor;

            // Рассчитываем смещение, чтобы отцентрировать Pane на сцене
            // Pivot для Scale у нас (0,0), поэтому смещаем сам Pane
            layout.setTranslateX((currentSceneWidth - scaledContentWidth) / 2.0);
            layout.setTranslateY((currentSceneHeight - scaledContentHeight) / 2.0);
        };
*/   Runnable updateScale = () -> {
            if (stage.getScene() == null) return;

            double currentSceneWidth = stage.getScene().getWidth();
            double currentSceneHeight = stage.getScene().getHeight();
            double baseDesignWidth = getWidth();   // 1140 - исходная ширина вашего Pane
            double baseDesignHeight = getHeight(); // 670 - исходная высота вашего Pane

            if (baseDesignWidth <= 0 || baseDesignHeight <= 0 || currentSceneWidth <= 0 || currentSceneHeight <= 0) {
                // Если какие-то размеры некорректны, устанавливаем масштаб в 1
                // и убираем смещение (на всякий случай, если оно было)
                scale.setX(1.0);
                scale.setY(1.0);
                layout.setTranslateX(0); // Убедимся, что нет смещения
                layout.setTranslateY(0); // Убедимся, что нет смещения
                return;
            }

            // --- НЕРАВНОМЕРНОЕ МАСШТАБИРОВАНИЕ для заполнения ---
            // Рассчитываем отдельные коэффициенты масштабирования для X и Y
            double scaleFactorX = currentSceneWidth / baseDesignWidth;
            double scaleFactorY = currentSceneHeight / baseDesignHeight;

            // Применяем эти разные коэффициенты
            scale.setX(scaleFactorX);
            scale.setY(scaleFactorY);

            // При таком подходе Pane будет растягиваться/сжиматься, чтобы заполнить всю сцену.
            // Его левый верхний угол (если pivot Scale = (0,0)) останется в левом верхнем углу сцены.
            // Поэтому TranslateX и TranslateY должны быть 0.
            layout.setTranslateX(0);
            layout.setTranslateY(0);
        };
        Platform.runLater(() -> {
            if (stage.getScene() != null) {
                updateScale.run(); // Первый вызов
                stage.getScene().widthProperty().addListener((obs, oldV, newV) -> updateScale.run());
                stage.getScene().heightProperty().addListener((obs, oldV, newV) -> updateScale.run());
            }
        });
        // === Обновление масштаба при изменении размера окна ===
   //     stage.widthProperty().addListener((obs, oldVal, newVal) -> {
   //         double scaleX = newVal.doubleValue() / getWidth(); // 800 — исходная ширина окна
   //         scale.setX(scaleX);
   //         check();
   //     });

    //    stage.heightProperty().addListener((obs, oldVal, newVal) -> {
    //        double scaleY = newVal.doubleValue() / getHeight(); // 600 — исходная высота
    //        scale.setY(scaleY);
    //        check();
    //    });

       // stage.widthProperty().addListener((obs, oldVal, newVal) -> repositionElements(positions));
       // stage.heightProperty().addListener((obs, oldVal, newVal) -> repositionElements(positions));
        update();

    }

    public void check() {

    }



    public void putPosition(Node node, ElementPosition position) {
        positions.put(node, position);
        if(node instanceof Region n) {
            if(!(position.width == -1 || position.height == -1)) {
                n.setPrefSize(position.width, position.height);
            }
        }
        node.setLayoutX(position.x);
        node.setLayoutY(position.y);
    }


    void update() {
        updateUITexts();
        repositionElements(positions);
    }

    protected abstract Stage getStage();

    public abstract ResourceBundle getBundle();

    abstract void initUI();

    abstract void updateUITexts();

    abstract Pane createLayout();

    abstract void pullOriginalPositions();

    abstract void fullCentering();


    void center(Label label, Region control, double offsetY) {
        Platform.runLater(() -> {
            if (label == null || control == null) return;
            double centerX = control.getLayoutX() + (control.getWidth() - label.getWidth()) / 2;
            double centerY = control.getLayoutY() - label.getHeight() - offsetY;

            label.setLayoutX(centerX);
            label.setLayoutY(centerY);
        });
        //    });
    }

    public abstract double getWidth();

    public abstract double getHeight();

    protected void repositionElements(Map<Node,ElementPosition> map) {
        /*
        double scaleX = stage.getWidth() / getWidth();
        double scaleY = stage.getHeight() / getHeight();

        Platform.runLater(() -> {
            for (Map.Entry<Node, ElementPosition> entry : map.entrySet()) {
                Node node = entry.getKey();
                ElementPosition pos = entry.getValue();

                // === Обновляем позиции пропорционально ===
                double newX = pos.x * scaleX;
                double newY = pos.y * scaleY;

                node.setLayoutX(newX);
                node.setLayoutY(newY);

                // === Если это Label — только позиция, размеры не меняем ===
                //    if (node instanceof Label) continue;

                // === Для всех остальных элементов обновляем размеры ===

                if (node instanceof Control control) {
                    double newW = pos.width * scaleX;
                    double newH = pos.height * scaleY;

                    if (pos.width > 0) control.setPrefWidth(newW);
                    if (pos.height > 0) control.setPrefHeight(newH);
                }

                if (node instanceof Labeled labeled) {
                    Font oldFont = labeled.getFont();
                    double newFontSize = oldFont.getSize() * scaleY;
                    labeled.setFont(Font.font(oldFont.getFamily(), newFontSize));
                }
                if(node instanceof Canvas canvas) {
                    double newW = pos.width * scaleX;
                    double newH = pos.height * scaleY;
                    canvas.resize(newW,newH);
                  // canvas.setWidth(newW);
                  // canvas.setHeight(newH);
                }
            }

            // === Центрирование языкового label над комбобоксом ===
            //   Platform.runLater(() -> {
            fullCentering();
            //   center(titleLabel,usernameField,150);
            //  });
        });

         */

    }

    public void showErrorToConsole(String text) {
        getClient().getConsole().printErr(text);
    }

    public Pane getLayout() {
        return layout;
    }

    public void throwErrorAlert() {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(bundle.getString("alert.title.error"));
        alert.setContentText(bundle.getString("alert.message.error"));
        alert.showAndWait();
    }

    public void throwConnectingErrorAlert() {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(bundle.getString("alert.title.error"));
        alert.setContentText(bundle.getString("error.server_sleep"));
        alert.showAndWait();
    }
}
