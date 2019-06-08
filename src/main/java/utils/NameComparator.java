package utils;


import com.github.promeg.pinyinhelper.PinyinMapDict;

import java.util.*;

import static com.github.promeg.pinyinhelper.Pinyin.*;

public class NameComparator implements Comparator<String> {

    static {
        //Personal dictionary is only effective for string (not char)
        init(newConfig().with(new PinyinMapDict() {
            @Override
            public Map<String, String[]> mapping() {
                Map<String, String[]> map = new HashMap<>();
                map.put("曾", new String[]{"ZENG"});
                map.put("茜", new String[]{"XI"});
                return map;
            }
        }));
    }

    @Override
    public int compare(String nameA, String nameB) {
        //Chinese character array
        char[] chArrayA = nameA.toCharArray();
        char[] chArrayB = nameB.toCharArray();

        //Pinyin array
        //Do not use dot as a separator!
        String separator = " ";
        String pyA = toPinyin(nameA, separator);
        String pyB = toPinyin(nameB, separator);

        String[] pyArrayA = pyA.split(separator);
        String[] pyArrayB = pyB.split(separator);


        for (int i = 0; i < chArrayA.length && i < chArrayB.length; i++) {
            if (chArrayA[i] != chArrayB[i]) {
                //Compare pinyin of single Chinese character
                int pyCom = stringCompare(pyArrayA[i], pyArrayB[i]);
                if (pyCom != 0) {
                    return pyCom;
                } else {
                    return Character.compare(chArrayA[i], chArrayB[i]);
                }
            }
        }
        return Integer.compare(chArrayA.length, chArrayB.length);
    }

    /**
     * Compare two strings
     * @param strA String A
     * @param strB String B
     * @return String comparision result
     */
    private int stringCompare(String strA, String strB) {
        char[] charsA = strA.toCharArray();
        char[] charsB = strB.toCharArray();
        for (int i = 0; (i < charsA.length) && (i < charsB.length); i++) {
            char a = charsA[i];
            char b = charsB[i];
            if (a != b) {
                return Character.compare(a, b);
            }
        }
        return Integer.compare(charsA.length, charsB.length);
    }
}