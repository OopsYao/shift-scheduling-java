package utils;

import java.util.Collection;

public class UnaryStatistic {
    private int n;
    private double $1stMom;
    private double $2ndMom;

    public void add(Number x) {
        double d = x.doubleValue();
        n++;
        $1stMom += (d - $1stMom) / n;
        $2ndMom += (d * d - $2ndMom) / n;
    }

    public void alter(Number ori, Number delta) {
        if (n == 0) return;
        double oriVal = ori.doubleValue();
        double delVal = delta.doubleValue();
        $1stMom += delVal / n;
        $2ndMom += delVal * (2 * oriVal + delVal) / n;
    }

    public UnaryStatistic(Collection<? extends Number> collection) {
        for (Number x : collection) {
            add(x);
        }
    }

    public UnaryStatistic() {}

    public double getMean() {
        return $1stMom;
    }

    public double getVariance() {
        return $2ndMom - $1stMom * $1stMom;
    }

    public double getStdVariance() {
        return Math.sqrt(getVariance());
    }

    public double getSampleVariance() {
        return n * getVariance() / (n - 1);
    }
}
