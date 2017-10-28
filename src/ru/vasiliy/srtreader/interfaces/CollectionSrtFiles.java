package ru.vasiliy.srtreader.interfaces;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import ru.vasiliy.srtreader.objects.SrtFile;

public class CollectionSrtFiles {

    private ObservableList<SrtFile> srtList = FXCollections.observableArrayList();

    public void add(SrtFile srtFile) {
        getSrtList().add(srtFile);
    }

    public ObservableList<SrtFile> getSrtList() {
        return srtList;
    }

}

