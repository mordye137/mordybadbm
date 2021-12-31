package edu.touro.mco152.bm.ui;

import edu.touro.mco152.bm.bmObserver;
import edu.touro.mco152.bm.persist.DiskRun;

/**
 * A Gui observer class that implements our bmObserver class
 */
public class guiObserver implements bmObserver {

    DiskRun run;

    /**
     * @param run DiskRun object
     */
    public guiObserver(DiskRun run){
        this.run = run;
    }

    /**
     * Overrides the update() method and updates the GUI with DiskRun info
     */
    @Override
    public void update() {
        Gui.runPanel.addRun(run);
    }
}
