package ru.vasiliy.srtreader.objects;

import javafx.beans.property.SimpleStringProperty;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;
import javazoom.jl.decoder.JavaLayerException;
import javazoom.jl.player.Player;
import javazoom.jl.player.advanced.AdvancedPlayer;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import static ru.vasiliy.srtreader.controllers.MainController.mp3File;

public class SrtFile {

    public SrtFile(){}

    public SrtFile(String count, String timeLine, String srtText, String origText, String checkText){
        this.count = new SimpleStringProperty(count);
        this.timeLine = new SimpleStringProperty(timeLine);
        this.srtText = new SimpleStringProperty(srtText);
        this.origText=new SimpleStringProperty(origText);
        this.checkText=new SimpleStringProperty(checkText);
        this.button = new Button(">");
        this.button.setOnMousePressed(new EventHandler<MouseEvent>() {
            @Override public void handle(MouseEvent mouseEvent) {
                if (mp3File!=null) {
                    try {
                        try {
                            FileInputStream mp3FileStream = new FileInputStream(mp3File);
                            AdvancedPlayer player = new AdvancedPlayer(mp3FileStream);
                            //An MP3 frame always represents 26ms of audio, regardless of the bitrate.  1/0.026 = 38.46 frames per second.
                            player.play(249,435);
                            try {
                                mp3FileStream.close();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        }
                    } catch (JavaLayerException e) {
                        e.printStackTrace();
                    }
                }
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

}
