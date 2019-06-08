package ssssuper.vos.dispatcher;

import ssssuper.vos.ShiftDispatcher;
import utils.NewLottery;

import java.util.Map;

public class ManInChargeDispatcher extends ShiftDispatcher<Boolean> {

    private Map<String, Boolean> map;

    @Override
    protected void alterLottery(String shift, NewLottery<Boolean> lottery) {
        Boolean has = map.getOrDefault(shift, false);
        if (!has) {
            lottery.add(true);
            map.put(shift, true);
        } else {
            lottery.add(false);
        }
    }
}

