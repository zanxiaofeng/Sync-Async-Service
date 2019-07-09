package zxf.practices.servlet.async;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.atomic.AtomicInteger;

import javax.servlet.AsyncContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.common.base.Strings;

@WebServlet(name = "asyncLongRunningServlet", urlPatterns = "/AsyncLongRunningServlet", loadOnStartup = 1, asyncSupported = true)
public class AsyncLongRunningServlet extends HttpServlet {
    private static final AtomicInteger callCounter = new AtomicInteger(0);

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        if (!Strings.isNullOrEmpty(request.getParameter("setthreadcount"))) {
            ThreadPoolExecutor executor = (ThreadPoolExecutor) request.getServletContext().getAttribute("executor");
            executor.setCorePoolSize(Integer.valueOf(request.getParameter("setthreadcount")));
            executor.setMaximumPoolSize(Integer.valueOf(request.getParameter("setthreadcount")));
            PrintWriter output = response.getWriter();
            output.write("Processing done for set thread count to " + request.getParameter("setthreadcount") + ".");
            return;
        }

        long startTime = System.currentTimeMillis();
        int callTimes = callCounter.incrementAndGet();

        System.out.println("AsyncLongRunningServlet  ::" + callTimes + "::doGet::Start::Name=" + Thread.currentThread().getName() + "::ID=" + Thread.currentThread().getId());

        request.setAttribute("org.apache.catalina.ASYNC_SUPPORTED", true);
        AsyncContext asyncCtx = request.startAsync();
        asyncCtx.addListener(new AsyncEventListener(callTimes));
        asyncCtx.setTimeout(600000);

        ThreadPoolExecutor executor = (ThreadPoolExecutor) request.getServletContext().getAttribute("executor");

        int fact = Integer.valueOf(request.getParameter("fact"));
        int sleepSecs = Integer.valueOf(request.getParameter("time"));
        executor.execute(new AsyncRequestProcessor(asyncCtx, callTimes, fact, sleepSecs));

        long useTime = System.currentTimeMillis() - startTime;

        System.out.println("AsyncLongRunningServlet  ::" + callTimes + "::doGet::End  ::Name=" + Thread.currentThread().getName() + "::ID=" + Thread.currentThread().getId() + "::Time Taken="
                + useTime + " ms::" + executor.getActiveCount() + "-" + (executor.getTaskCount() - executor.getCompletedTaskCount()) + ":" + executor.getCompletedTaskCount());
    }
}
