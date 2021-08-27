package zxf.practices.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.concurrent.atomic.AtomicInteger;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.common.base.Throwables;
import zxf.practices.servlet.util.LongProcessingUtil;

@WebServlet(name = "Sync", urlPatterns = "/SyncLongRunningServlet", loadOnStartup = 1)
public class SyncLongRunningServlet extends HttpServlet {
    private static final AtomicInteger callCounter = new AtomicInteger(0);

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        int callTimes = callCounter.incrementAndGet();
        System.out.println("SyncLongRunningServlet   ::" + callTimes + "::doGet::Start::Name=" + Thread.currentThread().getName());

        long startTime = System.currentTimeMillis();

        try {
            int number = Integer.valueOf(request.getParameter("number"));
            int sleeps = Integer.valueOf(request.getParameter("sleeps"));

            // Set java stack size please use -Xss16M
            LongProcessingUtil.longProcessingAndSleep(number, sleeps);

            try (PrintWriter output = response.getWriter()) {
                output.write("Sync processing done for number: " + number + ", sleeps: " + sleeps);
            }
        } catch (Throwable e) {
            System.out.println(Throwables.getStackTraceAsString(e));
        } finally {
            long useTime = System.currentTimeMillis() - startTime;
            System.out.println("SyncLongRunningServlet   ::" + callTimes + "::doGet::End  ::Name=" + Thread.currentThread().getName() + "::Time Taken="
                    + useTime + "ms.");
        }
    }
}
