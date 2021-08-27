package zxf.practices.servlet.async;

import java.io.PrintWriter;
import java.util.concurrent.ThreadPoolExecutor;

import javax.servlet.AsyncContext;

import com.google.common.base.Throwables;
import zxf.practices.servlet.util.LongProcessingUtil;

public class AsyncRequestProcessor implements Runnable {
    private AsyncContext asyncContext;
    private int callTimes;

    public AsyncRequestProcessor(AsyncContext asyncContext, int callTimes) {
        this.asyncContext = asyncContext;
        this.callTimes = callTimes;
    }

    @Override
    public void run() {
        System.out.println("AsyncRequestProcessor    ::" + callTimes + "::run::Start::Name=" + Thread.currentThread().getName() + "::Async Supported? "
                + asyncContext.getRequest().isAsyncSupported());
        long startTime = System.currentTimeMillis();
        try {
            int number = Integer.valueOf(asyncContext.getRequest().getParameter("number"));
            int sleeps = Integer.valueOf(asyncContext.getRequest().getParameter("sleeps"));

            // Set java stack size please use -Xss16M
            LongProcessingUtil.longProcessingAndSleep(number, sleeps);

            try (PrintWriter output = asyncContext.getResponse().getWriter()) {
                output.write("Async processing done for number: " + number + ", sleeps: " + sleeps);
            }
        } catch (Throwable e) {
            System.out.println(Throwables.getStackTraceAsString(e));
        } finally {
            long useTime = System.currentTimeMillis() - startTime;
            ThreadPoolExecutor executor = (ThreadPoolExecutor) asyncContext.getRequest().getServletContext().getAttribute("executor");
            System.out.println("AsyncRequestProcessor    ::" + callTimes + "::run::End  ::Name=" + Thread.currentThread().getName() + "::Time Taken="
                    + useTime + " ms::" + executor.getActiveCount() + "-" + (executor.getTaskCount() - executor.getCompletedTaskCount()) + ":" + executor.getCompletedTaskCount());
            asyncContext.complete();
        }
    }
}
