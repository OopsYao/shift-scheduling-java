package utils;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

public class RouletteWheel {

    private List<Double> probabilities;
    private List<Number> patterns;

    /**
     * Identify whether the probabilities is the latest
     */
    private boolean isLocked;

    public RouletteWheel() {
        init();
    }

    public RouletteWheel(List<? extends Number> patterns) {
        init();
        for (Number p : patterns) {
            this.patterns.add(p.doubleValue());
        }
        alterProbabilities();
    }

    public void addPattern(Number pattern) {
        if (pattern instanceof BigDecimal || pattern instanceof BigInteger) {

        }
        double p = pattern.doubleValue();
        if (p < 0) {
            return;
        }
        patterns.add(p);
        isLocked = false;
    }

    /**
     * @return The random result
     */
    public int spin() {
        double r = new Random().nextDouble();
        if (!isLocked) {
            alterProbabilities();
        }
        Iterator<Double> iterator = probabilities.iterator();

        double len = 0;
        int index = 0;
        while (iterator.hasNext()) {
            len += iterator.next();
            if (r < len) {
                break;
            }
            index++;
        }
        return index;
    }

    private void alterProbabilities() {
        probabilities.clear();

        boolean big = false;

        double total = 0;
        for (Number d : patterns) {
            if (d instanceof BigInteger || d instanceof BigDecimal) {
                big = true;
                break;
            } else {
                total += d.doubleValue();
            }
            if (total == Double.POSITIVE_INFINITY) {
                big = true;
                break;
            }
        }

        if (big) {
            System.out.println("BIG!!!");
            BigDecimal totalBig = new BigDecimal(0);
            for (Number d : patterns) {
                BigDecimal dd = getBigDecimal(d);
                totalBig = totalBig.add(dd);
            }
            for (Number d : patterns) {
                BigDecimal dd = getBigDecimal(d);
                BigDecimal pp = dd.divide(totalBig, 14, RoundingMode.HALF_EVEN);
                probabilities.add(pp.doubleValue());
            }
        } else {
            for (Number d : patterns) {
                probabilities.add(d.doubleValue() / total);
            }
        }
        isLocked = true;
    }

    private void init() {
        probabilities = new LinkedList<>();
        patterns = new LinkedList<>();
    }

    private BigDecimal getBigDecimal(Number x) {
        BigDecimal xx;
        if (x instanceof BigDecimal) {
            xx = ((BigDecimal) x);
        } else if (x instanceof BigInteger) {
            xx = new BigDecimal(((BigInteger) x));
        } else {
            xx = new BigDecimal(x.doubleValue());
        }
        return xx;
    }
}