package edu.touro.mco152.bm;

import com.sun.source.tree.AssertTree;
import edu.touro.mco152.bm.ui.Gui;
import edu.touro.mco152.bm.ui.MainFrame;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class NonSwingOutputTest implements IOutput{

    DiskWorker diskWorker = new DiskWorker(this);
    private int mbPerSecond;
    private Set<Integer> progressSet = new HashSet<Integer>();
    private int results;
    private boolean completed;



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


    /**
     * Make sure benchmark is started by checking that the MB/s is more than 0.
     * @throws Exception
     */
    @Test
    public void benchMarkStartTest() throws Exception {
        //Arrange
        setupDefaultAsPerProperties();

        //Act
        diskWorker.startExecution();

        //Assert
        assertTrue(mbPerSecond > 0);
    }


    /**
     * Make sure the center of the recorded percent entries set is around 50%
     * @throws Exception
     */
    @Test
    public void testPercentComplete() throws Exception {
        //Arrange
        //Default properties already set up

        //Act
        diskWorker.startExecution();
        List<Integer> progressList = new ArrayList<>(progressSet);

        //Assert
        assertTrue(progressList.get(progressSet.size() / 2) > 25 && progressList.get(progressSet.size() / 2) < 75);
    }

    /**
     * Make sure diskworker returns true,signaling it completed successfully
     * @throws Exception
     */
    @Test
    public void testCompletion() throws Exception {
        //Arrange
        //Default properties already set up

        //Act
        completed = diskWorker.startExecution();

        //Assert
        assertTrue(completed);
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