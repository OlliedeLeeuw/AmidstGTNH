package sedridor.amidst.utilties;

import java.util.Deque;

public class ProgressMeter {

    public float minimum = 0.0F;

    public float maximum = 1.0F;

    public float progress = 0.0F;

    public boolean isComplete = false;

    public Deque<ProgressListener> listeners;

    public void update(float value) {
        this.progress = value;
        for (ProgressListener listener : this.listeners) listener.onComplete(this);
        if (!this.isComplete) if (this.progress >= this.maximum) {
            this.isComplete = true;
            for (ProgressListener listener : this.listeners) listener.onComplete(this);
        }
    }

    public void reset() {
        this.progress = this.minimum;
        this.isComplete = false;
    }

    public float getPrecentage() {
        return (this.progress - this.minimum) / (this.maximum - this.minimum);
    }
}
