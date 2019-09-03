package ssssuper;

import utils.MapStatistics;

public class ShiftOrientedIndicator extends GeneralIndicator {

    private int idealShiftSize;

    public ShiftOrientedIndicator(int idealShiftSize) {
        this.idealShiftSize = idealShiftSize;
    }

    protected void preprocess(MapStatistics<String, Integer> manView, MapStatistics<String, Integer> shiftView) {

    }

    @Override
    public double[] getIdeal() {
        return new double[]{
                0, -1
        };
    }

    @Override
    public double[] getIndex() {
        preprocess(manView, shiftView);
        return new double[]{
                manView.getVariance(),
                shiftView.getDistance(idealShiftSize)
        };
    }
}
