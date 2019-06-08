package ssssuper.vos;

import ssssuper.DutySchedule;
import ssssuper.Excel;

import java.io.IOException;
import java.util.*;

public class VosDutySchedule extends DutySchedule {

    private Map<String, Collection<String>> editor;

    private Map<String, Collection<String>> manInCharge;

    private String[][] hierarchy;

    public VosDutySchedule(List<String[]> key, List<Integer> text,  String[]... hierarchy) {
        super(key, text, null, null, null);
        this.hierarchy = hierarchy;
    }

    @Override
    protected void iter(String person, String shift, Integer state) {
        personView.putIfAbsent(person, new LinkedList<>());
        shiftView.putIfAbsent(shift, new LinkedList<>());
        manInCharge.putIfAbsent(shift, new LinkedList<>());
        if (state != 0) {
            personView.get(person).add(shift);
            shiftView.get(shift).add(person);
            if (state == 2) {
                manInCharge.get(shift).add(person);
            }
        }
    }

    @Override
    public void toFile(String path, String filename) {
        Excel vosExcel = new VosExcel(shiftView, manInCharge, editor, hierarchy);
        try {
            vosExcel.toFile(path, filename);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
