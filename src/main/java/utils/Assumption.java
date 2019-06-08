package utils;

import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;

public class Assumption {

    private List<Number> list;

    private double $1stMom;

    private double $2ndMom;

    private boolean isUpdated;

    public Assumption() {
        list = new LinkedList<>();
    }

    public void add(Number x) {
        ListIterator<Number> iter = list.listIterator();
        while (iter.hasNext()) {
            if (x.doubleValue() < iter.next().doubleValue()) {
                iter.previous();
                iter.add(x);
                isUpdated = false;
                return;
            }
        }
        iter.add(x);
        isUpdated = false;
    }

    public double getMean() {
        if (isUpdated) return $1stMom;
        alter();
        return getMean();
    }

    public double getVariance() {
        if (isUpdated) return $2ndMom - $1stMom * $1stMom;
        alter();
        return getVariance();
    }

    public double getSampleVariance() {
        return list.size() * getVariance() / (list.size() - 1);
    }

    private void alter() {
        if (list.size() == 0) return;
//        BigDecimal sum1 = new BigDecimal(0);
//        BigDecimal sum2 = new BigDecimal(0);
        double sum1 = 0;
        double sum2 = 0;
        for (Number number : list) {
            double x = number.doubleValue();
            sum1 += x;
            sum2 += x * x;
//            BigDecimal x = new BigDecimal(number.toString());
//            sum1 = sum1.iter(x);
//            sum2 = sum2.iter(x.multiply(x));
        }
        $1stMom = sum1 / list.size();
        $2ndMom = sum2 / list.size();
//        BigDecimal n = new BigDecimal(list.size());
//        BigDecimal mu1 = sum1.divide(n, RoundingMode.HALF_EVEN);
//        BigDecimal mu2 = sum2.divide(n, RoundingMode.HALF_EVEN);
//        $1stMom = mu1.doubleValue();
//        $2ndMom = mu2.doubleValue();
        isUpdated = true;
    }
}
