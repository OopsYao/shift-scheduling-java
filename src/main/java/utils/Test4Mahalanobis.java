package utils;

import java.util.Collection;
import java.util.HashSet;

public class Test4Mahalanobis {
    public static void main(String[] args) {
        //Sample space
        double[] sample1 = {1, 0.2};
        double[] sample2 = {2, 0.1};
        double[] sample3 = {1, 0.5};

        Collection<double[]> sampleSpace = new HashSet<>();
        sampleSpace.add(sample1);
        sampleSpace.add(sample2);
        sampleSpace.add(sample3);

        //Collection way
        Mahalanobis mahalanobis = new Mahalanobis(sampleSpace);
        System.out.println("Collection way:");
        System.out.println(mahalanobis.getDistance(sample1, sample2));

        //Recommended way to generate a mahalanobis
        Mahalanobis m2 = new Mahalanobis();
        m2.add(sample1);
        m2.add(sample2);
        m2.add(sample3);
        System.out.println("Adding way:");
        for (double[] s : sampleSpace) {
            System.out.println(m2.getDistance(new double[]{0, 0}));
        }
        System.out.println(mahalanobis.getDistance(sample1, sample2));
    }
}
