import java.util.Arrays;
import java.util.Random;

class Population {

    public final boolean PRINT_GENERATION_INFO = true;

    private final static String RED_BEGIN = "\033[31m";
    private final static String GREEN_BEGIN = "\033[32m";
    private final static String COLOR_END = "\033[0m";

    private Random rand;

    private int size;
    private String target;
    private int generation = 0;

    private DNA[] population; // genotype is phenotype in this case
    private double[] fitness;
    private int[] translation;
    private double[] incFitness;
    final private int[] increasingNumbers; // [0..(size-1)]

    private int leftSearch(double[] xs, double val) {
        int l = 0, r = xs.length - 1;
        while (l < r) {
            int mid = (l + r) / 2;
            if (xs[mid] >= val) r = mid;
            else l = mid + 1;
        }
        return l;
    }

    private void betterFitness() {
        translation = new int[size];
        translation = Arrays.stream(increasingNumbers).boxed().sorted((i, j) -> Double.compare(fitness[j], fitness[i])).mapToInt(i -> i).toArray();

        incFitness = new double[size];
        int n = size;
        incFitness[0] = 2 * ((double)n - 1 - 0)/((double)n * (n-1));
        for (int i = 1; i < size; i++)
            incFitness[i] = incFitness[i-1] + 2 * ((double)n - 1 - i)/((double)n * (n-1));
    }

    private DNA pickMate() {
        double max = incFitness[size-1],
               pick = rand.nextDouble() * max;
        return population[translation[leftSearch(incFitness, pick)]];
    }

    Population(int _size, String _target) {
        rand = new Random();
        size = _size;
        target = _target;

        population = new DNA[size];
        fitness = new double[size];


        increasingNumbers = new int[size];
        // generation 0 is just random specimen
        for (int i = 0; i < size; i++) {
            increasingNumbers[i] = i;
            population[i] = new DNA(target);
            fitness[i] = population[i].fitness();
        }

        betterFitness();
    }

    private String colorCoded(String bestS) {
        StringBuilder str = new StringBuilder();
        for (int i = 0; i < bestS.length(); i++) {
            str.append(target.charAt(i) == bestS.charAt(i) ? GREEN_BEGIN : RED_BEGIN);
            str.append(bestS.charAt(i));
            str.append(COLOR_END);

        }
        return str.toString();
    }

    private boolean nextGeneration() {
        boolean foundTheTarget = false;
        DNA[] newPopulation = new DNA[size];

        int best_fitness_idx = 0; // for printing the best specimen in a generation
        double best_fitness = fitness[0];

        for (int i = 0; i < size; i++) {
            DNA parentA = pickMate(),
                parentB = pickMate(),
                child = parentA.cross(parentB);
            double cf = child.fitness() / size;

            if (child.toString().equals(target)) { // success
                foundTheTarget = true;
                if (PRINT_GENERATION_INFO)
                    System.out.println("gen " + (generation + 1) + " SUCCESS: <" + GREEN_BEGIN + child.toString() + COLOR_END + ">");
            }

            if (cf > best_fitness) { // keep searching for the best specimen in the generation
                best_fitness_idx = i;
                best_fitness = cf;
            }

            newPopulation[i] = child; // current population cannot be overriten now since i need it to call pickMate()
            fitness[i] = cf; // fitness can be overiden now
        }

        betterFitness();

        population = newPopulation;
        generation++;
        if (PRINT_GENERATION_INFO && !foundTheTarget) System.out.println("gen " + generation + ": " + colorCoded(population[best_fitness_idx].toString()));
        return foundTheTarget;
    }

    void search() {
        long start = System.currentTimeMillis();
        while (!nextGeneration())
            if (generation % 100 == 0) System.out.println();
        long end = System.currentTimeMillis();
        System.out.print("Done in " + generation + " generations and in " + ((end - start) / 1000.0) + " seconds ");
        System.out.println("(" + String.format("%.2f", (generation / ((end-start) / 1000.0))) + " generations per second)!");
    }
}
