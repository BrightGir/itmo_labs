package ru.bright.gui;

import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import ru.bright.model.Flat;
import ru.bright.model.Furnish;
import ru.bright.util.CustomComboBoxField;
import ru.bright.util.CustomField;
import ru.bright.util.CustomTextField;
import ru.bright.util.StatusNode;
import ru.bright.utils.AppAction;
import ru.bright.utils.ElementPosition;

import java.util.*;

public abstract class BaseFlatWindow extends Window{

    private Stage stage;
    private Label titleLabel;
    private Label houseLabel;


    private CustomTextField flatNameField;
    private CustomTextField flatRoomsField;
    private CustomTextField flatXField;
    private CustomTextField flatYField;
    private CustomTextField flatAreaField;
    private CustomComboBoxField<Furnish> flatFurnishField;
    private CustomTextField flatMetroField;
    private CustomComboBoxField<Boolean> flatHeatingField;
    private CustomTextField houseNameField;
    private CustomTextField houseFlatsField;
    private CustomTextField houseAgeField;


    private Button doneButton;
    private final int STATUS_OFFSET = 25;
    private final int LABEL_OFFSET = 18;

    public BaseFlatWindow(MainApp app) {
        super(app);
        this.stage = new Stage();
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
        registerListening(AppAction.SWITCH_LANGUAGE,()->{
            updateUITexts();
        });
        double baseFieldWidth = 149;
        double baseFieldHeight = 16;
        titleLabel = new Label(getTitle());
        houseLabel = new Label(getBundle().getString("title.house"));
        flatNameField = new CustomTextField("label.flat_name", this,
                20,61,baseFieldWidth,baseFieldHeight,LABEL_OFFSET, STATUS_OFFSET);
        flatNameField.styleControlUbuntuFont(13);
        flatNameField.styleStatusUbuntuFont(10);
        flatRoomsField = new CustomTextField("label.flat_rooms", this,
                183,61,baseFieldWidth,baseFieldHeight,LABEL_OFFSET, STATUS_OFFSET);
        flatRoomsField.styleControlUbuntuFont(13);
        flatRoomsField.styleStatusUbuntuFont(10);
        flatXField = new CustomTextField("label.flat_x", this,
                20,116,baseFieldWidth,baseFieldHeight,LABEL_OFFSET, STATUS_OFFSET);
        flatXField.styleControlUbuntuFont(13);
        flatXField.styleStatusUbuntuFont(10);
        flatYField = new CustomTextField("label.flat_y", this,
                183,116,baseFieldWidth,baseFieldHeight,LABEL_OFFSET, STATUS_OFFSET);
        flatYField.styleControlUbuntuFont(13);
        flatYField.styleStatusUbuntuFont(10);
        flatAreaField = new CustomTextField("label.flat_area", this,
                20,167,baseFieldWidth,baseFieldHeight,LABEL_OFFSET, STATUS_OFFSET);
        flatAreaField.styleControlUbuntuFont(13);
        flatAreaField.styleStatusUbuntuFont(10);
        flatFurnishField = new CustomComboBoxField<Furnish>("label.flat_furnish", this,
                183,167,baseFieldWidth,baseFieldHeight,LABEL_OFFSET, STATUS_OFFSET);
        flatFurnishField.styleControlUbuntuFont(13);
        flatFurnishField.styleStatusUbuntuFont(10);
        flatMetroField = new CustomTextField("label.flat_metro", this,
                20,221,baseFieldWidth,baseFieldHeight,LABEL_OFFSET, STATUS_OFFSET);
        flatMetroField.styleControlUbuntuFont(13);
        flatMetroField.styleStatusUbuntuFont(10);
        flatHeatingField = new CustomComboBoxField<Boolean>("label.flat_heating", this,
                183,221,baseFieldWidth,baseFieldHeight,LABEL_OFFSET, STATUS_OFFSET);
        flatHeatingField.styleControlUbuntuFont(13);
        flatHeatingField.styleStatusUbuntuFont(10);
        houseNameField = new CustomTextField("label.house_name", this,
                430,116,baseFieldWidth,baseFieldHeight,LABEL_OFFSET, STATUS_OFFSET);
        houseNameField.styleControlUbuntuFont(13);
        houseNameField.styleStatusUbuntuFont(10);
        houseFlatsField = new CustomTextField("label.house_flats", this,
                430,167,baseFieldWidth,baseFieldHeight,LABEL_OFFSET, STATUS_OFFSET);
        houseFlatsField.styleControlUbuntuFont(13);
        houseFlatsField.styleStatusUbuntuFont(10);
        houseAgeField = new CustomTextField("label.house_age", this,
                430,221,baseFieldWidth,baseFieldHeight,LABEL_OFFSET, STATUS_OFFSET);
        houseAgeField.styleControlUbuntuFont(13);
        houseAgeField.styleStatusUbuntuFont(10);
        doneButton = new Button(getButtonName());
        flatFurnishField.setItems(Arrays.asList(Furnish.values()));
        flatFurnishField.setView(lv -> new ListCell<>() {
            @Override
            protected void updateItem(Furnish item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(getBundle().getString("furnish." + item.name().toLowerCase()));
                }
            }
        }, new ListCell<>() {
            @Override
            protected void updateItem(Furnish item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(getBundle().getString("furnish." + item.name().toLowerCase()));
                }
            }
        });
        flatFurnishField.setDefault(Furnish.NONE);
        flatHeatingField.setItems(Arrays.asList(Boolean.FALSE, Boolean.TRUE));
        flatHeatingField.setView(lv -> new ListCell<Boolean>() {
            @Override
            protected void updateItem(Boolean item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(getBundle().getString(item ? "combobox.yes" : "combobox.false"));
                }
            }
        },new ListCell<Boolean>() {
            @Override
            protected void updateItem(Boolean item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(getBundle().getString(item ? "combobox.yes" : "combobox.false"));
                }
            }
        } );
        flatHeatingField.setDefault(false);

        flatNameField.setCorrectCheckout((() -> {
            if(isEmptyStatused(flatNameField)) {
                return false;
            }
            flatNameField.setEmptyStatus();
            return true;
        }));

        flatRoomsField.setCorrectCheckout((() -> {
            if(isEmptyStatused(flatRoomsField)) {
                return false;
            }
            if(!isIntStatused(flatRoomsField)) {
                return false;
            }
            int number = Integer.valueOf(flatRoomsField.getText());
            if(number <= 0) {
                flatRoomsField.setStatus(Color.RED,"flat_rooms_greater_0");
                return false;
            }
            flatRoomsField.setEmptyStatus();
            return true;
        }));

        flatXField.setCorrectCheckout((() -> {
            if(isEmptyStatused(flatXField)) {
                return false;
            }
            if(!isFloatStatused(flatXField)) {
                return false;
            }
            float number = Float.valueOf(flatXField.getText());
            if(number <= -699) {
                flatXField.setStatus(Color.RED,"flat_x_greater_-699");
                return false;
            }
            flatXField.setEmptyStatus();
            return true;
        }));

        flatYField.setCorrectCheckout((() -> {
            if(isEmptyStatused(flatYField)) {
                return false;
            }
            if(!isIntStatused(flatYField)) {
                return false;
            }
            flatYField.setEmptyStatus();
            return true;
        }));

        flatAreaField.setCorrectCheckout((() -> {
            if(isEmptyStatused(flatAreaField)) {
                return false;
            }
            if(!isFloatStatused(flatAreaField)) {
                return false;
            }
            float number = Float.valueOf(flatAreaField.getText());
            if(number <= 0) {
                flatAreaField.setStatus(Color.RED,"flat_area_greater_0");
                return false;
            }
            flatAreaField.setEmptyStatus();
            return true;
        }));

        flatMetroField.setCorrectCheckout((() -> {
            if(isEmptyStatused(flatMetroField)) {
                return false;
            }
            if(!isFloatStatused(flatMetroField)) {
                return false;
            }
            float number = Float.valueOf(flatMetroField.getText());
            if(number <= 0) {
                flatMetroField.setStatus(Color.RED,"flat_metro_greater_0");
                return false;
            }
            flatMetroField.setEmptyStatus();
            return true;
        }));

        houseNameField.setCorrectCheckout((() -> {
            if(isEmptyStatused(houseNameField)) {
                return false;
            }
            houseNameField.setEmptyStatus();
            return true;
        }));

        houseAgeField.setCorrectCheckout((() -> {
            if(isEmptyStatused(houseAgeField)) {
                return false;
            }
            if(!isIntStatused(houseAgeField)) {
                return false;
            }
            int number = Integer.valueOf(houseAgeField.getText());
            if(number <= 0) {
                houseAgeField.setStatus(Color.RED,"house_age_greater_0");
                return false;
            }
            if(number > 117) {
                houseAgeField.setStatus(Color.RED,"house_age_less_117");
                return false;
            }
            houseAgeField.setEmptyStatus();
            return true;
        }));

        houseFlatsField.setCorrectCheckout((() -> {
            if(isEmptyStatused(houseFlatsField)) {
                return false;
            }
            if(!isIntStatused(houseFlatsField)) {
                return false;
            }
            long number = Long.valueOf(houseFlatsField.getText());
            if(number <= 0) {
                houseFlatsField.setStatus(Color.RED,"house_flats_greater_0");
                return false;
            }
            houseFlatsField.setEmptyStatus();
            return true;
        }));


       // houseLabel.setFont(Font.font("Ubuntu-Bold",20));
        houseLabel.setFont(Font.font("Ubuntu-Italic",20));
        titleLabel.setFont(Font.font("Ubuntu-Bold",20));
        MainApp.createStyledGreenButton(doneButton,Font.font("Ubuntu-Bold",14));


    }

    public void clearAllFields() {
        flatNameField.setText("");
        flatAreaField.setText("");
        houseNameField.setText("");
        houseAgeField.setText("");
        houseFlatsField.setText("");
        flatXField.setText("");
        flatYField.setText("");
        flatMetroField.setText("");
        flatRoomsField.setText("");
    }

    public void setFields(Flat flat) {
        flatNameField.setText(flat.getName());
        flatAreaField.setText(String.valueOf(flat.getArea()));
        flatXField.setText(String.valueOf(flat.getCoordinates().getX()));
        flatYField.setText(String.valueOf(flat.getCoordinates().getY()));
        houseNameField.setText(flat.getHouse().getName());
        houseAgeField.setText(String.valueOf(flat.getHouse().getYear()));
        houseFlatsField.setText(String.valueOf(flat.getHouse().getNumberOfFlatsOnFloor()));
        flatHeatingField.setDefault(flat.getCentralHeating());
        flatFurnishField.setDefault(flat.getFurnish());
        flatMetroField.setText(String.valueOf(flat.getTimeToMetroOnFoot()));
        flatRoomsField.setText(String.valueOf(flat.getNumberOfRooms()));
    }


    @Override
    void pullOriginalPositions() {
        putPosition(houseLabel,new ElementPosition(430,46.63,150,45));
        putPosition(titleLabel,new ElementPosition(0,0,660,20));
        putPosition(doneButton,new ElementPosition(255,280,150,10));
        titleLabel.setAlignment(Pos.CENTER);
        houseLabel.setAlignment(Pos.CENTER);
        doneButton.setAlignment(Pos.CENTER);
    }



    public Button getButton() {
        return doneButton;
    }

    @Override
    protected void updateUITexts() {
        titleLabel.setText(getTitle());
        houseLabel.setText(getBundle().getString("title.house"));
        flatNameField.update();
        flatAreaField.update();
        flatRoomsField.update();
        flatXField.update();
        flatYField.update();
        flatFurnishField.update();
        flatMetroField.update();
        flatHeatingField.update();
        houseNameField.update();
        houseFlatsField.update();
        houseAgeField.update();
        doneButton.setText(getButtonName());
    }

    abstract String getTitle();

    abstract String getButtonName();

    @Override
    Pane createLayout() {
        Pane pane = new Pane();
        pane.setPrefSize(getWidth(),getHeight());
        pane.getChildren().addAll(titleLabel, houseLabel, doneButton);
        flatNameField.init(pane);
        flatAreaField.init(pane);
        flatRoomsField.init(pane);
        flatXField.init(pane);
        flatYField.init(pane);
        flatFurnishField.init(pane);
        flatMetroField.init(pane);
        flatHeatingField.init(pane);
        houseNameField.init(pane);
        houseFlatsField.init(pane);
        houseAgeField.init(pane);
        return pane;
    }

    @Override
    void fullCentering() {
       // center(titleLabel,getLayout(),-1);
    }

    protected boolean checkFieldsCorrect() {
        flatNameField.isCorrectField();
        flatAreaField.isCorrectField();
        flatRoomsField.isCorrectField();
        flatXField.isCorrectField();
        flatYField.isCorrectField();
        houseNameField.isCorrectField();
        houseFlatsField.isCorrectField();
        houseAgeField.isCorrectField();
        flatMetroField.isCorrectField();

        if(!flatNameField.isCorrectField()) {
            return false;
        }
        if(!flatRoomsField.isCorrectField()) {
            return false;
        }
        if(!flatXField.isCorrectField()) {
            return false;
        }
        if(!flatYField.isCorrectField()) {
            return false;
        }
        if(!flatAreaField.isCorrectField()) {
            return false;
        }
        if(!flatMetroField.isCorrectField()) {
            return false;
        }
        if(!houseNameField.isCorrectField()) {
            return false;
        }
        if(!houseAgeField.isCorrectField()) {
            return false;
        }
        if(!houseFlatsField.isCorrectField()) {
            return false;
        }

        return true;
    }


    public boolean isIntStatused(CustomTextField field) {
        try {
            Integer.valueOf(field.getText());
        } catch (NumberFormatException e) {
            field.setStatus(Color.RED, "error.must_be_int");
            return false;
        }
        return true;
    }


    public boolean isFloatStatused(CustomTextField field) {
        try {
            Float.valueOf(field.getText());
        } catch (NumberFormatException e) {
            field.setStatus(Color.RED, "error.invalid_number");
            return false;
        }
        return true;
    }

    public String getFlatName() {
        return flatNameField.getText();
    }

    public String getFlatArea() {
        return flatAreaField.getText();
    }

    public String getFlatRooms() {
        return flatRoomsField.getText();
    }

    public String getFlatX() {
        return flatXField.getText();
    }

    public String getFlatY() {
        return flatYField.getText();
    }

    public Furnish getFlatFurnish() {
        return ((ComboBox<Furnish>)flatFurnishField.getControl()).getValue();
    }

    public Boolean getFlatHeating() {
        return ((ComboBox<Boolean>)flatHeatingField.getControl()).getValue();
    }

    public String getFlatMetro() {
        return flatMetroField.getText();
    }

    public String getHouseName() {
        return houseNameField.getText();
    }

    public String getHouseFlats() {
        return houseFlatsField.getText();
    }

    public String getHouseAge() {
        return houseAgeField.getText();
    }

    public Button getDoneButton() {
        return doneButton;
    }




    public boolean isEmptyStatused(CustomTextField field) {
        if (field.getText().isEmpty()) {
            field.setStatus(Color.RED, "error.empty_credentials");
            return true;
        }
        return false;
    }




}
