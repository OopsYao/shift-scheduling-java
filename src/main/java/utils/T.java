package utils;

import java.util.*;

public class T {
    public static void main(String[] args) {

        double[][] m = {{1, 2}, {3, 4}};
        Mahalanobis mahalanobis = new Mahalanobis();

        MapStatistic<String> stat = new MapStatistic<>();
        stat.alter("mon", 1);
        stat.alter("tue", 2);
        stat.alter("wed", 0);
        System.out.println(stat.getDistance(1));

        HashMap<String, Integer> stringIntegerHashMap = new HashMap<>();
        Integer mon = stringIntegerHashMap.get("mon");
        System.out.println(mon);

    }

    public static void printMatrix(double[][] m) {
        for (int i = 0; i < m.length; i++) {
            for (int j = 0; j < m.length; j++) {
                System.out.print(m[i][j] + "\t\t");
            }
            System.out.println();
        }
    }
}
