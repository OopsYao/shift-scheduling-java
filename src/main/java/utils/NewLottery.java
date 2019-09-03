package utils;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class NewLottery<Key> {

    private Map<Key, Integer> box;

    private int total;

    private int typedRemained;

    private Random random;

    public NewLottery() {
        box = new HashMap<>();
        random = new Random();
    }

    public void add(Key key, int num) {
        if (box.containsKey(key)) {
            box.put(key, num + box.get(key));
        } else {
            box.put(key, num);
            if (num > 0) {
                typedRemained++;
            }
        }
        total += num;
    }

    public void add(Key key) {
        add(key, 1);
    }

    public Key draw() {
        if (total > 0) {
            int r = random.nextInt(total);
            int right = 0, left;
            for (Map.Entry<Key, Integer> entry : box.entrySet()) {
                left = right;
                right += entry.getValue();
                if (r >= left && r < right) {
                    Key key = entry.getKey();
                    int alter = right - left - 1;
                    total--;
                    if (alter == 0) {
                        typedRemained--;
                    }
                    box.put(key, alter);
                    return key;
                }
            }
        }
        return null;
    }

    public int getTypedRemained() {
        return typedRemained;
    }
}

