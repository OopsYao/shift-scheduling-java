package utils;

public class NewLotteryTest {
    public static void main(String[] args) {
        NewLottery<String> lottery = new NewLottery<>();
        lottery.add("五谷");

        lottery.add("三味");
        lottery.add("三味");
        lottery.add("三味");
        lottery.add("三味");
        lottery.add("三味");
        lottery.add("三味");
        for (int i = 0; i < 40; i++) {
            System.out.println(lottery.draw());

        }
    }
}
