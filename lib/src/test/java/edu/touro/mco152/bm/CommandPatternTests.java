package edu.touro.mco152.bm;

import edu.touro.mco152.bm.commands.ICommand;
import edu.touro.mco152.bm.commands.simpleInvoker;
import edu.touro.mco152.bm.ui.Gui;
import edu.touro.mco152.bm.ui.MainFrame;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.*;

import static edu.touro.mco152.bm.App.*;
import static edu.touro.mco152.bm.App.blockSequence;
import static edu.touro.mco152.bm.persist.DiskRun.BlockSequence.SEQUENTIAL;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class CommandPatternTests implements IOutput{

    private int mbPerSecond;
    private int results;
    private Set<Integer> progressSet = new HashSet<Integer>();

    /**
     * Bruteforce setup of static classes/fields to allow DiskWorker to run.
     *
     * @author lcmcohen
     */
    private void setupDefaultAsPerProperties()
    {
        /// Do the minimum of what  App.init() would do to allow to run.
        Gui.mainFrame = new MainFrame();
        App.p = new Properties();
        App.loadConfig();
        System.out.println(App.getConfigString());
        Gui.progressBar = Gui.mainFrame.getProgressBar(); //must be set or get Nullptr

        // configure the embedded DB in .jDiskMark
        System.setProperty("derby.system.home", App.APP_CACHE_DIR);

        // code from startBenchmark
        //4. create data dir reference
        App.dataDir = new File(App.locationDir.getAbsolutePath()+ File.separator+App.DATADIRNAME);

        //5. remove existing test data if exist
        if (App.dataDir.exists()) {
            if (App.dataDir.delete()) {
                App.msg("removed existing data dir");
            } else {
                App.msg("unable to remove existing data dir");
            }
        }
        else
        {
            App.dataDir.mkdirs(); // create data dir if not already present
        }
    }

    @Test
    public void writeTest() throws Exception {
        //Arrange
        setupDefaultAsPerProperties();
        ICommand writeBM = new writeBM(this, 25, 128, 2048, SEQUENTIAL);
        ICommand readBM = new readBM(this, 25, 128, 2048, SEQUENTIAL);

        simpleInvoker bmOptions = new simpleInvoker(writeBM, readBM);

        //Act
        bmOptions.writeBm();

        //Assert
        assertTrue(mbPerSecond > 0);
    }

    @Test
    public void readTest() {
        //Arrange
        setupDefaultAsPerProperties();
        ICommand writeBM = new writeBM(this, 25, 128, 2048, SEQUENTIAL);
        ICommand readBM = new readBM(this, 25, 128, 2048, SEQUENTIAL);

        simpleInvoker bmOptions = new simpleInvoker(writeBM, readBM);

        //Act
        bmOptions.readBM();

        //Assert
        assertTrue(mbPerSecond > 0);
    }


    @Override
    public boolean gotCancelled() {
        return false;
    }

    @Override
    public void setProgressStatus(int percentComplete) {
        progressSet.add(percentComplete);
    }

    @Override
    public void post(DiskMark wMark) {
        mbPerSecond = (int) wMark.getBwMbSec();
        results = (int) wMark.getCumAvg();
    }

    @Override
    public void abort(boolean b) {

    }

    @Override
    public void enact() {

    }
}
