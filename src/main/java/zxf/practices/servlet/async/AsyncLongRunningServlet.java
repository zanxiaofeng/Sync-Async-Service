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

@WebServlet(name = "Async", urlPatterns = "/AsyncLongRunningServlet", loadOnStartup = 1, asyncSupported = true)
public class AsyncLongRunningServlet extends HttpServlet {
    private static final AtomicInteger callCounter = new AtomicInteger(0);

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        ThreadPoolExecutor executor = (ThreadPoolExecutor) request.getServletContext().getAttribute("executor");

        if (!Strings.isNullOrEmpty(request.getParameter("set-thread-count"))) {
            setNewThreadCount(executor, request, response);
            return;
        }

        startAsyncProcessing(executor, request);
    }

    private void startAsyncProcessing(ThreadPoolExecutor executor, HttpServletRequest request) {
        long startTime = System.currentTimeMillis();
        int callTimes = callCounter.incrementAndGet();
        System.out.println("AsyncLongRunningServlet  ::" + callTimes + "::doGet::Start::Name=" + Thread.currentThread().getName());

        request.setAttribute("org.apache.catalina.ASYNC_SUPPORTED", true);
        AsyncContext asyncContext = request.startAsync();
        asyncContext.addListener(new AsyncEventListener(callTimes));
        asyncContext.setTimeout(600000);
        executor.execute(new AsyncRequestProcessor(asyncContext, callTimes));

        long useTime = System.currentTimeMillis() - startTime;
        System.out.println("AsyncLongRunningServlet  ::" + callTimes + "::doGet::End  ::Name=" + Thread.currentThread().getName() + "::Time Taken="
                + useTime + " ms::" + executor.getActiveCount() + "-" + (executor.getTaskCount() - executor.getCompletedTaskCount()) + ":" + executor.getCompletedTaskCount());
    }

    private void setNewThreadCount(ThreadPoolExecutor executor, HttpServletRequest request, HttpServletResponse response) throws IOException {
        Integer newThreadCount = Integer.valueOf(request.getParameter("set-thread-count"));
        executor.setCorePoolSize(newThreadCount);
        executor.setMaximumPoolSize(newThreadCount);
        try (PrintWriter output = response.getWriter()) {
            output.write("Processing done for set thread count to " + newThreadCount);
        }
    }
}
