package ru.vasiliy.srtreader.lib;

import java.util.ArrayList;

import static ru.vasiliy.srtreader.lib.MsgBoxClass.MsgBox;

public class SearchTextClass {

    //Поиск текста, игнорируя спецсимволы, лишние пробелы и т.п.
    public ArrayList<Integer> searchTextExt(String searchString, String basicText){
        ArrayList<Integer> arrayList = new ArrayList<Integer>();
        String[] searchStringMassive = new String[searchString.split(" ").length];
        int k =0;
        for (String retval : searchString.split(" ")) {
            searchStringMassive[k]=retval;

            k=k+1;
        }
        if (searchStringMassive.length==1){
            if (basicText.indexOf(searchStringMassive[0])!=-1) {
                arrayList.add(basicText.indexOf(searchStringMassive[0]));
                arrayList.add(basicText.indexOf(searchStringMassive[0]) + searchStringMassive[0].length());
                return arrayList;
            }else{
                return arrayList;
            }
        }
        int startIndex=0;
        int indexCountSearch=0;
        boolean searchResult= false;
        while(startIndex!=-1) {
            startIndex = basicText.indexOf(searchStringMassive[0],indexCountSearch);
            if (startIndex == -1) {
                return arrayList;
            }
            int lenght = searchStringMassive[0].length();
            int nextChar = startIndex + searchStringMassive[0].length();
            for (int z = 1; z < searchStringMassive.length; z++) {

                char buf[] = new char[searchStringMassive[z].length()];

                searchStringMassive[z].getChars(0, searchStringMassive[z].length(), buf, 0);

                boolean charStatus = true;
                char chr;
                int bufCount = 0;
                while (charStatus) {
                    chr = basicText.charAt(nextChar);
                    switch (chr) {
                        case ' ':
                        case '\n':
                            nextChar += 1;
                            lenght += 1;
                            break;
                        default:
                            if (chr == buf[bufCount]) {
                                nextChar += 1;
                                bufCount += 1;
                                lenght += 1;
                                if ((z==searchStringMassive.length-1)&&(bufCount==searchStringMassive[z].length())){
                                    char chrPunctuation = basicText.charAt(nextChar);
                                    switch (chrPunctuation) {
                                        case ',':
                                        case '.':
                                            nextChar += 1;
                                            lenght += 1;
                                            searchResult=true;
                                            break;
                                        default:
                                            searchResult=true;
                                    }
                                }
                                if (bufCount == buf.length) {
                                    charStatus = false;
                                    indexCountSearch=startIndex+1;
                                }
                            } else {
                                charStatus = false;
                                indexCountSearch=startIndex+1;
                            }
                    }
                }
            }
            if (searchResult){
                arrayList.add(startIndex);
                arrayList.add(startIndex + lenght);
                return arrayList;
            }
        }
        return arrayList;
    }


    public String checkSrtText(String searchString, String basicText){
        String[] searchStringMassive = new String[searchString.split(" ").length];
        int k =0;
        for (String retval : searchString.split(" ")) {
            searchStringMassive[k]=retval;
            k=k+1;
        }
        if (searchStringMassive.length==1){
            if (basicText.indexOf(searchStringMassive[0])!=-1) {
                return searchStringMassive[0];
            }else{
                return "QWERTY";
            }
        }
        int startIndex=0;
        int indexCountSearch=0;
        boolean searchResult= false;
        while(startIndex!=-1) {
            startIndex = basicText.toLowerCase().indexOf(searchStringMassive[0].toLowerCase(),indexCountSearch);
            if (startIndex == -1) {
                return "QWERTY";
            }
            int lenght = searchStringMassive[0].length();
            int nextChar = startIndex + searchStringMassive[0].length();
            boolean charStatus;
            boolean wordStatus;
            for (int z = 1; z < searchStringMassive.length; z++) {

                char buf[] = new char[searchStringMassive[z].length()];
                searchStringMassive[z].getChars(0, searchStringMassive[z].length(), buf, 0);

                char chr;
                int bufCount = 0;
                charStatus = true;
                wordStatus = true;
                while (charStatus&&wordStatus) {

                    if(nextChar<basicText.length()) {
                        chr = basicText.charAt(nextChar);
                    }else{
                        return "QWERTY";
                    }
                    switch (chr) {
                        case ' ':
                        case ';':
                        case ',':
                        case '-':
                        case '.':
                        case '–':
                        case '"':
                        case '?':
                        case '!':
                        case ':':
                        case '(':
                        case ')':
                        case '\n':
                            nextChar += 1;
                            lenght += 1;
                            break;
                        default:
                            if (String.valueOf(chr).equalsIgnoreCase( String.valueOf(buf[bufCount]))) {
                                nextChar += 1;
                                bufCount += 1;
                                lenght += 1;
                                if ((z==searchStringMassive.length-1)&&(bufCount==searchStringMassive[z].length())&&(startIndex!=-1)){
                                    char chrPunctuation = basicText.charAt(nextChar);
                                    switch (chrPunctuation) {
                                        case ',':
                                        case '.':
                                            nextChar += 1;
                                            lenght += 1;
                                            searchResult=true;
                                            break;
                                        default:
                                            searchResult=true;
                                    }

                                }
                                if (bufCount == buf.length) {
                                    wordStatus = false;
                                    indexCountSearch=startIndex+1;
                                }
                            } else {
                                charStatus = false;
                                indexCountSearch=startIndex+1;
                            }
                    }
                }
                if(!charStatus){
                    break;
                }
            }
            if (searchResult){
                return removeMoreSpaceAndLineBreak(basicText.substring(startIndex,startIndex + lenght).trim());
            }
        }
        return "QWERTY";
    }

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
}
