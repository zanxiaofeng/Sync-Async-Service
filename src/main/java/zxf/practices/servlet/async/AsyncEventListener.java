package zxf.practices.servlet.async;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.AsyncEvent;
import javax.servlet.AsyncListener;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebListener;

@WebListener
public class AsyncEventListener implements AsyncListener {
    private int callTimes;

    public AsyncEventListener() {
    }

    public AsyncEventListener(int callTimes) {
        this.callTimes = callTimes;
    }

    @Override
    public void onStartAsync(AsyncEvent asyncEvent) throws IOException {
        System.out.println("AsyncEventListener       ::" + callTimes + "::onStartAsync::Name=" + Thread.currentThread().getName() + "::ID=" + Thread.currentThread().getId());
        // we can log the event here
    }

    @Override
    public void onComplete(AsyncEvent asyncEvent) throws IOException {
        System.out.println("AsyncEventListener       ::" + callTimes + "::onComplete::Name=" + Thread.currentThread().getName() + "::ID=" + Thread.currentThread().getId());
        // we can do resource cleanup activity here
    }

    @Override
    public void onError(AsyncEvent asyncEvent) throws IOException {
        System.out.println("AsyncEventListener       ::" + callTimes + "::onError::Name=" + Thread.currentThread().getName() + "::ID=" + Thread.currentThread().getId());
        // we can return error response to client
    }

    @Override
    public void onTimeout(AsyncEvent asyncEvent) throws IOException {
        System.out.println("AsyncEventListener       ::" + callTimes + "::onTimeout::Name=" + Thread.currentThread().getName() + "::ID=" + Thread.currentThread().getId());
        // we can send appropriate response to client
        ServletResponse response = asyncEvent.getAsyncContext().getResponse();
        PrintWriter output = response.getWriter();
        output.write("TimeOut Error in Processing");
    }
}
