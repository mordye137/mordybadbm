package edu.touro.mco152.bm;

import com.sun.source.tree.AssertTree;
import edu.touro.mco152.bm.ui.Gui;
import edu.touro.mco152.bm.ui.MainFrame;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.Properties;

import static org.junit.jupiter.api.Assertions.*;

class NonSwingOutputTest implements IOutput{

    DiskWorker diskWorker = new DiskWorker(this);

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
    public void benchMarkStartTest() throws Exception {
        setupDefaultAsPerProperties();

        diskWorker.startExecution();


    }

    @Override
    public boolean gotCancelled() {
        return false;
    }

    @Override
    public void setProgressStatus(int percentComplete) {
        System.out.println(percentComplete);
    }

    @Override
    public void post(DiskMark wMark) {

    }

    @Override
    public void abort(boolean b) {

    }

    @Override
    public void enact() {

    }
}