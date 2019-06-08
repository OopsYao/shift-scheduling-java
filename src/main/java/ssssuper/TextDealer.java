package ssssuper;

import ga.GeneticAlgorithm;
import utils.Mahalanobis;

import java.util.*;

public class TextDealer {

    private int keySize;

    private ScheduleComputer indicator;

    private GeneticAlgorithm<List<Integer>> geneticAlgorithm;

    public TextDealer(List<String[]> key, UnitScheduleIndicator... indicator) {
        this.keySize = key.size();
        this.indicator = new ScheduleComputer(key, indicator);
        init();
    }

    public List<Integer> run() {
        return geneticAlgorithm.output();
    }

    private void init() {
        geneticAlgorithm = new GeneticAlgorithm<>() {
            @Override
            protected Map<Integer, Integer> getSelection(List<List<Integer>> population, int currentGeneration) {
                Mahalanobis mahalanobis = new Mahalanobis();
                List<double[]> indexList = new LinkedList<>();
                //Construction of sample space
                for (List<Integer> text : population) {
                    double[] index = indicator.getIndex(text);
                    mahalanobis.add(index);
                    indexList.add(index);
                }

                List<Double> fitness = new LinkedList<>();
                double[] ideal = indicator.getIdeal();
                for (double[] index : indexList) {
                    double distance = mahalanobis.getDistance(index, ideal);
                    fitness.add(1 / distance);
                }
                return rouletteWheelSelection(fitness);
            }

            @Override
            protected ArrayList<Integer> getCoupleSchedule(List<List<Integer>> population) {
                return getCoupleScheduleRandomly();
            }

            @Override
            protected void crossover(List<Integer> individualA, List<Integer> individualB) {
                twoPointCrossover(individualA, individualB);
            }

            @Override
            protected void mutate(List<Integer> integers) {
                Random random = new Random();
                int p = random.nextInt(integers.size());
                ListIterator<Integer> iterator = integers.listIterator();
                int count = 0;
                while (iterator.hasNext()) {
                    Integer ori = iterator.next();
                    if (count == p) {
                        iterator.set(1 - ori);
//                        int now;
//                        do {
//                            now = random.nextInt(3);
//                        } while (now == ori);
//                        iterator.set(now);
                        break;
                    }
                    count++;
                }

            }

            @Override
            protected List<Integer> generateRandomIndividual() {
                LinkedList<Integer> text = new LinkedList<>();
                Random random = new Random();
                for (int i = 0; i < keySize; i++) {
                    text.add(random.nextInt(2));
                }
                return text;
            }

            @Override
            protected List<Integer> getOffspring(List<Integer> parent) {
                return new LinkedList<>(parent);
            }
        };
        geneticAlgorithm.setScale(2000);
        geneticAlgorithm.setGeneration(1000);
    }
}
