package utils;


import java.util.LinkedList;

public class Test4Lottery {
    public static void main(String[] args) {

        Lottery lottery;
        LinkedList<Integer> integers = new LinkedList<>();
        integers.add(4);
        integers.add(4);

        int countA = 0;
        int countB = 0;
        int countC = 0;
        int times = 10000000;
        for (int i = 0; i < times; i++) {
            lottery = new Lottery(integers);
            //Person a draws lot
            if (lottery.draw() == 0) {
                countA++;
            }
            //Person b draws lot
            if (lottery.draw() == 0) {
                countB++;
            }
            //Person c draws lot
            if (lottery.draw() == 1) {
                countC++;
            }
        }
        double frequencyA = ((double) countA) / times;
        double frequencyB = ((double) countB) / times;
        double frequencyC = ((double) countC) / times;
        System.out.println(frequencyA);
        System.out.println(frequencyB);
        System.out.println(frequencyC);
    }
}
