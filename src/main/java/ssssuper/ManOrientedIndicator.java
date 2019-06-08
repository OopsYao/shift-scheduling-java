package ssssuper;

public class ManOrientedIndicator extends GeneralIndicator {

    private int idealArrangedTimes;

    public ManOrientedIndicator(int idealArrangedTimes) {
        this.idealArrangedTimes = idealArrangedTimes;
    }

    @Override
    public double[] getIndex() {
        return new double[]{
                manView.getDistance(idealArrangedTimes),
                shiftView.getVariance()
        };
    }

    @Override
    public double[] getIdeal() {
        return new double[]{
                -1, 0
        };
    }
}
