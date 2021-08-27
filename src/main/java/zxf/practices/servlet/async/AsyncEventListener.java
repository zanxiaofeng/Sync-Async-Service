package zxf.practices.servlet.async;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.AsyncEvent;
import javax.servlet.AsyncListener;

public class AsyncEventListener implements AsyncListener {
    private int callTimes;

    public AsyncEventListener(int callTimes) {
        this.callTimes = callTimes;
    }

    @Override
    public void onStartAsync(AsyncEvent asyncEvent) throws IOException {
        System.out.println("AsyncEventListener       ::" + callTimes + "::onStartAsync::Name=" + Thread.currentThread().getName());
        // we can log the event here
    }

    @Override
    public void onComplete(AsyncEvent asyncEvent) throws IOException {
        System.out.println("AsyncEventListener       ::" + callTimes + "::onComplete  ::Name=" + Thread.currentThread().getName());
        // we can do resource cleanup activity here
    }

    @Override
    public void onError(AsyncEvent asyncEvent) throws IOException {
        System.out.println("AsyncEventListener       ::" + callTimes + "::onError     ::Name=" + Thread.currentThread().getName());
        // we can return error response to client
    }

    @Override
    public void onTimeout(AsyncEvent asyncEvent) throws IOException {
        System.out.println("AsyncEventListener       ::" + callTimes + "::onTimeout   ::Name=" + Thread.currentThread().getName());
        // we can send appropriate response to client
        try (PrintWriter output = asyncEvent.getSuppliedResponse().getWriter()) {
            output.write("Timeout error in async processing.");
        }
    }
}
