package model;

import ssssuper.UnitScheduleIndicator;
import utils.TimeConsume;
import utils.UnaryStatistic;

import java.util.*;

public class ScheduleAnalyser {

    private double[] ideal;

    private List<UnitScheduleIndicator> indicatorList;

    public ScheduleAnalyser() {
        ideal = new double[]{0, 2, 0};
    }

    public double[] getIdeal() {
        return ideal;
    }

    public double[] getIndex(Schedule schedule) {
        TimeConsume timeConsume = new TimeConsume();
        //Two maps construction
        List<Boolean> core = schedule.getCore();
        List<String[]> availableAppointments = Schedule.getAvaList();


        Map<String, Integer> times = new HashMap<>();
        Map<String, Integer> sizes = new HashMap<>();

        UnaryStatistic timesStat = new UnaryStatistic();
        UnaryStatistic sizeStat = new UnaryStatistic();

        Iterator<String[]> avaIter = availableAppointments.iterator();
        Iterator<Boolean> corIter = core.iterator();


        while (avaIter.hasNext() && corIter.hasNext()) {
            String[] ava = avaIter.next();
            String p = ava[0];
            String s = ava[1];
            Boolean state = corIter.next();

            if (!times.containsKey(p)) {
                times.put(p, 0);
                timesStat.add(0);
            }
            if (!sizes.containsKey(s)) {
                sizes.put(s, 0);
                sizeStat.add(0);
            }

            if (state) {
                int oriTimes = times.get(p);
                int oriSize = sizes.get(s);

                times.put(p, 1 + oriTimes);
                sizes.put(s, 1 + oriSize);

                timesStat.alter(oriTimes, 1);
                sizeStat.alter(oriSize, 1);
            }
        }

        double timesMean = timesStat.getMean();
        double timesVar = timesStat.getStdVariance();
        double sizeVar = sizeStat.getStdVariance();
//        double timesVar = timesStat.getVariance();
//        double sizeVar = sizeStat.getVariance();


        double[] index= {timesVar, timesMean, sizeVar};

        timeConsume.printTimeConsumed();
        return index;
    }

    public Schedule clone(Schedule schedule) {
        return new Schedule(new LinkedList<>(schedule.getCore()));
    }

    public Schedule generateScheduleRandomly() {
        List<Boolean> core = new LinkedList<>();
        Random random = new Random();
        int size = Schedule.getAvaList().size();
        for (int i = 0; i < size; i++) {
            core.add(random.nextBoolean());
        }
        return new Schedule(core);
    }

    public void mutate(Schedule schedule) {
        int size = Schedule.getAvaList().size();
        Random random = new Random();
        int p = random.nextInt(size);

        ListIterator<Boolean> iterator = schedule.getCore().listIterator();
        int count = 0;
        while (iterator.hasNext()) {
            boolean ori = iterator.next();
            if (count == p) {
                iterator.set(!ori);
                break;
            }
            count++;
        }
    }
}
