package zxf.practices.servlet.util;


import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class LongProcessingUtil {
    public static void longProcessingAndSleep(int number, int sleeps) throws InterruptedException {
        longProcessing(number);
        Thread.sleep(sleeps * 1000);
    }

    private static void longProcessing(int number) {
        List<Double> numbers = new ArrayList<>(number);
        for (int i = 0; i < number; i++) {
            numbers.add(Math.random());
        }
        numbers.sort(Comparator.comparingDouble(Double::doubleValue));
    }
}
