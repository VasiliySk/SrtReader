package ru.vasiliy.srtreader.objects;

import javafx.beans.property.SimpleStringProperty;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;
import javafx.util.Duration;

import java.util.ArrayList;

import static ru.vasiliy.srtreader.controllers.MainController.collectionSrtFiles;
import static ru.vasiliy.srtreader.controllers.MainController.playerMP3;

public class SrtFile {

    public SrtFile(){}

    public SrtFile(String count, String timeLine, String srtText, String origText, String checkText){
        this.count = new SimpleStringProperty(count);
        this.timeLine = new SimpleStringProperty(timeLine);
        this.srtText = new SimpleStringProperty(srtText);
        this.origText=new SimpleStringProperty(origText);
        this.checkText=new SimpleStringProperty(checkText);
        this.button = new Button(">");
        this.button.setOnMousePressed(mouseEvent -> {
            if (playerMP3!=null) {
                playerMP3.setStartTime(Duration.millis(StartTime(this.timeLine.getValue())));
                if(collectionSrtFiles.getSrtList().size()>Integer.valueOf(this.count.getValue())) {
                    playerMP3.setStopTime(Duration.millis(StartTime(collectionSrtFiles.getSrtList().get(Integer.valueOf(this.count.getValue())).getTimeLine())));
                }else{
                    playerMP3.setStopTime(Duration.millis(playerMP3.getTotalDuration().toMillis()));
                }
                playerMP3.play();
                playerMP3.setOnEndOfMedia(()-> playerMP3.stop());
            }
        });

    }

    private SimpleStringProperty count = new SimpleStringProperty("");
    private SimpleStringProperty  timeLine= new SimpleStringProperty("");
    private SimpleStringProperty srtText= new SimpleStringProperty("");
    private SimpleStringProperty origText = new SimpleStringProperty("");
    private SimpleStringProperty checkText = new SimpleStringProperty("");
    private Button button;

    public Button getButton() {
        return button;
    }

    public void setButton(Button button) {
        this.button = button;
    }


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

    //Определяем время начала воспроизведения аудио фрагмента
    private int StartTime (String timeLine){
        int startTime;
        String[] massiveTimeLine=timeLine.split(" --> ");
        String[] massiveTime = massiveTimeLine[0].split(":");
        int startHour = Integer.valueOf(massiveTime[0])*60*60*1000;
        int startMinutes = Integer.valueOf(massiveTime[1])*60*1000;
        String[] massiveSecondAndMills = massiveTime[2].split(",");
        int startSeconds = Integer.valueOf(massiveSecondAndMills[0])*1000;
        int startMills = Integer.valueOf(massiveSecondAndMills[1]);
        startTime = startHour+startMinutes+startSeconds+startMills;
        return startTime;
    }

}
