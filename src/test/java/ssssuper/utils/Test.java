package ssssuper.utils;

import utils.MapStatistics;

class Test {
    public static void main(String[] args) {
        MapStatistics<String, Integer> stat = new MapStatistics<>();
        stat.alter("a");
        stat.alter("b");
        stat.alter("c");
        stat.plus(2);
        System.out.println(stat.getMap());
    }
}
