package ru.vasiliy.srtreader.interfaces;

import javafx.collections.ObservableList;
import ru.vasiliy.srtreader.objects.SrtFile;

import java.io.*;

public class ProjectSrt {
    private File projectFile;
    private File originalTextFile;
    private File extendedSrtFile;

    public ProjectSrt(File projectFile, File originalTextFile, File extendedSrtFile) {
        this.projectFile = projectFile;
        this.originalTextFile = originalTextFile;
        this.extendedSrtFile = extendedSrtFile;
    }
    public ProjectSrt(){}

    public File getProjectFile() {
        return projectFile;
    }

    public void setProjectFile(File projectFile) {
        this.projectFile = projectFile;
    }

    public File getOriginalTextFile() {
        return originalTextFile;
    }

    public void setOriginalTextFile(File originalTextFile) {
        this.originalTextFile = originalTextFile;
    }

    public File getExtendedSrtFile() {
        return extendedSrtFile;
    }

    public void setExtendedSrtFile(File extendedSrtFile) {
        this.extendedSrtFile = extendedSrtFile;
    }

    public void saveProjectFile(){
        try {
            FileWriter fileWriter = new FileWriter(projectFile);
            BufferedWriter buffer = new BufferedWriter(fileWriter);
            String name = fileName(projectFile.getAbsolutePath());
            buffer.write( name+".ptxt") ;
            buffer.newLine();
            buffer.write( name+".esrt") ;
            buffer.close();
            originalTextFile = new File(fileName(projectFile.getAbsolutePath())+".ptxt");
            extendedSrtFile = new File(fileName(projectFile.getAbsolutePath())+".esrt");

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void saveOriginalTextFile(String originalText){
        try {
            FileWriter fileWriter = new FileWriter(originalTextFile);
            BufferedWriter buffer = new BufferedWriter(fileWriter);
            buffer.write(originalText);
            buffer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void saveExtendedSrtFile(ObservableList<SrtFile> srtList){
        try {
            FileWriter fileWriter = new FileWriter(extendedSrtFile);
            BufferedWriter buffer = new BufferedWriter(fileWriter);
            for(int i=0;i<srtList.size();i++){
                if(i<srtList.size()-1) {
                    buffer.write(srtList.get(i).getCount());
                    buffer.newLine();
                    buffer.write(srtList.get(i).getTimeLine());
                    buffer.newLine();
                    buffer.write(srtList.get(i).getSrtText());
                    buffer.newLine();
                    buffer.write(srtList.get(i).getOrigText());
                    buffer.newLine();
                    buffer.write(srtList.get(i).getCheckText());
                    buffer.newLine();
                }else{
                    buffer.write(srtList.get(i).getCount());
                    buffer.newLine();
                    buffer.write(srtList.get(i).getTimeLine());
                    buffer.newLine();
                    buffer.write(srtList.get(i).getSrtText());
                    buffer.newLine();
                    buffer.write(srtList.get(i).getOrigText());
                    buffer.newLine();
                    buffer.write(srtList.get(i).getCheckText());
                }
            }
            buffer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String fileName (String FileNameWithExtension){
        int pos = FileNameWithExtension.lastIndexOf(".");
        if (pos > 0) {
            return FileNameWithExtension.substring(0, pos);
        }
        return "";
    }

    public String openOriginalTextFile(String fileAdress){
        File file = new File(fileAdress);
        StringBuilder stringBuilder = new StringBuilder();
        try {
            FileReader fr = new FileReader(file);
            BufferedReader reader = new BufferedReader(fr);
            String line = reader.readLine();
            String lineWithLineBreak = line + "\n";
            stringBuilder.append(lineWithLineBreak);
            while (line != null) {
                line = reader.readLine();
                if (line!=null){
                    lineWithLineBreak = line + "\n";
                    stringBuilder.append(lineWithLineBreak);
                }

            }
            reader.close();
            return stringBuilder.toString();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return "";
        }catch (IOException e) {
            e.printStackTrace();
            return "";
        }
    }

    public void openExtendedSrtFile(String fileAdress, CollectionSrtFiles collectionSrtFiles){
        String line1="";
        String line2="";
        String line3="";
        String line4="";
        String line5="";
        int count =0;
        File file = new File(fileAdress);
        try {
            FileReader fr = new FileReader(file);
            BufferedReader reader = new BufferedReader(fr);
            String line = reader.readLine();
            line1=line;
            count =2;
            while (line != null) {
                switch (count){
                    case 1:
                        line = reader.readLine();
                        line1=line;
                        count=2;
                        break;
                    case 2:
                        line = reader.readLine();
                        line2=line;
                        count=3;
                        break;
                    case 3:
                        line = reader.readLine();
                        line3=line;
                        count=4;
                        break;
                    case 4:
                        line = reader.readLine();
                        line4=line;
                        count=5;
                        break;
                    case 5:
                        line = reader.readLine();
                        line5=line;
                        SrtFile srtFile = new SrtFile(line1,line2,line3,line4,line5);
                        collectionSrtFiles.add(srtFile);
                        count=1;
                        break;
                }
            }
            reader.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void saveSrtFile(File file,CollectionSrtFiles collectionSrtFiles){

        try {
            FileWriter fileWriter = new FileWriter(file);
            BufferedWriter buffer = new BufferedWriter(fileWriter);
            System.out.println(collectionSrtFiles.getSrtList().size());
            for(int i=0; i<collectionSrtFiles.getSrtList().size();i++){
                buffer.write(collectionSrtFiles.getSrtList().get(i).getCount());
                buffer.newLine();
                buffer.write(collectionSrtFiles.getSrtList().get(i).getTimeLine());
                buffer.newLine();
                buffer.write(collectionSrtFiles.getSrtList().get(i).getOrigText());
                buffer.newLine();
                buffer.newLine();
            }
         buffer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}

