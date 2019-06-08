package utils;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class MapStatistic<Key> {

    private Double $1stMom;

    private Double $2ndMom;

    private boolean updated;

    private Map<Key, Double> map;

    public MapStatistic() {
        map = new HashMap<>();
    }

    public void alter(Key key, Number delta) {
        if (delta.doubleValue() == 0) {
            map.putIfAbsent(key, 0D);
        } else {
            map.put(key, map.getOrDefault(key, 0D) + delta.doubleValue());
        }
        updated = false;
    }

    public void alter(Key key) {
        map.putIfAbsent(key, 0D);
    }

    public void clear() {
        map.clear();
        updated = false;
    }

    public void plus(MapStatistic<Key> mapStatistic) {
        Map<Key, ? extends Number> deltaMap = mapStatistic.getMap();
        Set<Key> set = new HashSet<>(deltaMap.keySet());
        set.forEach(key -> {
            double ori = this.map.getOrDefault(key, 0D);
            this.map.put(key, ori + deltaMap.get(key).doubleValue());
        });
        updated = false;
    }

    public double getVariance() {
        if (!updated) compute();
        return $2ndMom - $1stMom * $1stMom;
    }

    public double getMean() {
        if (!updated) compute();
        return $1stMom;
    }

    public double getDistance(Map<Key, ? extends Number> point, Number absentValue) {
        double d = 0;
        Set<Key> set = new HashSet<>(map.keySet());
        set.addAll(point.keySet());
        for (Key key : set) {
            double a = map.getOrDefault(key, 0D);
            double b;
            Number numberB;
            if (point.size() == 0 || null == (numberB = point.get(key))) {
                b = absentValue.doubleValue();
            } else {
                b = numberB.doubleValue();
            }
            d += (a - b) * (a - b);
        }
        return d;
    }

    public double getDistance(Map<Key, ? extends Number> point) {
        return getDistance(point, 0);
    }

    public double getDistance(Number absentValue) {
        return getDistance(new HashMap<>(), absentValue);
    }

    private void compute() {
        $1stMom = 0D;
        $2ndMom = 0D;
        map.values().stream().mapToDouble(Double::doubleValue).forEach(x -> {
            $1stMom += x;
            $2ndMom += x * x;
        });
        $1stMom /= map.size();
        $2ndMom /= map.size();
        updated = true;
    }

    public Map<Key, ? extends Number> getMap() {
        return map;
    }

}

