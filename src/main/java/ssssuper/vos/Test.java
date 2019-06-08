package ssssuper.vos;

import dao.impl.ConnectionFactoryImpl;
import ssssuper.GeneralIndicator;
import ssssuper.ShiftOrientedIndicator;
import ssssuper.ShiftScheduleProcess;
import ssssuper.TextDealer;
import ssssuper.vos.dispatcher.EnhancedDispatcher;
import ssssuper.vos.dispatcher.PlaceDispatcher;
import utils.MapStatistics;
import utils.TimeConsume;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

public class Test {

    public static void main(String[] args) throws IOException {
        TimeConsume timeConsume = new TimeConsume();
        List<String[]> freshmanTrivialKey = new LinkedList<>();
        List<String[]> freshmanEditorKey = new LinkedList<>();
        List<String[]> sophomoreTrivialKey = new LinkedList<>();
        LinkedList<String[]> sophomoreEditorKey = new LinkedList<>();
        String[] weekdays = {"周一", "周二", "周三", "周四", "周五", "周六", "周日"};
        String[] dayShifts = {"中午", "傍晚"};
        String[] places = {"五谷", "三味"};

        try {
            Connection conn = new ConnectionFactoryImpl().getConnection();
            PreparedStatement prepareStatement = conn.prepareStatement("select * from 点歌季空课表");
            ResultSet resultSet = prepareStatement.executeQuery();
            while (resultSet.next()) {
                String person = resultSet.getString(1);
                String grade = resultSet.getString(2);
                String depart = resultSet.getString(3);
                for (int i = 0; i < weekdays.length; i++) {
                    String day = weekdays[i];
                    String state = resultSet.getString(4 + i);
                    for (int j = 0; j < dayShifts.length; j++) {
                        if (state.contains("第" + (4 + 5 * j) + "节")) {
                            String[] match = {person, day + dayShifts[j]};
                            if (grade.contains("17")) {
                                //sophomore
                                sophomoreTrivialKey.add(match);
                                if (depart.contains("编辑")) {
                                    sophomoreEditorKey.add(match);
                                }
                            } else {
                                //freshman
                                freshmanTrivialKey.add(match);
                                if (depart.contains("编辑")) {
                                    freshmanEditorKey.add(match);
                                }
                            }

                        }
                    }
                }
            }
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        String separator = "-";
        List<Integer> freshmanEditorText = new TextDealer(freshmanEditorKey, new ShiftOrientedIndicator(2)).run();
        //Make freshman key = freshman key - freshmanEditorText
        ListIterator<String[]> freshmanIter = freshmanTrivialKey.listIterator();

        HashMap<String, Collection<String>> shiftView = new HashMap<>();
        Map<String, Collection<String>> editorMap = new HashMap<>();

        MapStatistics<String, Integer> freshmanViewStat = new MapStatistics<>();
        MapStatistics<String, Integer> freshmanShiftViewStat = new MapStatistics<>();

        PlaceDispatcher editorPlaceDispatcher = new PlaceDispatcher(places);
        new ShiftScheduleProcess(freshmanEditorKey, freshmanEditorText)
                .process((person, shift, state) -> {
                    freshmanShiftViewStat.alter(shift);
                    freshmanViewStat.alter(person);

                    if (state != 0) {
                        //Remove this match in freshman key
                        while (freshmanIter.hasNext()) {
                            String[] ps = freshmanIter.next();
                            if (person.equals(ps[0]) && shift.equals(ps[1])) {
                                freshmanIter.remove();
                            }
                        }

                        //data maintained
                        freshmanShiftViewStat.alter(shift, 1);
                        freshmanViewStat.alter(person, 1);

                    }
                })
                .process(editorPlaceDispatcher)
                .process(((person, shift, state) -> {
                    Arrays.stream(places).forEach(place -> {
                        String shiftWithPlace = shift + separator + place;
                        editorMap.putIfAbsent(shiftWithPlace, new HashSet<>());
                        shiftView.putIfAbsent(shiftWithPlace, new HashSet<>());
                    });
                    if (state != 0) {
                        String dispatch = shift + separator + editorPlaceDispatcher.dispatch(shift);
                        editorMap.get(dispatch).add(person);
                        shiftView.get(dispatch).add(person);
                    }
                }));

        String editorMapStr = editorMap.values().stream().map(s -> s.size() + "").sorted().collect(Collectors.joining(","));
        System.out.println("editorMapStr = " + editorMapStr);

        List<Integer> sophomoreText = new TextDealer(sophomoreTrivialKey, new GeneralIndicator() {

            @Override
            public double[] getIndex() {
                return new double[]{
                        manView.getDistance(2),
                        shiftView.getDistance(4)
                };
            }

            @Override
            public double[] getIdeal() {
                return new double[]{
                        0, -3
                };
            }
        }).run();

        PlaceDispatcher sophomorePlaceDispatcher = new PlaceDispatcher(places);
        Map<String, Collection<String>> sophomoreMap = new HashMap<>();
        new ShiftScheduleProcess(sophomoreTrivialKey, sophomoreText)
                .process(sophomorePlaceDispatcher)
                .process(((person, shift, state) -> {
                    Arrays.stream(places).forEach(p -> {
                        String shiftWithPlace = shift + separator + p;
                        shiftView.putIfAbsent(shiftWithPlace, new HashSet<>());
                        sophomoreMap.putIfAbsent(shiftWithPlace, new HashSet<>());
                    });
                    if (state != 0) {
                        String dispatch = shift + separator + sophomorePlaceDispatcher.dispatch(shift);
                        sophomoreMap.get(dispatch).add(person);
                        //shift view
                        shiftView.get(dispatch).add(person);
                    }
                }));


        String sophomore = sophomoreMap.values().stream().map(s -> s.size() + "").sorted().collect(Collectors.joining(","));
        System.out.println("sophomore = " + sophomore);

        List<Integer> freshmanTrivialText = new TextDealer(freshmanTrivialKey, new GeneralIndicator() {

            @Override
            public double[] getIndex() {
                return new double[]{
                        manView.plus(freshmanViewStat).getVariance(),
                        shiftView.plus(freshmanShiftViewStat).getDistance(8)
                };
            }

            @Override
            public double[] getIdeal() {
                return new double[]{
                        0, -5
                };
            }
        }).run();
        HashMap<String, Collection<String>> manInCharge = new HashMap<>();
        EnhancedDispatcher freshmanDispatcher = new EnhancedDispatcher(places);
        new ShiftScheduleProcess(freshmanTrivialKey, freshmanTrivialText)
                .process(freshmanDispatcher)
                .process(((person, shift, state) -> {
                    Arrays.stream(places).forEach(place -> {
                        String shiftWithPlace = shift + separator + place;
                        shiftView.putIfAbsent(shiftWithPlace, new HashSet<>());
                        manInCharge.putIfAbsent(shiftWithPlace, new HashSet<>());
                    });
                    if (state != 0) {
                        EnhancedDispatcher.Code dispatch = freshmanDispatcher.dispatch(shift);
                        String shiftWithPlace = shift + separator + dispatch.getPlace();
                        shiftView.get(shiftWithPlace).add(person);
                        if (dispatch.isOnCharge()) {
                            manInCharge.get(shiftWithPlace).add(person);
                        }
                    }
                }));
        String freshmanInCharge = manInCharge.values().stream().map(s -> s.size() + "").sorted().collect(Collectors.joining(","));
        System.out.println("freshmanInCharge = " + freshmanInCharge);


        String shiftViewStr = shiftView.values().stream().map(s -> s.size() + "").sorted().collect(Collectors.joining(","));
        System.out.println("shiftViewStr = " + shiftViewStr);

//        manInCharge.forEach((shift, personnel) -> personnel.addAll(sophomoreMap.get(shift)));

        new VosExcel(shiftView, manInCharge, editorMap, weekdays, dayShifts, places).toFile("排班表" + System.nanoTime() % 20);

        timeConsume.printTimeConsumed();
    }

}
