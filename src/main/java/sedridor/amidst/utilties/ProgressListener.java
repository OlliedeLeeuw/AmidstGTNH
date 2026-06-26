package sedridor.amidst.utilties;

public abstract class ProgressListener {

    public abstract void onUpdate(ProgressMeter paramProgressMeter, double paramDouble);

    public abstract void onComplete(ProgressMeter paramProgressMeter);
}
