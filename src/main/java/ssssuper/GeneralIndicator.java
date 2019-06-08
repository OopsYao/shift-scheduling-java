package ssssuper;

import utils.MapStatistics;

public abstract class GeneralIndicator implements UnitScheduleIndicator {

    protected MapStatistics<String, Integer> manView;

    protected MapStatistics<String, Integer> shiftView;

    protected GeneralIndicator() {
        manView = new MapStatistics<>();
        shiftView = new MapStatistics<>();
    }

    @Override
    public void iter(String person, String shift, Integer state) {
        if (state != 0) {
            manView.alter(person, 1);
            shiftView.alter(shift, 1);
        } else {
            manView.alter(person);
            shiftView.alter(shift);
        }
    }

    @Override
    public void clear() {
        manView.clear();
        shiftView.clear();
    }
}
