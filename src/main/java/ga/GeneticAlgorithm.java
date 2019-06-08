package ga;

import utils.RouletteWheel;

import java.util.*;

public abstract class GeneticAlgorithm<Individual> {

    /**
     * Current population in a generation(iteration)
     */
    private List<Individual> population;

    /**
     * The scale of the population
     */
    private int scale;

    /**
     * Expected time of iteration
     */
    private int generation;

    /**
     * The probability of a crossover
     */
    private double alpha;

    /**
     * The probability of a mutation
     */
    private double beta;

    private int currentGeneration;

    public void setGeneration(int generation) {
        if (generation > 0) {
            this.generation = generation;
        }
    }

    public void setScale(int scale) {
        if (scale > 0) {
            this.scale = scale;
        }
    }

    /**
     * A complete config
     *
     * @param alpha      Probability of a crossover
     * @param beta       Probability of a mutation
     * @param generation The total generation
     * @param scale      The size of the population
     */
    public GeneticAlgorithm(double alpha, double beta, int generation, int scale) {
        setParameter(alpha, beta, generation, scale);
    }

    /**
     * A default config
     */
    public GeneticAlgorithm() {
        setParameter(0.7, 0.1, 1000, 1000);
    }

    /**
     * @return The fittest individual in the latest generation(iteration)
     */
    public Individual output() {
        run();

        Map<Integer, Integer> selection = getSelection(population, currentGeneration);
        int max = 0;
        int maxIndex = 0;
        for (Map.Entry<Integer, Integer> entry : selection.entrySet()) {
            int val = entry.getValue();
            if (val > max) {
                maxIndex = entry.getKey();
                max = val;
            }
        }

        return population.get(maxIndex);
    }

    /**
     * Start!!!
     */
    private void run() {
        //Initialization -- getting the primitive population
        init();

        //In one generation(iteration)
        for (int i = 0; i < generation; i++) {
            currentGeneration++;
            //Selection
            select();

            //Crossover
            ArrayList<Integer> schedule = getCoupleSchedule(population);
            for (int j = 0; j + 1 < schedule.size(); j += 2) {
                if (new Random().nextDouble() < alpha) {
                    crossover(population.get(schedule.get(j)), population.get(schedule.get(j + 1)));
                }
            }

            //Mutation
            for (Individual individual : population) {
                if (new Random().nextDouble() < beta) {
                    mutate(individual);
                }
            }
        }
    }

    /**
     * Initialize the primitive population
     */
    private void init() {
        population = new LinkedList<>();
        for (int i = 0; i < scale; i++) {
            population.add(generateRandomIndividual());
        }
    }

    /**
     * Selection
     */
    private void select() {
        Map<Integer, Integer> selection = getSelection(population, currentGeneration);
        LinkedList<Individual> offspring = new LinkedList<>();
        for (Map.Entry<Integer, Integer> entry : selection.entrySet()) {
            int num = entry.getValue();
            int parentIndex = entry.getKey();
            for (int i = 0; i < num; i++) {
                offspring.add(getOffspring(population.get(parentIndex)));
            }
        }
        population = offspring;
    }

    /**
     * Can be viewed as the meta-constructor
     *
     * @param alpha      The probability of a crossover
     * @param beta       The probability of a mutation
     * @param generation The total generations
     * @param size       The scale of the population
     */
    private void setParameter(double alpha, double beta, int generation, int size) {
        this.alpha = alpha;
        this.beta = beta;
        this.generation = generation;
        this.scale = size;
    }

    private Map<Integer, Integer> generatingByRouletteWheel(List<Double> pattern) {
        RouletteWheel rouletteWheel = new RouletteWheel(pattern);
//        ArrayList<Integer> schedule = new ArrayList<>();
        HashMap<Integer, Integer> schedule = new HashMap<>();
        for (int i = 0; i < pattern.size(); i++) {
            int spin = rouletteWheel.spin();
            schedule.put(spin, 1 + schedule.getOrDefault(spin, 0));
//            schedule.iter(spin);
        }
        return schedule;
    }

    //Abstract part

    protected abstract Map<Integer, Integer> getSelection(List<Individual> population, int currentGeneration);

    protected abstract ArrayList<Integer> getCoupleSchedule(List<Individual> population);

    protected abstract void crossover(Individual individualA, Individual individualB);

    protected abstract void mutate(Individual individual);

    /**
     * Randomly generate an individual
     *
     * @return An individual
     */
    protected abstract Individual generateRandomIndividual();

    /**
     * Copy
     *
     * @param parent The template
     * @return The copy
     */
    protected abstract Individual getOffspring(Individual parent);

    //library
    protected ArrayList<Integer> getCoupleScheduleRandomly() {
        ArrayList<Integer> schedule = new ArrayList<>();
        for (int i = 0; i < scale; i++) {
            schedule.add(i);
        }
        Collections.shuffle(schedule);
        return schedule;
    }

    protected Map<Integer, Integer> rouletteWheelSelection(List<Double> fitness) {
        //Generating the population
        return generatingByRouletteWheel(fitness);
    }

    protected Map<Integer, Integer> linearRankSelection(Comparator<Individual> comparator) {
        List<Double> ranks = new LinkedList<>();
        for (Individual individual : population) {
            double r = 1;
            int s = 0;
            for (Individual compared : population) {
                boolean equals = individual.equals(compared);
                int compare = comparator.compare(compared, individual);
                if (!equals && compare < 0) {
                    r++;
                }
                if (!equals && compare == 0) {
                    s++;
                }
            }
            r += (s - 1) / 2d;
            ranks.add(r);
        }
        return generatingByRouletteWheel(ranks);
    }

    ArrayList<Integer> exponentialRankSelection(ArrayList<Integer> rank) {
        return null;
    }

    ArrayList<Integer> tournamentSelection() {
        return null;
    }

    protected void twoPointCrossover(List geneA, List geneB) {

        int sizeA = geneA.size();
        int sizeB = geneB.size();
        int size = sizeA > sizeB ? sizeB : sizeA;


        Random random = new Random();
        int changeInterval = 1 + random.nextInt(size);
        int start = random.nextInt(1 + size);
        int end = random.nextInt(1 + size);

        if (start > end) {
            int tmp = start;
            start = end;
            end = tmp;
        }

        List sl1 = new LinkedList<>(geneA.subList(start, end));
        List sl2 = new LinkedList<>(geneB.subList(start, end));

        slice(geneA, sl2, start, end);
        slice(geneB, sl1, start, end);
    }

    protected void singlePointMutate(List<?> list) {
    }

    private void slice(List l, List sl, int start, int end) {
        List slStart = new LinkedList(l.subList(0, start));
        List slEnd = new LinkedList<>(l.subList(end, l.size()));

        l.clear();
        l.addAll(slStart);
        l.addAll(sl);
        l.addAll(slEnd);
    }
}