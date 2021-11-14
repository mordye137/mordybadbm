package edu.touro.mco152.bm;

import javax.swing.*;
import java.util.List;

public class GUISwing extends SwingWorker<Boolean, DiskMark> implements IWorker {

    @Override
    public Boolean startExecution() throws Exception {
        return doInBackground();
    }

    @Override
    public void operation(List<DiskMark> markList) { process(markList); }

    @Override
    public void complete() { done(); }

    @Override
    public boolean gotCancelled() { return isCancelled(); }

    @Override
    public void setProgressStatus(int percentComplete) { setProgress((int) percentComplete);}

    @Override
    public void post(DiskMark wMark) { publish(wMark); }

    protected Boolean doInBackground() throws Exception {
        DiskWorker diskWorker = new DiskWorker();
        return diskWorker.startExecution();
    }
}
