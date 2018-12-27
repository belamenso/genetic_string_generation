public class Main {

    public static void main(String[] args) {
        String target = "To be or not to be? This is the question!";
        if (args.length >= 2) target = args[1];
        new Population(1000, target).search();
    }
}
