package ru.vasiliy.srtreader.controllers;

import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import ru.vasiliy.srtreader.interfaces.CollectionSrtFiles;
import ru.vasiliy.srtreader.interfaces.OriginalTextClass;
import ru.vasiliy.srtreader.objects.SrtFile;

import java.io.*;

public class MainController {
    @FXML
    private TextArea textAreaOrig;
    @FXML
    private MenuItem menuTxtTemp;
    @FXML
    private TextField txtFilter;
    @FXML
    private TableView<SrtFile> tbvTable;
    @FXML
    private TableColumn<SrtFile,String>tbcIndex;
    @FXML
    private TableColumn<SrtFile,String> tbcTimeLine;
    @FXML
    private TableColumn<SrtFile,String> tbcSrtText;
    @FXML
    private TableColumn<SrtFile,String> tbcOrigText;
    @FXML
    public TableColumn <SrtFile,String>tbcCheckText;

    private CollectionSrtFiles collectionSrtFiles = new CollectionSrtFiles();

    private OriginalTextClass originalTextClass = new OriginalTextClass();

    private String result;

    private String[] originalText;
    private String[] editText;

    @FXML
    private MenuBar menuFile;


    @FXML
    private void initialize(){
        tbcIndex.setCellValueFactory(new PropertyValueFactory<SrtFile,String>("count"));
        tbcTimeLine.setCellValueFactory(new PropertyValueFactory<SrtFile,String>("timeLine"));
        tbcSrtText.setCellValueFactory(new PropertyValueFactory<SrtFile,String>("srtText"));
        tbcOrigText.setCellValueFactory(new PropertyValueFactory<SrtFile,String>("origText"));
        tbcCheckText.setCellValueFactory(new PropertyValueFactory<SrtFile,String>("checkText"));

        FilteredList<SrtFile> filteredData = new FilteredList<>(collectionSrtFiles.getSrtList(), p -> true);

        txtFilter.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredData.setPredicate(srtFile -> {

                if (newValue == null || newValue.isEmpty()) {
                    return true;
                }


                String lowerCaseFilter = newValue.toLowerCase();

                if (srtFile.getCount().toLowerCase().indexOf(lowerCaseFilter) != -1) {
                    return true; // Filter matches first name.
                } else if (srtFile.getTimeLine().toLowerCase().indexOf(lowerCaseFilter) != -1) {
                    return true; // Filter matches last name.
                } else if (srtFile.getSrtText().toLowerCase().indexOf(lowerCaseFilter) != -1) {
                    return true; // Filter matches last name.
                } else if (srtFile.getOrigText().toLowerCase().indexOf(lowerCaseFilter) != -1) {
                return true; // Filter matches last name.
                }else if (srtFile.getCheckText().toLowerCase().indexOf(lowerCaseFilter) != -1) {
                    return true; // Filter matches last name.
                }
                return false; // Does not match.
            });
        });

        SortedList<SrtFile> sortedData = new SortedList<>(filteredData);
        sortedData.comparatorProperty().bind(tbvTable.comparatorProperty());
        tbvTable.setItems(sortedData);

        //tbvTable.setItems(collectionSrtFiles.getSrtList());

        tbcIndex.setCellFactory(TextFieldTableCell.forTableColumn());

        tbcIndex.setOnEditCommit(
                t -> t.getTableView().getItems().get(
                        t.getTablePosition().getRow()).setCount(t.getNewValue())
        );

        tbcTimeLine.setCellFactory(TextFieldTableCell.forTableColumn());

        tbcTimeLine.setOnEditCommit(
                t -> t.getTableView().getItems().get(
                        t.getTablePosition().getRow()).setTimeLine(t.getNewValue())
        );

        tbcSrtText.setCellFactory(TextFieldTableCell.forTableColumn());

        tbcSrtText.setOnEditCommit(
                t -> t.getTableView().getItems().get(
                        t.getTablePosition().getRow()).setSrtText(t.getNewValue())
        );

        tbcOrigText.setCellFactory(TextFieldTableCell.forTableColumn());

        tbcOrigText.setOnEditCommit(
                t -> t.getTableView().getItems().get(
                        t.getTablePosition().getRow()).setOrigText(t.getNewValue())
        );

    }

    public void actionClose(ActionEvent actionEvent) {
        Stage stage = (Stage) menuFile.getScene().getWindow();
        stage.close();
    }

    public void openFile(ActionEvent actionEvent) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open SRT File");
        fileChooser.setInitialDirectory(new File("D:\\Java\\Audiobooks"));
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
                            SrtFile srtFile = new SrtFile(srtLines[0],srtLines[1],srtLines[2],"","");
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

    public void openTxtFile(ActionEvent actionEvent) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open Txt File");
        fileChooser.setInitialDirectory(new File("D:\\Java\\Audiobooks"));
        Stage stage = (Stage) menuFile.getScene().getWindow();
        fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("TXT Files", "*.txt") );
        File file = fileChooser.showOpenDialog(stage);
        originalTextClass.setCltStrFiles(collectionSrtFiles);
        originalTextClass.openTxtFile(file);
        textAreaOrig.setText(originalTextClass.getStringBuilderWithLineBreak().toString());

        result = originalTextClass.toString();

        result = originalTextClass.removeMoreSpace(result);

        originalTextClass.splitOriginalText();

        originalTextClass.splitEditText();
    }

    public void openTest(ActionEvent actionEvent) {
        for(int i=0;i<collectionSrtFiles.getSrtList().size();i++) {
            SrtFile tmpSrtFile = new SrtFile();
            tmpSrtFile.setCount(collectionSrtFiles.getSrtList().get(i).getCount());
            tmpSrtFile.setTimeLine(collectionSrtFiles.getSrtList().get(i).getTimeLine());
            tmpSrtFile.setSrtText(collectionSrtFiles.getSrtList().get(i).getSrtText());
            tmpSrtFile.setOrigText(originalTextClass.checkText(i));
            if (tmpSrtFile.getOrigText().equals("QWERTY")) {
                tmpSrtFile.setCheckText("!false");
            }else {
                tmpSrtFile.setCheckText("!true");
            }
            collectionSrtFiles.getSrtList().set(i,tmpSrtFile);
            System.out.println(originalTextClass.checkText(i));
        }
    }

    public void actionTemp(ActionEvent actionEvent) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("Перваая строка"+"\n");
        stringBuilder.append("Вторая строка"+"\n");
        stringBuilder.append("Третья строка"+"\n");
        textAreaOrig.setText(stringBuilder.toString());

    }
}
