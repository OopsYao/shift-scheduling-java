package utils;


import java.util.ArrayList;
import java.util.List;

public class Test4RouletteWheel {

    public static void main(String[] args) {

        //Generating samples
        List<Double> scores = new ArrayList<>();
        scores.add(3d);
        scores.add(5d);
        scores.add(4d);
        scores.add(9d);

        //Generating probabilities
        List<Double> probabilities = new ArrayList<>();
        double total = 0;
        for (Double score : scores) {
            total += score;
        }
        for (Double score : scores) {
            probabilities.add(score / total);
        }


        //Initialization
        List<Double> frequencies = new ArrayList<>();
        for (int i = 0; i < scores.size(); i++) {
            frequencies.add(0d);
        }

        //Start the game!
        RouletteWheel rouletteWheel = new RouletteWheel(probabilities);
        int n = 100000000;
        for (int i = 0; i < n; i++) {
            int num = rouletteWheel.spin();
            frequencies.set(num, 1d / n + frequencies.get(num));
        }


        System.out.println("Probability: " + probabilities);
        System.out.println("Frequency: " + frequencies);

    }
}
