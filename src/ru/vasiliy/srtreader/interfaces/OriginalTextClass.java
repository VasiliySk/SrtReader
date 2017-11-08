package ru.vasiliy.srtreader.interfaces;

import java.io.*;
import java.net.SocketPermission;
import java.util.ArrayList;

public class OriginalTextClass {

    private StringBuilder stringBuilder = new StringBuilder();
    private String resultText;
    private CollectionSrtFiles cltStrFiles;
    private String[] originalText;
    private String[] editText;

    public void openTxtFile(File file){
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

    }

    public String toString(){
        return stringBuilder.toString();
    }

    public String removeMoreSpace(String result){
        while(result.contains("  ")) {
            String replace = result.replace("  ", " ");
            result=replace;
        }
        resultText = result;
        return result;
    }

    public void splitOriginalText(){
        originalText =  new String[resultText.split(" ").length];
        int k =0;
        for (String retval : resultText.split(" ")) {
            originalText[k]=retval;
            k=k+1;
        }
    }

    public void splitEditText(){
        editText = new String[resultText.split(" ").length];
        int i =0;
        for (String retval : resultText.split(" ")) {
            editText[i]=delNoDigOrLet(retval);
            i=i+1;
        }
    }

    private String delNoDigOrLet (String s) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < s.length(); i++) {
            if (Character .isLetterOrDigit(s.charAt(i)))
                sb.append(s.charAt(i));
        }
        return sb.toString();
    }

    public CollectionSrtFiles getCltStrFiles() {
        return cltStrFiles;
    }

    public void setCltStrFiles(CollectionSrtFiles cltStrFiles) {
        this.cltStrFiles = cltStrFiles;
    }

    public String checkText(int count){
        ArrayList<Boolean> bools= new ArrayList<>();
        String[] srt = cltStrFiles.getSrtList().get(count).getSrtText().split(" ");
        String result="";
        for(int i=0;i<editText.length;i++){
            if(srt[0].equalsIgnoreCase(editText[i])){
                for (int k=0;k<srt.length;k++){
                    if ((i + k)<editText.length) {
                        if (srt[k].equalsIgnoreCase(editText[i + k])) {
                            bools.add(true);
                            if (result.equals("")) {
                                result = originalText[i + k];
                            } else {
                                result = result + " " + originalText[i + k];
                            }
                        } else {
                            bools.add(false);
                        }
                    }else{bools.add(false);}
                }
            if(checkResult(bools)){
                bools.clear();
                return result;
                }
            bools.clear();
            result="";
            }
        }
        bools.clear();
        return "QWERTY";
    }

    private boolean checkResult(ArrayList<Boolean> bools){
        for(int i=0; i<bools.size();i++){
            if (!bools.get(i)){
                return false;
            }
        }
        return true;
    }
}
