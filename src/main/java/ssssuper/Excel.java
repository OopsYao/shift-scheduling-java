package ssssuper;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFRichTextString;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import utils.HierarchyComparator;
import utils.NameComparator;

import javax.swing.filechooser.FileSystemView;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Excel {

    protected Workbook workbook;

    private Sheet overview;

    private Sheet personal;

    private String[][] hierarchy;

    private String[][] horizontal;

    private String[][] vertical;

    private Comparator<String> shiftComparator;

    private Comparator<String> nameComparator;

    private Map<String, ? extends Collection<String>> overViewMap;

    private Map<String, ? extends Collection<String>> personalMap;

    public Excel(Map<String, ? extends Collection<String>> overViewMap, String[]... hierarchy) {
        this.hierarchy = hierarchy;
        this.overViewMap = overViewMap;
        init();
    }

    public void toFile(String path, String filename) throws IOException {
        headerFulfill();
        overViewFulfill();
        personalFulfill();
        String fileLocation = path + File.separator + filename + ".xlsx";
        FileOutputStream fileOutputStream = new FileOutputStream(fileLocation);
        workbook.write(fileOutputStream);
        workbook.close();
    }

    public void toFile(String filename) throws IOException {
        FileSystemView fileSystemView = FileSystemView.getFileSystemView();
        toFile(fileSystemView.getHomeDirectory().getPath(), filename);
    }

    /**
     * @param shift shift
     * @return the cell referred to the shift
     */
    protected Cell getCell(String shift) {
        //(x, y) and offset
        int x = vertical.length + getVectorPosition(shift, horizontal);
        int y = horizontal.length + getVectorPosition(shift, vertical);

        return overview.getRow(y).getCell(x, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
    }

    /**
     * @param person person
     * @return the shift cell referred to the person (it will be created)
     */
    protected Cell addCell(String person) {
        Row row = personal.createRow(1 + personal.getLastRowNum());
        row.createCell(0).setCellValue(person);
        return row.createCell(1);
    }

    /**
     * @param map map
     * @return the inverse of the map (relation)
     */
    protected Map<String, Collection<String>> inverse(Map<String, ? extends Collection<String>> map) {
        Map<String, Collection<String>> inverseMap = new HashMap<>();
        map.forEach((key, value) -> {
//            for (String v : value) {
//                inverseMap.putIfAbsent(v, new HashSet<>());
//                inverseMap.get(v).add(key);
//            }
            value.forEach(v -> {
                inverseMap.putIfAbsent(v, new HashSet<>());
                inverseMap.get(v).add(key);
            });
        });
        return inverseMap;
    }

    protected void set(String shift, String personnel) {
        getCell(shift).setCellValue(personnel);
    }

    protected void add(String person, String hisShifts) {
        addCell(person).setCellValue(hisShifts);
    }

    /**
     * @param richText          rich text
     * @param specialCollection set of the special
     * @param specialFont       special font
     */
    protected void applyFont(RichTextString richText, Collection<String> specialCollection, Font specialFont) {
        String text = richText.getString();
        if (specialCollection != null) {
            specialCollection.forEach(str -> {
                int i = text.indexOf(str);
                if (i >= 0 && i + str.length() <= text.length())
                    richText.applyFont(i, i + str.length(), specialFont);
            });
        }
    }

    protected RichTextString applyFont(String text, Collection<String> specialCollection, Font specialFont) {
        XSSFRichTextString richText = new XSSFRichTextString(text);
        applyFont(richText, specialCollection, specialFont);
        return richText;
    }

    private void init() {
        dispatcher(hierarchy);

        workbook = new XSSFWorkbook();
        overview = workbook.createSheet("总览");
        personal = workbook.createSheet("个人视图");

        nameComparator = new NameComparator();
        shiftComparator = new HierarchyComparator(hierarchy);

        personalMap = inverse(overViewMap);
    }

    /**
     * dispatch the hierarchy to horizontal and vertical
     *
     * @param hierarchy the total hierarchy
     */
    private void dispatcher(String[][] hierarchy) {
        if (hierarchy.length <= 1) {
            vertical = hierarchy;
            horizontal = new String[0][];
        } else {
            horizontal = new String[hierarchy.length / 2][];
            vertical = new String[(1 + hierarchy.length) / 2][];
            for (int i = 0; i < hierarchy.length; i++) {
                int index = i / 2;
                switch (i) {
                    case 0:
                        horizontal[index] = hierarchy[i];
                        break;
                    case 1:
                        vertical[index] = hierarchy[i];
                        break;
                    default:
                        if (i % 2 == 1) {
                            horizontal[index] = hierarchy[i];
                        } else {
                            vertical[index] = hierarchy[i];
                        }
                }
            }

        }
    }

    private void headerFulfill() {
        //Overview
        if (hierarchy.length <= 1) {
            String[] strings = hierarchy[0];
            for (int i = 0; i < strings.length; i++) {
                overview.createRow(i).createCell(0).setCellValue(strings[i]);
            }
        } else {
            //Horizontal header
            int width = Arrays.stream(horizontal).mapToInt(strings -> strings.length).reduce(1, (a, b) -> a * b);
            for (int i = 0, len = width, counter = 1; i < horizontal.length; i++) {
                //row i
                int size = horizontal[i].length;
                len /= size;
                counter *= size;
                Row row = overview.createRow(i);
                for (int j = 0, start = vertical.length; j < counter; j++, start += len) {
                    if (len != 1)
                        overview.addMergedRegion(new CellRangeAddress(i, i, start, start + len - 1));
                    row.createCell(start).setCellValue(horizontal[i][j % size]);
                }
            }
            //Vertical header
            int height = Arrays.stream(vertical).mapToInt(strings -> strings.length).reduce(1, (a, b) -> a * b);
            for (int i = 0, len = height, counter = 1; i < vertical.length; i++) {
                //column i
                int size = vertical[i].length;
                len /= size;
                counter *= size;
                for (int j = 0, start = horizontal.length; j < counter; j++, start += len) {
                    if (len != 1)
                        overview.addMergedRegion(new CellRangeAddress(start, start + len - 1, i, i));

                    Row row;
                    if ((row = overview.getRow(start)) == null) {
                        row = overview.createRow(start);
                    }
                    row.createCell(i).setCellValue(vertical[i][j % size]);
                }

            }
        }

        //Personal
        Row row = personal.createRow(0);
        row.createCell(0).setCellValue("姓名");
        row.createCell(1).setCellValue("班次");
    }

    private void overViewFulfill() {
        overViewMap.forEach((shift, personnel) -> set(shift, toString(personnel, nameComparator)));
    }

    private void personalFulfill() {
        TreeMap<String, Collection<String>> treeMap = new TreeMap<>(nameComparator);
        treeMap.putAll(personalMap);
        treeMap.forEach((person, shift) -> add(person, toString(shift, shiftComparator)));
    }

    /**
     * @param shift  Shift
     * @param vector Hierarchy in one direction. Horizontal or Vertical
     * @return the position in that direction. X or Y
     */
    private int getVectorPosition(String shift, String[][] vector) {
        //Table position
        int vectorPosition = 0;
        for (String[] indexArray : vector) {
            int index = indexOf(shift, indexArray);
            if (index == -1) {
                try {
                    throw new Exception("Implicit position");
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    index = 0;
                }
            }
            vectorPosition = index + indexArray.length * vectorPosition;
        }
        return vectorPosition;
    }

    /**
     * @param str        str which contains the element of this array
     * @param indexArray the array
     * @return the index (if not contained, returns -1)
     */
    private int indexOf(String str, String[] indexArray) {
        return IntStream.range(0, indexArray.length).filter(i -> str.contains(indexArray[i])).findFirst().orElse(-1);
    }

    private String toString(Collection<String> collection, Comparator<String> comparator) {
        return collection.stream().sorted(comparator).collect(Collectors.joining(", "));
    }
}
