package ru.vasiliy.srtreader.objects;

import javafx.beans.property.SimpleStringProperty;

public class SrtFile {

    public SrtFile(){}

    public SrtFile(String count, String timeLine, String srtText){
        this.count = new SimpleStringProperty(count);
        this.timeLine = new SimpleStringProperty(timeLine);
        this.srtText = new SimpleStringProperty(srtText);
    }

    private SimpleStringProperty count = new SimpleStringProperty("");
    private SimpleStringProperty  timeLine= new SimpleStringProperty("");;
    private SimpleStringProperty srtText= new SimpleStringProperty("");;

    /*public SimpleStringProperty getCount() {
        return count;
    }*/

    public String getCount() {
        return count.get();
    }

    public String getTimeLine() {
        return timeLine.get();
    }

    public String getSrtText() {
        return srtText.get();
    }

    public void setCount(SimpleStringProperty count) {
        this.count = count;
    }

    public void setCount(String count) {
        this.count.set(count);
    }

   /* public SimpleStringProperty getTimeLine() {
        return timeLine;
    }*/

    public void setTimeLine(SimpleStringProperty timeLine) {
        this.timeLine = timeLine;
    }

    public void setTimeLine(String timeLine) {
        this.timeLine.set(timeLine);
    }

    /*
    public void setSrtText(SimpleStringProperty srtText) {
        this.srtText = srtText;
    }*/

    public void setSrtText(String srtText) {
        this.srtText.set(srtText);
    }

    public SimpleStringProperty countProperty() {
        return count;
    }

    public SimpleStringProperty timeLineProperty() {
        return timeLine;
    }

    public SimpleStringProperty srtTextProperty() {
        return srtText;
    }

}
