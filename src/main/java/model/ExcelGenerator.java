package model;

import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.*;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

public class ExcelGenerator {

    private XSSFWorkbook workbook;

    private XSSFSheet overview;

    private XSSFSheet personal;

    private int personalIndex;

    private String[] weekdays;

    private String[] dayShifts;

    private String[] places;

    private boolean placeColumnConsidered;

    private XSSFFont fontSp;

    private CellStyle titleStyle;

    private CellStyle overviewContentStyle;

    private CellStyle personalContentStyle;


    public ExcelGenerator(String[] weekdays, String[] dayShifts, String[] places) {
        this.weekdays = weekdays;
        this.dayShifts = dayShifts;
        this.places = places;
        init();
    }

    public ExcelGenerator(String[] weekdays, String[] dayShifts) {
        this.weekdays = weekdays;
        this.dayShifts = dayShifts;
        init();
    }

    public void set(String shift, String personnel) {
        set(shift, personnel, null);
    }

    public void set(String shift, String personnel, List<String> manInCharge) {
        int i = indexOf(weekdays, shift);
        int j = indexOf(dayShifts, shift);
        int len;
        int k;
        if (placeColumnConsidered) {
            len = places.length;
            k = indexOf(places, shift);
        } else {
            len = 1;
            k = 0;
        }

        XSSFCell cell = overview.getRow(1 + len * j + k)
                .createCell((len > 1 ? 2 : 1) + i);

        cell.setCellStyle(overviewContentStyle);

        if (manInCharge == null) {
            cell.setCellValue(personnel);
        } else {
            cell.setCellValue(applySpecial(personnel, manInCharge, fontSp));
        }
    }

    public void add(String person, String hisShifts) {
        add(person, hisShifts, null);
    }

    public void add(String person, String hisShifts, List<String> shiftsInHisCharge) {
        XSSFRow row = personal.createRow(personalIndex);
        row.createCell(0).setCellValue(person);
        XSSFCell cell = row.createCell(1);

        if (shiftsInHisCharge == null || shiftsInHisCharge.size() <= 0) {
            cell.setCellValue(hisShifts);
        } else {
            cell.setCellValue(applySpecial(hisShifts, shiftsInHisCharge, fontSp));
        }

        personalIndex++;
    }

    public void toFile(String path, String fileName) {
        try {
            String fileLocation = path + File.separator + fileName + ".xlsx";
            FileOutputStream fileOutputStream = new FileOutputStream(fileLocation);
            workbook.write(fileOutputStream);
            workbook.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void init() {
        placeColumnConsidered = places != null && places.length > 1;
        workbook = new XSSFWorkbook();
        overview = workbook.createSheet("总览");
        personal = workbook.createSheet("分表");

        fontSp = workbook.createFont();
        //Red
        fontSp.setColor(Font.COLOR_RED);
        //Light blue
        fontSp.setColor(HSSFColor.HSSFColorPredefined.LIGHT_BLUE.getIndex());


        //Style
        titleStyle = workbook.createCellStyle();
        titleStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        titleStyle.setAlignment(HorizontalAlignment.CENTER);

        overviewContentStyle = workbook.createCellStyle();
        overviewContentStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        overviewContentStyle.setWrapText(true);

        personalContentStyle = workbook.createCellStyle();
        personalContentStyle.setVerticalAlignment(VerticalAlignment.CENTER);

        //Header
        XSSFRow headerOverview = overview.createRow(0);
        for (int i = 0; i < weekdays.length; i++) {
            XSSFCell cell = headerOverview.createCell((placeColumnConsidered ? 2 : 1) + i);
            cell.setCellValue(weekdays[i]);
            cell.setCellStyle(titleStyle);
        }

        //Column header
        if (!placeColumnConsidered) {
            for (int i = 0; i < dayShifts.length; i++) {
                XSSFCell cell = overview.createRow(1 + i).createCell(0);
                cell.setCellValue(dayShifts[i] + (places == null ? "" : (" - " + places[0])));
                cell.setCellStyle(titleStyle);
            }
        } else {
            for (int i = 0; i < dayShifts.length; i++) {
                for (int j = 0; j < places.length; j++) {
                    int rowIndex = 1 + places.length * i + j;
                    XSSFRow row = overview.createRow(rowIndex);
                    XSSFCell cell = row.createCell(1);
                    cell.setCellValue(places[j]);
                    cell.setCellStyle(titleStyle);
                    if (j == 0) {
                        XSSFCell headCell = row.createCell(0);
                        //Merge
                        overview.addMergedRegion(new CellRangeAddress(rowIndex, places.length - 1 + rowIndex,
                                0, 0));

                        headCell.setCellValue(dayShifts[i]);
                        headCell.setCellStyle(titleStyle);
                    }
                }
            }
        }

        //Personal sheet header
        add("姓名", "排班");

        //Width adjustment
        for (int i = 0; i < weekdays.length; i++) {
            overview.setColumnWidth((placeColumnConsidered ? 2 : 1) + i, 18 * 256);
        }
    }

    private int indexOf(String[] keywordArray, String word) {
        int i;
        for (i = 0; i < keywordArray.length; i++) {
            if (word.contains(keywordArray[i])) {
                break;
            }
        }
        return i;
    }

    private RichTextString applySpecial(String text, List<String> specialList, Font specialFont) {
        XSSFRichTextString richStr = new XSSFRichTextString(text);
        for (String str : specialList) {
            int i = text.indexOf(str);
            if (i >= 0 && i + str.length() < text.length())
                richStr.applyFont(i, i + str.length(), specialFont);
        }
        return richStr;
    }
}
