package ssssuper.vos;

import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.RichTextString;
import ssssuper.Excel;

import java.util.Collection;
import java.util.Map;

public class VosExcel extends Excel {

    private Map<String, ? extends Collection<String>> manInCharge;

    private Map<String, ? extends Collection<String>> shiftsInHisCharge;

    private Font redFont;

    private Map<String, ? extends Collection<String>> editor;

    private Map<String, ? extends Collection<String>> shiftsOfEditor;

    private Font blueFont;

    VosExcel(Map<String, ? extends Collection<String>> overViewMap, Map<String, ? extends Collection<String>> manInCharge, Map<String, ? extends Collection<String>> editor, String[]... hierarchy) {
        super(overViewMap, hierarchy);
        this.manInCharge = manInCharge;
        this.editor = editor;
        init();
    }

    @Override
    protected void set(String shift, String personnel) {
        RichTextString richTextString = applyFont(personnel, manInCharge.get(shift), redFont);
        applyFont(richTextString, editor.get(shift), blueFont);
        super.getCell(shift).setCellValue(richTextString);
    }

    @Override
    protected void add(String person, String hisShifts) {
        RichTextString richTextString = super.applyFont(hisShifts, shiftsInHisCharge.get(person), redFont);
        super.applyFont(richTextString, shiftsOfEditor.get(person), blueFont);
        super.addCell(person).setCellValue(richTextString);
    }

    private void init() {
        shiftsInHisCharge = super.inverse(manInCharge);
        shiftsOfEditor = super.inverse(editor);

        redFont = super.workbook.createFont();
        redFont.setColor(Font.COLOR_RED);
        blueFont = super.workbook.createFont();
        blueFont.setColor(HSSFColor.HSSFColorPredefined.LIGHT_BLUE.getIndex());
    }

}
