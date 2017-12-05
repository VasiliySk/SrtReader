package ru.vasiliy.srtreader.lib;

import java.util.ArrayList;

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
        int startIndex=0;
        int indexCountSearch=0;
        boolean searchResult= false;
        while(startIndex!=-1) {
            startIndex = basicText.indexOf(searchStringMassive[0],indexCountSearch);
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
                            nextChar += 1;
                            lenght += 1;
                            break;
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
                                    searchResult=true;
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

}
