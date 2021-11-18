package edu.touro.mco152.bm;

import edu.touro.mco152.bm.ui.Gui;

import javax.swing.*;
import java.util.List;

import static edu.touro.mco152.bm.App.dataDir;

public class SwingOutput extends SwingWorker<Boolean, DiskMark> implements IOutput {
    DiskWorker diskWorker;

    public void setDiskWorker(DiskWorker diskWorker){
        this.diskWorker = diskWorker;
    }

    //process
    @Override
    public void process(List<DiskMark> markList) {
        markList.stream().forEach((dm) -> {
            if (dm.type == DiskMark.MarkType.WRITE) {
                Gui.addWriteMark(dm);
            } else {
                Gui.addReadMark(dm);
            }
        });
    }

    //done
    @Override
    public void done() {
        if (App.autoRemoveData) {
            Util.deleteDirectory(dataDir);
        }
        App.state = App.State.IDLE_STATE;
        Gui.mainFrame.adjustSensitivity();
    }

    //isCancelled
    @Override
    public boolean gotCancelled() { return isCancelled(); }

    //setProgress
    @Override
    public void setProgressStatus(int percentComplete) { setProgress((int) percentComplete);}

    //publish
    @Override
    public void post(DiskMark wMark) { publish(wMark); }

    //cancel
    @Override
    public void abort(boolean mayInterruptIfRunning) { cancel(mayInterruptIfRunning); }

    //execute
    @Override
    public void enact() { execute(); }

    protected Boolean doInBackground() throws Exception {
        return diskWorker.startExecution();
    }
}
