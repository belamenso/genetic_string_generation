import java.util.Random;

class Population {
    private final static String RED_BEGIN = "\033[31m";
    private final static String GREEN_BEGIN = "\033[32m";
    private final static String COLOR_END = "\033[0m";

    private Random rand;

    private int size;
    private String target;
    private int generation = 0;

    private DNA[] population;
    private double[] fitness;
    private double[] incFitness; // optimization

    private int leftSearch(double[] xs, double val) {
        int l = 0, r = xs.length - 1;
        while (l < r) {
            int mid = (l + r) / 2;
            if (xs[mid] >= val) r = mid;
            else l = mid + 1;
        }
        return l;
    }

    private DNA pickMate() {
        double max = incFitness[size-1],
               pick = rand.nextDouble() * max;
        return population[leftSearch(incFitness, pick)];
    }

    Population(int _size, String _target) {
        rand = new Random();
        size = _size;
        target = _target;

        population = new DNA[size];
        fitness = new double[size];
        incFitness = new double[size];

        for (int i = 0; i < size; i++) {
            population[i] = new DNA(target);
            fitness[i] = population[i].fitness();
        }
        incFitness[0] = fitness[0];
        for (int i = 1; i < size; i++) incFitness[i] = incFitness[i-1] + fitness[i];
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
        boolean done_it = false;
        int best = 0;
        double best_fitness = fitness[0];
        DNA[] newPopulation = new DNA[size];
        for (int i = 0; i < size; i++) {
            DNA parentA = pickMate();
            DNA parentB = pickMate();
            DNA child = parentA.cross(parentB);
            double cf = child.fitness();
            if (child.toString().equals(target)) {
                done_it = true;
                System.out.println("FOUND IT: <" + GREEN_BEGIN + child.toString() + COLOR_END + ">");
            }
            if (cf / size > best_fitness) { best = i; best_fitness = cf / size; }
            newPopulation[i] = child;
            fitness[i] = cf / size;
            if (i == 0) incFitness[0] = fitness[0];
            else incFitness[i] = incFitness[i-1] + fitness[i];
        }
        if (!done_it) System.out.println("gen " + generation + ": " + colorCoded(population[best].toString()));
        population = newPopulation;
        generation++;
        return done_it;
    }

    void search() {
        long start = System.currentTimeMillis();
        while (!nextGeneration())
            if (generation % 100 == 0) System.out.println();
        long end = System.currentTimeMillis();
        System.out.println("Done in " + generation + " generations and in " + ((end - start) / 1000.0) + " seconds!");
    }
}
