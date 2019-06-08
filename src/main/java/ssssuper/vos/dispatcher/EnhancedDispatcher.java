package ssssuper.vos.dispatcher;


import ssssuper.vos.ShiftDispatcher;
import utils.NewLottery;

import java.util.HashMap;
import java.util.Map;

public class EnhancedDispatcher extends ShiftDispatcher<EnhancedDispatcher.Code> {

    private Map<String, Boolean> hasMap;

    private Map<String, Integer> counterMap;

    private String[] places;

    public EnhancedDispatcher(String[] places) {
        this.places = places;
        hasMap = new HashMap<>();
        counterMap = new HashMap<>();
    }

    @Override
    protected void alterLottery(String shift, NewLottery<Code> lottery) {
        Integer c = counterMap.getOrDefault(shift, 0);
        lottery.add(new Code(!hasMap.getOrDefault(shift, false), places[c % places.length]));
        if (++c >= places.length)
            hasMap.put(shift, true);
        counterMap.put(shift, c);
    }

    public static class Code {

        private boolean onCharge;

        private String place;

        public Code(boolean onCharge, String place) {
            this.onCharge = onCharge;
            this.place = place;
        }

        public boolean isOnCharge() {
            return onCharge;
        }

        public String getPlace() {
            return place;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj instanceof Code) {
                return ((Code) obj).onCharge == this.onCharge && ((Code) obj).place.equals(this.place);
            } else {
                return false;
            }
        }
    }
}
