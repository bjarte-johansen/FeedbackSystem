package root.__ignore__.no_test_extra;

import root.includes.Functional;

public class TryWithTimer implements AutoCloseable {
    private long startNanos;
    private long stopNanos = -1;
    private final String name;

    public TryWithTimer(String name) {
        this.name = name;
        this.start();
    }

    public void start(){
        this.startNanos = System.nanoTime();
    }
    public void stop(){
        this.stopNanos = System.nanoTime();
    }
    public long elapsed(){
        if(stopNanos == -1){
            return System.nanoTime() -this.startNanos;
        }else {
            return this.stopNanos - this.startNanos;
        }
    }

    public static void withMeasurement(String name, Functional.ThrowingRunnable runnable) {
        try(var t =  new TryWithTimer(name)) {
            try {
                runnable.run();
            }catch(Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

    @Override
    public void close() {
        long dt = System.nanoTime() - startNanos;
        System.out.println(name + ": " + dt + " ns, " + (dt / 1_000_000.0) + " ms");
    }
}
