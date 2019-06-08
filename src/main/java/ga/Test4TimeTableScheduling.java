package ga;

import model.Schedule;
import model.ScheduleAnalyser;
import utils.Mahalanobis;
import utils.TimeConsume;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class Test4TimeTableScheduling {
    public static void main(String[] args) {
        TimeConsume timeConsume = new TimeConsume();
        final ScheduleAnalyser scheduleAnalyser = new ScheduleAnalyser();
        GeneticAlgorithm<Schedule> geneticAlgorithm = new GeneticAlgorithm<>() {
            @Override
            protected Map<Integer, Integer> getSelection(List<Schedule> population, int currentGeneration) {
                List<double[]> indexList = new LinkedList<>();
                Mahalanobis mahalanobis = new Mahalanobis();
                for (Schedule schedule : population) {
                    double[] index = scheduleAnalyser.getIndex(schedule);
                    indexList.add(index);
                    mahalanobis.add(index);
                }

                double[] ideal = scheduleAnalyser.getIdeal();
                LinkedList<Double> fitness = new LinkedList<>();
                for (double[] index : indexList) {
                    double distance = mahalanobis.getDistance(index, ideal);
                    double v = 1 / distance;
                    fitness.add(v);
                }
                return rouletteWheelSelection(fitness);
            }

            @Override
            protected ArrayList<Integer> getCoupleSchedule(List<Schedule> population) {
                return getCoupleScheduleRandomly();
            }

            @Override
            protected void crossover(Schedule individualA, Schedule individualB) {
                twoPointCrossover(individualA.getCore(), individualB.getCore());
            }

            @Override
            protected void mutate(Schedule schedule) {
                scheduleAnalyser.mutate(schedule);
            }

            @Override
            protected Schedule generateRandomIndividual() {
                return scheduleAnalyser.generateScheduleRandomly();
            }

            @Override
            protected Schedule getOffspring(Schedule parent) {
                return scheduleAnalyser.clone(parent);
            }

        };
        geneticAlgorithm.setGeneration(2000);
        geneticAlgorithm.setScale(1000);

        //Output
        Schedule output = geneticAlgorithm.output();
        output.consoleLog();
        output.toFile("C:/Users/affgs/Desktop", "old-std-var");

        timeConsume.printTimeConsumed();
    }
}