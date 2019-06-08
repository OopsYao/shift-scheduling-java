package ssssuper;

import java.util.Iterator;
import java.util.List;

public class ShiftScheduleProcess {

    private List<String[]> key;

    private List<Integer> text;

    public ShiftScheduleProcess(List<String[]> key, List<Integer> text) {
        if (key.size() != text.size()) try {
            throw new Exception("Not match: key & text");
        } catch (Exception e) {
            e.printStackTrace();
        }
        this.key = key;
        this.text = text;
    }

    public ShiftScheduleProcess process(Processor processor) {
        Iterator<String[]> keyIter = key.iterator();
        Iterator<Integer> txtIter = text.iterator();
        while (keyIter.hasNext() && txtIter.hasNext()) {
            String[] key = keyIter.next();
            processor.iter(key[0], key[1], txtIter.next());
        }
        return this;
    }
}
