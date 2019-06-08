package ssssuper;

import utils.MapStatistic;

import java.util.Map;

public class ShiftScheduleStat {

    private MapStatistic<String> shiftView;

    private MapStatistic<String> manView;

    public void add(String person, String shift, Integer state) {
        if (state != 0) {
            shiftView.alter(shift, 1);
            manView.alter(person, 1);
        } else {
            shiftView.alter(shift);
            manView.alter(person);
        }
    }

    public Map<String, ? extends Number> getManViewStatMap() {
        return manView.getMap();
    }

    public Map<String, ? extends Number> getShiftViewStatMap() {
        return shiftView.getMap();
    }
}
