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

@WebServlet(urlPatterns = "/AsyncLongRunningServlet", asyncSupported = true)
public class AsyncLongRunningServlet extends HttpServlet {
	private static final long serialVersionUID = 10000L;
	private static final AtomicInteger callCounter = new AtomicInteger(0);

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		if (!Strings.isNullOrEmpty(request.getParameter("setthreadcount"))) {
			//手动调整线程数以适应当前系统中IO任务和计算任务的分布情况。
			ThreadPoolExecutor executor = (ThreadPoolExecutor) request.getServletContext().getAttribute("executor");
			executor.setCorePoolSize(Integer.valueOf(request.getParameter("setthreadcount")));
			executor.setMaximumPoolSize(Integer.valueOf(request.getParameter("setthreadcount")));
			PrintWriter output = response.getWriter();
			output.write("Processing done for set thread count to " + request.getParameter("setthreadcount") + ".");
		} else {
			int calltimes = callCounter.incrementAndGet();
			System.out.println("AsyncLongRunningServlet  ::" + calltimes + "::doGet::Start::Name=" + Thread.currentThread().getName() + "::ID=" + Thread.currentThread().getId());

			long startTime = System.currentTimeMillis();

			request.setAttribute("org.apache.catalina.ASYNC_SUPPORTED", true);
			AsyncContext asyncCtx = request.startAsync();
			asyncCtx.addListener(new AsyncEventListener(calltimes));
			asyncCtx.setTimeout(600000);

			ThreadPoolExecutor executor = (ThreadPoolExecutor) request.getServletContext().getAttribute("executor");

			int fact = Integer.valueOf(request.getParameter("fact"));
			int sleepSecs = Integer.valueOf(request.getParameter("time"));
			executor.execute(new AsyncRequestProcessor(asyncCtx, calltimes, fact, sleepSecs));

			long endTime = System.currentTimeMillis();

			System.out.println("AsyncLongRunningServlet  ::" + calltimes + "::doGet::End::Name=" + Thread.currentThread().getName() + "::ID=" + Thread.currentThread().getId() + "::Time Taken="
					+ (endTime - startTime) + " ms::" + executor.getActiveCount() + "-" + (executor.getTaskCount() - executor.getCompletedTaskCount()) + ":" + executor.getCompletedTaskCount());
		}
	}
}