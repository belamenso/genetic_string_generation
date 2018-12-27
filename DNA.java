import java.util.Random;

public class DNA {
    static final double mutationRate = 0.0027;

    private Random random;

    private char genes[];
    private String target;

    private char randomChar() {
        return (char) (random.nextInt(- 32 + 126 + 1) + 32);
    }

    DNA(String _target) {
        random = new Random();
        target = _target;
        genes = new char[target.length()];

        for (int i = 0; i < genes.length; i++)
            genes[i] = randomChar();
    }

    public String toString() {
        return new String(genes);
    }

    double fitness() {
        int score = 0;
        for (int i = 0; i < genes.length; i++)
            if (genes[i] == target.charAt(i)) score++;
        return ((double)score * score) / (target.length() * target.length());
    }

    DNA cross(DNA partner) {
        DNA child = new DNA(target);
        for (int i = 0; i < genes.length; i++) {
            child.genes[i] = (random.nextDouble() < 0.5) ? genes[i] : partner.genes[i];
            if (random.nextDouble() < mutationRate) child.genes[i] = randomChar();
        }
        return child;
    }

}
