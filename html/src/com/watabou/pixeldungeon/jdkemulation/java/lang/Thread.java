package java.lang;

import com.google.gwt.user.client.Timer;

public class Thread implements Runnable{

    private boolean alive = false;

    @Override
    public void run() {}

    public void start(){
        alive = true;
        new Timer() {
            @Override
            public void run() {
                Thread.this.run();
                alive = false;
            }
        }.schedule(0);
    }

    public final boolean isAlive(){
        return alive;
    }
}
