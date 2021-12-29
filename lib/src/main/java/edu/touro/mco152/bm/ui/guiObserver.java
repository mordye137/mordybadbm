package edu.touro.mco152.bm.ui;

import edu.touro.mco152.bm.bmObserver;
import edu.touro.mco152.bm.persist.DiskRun;

public class guiObserver implements bmObserver {

    DiskRun run;

    public guiObserver(DiskRun run){
        this.run = run;
    }

    @Override
    public void update() {
        Gui.runPanel.addRun(run);
    }
}
