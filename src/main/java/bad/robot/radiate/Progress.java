package bad.robot.radiate;

public class Progress {

    private int current;
    private final double max;

    public Progress(int current, int max) {
        this.current = current;
        this.max = max;
    }

    public void increment() {
        current++;
    }

    public void decrement() {
        current--;
    }

    public Progress add(Progress progress) {
        return new Progress(current + progress.current, (int) (max + progress.max));
    }

    public int current() {
        return current;
    }

    public int asAngle() {
        return - asPercentageOf(360).intValue();
    }

    private int asPercentage() {
        return asPercentageOf(100).intValue();
    }

    private Double asPercentageOf(int i) {
        return current / max * i;
    }

    public boolean complete() {
        return current >= max;
    }

    @Override
    public String toString() {
        return asPercentage() + "%";
    }

    public boolean lessThan(Progress progress) {
        return asPercentage() < progress.asPercentage();
    }

    public boolean greaterThan(Progress progress) {
        return asPercentage() > progress.asPercentage();
    }
}
