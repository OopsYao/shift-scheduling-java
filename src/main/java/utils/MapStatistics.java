package utils;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

public class MapStatistics<Key, Num extends Number> {

    private Num zero;

    private Map<Key, Num> map;

    public MapStatistics() {
        map = new HashMap<>();
        zero = convert(0);
    }

    public void alter(Key key, Num delta) {
        if (delta.doubleValue() == 0) {
            map.putIfAbsent(key, delta);
        } else {
            map.put(key, plus(delta, map.getOrDefault(key, zero)));
        }
    }

    public void alter(Key key) {
        alter(key, zero);
    }

    public double getMean() {
        return map.values().stream().mapToDouble(Number::doubleValue).average().orElse(0);
    }

    public Num getQuadraticSum() {
        return convert(map.values().stream().mapToDouble(x -> x.doubleValue() * x.doubleValue()).sum());
    }

    public double getVariance() {
        double mean = getMean();
        double meanSquare = mean * mean;
        return map.values().stream().mapToDouble(x -> x.doubleValue() * x.doubleValue() - meanSquare).average().orElse(0);
    }

    public MapStatistics<Key, Num> plus(Map<Key, Num> addendMap, Num absentValue) {
        HashSet<Key> keySet = new HashSet<>(map.keySet());
        if (addendMap != null)
            keySet.addAll(addendMap.keySet());
        keySet.forEach(key -> {
            Num addend;
            if (addendMap == null || (addend = addendMap.get(key)) == null) {
                addend = absentValue;
            }
            map.put(key, plus(map.getOrDefault(key, zero), addend));
        });
        return this;
    }

    public MapStatistics<Key, Num> plus(MapStatistics<Key, Num> addend, Num absentValue) {
        return plus(addend.map, absentValue);
    }

    public MapStatistics<Key, Num> plus(MapStatistics<Key, Num> addend) {
        return plus(addend.map, zero);
    }

    public MapStatistics<Key, Num> plus(Num absentValue) {
        return plus((Map<Key, Num>) null, absentValue);
    }

    public double getDistance(Map<Key, ? extends Number> targetMap, Number absentValue) {
        HashSet<Key> keySet = new HashSet<>(map.keySet());
        if (targetMap != null)
            keySet.addAll(targetMap.keySet());
        return keySet.stream().mapToDouble(key -> {
            double target = absentValue.doubleValue();
            if (targetMap != null && targetMap.containsKey(key)) {
                target = targetMap.get(key).doubleValue();
            }
            double dist = map.getOrDefault(key, zero).doubleValue() - target;
            return dist * dist;
        }).sum();
    }

    public double getDistance(MapStatistics<Key, ? extends Number> target, Number absentValue) {
        return getDistance(target.map, absentValue);
    }

    public double getDistance(Map<Key, ? extends Number> targetMap) {
        return getDistance(targetMap, 0);
    }

    public double getDistance(MapStatistics<Key, ? extends Number> target) {
        return getDistance(target, 0);
    }

    public double getDistance(Number absentValue) {
        return getDistance((Map<Key, ? extends Number>) null, absentValue);
    }

    public Map<Key, Num> getMap() {
        return map;
    }

    public void clear() {
        map.clear();
    }

    @SuppressWarnings("unchecked")
    private Num convert(Number x) {
        return (Num) x;
    }

    private Num plus(Num x, Num y) {
        Number z;
        if (x.getClass() == Integer.class) {
            z = x.intValue() + y.intValue();
        } else if (x.getClass() == Float.class) {
            z = x.floatValue() + y.floatValue();
        } else if (x.getClass() == Long.class) {
            z = x.longValue() + y.longValue();
        } else if (x.getClass() == Short.class) {
            z = x.shortValue() + y.shortValue();
        } else if (x.getClass() == Byte.class) {
            z = x.byteValue() + y.byteValue();
        } else {
            z = x.doubleValue() + y.doubleValue();
        }
        return convert(z);
    }
}

