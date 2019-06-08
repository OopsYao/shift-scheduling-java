package ssssuper.vos.dispatcher;

import ssssuper.vos.ShiftDispatcher;
import utils.NewLottery;

import java.util.HashMap;
import java.util.Map;

public class PlaceDispatcher extends ShiftDispatcher<String> {

    private Map<String, Integer> counter;

    private String[] placeArray;

    public PlaceDispatcher(String[] placeArray) {
        counter = new HashMap<>();
        this.placeArray = placeArray;
    }

    @Override
    public void alterLottery(String shift, NewLottery<String> lottery) {
        Integer c = counter.getOrDefault(shift, 0);
        lottery.add(placeArray[c++]);
        c %= placeArray.length;
        counter.put(shift, c);
    }
}
