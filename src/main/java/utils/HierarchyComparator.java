package utils;

import java.util.Comparator;

public class HierarchyComparator implements Comparator<String> {

    private String[][] hierarchy;

    public HierarchyComparator(String[]... hierarchy) {
        this.hierarchy = hierarchy;
    }

    @Override
    public int compare(String o1, String o2) {
        if (o1.equals(o2)) return 0;
        for (String[] indexArray : hierarchy) {
            int index1 = indexOf(o1, indexArray);
            int index2 = indexOf(o2, indexArray);
            if (index1 != index2)
                return Integer.compare(index1, index2);
        }
        return 0;
    }

    private int indexOf(String str, String[] indexArray) {
        int index = -1;
        for (int i = 0; i < indexArray.length; i++) {
            if (str.contains(indexArray[i])) {
                index = i;
                break;
            }
        }
        return index;
    }

}
