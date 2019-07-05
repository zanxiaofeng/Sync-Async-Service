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

@WebServlet(urlPatterns = "/LongRunningServlet")
public class LongRunningServlet extends HttpServlet {
	private static final long serialVersionUID = 10000L;
	private static final AtomicInteger callCounter = new AtomicInteger(0);

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		int calltimes = callCounter.incrementAndGet();
		System.out.println("LongRunningServlet       ::" + calltimes + "::doGet::Start::Name=" + Thread.currentThread().getName() + "::ID=" + Thread.currentThread().getId());

		long startTime = System.currentTimeMillis();
		long result = 0;
		try {
			int fact = Integer.valueOf(request.getParameter("fact"));
			int sleepSecs = Integer.valueOf(request.getParameter("time"));

			// Set java stack size please use -Xss16M
			result = longProcessing(fact, sleepSecs);

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