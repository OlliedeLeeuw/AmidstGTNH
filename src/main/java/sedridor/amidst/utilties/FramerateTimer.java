package sedridor.amidst.utilties;

public class FramerateTimer {

    private int tickCounter;

    private long lastUpdate;

    private long msPerUpdate;

    private float currentFPS = 0.0F;

    public FramerateTimer(int updatesPerSecond) {
        this.msPerUpdate = (long) (1000.0F * 1.0F / (float) updatesPerSecond);
        reset();
    }

    public void reset() {
        this.tickCounter = 0;
        this.lastUpdate = System.currentTimeMillis();
    }

    public void tick() {
        this.tickCounter++;
        long curTime = System.currentTimeMillis();
        if (curTime - this.lastUpdate > this.msPerUpdate) {
            float timeDifference = (float) (curTime - this.lastUpdate);
            timeDifference /= 1000.0F;
            timeDifference = (float) this.tickCounter / timeDifference;
            this.currentFPS = timeDifference;
            this.tickCounter = 0;
            this.lastUpdate = curTime;
        }
    }

    public float getFramerate() {
        return this.currentFPS;
    }

    public String toString() {
        return "FPS: " + String.format("%.1f", this.currentFPS);
    }
}
