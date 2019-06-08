package ssssuper;

import model.ExcelGenerator;

import javax.swing.filechooser.FileSystemView;
import java.util.*;

public class DutySchedule {

    private List<String[]> key;

    private List<Integer> text;

    private Comparator<String> nameComparator;

    private Comparator<String> shiftComparator;

    private ExcelGenerator excelGenerator;

    private Excel excel;

    protected Map<String, List<String>> personView;

    protected Map<String, List<String>> shiftView;

    protected Map<String, List<String>> manInCharge;

    public DutySchedule(List<String[]> key, List<Integer> text, Excel excel, Comparator<String> nameComparator, Comparator<String> shiftComparator) {
        this.key = key;
        this.text = text;
        this.excel = excel;
        this.nameComparator = nameComparator;
        this.shiftComparator = shiftComparator;

        personView = new HashMap<>();
        shiftView = new HashMap<>();
        manInCharge = new HashMap<>();

        mapInit();
    }

    public void print() {
        System.out.println("Person view:");
        System.out.println(personView);
        System.out.println();
        System.out.println("Shift view:");
        System.out.println(shiftView);
        System.out.println();
        System.out.println("Man in charge:");
        System.out.println(manInCharge);
    }

    public void toFile(String path, String filename) {
        //Overview schedule
        for (Map.Entry<String, List<String>> e : shiftView.entrySet()) {
            String shift = e.getKey();
            List<String> personList = e.getValue();
            personList.sort(nameComparator);

            String personnelStr = toString(personList);
            excelGenerator.set(shift, personnelStr, manInCharge.get(shift));
        }

        //Personal schedule
        TreeMap<String, List<String>> personal = new TreeMap<>(nameComparator);
        personal.putAll(personView);
        for (Map.Entry<String, List<String>> e : personal.entrySet()) {
            String person = e.getKey();
            List<String> hisShifts = e.getValue();

            //Shifts in his charge
            List<String> shiftsInHisCharge = new LinkedList<>();
            for (String shift : hisShifts) {
                if (manInCharge.get(shift).contains(person)) {
                    //Shift he is in charge of
                    shiftsInHisCharge.add(shift);
                }
            }

            hisShifts.sort(shiftComparator);

            excelGenerator.add(person, toString(hisShifts), shiftsInHisCharge);
        }

        excelGenerator.toFile(path, filename);
    }

    public void toFile() {
        FileSystemView fileSystemView = FileSystemView.getFileSystemView();
        toFile(fileSystemView.getHomeDirectory().getPath(), "排班表");
    }

    private void mapInit() {
        Iterator<String[]> keyIter = key.iterator();
        Iterator<Integer> textIter = text.iterator();
        while (keyIter.hasNext() && textIter.hasNext()) {
            String[] key = keyIter.next();
            String p = key[0];
            String s = key[1];
            iter(p, s, textIter.next());
        }
    }

    private String toString(List<String> list) {
        if (list == null) {

            return null;
        }
        if (list.size() == 0) return "";
        StringBuilder stringBuilder = new StringBuilder();
        String separator = ", ";
        for (String str : list) {
            stringBuilder.append(separator).append(str);
        }
        return stringBuilder.substring(separator.length());
    }

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
}
