package zxf.practices.servlet.async;

import java.io.PrintWriter;
import java.util.concurrent.ThreadPoolExecutor;

import javax.servlet.AsyncContext;

import com.google.common.base.Throwables;
import zxf.practices.servlet.util.LongProcessingUtil;

public class AsyncRequestProcessor implements Runnable {
    private AsyncContext asyncContext;
    private int callTimes, fact, sleepSecs;

    public AsyncRequestProcessor(AsyncContext asyncContext, int callTimes, int fact, int sleepSecs) {
        this.asyncContext = asyncContext;
        this.callTimes = callTimes;
        this.fact = fact;
        this.sleepSecs = sleepSecs;
    }

    @Override
    public void run() {
        System.out.println("AsyncRequestProcessor    ::" + callTimes + "::run::Start::Name=" + Thread.currentThread().getName() + "::ID=" + Thread.currentThread().getId() + "::Async Supported? "
                + asyncContext.getRequest().isAsyncSupported());
        long startTime = System.currentTimeMillis();
        try {
            // Set java stack size please use -Xss16M
            long result = LongProcessingUtil.longProcessing(fact, sleepSecs);
            PrintWriter output = asyncContext.getResponse().getWriter();
            output.write("Processing done for " + sleepSecs + " seconds!!" + result);
        } catch (Throwable e) {
            System.out.println(Throwables.getStackTraceAsString(e));
        } finally {
            long endTime = System.currentTimeMillis();
            ThreadPoolExecutor executor = (ThreadPoolExecutor) asyncContext.getRequest().getServletContext().getAttribute("executor");
            System.out.println("AsyncRequestProcessor    ::" + callTimes + "::run::End::Name=" + Thread.currentThread().getName() + "::ID=" + Thread.currentThread().getId() + "::Time Taken="
                    + (endTime - startTime) + " ms::" + executor.getActiveCount() + "-" + (executor.getTaskCount() - executor.getCompletedTaskCount()) + ":" + executor.getCompletedTaskCount());
            asyncContext.complete();
        }
    }
}
