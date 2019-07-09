package zxf.practices.servlet.util;

import com.google.common.base.Throwables;

public class LongProcessingUtil {
    public static long longProcessing(long value, int sleepSecs) {
        long result = 0;
        for (int i = 0; i < sleepSecs / 8; i++) {
            result = longProcessingInternal(value, (long) (6000 * Math.random()));
        }
        return result;
    }

    private static long longProcessingInternal(long value, long limit) {
        if (value > 1) {
            if (value <= limit) {
                randomTest(value);
            }
            return value + longProcessingInternal(value - 1, limit);
        } else {
            try {
                Thread.sleep(8000);
            } catch (Throwable e) {
                System.out.println(Throwables.getStackTraceAsString(e));
            }
            return 1;
        }
    }

    private static void randomTest(long value) {
        for (int i = 0; i < value / 10; i++) {
            Math.random();
        }
    }
}
