package ru.vasiliy.srtreader.lib;

import javafx.scene.control.Alert;
import javafx.stage.StageStyle;

public class MsgBoxClass {

    public static void MsgBox(String text){
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Сообщение");
        alert.setHeaderText(null);
        alert.setContentText(text);
        alert.initStyle(StageStyle.UTILITY);
        alert.showAndWait();
    }


}
