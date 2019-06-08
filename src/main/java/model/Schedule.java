package model;

import dao.impl.ConnectionFactoryImpl;
import utils.HierarchyComparator;
import utils.Lottery;
import utils.NameComparator;

import javax.swing.filechooser.FileSystemView;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;


public class Schedule {
    private List<Boolean> core;

    private static List<String[]> availableAppointments;
    private static Map<String, Integer> stdShiftSize;
    private static Map<String, Integer> maxShiftSize;

    private static Comparator<String> shiftComparator;
    private static Comparator<String> nameComparator;

    private static String[] weekdays = {"周一", "周二", "周三", "周四", "周五", "周六", "周日"};
    private static String[] dayShifts = {"中午", "傍晚"};
    private static String[] places = {"三味", "五谷"};
    private static String[] shifts;

    static {
        //Title instructor
        shifts = new String[2 * weekdays.length];
        for (int i = 0; i < weekdays.length; i++) {
            shifts[2 * i] = weekdays[i] + "中午";
            shifts[2 * i + 1] = weekdays[i] + "傍晚";
        }
        //Comparator
        shiftComparator = new HierarchyComparator(weekdays, dayShifts, places);
        nameComparator = new NameComparator();

        //Preloading available appointments
        availableAppointments = new LinkedList<>();
        try {
            Connection conn = new ConnectionFactoryImpl().getConnection();
            PreparedStatement prepareStatement = conn.prepareStatement("select * from origin_data");
            ResultSet resultSet = prepareStatement.executeQuery();
            while (resultSet.next()) {
                String person = resultSet.getString(1);
                for (int i = 0; i < weekdays.length; i++) {
                    String day = weekdays[i];
                    String state;
                    if (i >= 5) {
                        //Weekends
                        state = "第4节第5节第9节第10节";
                    } else {
                        state = resultSet.getString(2 + i);
                    }

                    if (state.contains("第4节")) {
                        availableAppointments.add(new String[]{person, day + "中午"});
                    }
                    if (state.contains("第9节")) {
                        availableAppointments.add(new String[]{person, day + "傍晚"});
                    }
                }
            }
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        //Preloading the standard shift-size
        stdShiftSize = new HashMap<>();
        for (String day : weekdays) {
            stdShiftSize.put(day + "中午", 10);
            stdShiftSize.put(day + "傍晚", 10);
        }
        stdShiftSize.put("周五傍晚", 8);
        stdShiftSize.put("周六中午", 8);
        stdShiftSize.put("周六傍晚", 8);
        stdShiftSize.put("周日中午", 8);

        //Preloading the max shift-size
        maxShiftSize = new HashMap<>();
        Map<String, Integer> timesMax = new HashMap<>();
        for (String[] app : availableAppointments) {
            String p = app[0];
            String s = app[1];
            timesMax.put(p, 1 + timesMax.getOrDefault(p, 0));
            maxShiftSize.put(s, 1 + maxShiftSize.getOrDefault(s, 0));
        }
        System.out.println("Schedule max:");
        System.out.println(maxShiftSize);
    }

    public Schedule(List<Boolean> core) {
        this.core = core;
    }

    public Comparator<String> getShiftComparator() {
        return shiftComparator;
    }

    public List<Boolean> getCore() {
        return core;
    }

    /**
     * @return Two maps
     */
    private Map<String, Integer>[] getMapsForDev() {
        Map<String, Integer> times = new HashMap<>();
        Map<String, Integer> sizes = new HashMap<>();

        Iterator<String[]> avaIter = availableAppointments.iterator();
        Iterator<Boolean> corIter = core.iterator();

        while (avaIter.hasNext() && corIter.hasNext()) {
            String[] next = avaIter.next();
            boolean c = corIter.next();

            String p = next[0];
            String s = next[1];

            times.putIfAbsent(p, 0);
            sizes.putIfAbsent(s, 0);
            if (c) {
                times.put(p, 1 + times.get(p));
                sizes.put(s, 1 + sizes.get(s));
            }
        }
        return new Map[]{times, sizes};
    }

    protected Map[] getDetailedMaps() {
        Map<String, Integer>[] mapsForDev = getMapsForDev();
        Map<String, Integer> sizes = mapsForDev[1];
        HashMap<String, Lottery> lotMap = new HashMap<>();
        for (Map.Entry<String, Integer> e : sizes.entrySet()) {
            String shift = e.getKey();
            int size = e.getValue();
            Lottery lottery = new Lottery();
            //Sanwei
            lottery.add(size / 2);
            //Wugu
            lottery.add(size - size / 2);
            lotMap.put(shift, lottery);
        }

        Map<String, List<String>> assignment = new HashMap<>();
        Map<String, List<String>> shift = new HashMap<>();

        Iterator<String[]> avaIter = availableAppointments.iterator();
        Iterator<Boolean> corIter = core.iterator();
        while (avaIter.hasNext() && corIter.hasNext()) {
            String[] next = avaIter.next();
            String p = next[0];
            String s = next[1];

            assignment.putIfAbsent(p, new LinkedList<>());
            shift.putIfAbsent(s + " - 三味", new LinkedList<>());
            shift.putIfAbsent(s + " - 五谷", new LinkedList<>());

            if (corIter.next()) {
                int draw = lotMap.get(s).draw();
                if (draw == 0) {
                    s += " - 三味";
                } else if (draw == 1) {
                    s += " - 五谷";
                } else {
                    System.out.println("Exception: draw " + draw);
                }

                assignment.get(p).add(s);
                shift.get(s).add(p);
            }
        }

        return new Map[]{assignment, shift};
    }

    /**
     * A console display
     */
    public void consoleLog() {
        Map<String, Integer>[] mapsForDev = getMapsForDev();
        Map<String, Integer> times = mapsForDev[0];
        Map<String, Integer> sizes = new TreeMap<>(shiftComparator);
        sizes.putAll(mapsForDev[1]);

        List<Integer> timesVal = new LinkedList<>(times.values());
        Collections.sort(timesVal);

        System.out.println("Current data properties:");
        System.out.print("Shift sizes: ");
        System.out.println(sizes);
        System.out.print("Scheduled times: ");
        System.out.println(timesVal);
    }


    private String convert(List<String> strList) {
        if (strList.size() == 0) return "";
        StringBuilder stringBuilder = new StringBuilder();
        for (String str : strList) {
            stringBuilder.append(", ").append(str);
        }
        return stringBuilder.substring(2);
    }

    public void toFile(String path, String filename) {
        ExcelGenerator excelGenerator = new ExcelGenerator(weekdays, dayShifts, places);
        Map[] mapsForUsr = getDetailedMaps();
        Map<String, List<String>> overview = mapsForUsr[1];
        for (Map.Entry<String, List<String>> e : overview.entrySet()) {
            String shift = e.getKey();
            List<String> personnel = e.getValue();
            personnel.sort(nameComparator);

            excelGenerator.set(shift, convert(personnel));
        }

        TreeMap<String, List<String>> personal = new TreeMap<>(nameComparator);
        personal.putAll(mapsForUsr[0]);
        for (Map.Entry<String, List<String>> e : personal.entrySet()) {
            String person = e.getKey();
            List<String> hisShifts = e.getValue();
            hisShifts.sort(shiftComparator);

            excelGenerator.add(person, convert(hisShifts));
        }

        excelGenerator.toFile(path, filename);
    }

    public void toFile() {
        FileSystemView fileSystemView = FileSystemView.getFileSystemView();
        toFile(fileSystemView.getHomeDirectory().getPath(), "排班表");
    }

    public static List<String[]> getAvaList() {
        return availableAppointments;
    }
}