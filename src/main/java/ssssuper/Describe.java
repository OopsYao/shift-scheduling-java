package ssssuper;

import utils.MapStatistic;

import java.util.TreeSet;

public class Describe implements Processor {

    private MapStatistic<String> shiftView;

    private MapStatistic<String> manView;

    public Describe() {
        shiftView = new MapStatistic<>();
        manView = new MapStatistic<>();
    }

    @Override
    public void iter(String person, String shift, Integer state) {
        if (state != 0) {
            shiftView.alter(shift, 1);
            manView.alter(person, 1);
        } else {
            shiftView.alter(shift);
            manView.alter(person);
        }
    }

    public void print() {
        System.out.println("Shift view: " + print(shiftView));
        System.out.println("Man view: " + print(manView));
    }

    private String print(MapStatistic<?> stat) {
        TreeSet<Integer> integers = new TreeSet<>();
        stat.getMap().values().forEach(x -> integers.add(x.intValue()));
        return integers.toString();
    }
}
