package utils;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * An imitation of sampling without replacement
 */
public class Lottery {

    private Map<Integer, Integer> lotteryBox;
    private int typeRemained;

    public Lottery() {
        lotteryBox = new HashMap<>();
    }

    public Lottery(List<Integer> list){
        lotteryBox = new HashMap<>();
        for (int x : list) {
            add(x);
        }
    }

    public void add(int size) {
        if (size <= 0) {
            return;
        }
        lotteryBox.put(lotteryBox.size(), size);
        typeRemained++;
    }

    public int draw() {
        if (typeRemained == 0) {
            return -1;
        }
        RouletteWheel rouletteWheel = new RouletteWheel(new LinkedList<>(lotteryBox.values()));
        int spin = rouletteWheel.spin();
        lotteryBox.put(spin, lotteryBox.get(spin) - 1);
        if (lotteryBox.get(spin) == 0) {
            typeRemained--;
        }
        return spin;
    }
}
