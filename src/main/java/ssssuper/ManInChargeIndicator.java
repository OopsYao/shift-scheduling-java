package ssssuper;

import utils.MapStatistic;

public class ManInChargeIndicator implements UnitScheduleIndicator {

    private MapStatistic<String> shiftView;

    public ManInChargeIndicator() {
        shiftView = new MapStatistic<>();
    }

    @Override
    public void iter(String person, String shift, Integer state) {
        if (state == 2) {
            shiftView.alter(shift, 1);
        } else {
            shiftView.alter(shift);
        }
    }

    @Override
    public double[] getIndex() {
        return new double[]{
                shiftView.getDistance(1)
        };
    }

    @Override
    public double[] getIdeal() {
        return new double[]{
                -3
        };
    }

    @Override
    public void clear() {
        shiftView.clear();
    }
}
