package ssssuper;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class ScheduleComputer {

    private UnitScheduleIndicator[] indicatorArray;

    private List<String[]> key;

    private double[] ideal;

    public ScheduleComputer(List<String[]> key, UnitScheduleIndicator... indicators) {
        this.indicatorArray = indicators;
        this.key = key;

        idealInit();
    }


    public double[] getIndex(List<Integer> text) {
        //Loop alter
        Iterator<String[]> iterKey = key.iterator();
        Iterator<Integer> iterText = text.iterator();
        while (iterKey.hasNext() && iterText.hasNext()) {
            String[] pair = iterKey.next();
            Integer state = iterText.next();
            iter(pair[0], pair[1], state);
        }

        //Cal and clear
        double[] finalIndex = new double[ideal.length];
        int counter = 0;
        for (UnitScheduleIndicator indicator : indicatorArray) {
            double[] index = indicator.getIndex();
            indicator.clear();
            for (double i : index) {
                finalIndex[counter++] = i;
            }
        }
        return finalIndex;
    }

    public double[] getIdeal() {
        return ideal;
    }

    private void idealInit() {
        LinkedList<Double> idealList = new LinkedList<>();
        for (UnitScheduleIndicator indicator : indicatorArray) {
            double[] ideal = indicator.getIdeal();
            for (double i : ideal) {
                idealList.add(i);
            }
        }
        this.ideal = new double[idealList.size()];
        int counter = 0;
        for (double i : idealList) {
            ideal[counter++] = i;
        }
    }

    private void iter(String person, String shift, Integer state) {
        for (UnitScheduleIndicator indicator : indicatorArray) {
            indicator.iter(person, shift, state);
        }
    }
}
