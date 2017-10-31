package ru.vasiliy.srtreader.controllers;

import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.MenuBar;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import ru.vasiliy.srtreader.interfaces.CollectionSrtFiles;
import ru.vasiliy.srtreader.objects.SrtFile;
import javafx.scene.control.TableColumn.CellEditEvent;

import java.io.*;

public class MainController {
    @FXML
    private TextField txtFilter;
    @FXML
    private TableView tbvTable;
    @FXML
    private TableColumn<SrtFile,String>tbcIndex;
    @FXML
    private TableColumn<SrtFile,String> tbcTimeLine;
    @FXML
    private TableColumn<SrtFile,String> tbcSrtText;

    private CollectionSrtFiles collectionSrtFiles = new CollectionSrtFiles();

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
                }
                return false; // Does not match.
            });
        });

        SortedList<SrtFile> sortedData = new SortedList<>(filteredData);
        sortedData.comparatorProperty().bind(tbvTable.comparatorProperty());
        tbvTable.setItems(sortedData);

      //  tbvTable.setItems(collectionSrtFiles.getSrtList());

        tbcIndex.setCellFactory(TextFieldTableCell.forTableColumn());

        tbcIndex.setOnEditCommit(
                new EventHandler<CellEditEvent<SrtFile, String>>() {
                    @Override
                    public void handle(CellEditEvent<SrtFile, String> t) {
                        ((SrtFile) t.getTableView().getItems().get(
                                t.getTablePosition().getRow())
                        ).setCount(t.getNewValue());
                    }
                }
        );

        tbcTimeLine.setCellFactory(TextFieldTableCell.forTableColumn());

        tbcTimeLine.setOnEditCommit(
                new EventHandler<CellEditEvent<SrtFile, String>>() {
                    @Override
                    public void handle(CellEditEvent<SrtFile, String> t) {
                        ((SrtFile) t.getTableView().getItems().get(
                                t.getTablePosition().getRow())
                        ).setTimeLine(t.getNewValue());
                    }
                }
        );

        tbcSrtText.setCellFactory(TextFieldTableCell.forTableColumn());

        tbcSrtText.setOnEditCommit(
                new EventHandler<CellEditEvent<SrtFile, String>>() {
                    @Override
                    public void handle(CellEditEvent<SrtFile, String> t) {
                        ((SrtFile) t.getTableView().getItems().get(
                                t.getTablePosition().getRow())
                        ).setSrtText(t.getNewValue());
                    }
                }
        );

    }

    public void actionClose(ActionEvent actionEvent) {
        Stage stage = (Stage) menuFile.getScene().getWindow();
        stage.close();
    }

    public void openFile(ActionEvent actionEvent) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open SRT File");
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

    public void openTxtFile(ActionEvent actionEvent) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open Txt File");
        Stage stage = (Stage) menuFile.getScene().getWindow();
        fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("SRT Files", "*.txt") );
        File file = fileChooser.showOpenDialog(stage);
        StringBuilder stringBuilder = new StringBuilder();
        if(file!=null){
            try {
                FileReader fr = new FileReader(file);
                //создаем BufferedReader с существующего FileReader для построчного считывания
                BufferedReader reader = new BufferedReader(fr);
                // считаем сначала первую строку
                String line = reader.readLine();
                String lineExt = line +" ";
                stringBuilder.append(lineExt);
                while (line != null) {
                    // считываем остальные строки в цикле
                    System.out.println(lineExt);
                    line = reader.readLine();
                    lineExt = line+ " ";
                    stringBuilder.append(lineExt);
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }


        result = stringBuilder.toString();

        //Удаляем лищние пробелы
        while(result.contains("  ")) {
            String replace = result.replace("  ", " ");
            result=replace;
        }
        //Создаем массив из слов без знаком пунктуации
        editText = new String[result.split(" ").length];
        int i =0;
        for (String retval : result.split(" ")) {
            editText[i]=delNoDigOrLet(retval);
            i=i+1;
        }


        for(int j=0; j<7;j++){
            System.out.println(editText[j]);
        }

        //Создаем массив из слов со знаками пунктуации
        originalText =  new String[result.split(" ").length];
        int k =0;
        for (String retval : result.split(" ")) {
            originalText[k]=retval;
            k=k+1;
        }

        for(int j=0; j<7;j++){
            System.out.println(originalText[j]);
        }

    }

    private static String delNoDigOrLet (String s) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < s.length(); i++) {
            if (Character .isLetterOrDigit(s.charAt(i)))
                sb.append(s.charAt(i));
        }
        return sb.toString();
    }

    public void openTest(ActionEvent actionEvent) {
        boolean check =false;
        System.out.println(collectionSrtFiles.getSrtList().get(0).getSrtText());
        System.out.println(collectionSrtFiles.getSrtList().get(0).getSrtText().split(" ").length);
        String[] srt = collectionSrtFiles.getSrtList().get(0).getSrtText().split(" ");
        for(int i=0;i<editText.length;i++){
           if(srt[0].equals(editText[i])){
               check=true;
               for (int k=0;k<srt.length;k++){
                   if(srt[0+k].equals(editText[i+k])){
                       check=true;
                   }else {
                       check=false;
                       return;
                   }
               }
           }
        }
        System.out.println(check);
    }
}
