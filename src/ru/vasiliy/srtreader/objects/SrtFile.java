package ru.vasiliy.srtreader.objects;

import javafx.beans.property.SimpleStringProperty;

public class SrtFile {

    public SrtFile(){}

    public SrtFile(String count, String timeLine, String srtText, String origText, String checkText){
        this.count = new SimpleStringProperty(count);
        this.timeLine = new SimpleStringProperty(timeLine);
        this.srtText = new SimpleStringProperty(srtText);
        this.origText=new SimpleStringProperty(origText);
        this.checkText=new SimpleStringProperty(checkText);
    }

    private SimpleStringProperty count = new SimpleStringProperty("");
    private SimpleStringProperty  timeLine= new SimpleStringProperty("");
    private SimpleStringProperty srtText= new SimpleStringProperty("");
    private SimpleStringProperty origText = new SimpleStringProperty("");
    private SimpleStringProperty checkText = new SimpleStringProperty("");

    public String getCount() {
        return count.get();
    }

    public String getTimeLine() {
        return timeLine.get();
    }

    public String getSrtText() {
        return srtText.get();
    }

    public String getOrigText(){return origText.get();}

    public String getCheckText(){return checkText.get();}

    public void setCount(SimpleStringProperty count) {
        this.count = count;
    }

    public void setCount(String count) {
        this.count.set(count);
    }

    public void setTimeLine(SimpleStringProperty timeLine) {
        this.timeLine = timeLine;
    }

    public void setTimeLine(String timeLine) {
        this.timeLine.set(timeLine);
    }

    public void setSrtText(String srtText) {
        this.srtText.set(srtText);
    }

    public void setOrigText(String origText){this.origText.set(origText);}

    public void setCheckText(String checkText){this.checkText.set(checkText);}

    public SimpleStringProperty countProperty() {
        return count;
    }

    public SimpleStringProperty timeLineProperty() {
        return timeLine;
    }

    public SimpleStringProperty srtTextProperty() {
        return srtText;
    }

    public SimpleStringProperty origTextProperty(){return origText;}

    public SimpleStringProperty checkTextProperty(){return checkText;}

}
