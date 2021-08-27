package zxf.practices.servlet.async;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

import com.google.common.util.concurrent.ThreadFactoryBuilder;

@WebListener
public class AsyncContextListener implements ServletContextListener {
    public void contextInitialized(ServletContextEvent servletContextEvent) {
        System.out.println("AsyncContextListener::contextInitialized::Start::Name=" + Thread.currentThread().getName());
        ThreadFactory threadFactory = new ThreadFactoryBuilder().setNameFormat("practices-async-servlet-%d").build();
        ThreadPoolExecutor executor = new ThreadPoolExecutor(200, 200, 80000L, TimeUnit.MILLISECONDS, new ArrayBlockingQueue<Runnable>(10000), threadFactory);
        servletContextEvent.getServletContext().setAttribute("executor", executor);
        System.out.println("AsyncContextListener::contextInitialized::End  ::Name=" + Thread.currentThread().getName());
    }

    public void contextDestroyed(ServletContextEvent servletContextEvent) {
        System.out.println("AsyncContextListener::contextDestroyed::Start::Name=" + Thread.currentThread().getName());
        ThreadPoolExecutor executor = (ThreadPoolExecutor) servletContextEvent.getServletContext().getAttribute("executor");
        executor.shutdown();
        System.out.println("AsyncContextListener::contextDestroyed::End  ::Name=" + Thread.currentThread().getName());
    }
}