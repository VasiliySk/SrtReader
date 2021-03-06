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
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.Window;
import javafx.util.Callback;
import javafx.util.converter.DefaultStringConverter;
import ru.vasiliy.srtreader.interfaces.CollectionSrtFiles;
import ru.vasiliy.srtreader.interfaces.OriginalTextClass;
import ru.vasiliy.srtreader.interfaces.ProjectSrt;
import ru.vasiliy.srtreader.lib.SearchTextClass;
import ru.vasiliy.srtreader.objects.SrtFile;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

import static ru.vasiliy.srtreader.lib.MsgBoxClass.MsgBox;

public class MainController {
    @FXML
    private VBox vBoxField;
    @FXML
    private MenuItem menuMP3Open;//Меню "Открыть MP3 файл"
    @FXML
    private Label lblStatusText; //Текстовая строка статуса
    @FXML
    private MenuItem menuOpen;//Меню "Открыть SRT файл"
    @FXML
    private MenuItem menuTxtOpen;//Меню "Открыть TXT файл"
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
    private TableColumn <SrtFile,String>tbcCheckText;
    @FXML
    private TableColumn <SrtFile,String>tbcMP3Play; //Столбец таблицы для кнопки проигрывания музыки

    public static CollectionSrtFiles collectionSrtFiles = new CollectionSrtFiles();

    private OriginalTextClass originalTextClass = new OriginalTextClass();

    private ProjectSrt projectSrt =new ProjectSrt();

    private String result;

    private String[] originalText;
    private String[] editText;

    private boolean openSrtFile = false;
    private boolean openMP3File = false;

    public static MediaPlayer playerMP3;
    private File mp3File;

    @FXML
    private MenuBar menuFile;

    private ObservableList<String> statusField;

    private FilteredList<SrtFile> filteredData = new FilteredList<>(collectionSrtFiles.getSrtList(), p -> true);
    private SortedList<SrtFile> sortedData = new SortedList<>(filteredData);

    // Инициализация проекта
    @FXML
    private void initialize(){

        tbcIndex.setCellValueFactory(new PropertyValueFactory<SrtFile,String>("count"));
        tbcTimeLine.setCellValueFactory(new PropertyValueFactory<SrtFile,String>("timeLine"));
        tbcSrtText.setCellValueFactory(new PropertyValueFactory<SrtFile,String>("srtText"));
        tbcOrigText.setCellValueFactory(new PropertyValueFactory<SrtFile,String>("origText"));
        tbcCheckText.setCellValueFactory(new PropertyValueFactory<SrtFile,String>("checkText"));
        tbcMP3Play.setCellValueFactory(new PropertyValueFactory<SrtFile,String>("button"));

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
        menuMP3Open.setAccelerator(KeyCombination.keyCombination("shortcut+M"));

        //Меню по правой кнопке
        ContextMenu contextMenu = new ContextMenu();
        MenuItem menuItemSearchText = new MenuItem("Поиск");
        menuItemSearchText.setOnAction(e -> {
            SrtFile selectedSrtFile = tbvTable.getSelectionModel().getSelectedItem();
            SearchTextClass searchTextClass = new SearchTextClass();
            ArrayList<Integer> arrayList = searchTextClass.searchTextExt(selectedSrtFile.getOrigText(),textAreaOrig.getText());
            if (arrayList.size()!=0) {
                textAreaOrig.requestFocus();
                textAreaOrig.positionCaret(arrayList.get(0));
                textAreaOrig.selectPositionCaret(arrayList.get(1));
            }
        });
        MenuItem menuItemSelectionText = new MenuItem("Подбор текста");
        menuItemSelectionText.setOnAction(e -> {
            selectionOfTtext();
        });
        contextMenu.getItems().addAll(menuItemSearchText,menuItemSelectionText);
        tbvTable.setContextMenu(contextMenu);

        //Удаляем лишние пробелы в вводимом тексте
        tbcOrigText.setOnEditCommit(
                (TableColumn.CellEditEvent<SrtFile, String> t) -> {
                    TableView tempTable = (TableView)t.getTableView();
                    SrtFile tempPerson = (SrtFile) tempTable.getItems().get(t.getTablePosition().getRow());
                    tempPerson.setOrigText(removeMoreSpaceAndLineBreak(t.getNewValue()));
                });

    }

    //Подбор строки в случае, если в строке выше и строке ниже есть текст
    private void selectionOfTtext() {
        SrtFile selectedSrtFile = tbvTable.getSelectionModel().getSelectedItem();
        if(selectedSrtFile.getOrigText().equals("QWERTY")) {
            int stringCount = Integer.valueOf(selectedSrtFile.getCount());
            SrtFile upSelectedSrtFile = collectionSrtFiles.getSrtList().get(stringCount - 2);
            SrtFile downSelectedSrtFile = collectionSrtFiles.getSrtList().get(stringCount);
            if ((upSelectedSrtFile.getOrigText().equals("QWERTY")) || (downSelectedSrtFile.getOrigText().equals("QWERTY"))) {
                MsgBox("Не найдено.");
            } else {
                SearchTextClass searchTextClass = new SearchTextClass();
                ArrayList<Integer> upArrayList = searchTextClass.searchTextExt(upSelectedSrtFile.getOrigText(), textAreaOrig.getText());
                ArrayList<Integer> downArrayList = searchTextClass.searchTextExt(downSelectedSrtFile.getOrigText(), textAreaOrig.getText());
                selectedSrtFile.setOrigText(removeMoreSpaceAndLineBreak(textAreaOrig.getText(upArrayList.get(1), downArrayList.get(0)).trim()));
                selectedSrtFile.setCheckText("!edited");
                textAreaOrig.requestFocus();
                textAreaOrig.positionCaret(upArrayList.get(1));
                textAreaOrig.selectPositionCaret(downArrayList.get(0));
            }
        }
    }

    //Закрываем программу
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
            openSrtFile = true;
            if (openMP3File) {
                menuSaveProject.setDisable(false);
                menuSaveProjectAs.setDisable(false);
            }
            lblStatusText.setText("Файл субтитров загружен.");
        }
    }

    // Открываем текстовый файл с оригинальным текстом
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

    // Подбираем оригинальный текст
    private void reconciliationTexts(){
        SearchTextClass searchTextClass=new SearchTextClass();
        for(int i=0;i<collectionSrtFiles.getSrtList().size();i++) {
            SrtFile tmpSrtFile = new SrtFile();
            tmpSrtFile.setCount(collectionSrtFiles.getSrtList().get(i).getCount());
            tmpSrtFile.setTimeLine(collectionSrtFiles.getSrtList().get(i).getTimeLine());
            tmpSrtFile.setSrtText(collectionSrtFiles.getSrtList().get(i).getSrtText());
            tmpSrtFile.setButton(collectionSrtFiles.getSrtList().get(i).getButton());
            //tmpSrtFile.setOrigText(originalTextClass.checkText(i));
            tmpSrtFile.setOrigText(searchTextClass.checkSrtText(collectionSrtFiles.getSrtList().get(i).getSrtText(),textAreaOrig.getText()));
            if (tmpSrtFile.getOrigText().equals("QWERTY")) {
                tmpSrtFile.setCheckText("!false");
            }else {
                tmpSrtFile.setCheckText("!true");
            }
            collectionSrtFiles.getSrtList().set(i,tmpSrtFile);
        }
    }

    //Ищем текст в текстовом поле и выделяем его
    public void actionSearch(KeyEvent keyEvent) {
        if(keyEvent.getCode().equals(KeyCode.ENTER)){
            SearchTextClass searchTextClass = new SearchTextClass();
            ArrayList<Integer> arrayList = searchTextClass.searchTextExt(txtSearch.getText(),textAreaOrig.getText());
            if (arrayList.size()!=0) {
                textAreaOrig.requestFocus();
                textAreaOrig.positionCaret(arrayList.get(0));
                textAreaOrig.selectPositionCaret(arrayList.get(1));
            }
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

                line = reader.readLine();
                mp3File = new File(line);
                playerMP3= new MediaPlayer(new Media(mp3File.toURI().toString()));

                menuSaveProject.setDisable(false);
                menuSaveProjectAs.setDisable(false);
                menuTxtOpen.setDisable(true);
                menuOpen.setDisable(true);
                menuMP3Open.setDisable(true);
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
        if(!file.toString().endsWith(".psrt")){
            File fileExt = new File(file.getAbsolutePath()+".psrt");
            projectSrt.setProjectFile(fileExt);
        }else {
            projectSrt.setProjectFile(file);
        }
        if (file!=null){
            projectSrt.saveProjectFile();
            projectSrt.saveOriginalTextFile(textAreaOrig.getText());
            projectSrt.saveExtendedSrtFile(collectionSrtFiles.getSrtList());
            projectSrt.saveMP3File(mp3File);
            lblStatusText.setText("Проект сохранен.");
        }
    }

    //Сохрвняем SRT файл
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
            if (!file.toString().endsWith(".srt")){
                File fileExt = new File(file.getAbsolutePath()+".srt");
                projectSrt.saveSrtFile(fileExt, collectionSrtFiles);
            }else{
                projectSrt.saveSrtFile(file, collectionSrtFiles);
            }
            lblStatusText.setText("Файл субтитров сохранен.");
        }
    }

    //Обновляем счетчик отображаемых строк
    private void updateCountList(){
        lblStatusText.setText("Количество записей в таблице: "+sortedData.size());
    }

    //Ищем следующую строку без подобранного текста
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

    //Ищем предыдущую строку без подобранного текста
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

    //Тестовая функция
    public void actionTemp(ActionEvent actionEvent) {

        String searchString = "in. THE AUTHOR. HARTFORD, 1876.  Chapter I";
        SearchTextClass searchTextClass=new SearchTextClass();
        System.out.println(searchTextClass.checkSrtText(searchString,textAreaOrig.getText()));

    }

    //Удаляем лищние пробелы и переносы строк
    private String removeMoreSpaceAndLineBreak(String result){
        while(result.contains("  ")) {
            String replace = result.replace("  ", " ");
            result=replace;
        }
        while(result.contains("\n")) {
            String replace = result.replace("\n", "");
            result=replace;
        }
        return result;
    }

    //Открываем MP3 файл
    public void openMP3File(ActionEvent actionEvent) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Открыть MP3 файл");
        //Проверяем существует ли папка по умолчанию
        Path path = Paths.get("D:\\Java\\Audiobooks");
        if (Files.exists(path)) {
            fileChooser.setInitialDirectory(new File("D:\\Java\\Audiobooks"));
        }
        Stage stage = (Stage) menuFile.getScene().getWindow();
        fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("MP3 файл", "*.mp3") );
        mp3File = fileChooser.showOpenDialog(stage);
        if(mp3File!=null) {
            playerMP3= new MediaPlayer(new Media(mp3File.toURI().toString()));
            openMP3File = true;
            if (openSrtFile) {
                menuSaveProject.setDisable(false);
                menuSaveProjectAs.setDisable(false);
            }
            lblStatusText.setText("MP3 файл загружен.");
        }
    }
}
