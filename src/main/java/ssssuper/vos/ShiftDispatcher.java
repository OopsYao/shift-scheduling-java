package ssssuper.vos;

import ssssuper.Processor;
import utils.NewLottery;

import java.util.HashMap;
import java.util.Map;

public abstract class ShiftDispatcher<LotResult> implements Processor {

    protected Map<String, NewLottery<LotResult>> lotteryMap;

    public ShiftDispatcher() {
        lotteryMap = new HashMap<>();
    }

    protected abstract void alterLottery(String shift, NewLottery<LotResult> lottery);

    @Override
    public void iter(String person, String shift, Integer state) {
        lotteryMap.putIfAbsent(shift, new NewLottery<>());
        if (state != 0)
            alterLottery(shift, lotteryMap.get(shift));
    }

    public LotResult dispatch(String shift) {
        return lotteryMap.get(shift).draw();
    }
}
