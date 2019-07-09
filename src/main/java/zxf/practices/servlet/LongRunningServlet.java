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

@WebServlet(name = "longRunningServlet", urlPatterns = "/LongRunningServlet", loadOnStartup = 1)
public class LongRunningServlet extends HttpServlet {
    private static final AtomicInteger callCounter = new AtomicInteger(0);

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        int calltimes = callCounter.incrementAndGet();
        System.out.println("LongRunningServlet       ::" + calltimes + "::doGet::Start::Name=" + Thread.currentThread().getName() + "::ID=" + Thread.currentThread().getId());

        long startTime = System.currentTimeMillis();

        long result = 0;
        try {
            int fact = Integer.valueOf(request.getParameter("fact"));
            int sleepSecs = Integer.valueOf(request.getParameter("time"));

            // Set java stack size please use -Xss16M
            result = LongProcessingUtil.longProcessing(fact, sleepSecs);

            PrintWriter output = response.getWriter();
            output.write("Processing done for " + sleepSecs + " seconds!!" + result);
        } catch (Throwable e) {
            System.out.println(Throwables.getStackTraceAsString(e));
        } finally {
            long endTime = System.currentTimeMillis();
            System.out.println("LongRunningServlet       ::" + calltimes + "::doGet::End::Name=" + Thread.currentThread().getName() + "::ID=" + Thread.currentThread().getId() + "::Time Taken="
                    + (endTime - startTime) + " ms." + result);
        }
    }
}
