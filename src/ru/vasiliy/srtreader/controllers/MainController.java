package ru.vasiliy.srtreader.controllers;

import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.cell.ComboBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.input.*;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.Window;
import javafx.util.Callback;
import javafx.util.converter.DefaultStringConverter;
import ru.vasiliy.srtreader.interfaces.CollectionSrtFiles;
import ru.vasiliy.srtreader.interfaces.OriginalTextClass;
import ru.vasiliy.srtreader.interfaces.ProjectSrt;
import ru.vasiliy.srtreader.objects.SrtFile;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static ru.vasiliy.srtreader.lib.MsgBoxClass.MsgBox;

public class MainController {
    @FXML
    private Label lblStatusText; //Текстовая строка статуса
    @FXML
    private MenuItem menuOpen;//Меню "Открыть SRT файл"
    @FXML
    private MenuItem menuTxtOpen;//Меню "Открыть TXT файл"
    @FXML
    private VBox vBox;
    @FXML
    private MenuItem menuSaveProjectAs;//Меню "Сохранить проект как"
    @FXML
    private MenuItem menuSaveProject;//Меню "Сохранить проект"
    @FXML
    private MenuItem menuOpenProject;
    @FXML
    private TextField txtSearch;
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

    private ProjectSrt projectSrt =new ProjectSrt();

    private String result;

    private String[] originalText;
    private String[] editText;

    @FXML
    private MenuBar menuFile;

    private ObservableList<String> statusField;

    FilteredList<SrtFile> filteredData = new FilteredList<>(collectionSrtFiles.getSrtList(), p -> true);
    SortedList<SrtFile> sortedData = new SortedList<>(filteredData);


    @FXML
    private void initialize(){

        tbcIndex.setCellValueFactory(new PropertyValueFactory<SrtFile,String>("count"));
        tbcTimeLine.setCellValueFactory(new PropertyValueFactory<SrtFile,String>("timeLine"));
        tbcSrtText.setCellValueFactory(new PropertyValueFactory<SrtFile,String>("srtText"));
        tbcOrigText.setCellValueFactory(new PropertyValueFactory<SrtFile,String>("origText"));
        tbcCheckText.setCellValueFactory(new PropertyValueFactory<SrtFile,String>("checkText"));

        collectionSrtFiles.getSrtList().addListener((ListChangeListener) (c) ->{
            updateCountList();
        });



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
            updateCountList();
        });


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

        //Вызов ComboBox при редактировании tbcCheckText
        statusField = FXCollections.observableArrayList();
        statusField.add("!true");
        statusField.add("!false");
        statusField.add("!edited");
        tbcCheckText.setCellFactory(ComboBoxTableCell.forTableColumn(new DefaultStringConverter(),statusField));

        //Быстрые клавиши Ctrl+... для меню
        menuOpenProject.setAccelerator(KeyCombination.keyCombination("shortcut+O"));
        menuSaveProject.setAccelerator(KeyCombination.keyCombination("shortcut+S"));
        menuOpen.setAccelerator(KeyCombination.keyCombination("shortcut+R"));
        menuTxtOpen.setAccelerator(KeyCombination.keyCombination("shortcut+T"));

        //Меню по правой кнопке
        ContextMenu contextMenu = new ContextMenu();
        MenuItem menuItemSearchText = new MenuItem("Поиск");
        MenuItem menuItemSelectionText = new MenuItem("Подбор текста");
        contextMenu.getItems().addAll(menuItemSearchText,menuItemSelectionText);
        tbvTable.setContextMenu(contextMenu);


    }

    public void actionClose(ActionEvent actionEvent) {
        Stage stage = (Stage) menuFile.getScene().getWindow();
        stage.close();
    }

    //Выбираем и загружаем SRT файл
    public void openFile(ActionEvent actionEvent) {
        if (originalTextClass.getResultText().equals("")) {
            MsgBox("Откройте сначала файл с текстом!");
        } else {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Open SRT File");
            //Проверяем существует ли папка по умолчанию
            Path path = Paths.get("D:\\Java\\Audiobooks");
            if (Files.exists(path)) {
                fileChooser.setInitialDirectory(new File("D:\\Java\\Audiobooks"));
            }
            Stage stage = (Stage) menuFile.getScene().getWindow();
            fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("SRT Files", "*.srt"));
            File file = fileChooser.showOpenDialog(stage);
            if (file != null) {
                try {
                    int count = 0;
                    String[] srtLines = new String[3];
                    FileReader fr = new FileReader(file);
                    //создаем BufferedReader с существующего FileReader для построчного считывания
                    BufferedReader reader = new BufferedReader(fr);
                    // считаем сначала первую строку
                    String line = reader.readLine();
                    while (line != null) {

                        switch (count) {
                            case 0:
                                srtLines[0] = line;
                                count = count + 1;
                                break;
                            case 1:
                                srtLines[1] = line;
                                count = count + 1;
                                break;
                            case 2:
                                srtLines[2] = line;
                                SrtFile srtFile = new SrtFile(srtLines[0], srtLines[1], srtLines[2], "", "");
                                collectionSrtFiles.add(srtFile);
                                count = count + 1;
                                break;
                            case 3:
                                count = 0;
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
            reconciliationTexts();
            menuSaveProject.setDisable(false);
            menuSaveProjectAs.setDisable(false);
            lblStatusText.setText("Файл субтитров загружен.");
        }
    }

    public void openTxtFile(ActionEvent actionEvent) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open Txt File");
        //Проверяем существует ли папка по умолчанию
        Path path = Paths.get("D:\\Java\\Audiobooks");
        if (Files.exists(path)) {
            fileChooser.setInitialDirectory(new File("D:\\Java\\Audiobooks"));
        }
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
        lblStatusText.setText("Оригинальный текст загружен.");
    }

    private void reconciliationTexts(){
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
        }
    }

    public void actionTemp(ActionEvent actionEvent) {
        searchTextExt();
    }

    //Поиск текста, игнорируя спецсимволы, лишние пробелы и т.п.
    private void searchTextExt(){
        String searchString = "architecture. The odd superstitions";
        String basicText = textAreaOrig.getText();
        int index = basicText.indexOf(searchString);
        MsgBox(index);
    }

    public void actionSearch(KeyEvent keyEvent) {
        if(keyEvent.getCode().equals(KeyCode.ENTER)){
            searchText(txtSearch.getText());
        }

    }

    private void searchText(String srhText){
        int index = textAreaOrig.getText().indexOf(srhText);
        if (index!=-1) {
            textAreaOrig.requestFocus();
            textAreaOrig.positionCaret(index);
            int lenght = srhText.length();
            textAreaOrig.selectPositionCaret(index + lenght);
        }
    }

    public void actionTableClick(MouseEvent mouseEvent) {
        if(mouseEvent.getButton()== MouseButton.SECONDARY){
           /* SrtFile selectedSrtFile = tbvTable.getSelectionModel().getSelectedItem();
            searchText(selectedSrtFile.getOrigText());*/
        }
    }

    //Открываем сохраненный ранее проект
    public void openProject(ActionEvent actionEvent) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Открыть проект");
        //Проверяем существует ли папка по умолчанию
        Path path = Paths.get("D:\\Java\\Audiobooks");
        if (Files.exists(path)) {
            fileChooser.setInitialDirectory(new File("D:\\Java\\Audiobooks"));
        }
        Stage stage = (Stage) menuFile.getScene().getWindow();
        fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("Файл проекта", "*.psrt") );
        File file = fileChooser.showOpenDialog(stage);
        if(file!=null) {
            collectionSrtFiles.clean();
            projectSrt.setProjectFile(file);
            try {
                FileReader fr = new FileReader(file);
                BufferedReader reader = new BufferedReader(fr);
                String line = reader.readLine();
                projectSrt.setOriginalTextFile(new File (line));
                textAreaOrig.setText(projectSrt.openOriginalTextFile(line));
                line = reader.readLine();
                projectSrt.setExtendedSrtFile(new File(line));
                projectSrt.openExtendedSrtFile(line, collectionSrtFiles);
                menuSaveProject.setDisable(false);
                menuSaveProjectAs.setDisable(false);
                menuTxtOpen.setDisable(true);
                menuOpen.setDisable(true);
                lblStatusText.setText("Проект загружен.");
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    //Сохраняем проект
    public void saveProject(ActionEvent actionEvent)
    {
        if(projectSrt.getProjectFile()!=null){
            projectSrt.saveOriginalTextFile(textAreaOrig.getText());
            projectSrt.saveExtendedSrtFile(collectionSrtFiles.getSrtList());
            lblStatusText.setText("Проект сохранен.");
        }else{
            saveProjectAs(actionEvent);
            lblStatusText.setText("Проект сохранен.");
        }
    }

    //Сохраняем проект с выбором места и названия файла проекта
    public void saveProjectAs(ActionEvent actionEvent) {
        FileChooser fileChooser = new FileChooser();
        Window parentWindow = menuFile.getScene().getWindow();
        //Проверяем существует ли папка по умолчанию
        Path path = Paths.get("D:\\Java\\Audiobooks");
        if (Files.exists(path)) {
            fileChooser.setInitialDirectory(new File("D:\\Java\\Audiobooks"));
        }
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("Файл проекта", "*.psrt");
        fileChooser.getExtensionFilters().add(extFilter);
        File file = fileChooser.showSaveDialog(parentWindow);
        projectSrt.setProjectFile(file);
        if (file!=null){
            projectSrt.saveProjectFile();
            projectSrt.saveOriginalTextFile(textAreaOrig.getText());
            projectSrt.saveExtendedSrtFile(collectionSrtFiles.getSrtList());
            lblStatusText.setText("Проект сохранен.");
        }
    }

    public void saveSrtFile(ActionEvent actionEvent) {
        FileChooser fileChooser = new FileChooser();
        Window parentWindow = menuFile.getScene().getWindow();
        //Проверяем существует ли папка по умолчанию
        Path path = Paths.get("D:\\Java\\Audiobooks");
        if (Files.exists(path)) {
            fileChooser.setInitialDirectory(new File("D:\\Java\\Audiobooks"));
        }
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("Srt файл", "*.srt");
        fileChooser.getExtensionFilters().add(extFilter);
        File file = fileChooser.showSaveDialog(parentWindow);
        if (file!=null) {
            projectSrt.saveSrtFile(file, collectionSrtFiles);
            lblStatusText.setText("Файл субтитров сохранен.");
        }
    }

    private void updateCountList(){
        lblStatusText.setText("Количество записей в таблице: "+sortedData.size());
    }

    public void actionDown(ActionEvent actionEvent) {
        int selectedSrtCount=tbvTable.getSelectionModel().getSelectedIndex();
        if (selectedSrtCount != -1) {
            for (int i =selectedSrtCount+1; i <sortedData.size(); i++){
                if(sortedData.get(i).getOrigText().equals("QWERTY")){
                    tbvTable.requestFocus();
                    tbvTable.getFocusModel().focus(i);
                    tbvTable.getSelectionModel().select(i);
                    tbvTable.scrollTo(i-3);
                    break;
                }
            }
        } else {
            for (int i =0; i <sortedData.size(); i++){
                if(sortedData.get(i).getOrigText().equals("QWERTY")){
                    tbvTable.requestFocus();
                    tbvTable.getFocusModel().focus(i);
                    tbvTable.getSelectionModel().select(i);
                    tbvTable.scrollTo(i-3);
                    break;
                }
            }
        }
    }

    public void actionUp(ActionEvent actionEvent) {
        int selectedSrtCount=tbvTable.getSelectionModel().getSelectedIndex();
        if (selectedSrtCount != -1) {
            for (int i =selectedSrtCount-1; i >=0; i--){
                if(sortedData.get(i).getOrigText().equals("QWERTY")){
                    tbvTable.requestFocus();
                    tbvTable.getFocusModel().focus(i);
                    tbvTable.getSelectionModel().select(i);
                    tbvTable.scrollTo(i-3);
                    break;
                }
            }
        }
    }
}
