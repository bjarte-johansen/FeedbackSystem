package root.A_TODO.no_test_extra;

public class TryWithTimer implements AutoCloseable {
    private final long start = System.nanoTime();
    private final String name;

    public TryWithTimer(String name) { this.name = name; }

    @Override
    public void close() {
        long dt = System.nanoTime() - start;
        System.out.println(name + ": " + dt + " ns, " + (dt / 1_000_000.0) + " ms");
    }
}
