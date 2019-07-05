package zxf.practices.servlet.async;

import java.io.PrintWriter;
import java.util.concurrent.ThreadPoolExecutor;

import javax.servlet.AsyncContext;

import com.google.common.base.Throwables;

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
			long result = longProcessing(fact, sleepSecs);
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

	private long longProcessing(long value, int sleepSecs) {
		long result = 0;
		for (int i = 0; i < sleepSecs / 8; i++) {
			result = longProcessingInternal(value, (long) (6000 * Math.random()));
		}
		return result;
	}

	private long longProcessingInternal(long value, long limit) {
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

	private void randomTest(long value) {
		for (int i = 0; i < value / 10; i++) {
			Math.random();
		}
	}
}
