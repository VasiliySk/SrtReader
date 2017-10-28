package ru.vasiliy.srtreader.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.MenuBar;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import ru.vasiliy.srtreader.interfaces.CollectionSrtFiles;
import ru.vasiliy.srtreader.objects.SrtFile;

import java.io.*;

public class MainController {
    @FXML
    private TableView tbvTable;
    @FXML
    private TableColumn tbcIndex;
    @FXML
    private TableColumn tbcTimeLine;
    @FXML
    private TableColumn tbcSrtText;

    private CollectionSrtFiles collectionSrtFiles = new CollectionSrtFiles();

    @FXML
    private MenuBar menuFile;


    @FXML
    private void initialize(){
        tbcIndex.setCellValueFactory(new PropertyValueFactory<SrtFile,String>("count"));
        tbcTimeLine.setCellValueFactory(new PropertyValueFactory<SrtFile,String>("timeLine"));
        tbcSrtText.setCellValueFactory(new PropertyValueFactory<SrtFile,String>("srtText"));
        tbvTable.setItems(collectionSrtFiles.getSrtList());

    }

    public void actionClose(ActionEvent actionEvent) {
        Stage stage = (Stage) menuFile.getScene().getWindow();
        stage.close();
    }

    public void openFile(ActionEvent actionEvent) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open Resource File");
        Stage stage = (Stage) menuFile.getScene().getWindow();
        fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("SRT Files", "*.srt") );
        File file = fileChooser.showOpenDialog(stage);
        if(file!=null){
            try {
                int count=0;
                String[] srtLines = new String[3];
                 FileReader fr = new FileReader(file);
                //создаем BufferedReader с существующего FileReader для построчного считывания
                BufferedReader reader = new BufferedReader(fr);
                // считаем сначала первую строку
                String line = reader.readLine();
                while (line != null) {

                    switch (count){
                        case 0:
                            srtLines[0]=line;
                            count = count + 1;
                            break;
                        case 1:
                            srtLines[1]=line;
                            count = count + 1;
                            break;
                        case 2:
                            srtLines[2]=line;
                            SrtFile srtFile = new SrtFile(srtLines[0],srtLines[1],srtLines[2]);
                            collectionSrtFiles.add(srtFile);
                            count = count + 1;
                            break;
                        case 3:
                            count=0;
                            break;
                    }
                    // считываем остальные строки в цикле
                    line = reader.readLine();
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
