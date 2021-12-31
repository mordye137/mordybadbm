package edu.touro.mco152.bm;

import edu.touro.mco152.bm.commands.ICommand;
import edu.touro.mco152.bm.commands.readBM;
import edu.touro.mco152.bm.commands.simpleInvoker;
import edu.touro.mco152.bm.commands.writeBM;
import edu.touro.mco152.bm.persist.dbObserver;
import edu.touro.mco152.bm.slack.slackObserver;
import edu.touro.mco152.bm.ui.Gui;
import edu.touro.mco152.bm.ui.guiObserver;

import javax.swing.*;

import static edu.touro.mco152.bm.App.*;

/**
 * Run the disk benchmarking as a Swing-compliant thread (only one of these threads can run at
 * once.) Cooperates with Swing to provide and make use of interim and final progress and
 * information, which is also recorded as needed to the persistence store, and log.
 * <p>
 * Depends on static values that describe the benchmark to be done having been set in App and Gui classes.
 * The DiskRun class is used to keep track of and persist info about each benchmark at a higher level (a run),
 * while the DiskMark class described each iteration's result, which is displayed by the UI as the benchmark run
 * progresses.
 * <p>
 * This class only knows how to do 'read' or 'write' disk benchmarks. It is instantiated by the
 * startBenchmark() method.
 * <p>
 * To be Swing compliant this class extends SwingWorker and declares that its final return (when
 * doInBackground() is finished) is of type Boolean, and declares that intermediate results are communicated to
 * Swing using an instance of the DiskMark class.
 */

//Diskworker constructer that takes in a worker to display results

public class DiskWorker {
    IOutput worker;

    public DiskWorker(IOutput worker) {
        this.worker = worker;
    }

    public Boolean startExecution() throws Exception {

        //Instantaite observers
        dbObserver dbObserver;
        guiObserver guiObserver;
        slackObserver slackObserver;

        /*
         * Constructs 2 commands with worker interfaces and parameters from App
         * and passes them to an invoker.
         */
        writeBM writeBM = new writeBM(worker, numOfMarks, numOfBlocks, blockSizeKb, blockSequence);
        readBM readBM = new readBM(worker, numOfMarks, numOfBlocks, blockSizeKb, blockSequence);

        simpleInvoker bmOptions = new simpleInvoker(writeBM, readBM);


        /*
          We 'got here' because: a) End-user clicked 'Start' on the benchmark UI,
          which triggered the start-benchmark event associated with the App::startBenchmark()
          method.  b) startBenchmark() then instantiated a DiskWorker, and called
          its (super class's) execute() method, causing Swing to eventually
          call this doInBackground() method.
         */
        System.out.println("*** starting new worker thread");
        msg("Running readTest " + App.readTest + "   writeTest " + App.writeTest);
        msg("num files: " + numOfMarks + ", num blks: " + numOfBlocks
                + ", blk size (kb): " + blockSizeKb + ", blockSequence: " + blockSequence);

        /*
          init local vars that keep track of benchmarks, and a large read/write buffer
         */
        int wUnitsComplete = 0, rUnitsComplete = 0, unitsComplete;
        int wUnitsTotal = App.writeTest ? numOfBlocks * numOfMarks : 0;
        int rUnitsTotal = App.readTest ? numOfBlocks * numOfMarks : 0;
        int unitsTotal = wUnitsTotal + rUnitsTotal;
        float percentComplete;

        int blockSize = blockSizeKb * KILOBYTE;
        byte[] blockArr = new byte[blockSize];
        for (int b = 0; b < blockArr.length; b++) {
            if (b % 2 == 0) {
                blockArr[b] = (byte) 0xFF;
            }
        }

        DiskMark wMark, rMark;  // declare vars that will point to objects used to pass progress to UI

        Gui.updateLegend();  // init chart legend info

        if (App.autoReset) {
            App.resetTestData();
            Gui.resetTestData();
        }

        int startFileNum = App.nextMarkNumber;

        /*
          The GUI allows either a write, read, or both types of BMs to be started. They are done serially.
          This now uses an invoker to initiate the write benchmark
         */
        if (App.writeTest) {
            bmOptions.writeBm();
        }

        /*
          Most benchmarking systems will try to do some cleanup in between 2 benchmark operations to
          make it more 'fair'. For example a networking benchmark might close and re-open sockets,
          a memory benchmark might clear or invalidate the Op Systems TLB or other caches, etc.
         */

        // try renaming all files to clear catch
        if (App.readTest && App.writeTest && !worker.gotCancelled()) {
            JOptionPane.showMessageDialog(Gui.mainFrame,
                    "For valid READ measurements please clear the disk cache by\n" +
                            "using the included RAMMap.exe or flushmem.exe utilities.\n" +
                            "Removable drives can be disconnected and reconnected.\n" +
                            "For system drives use the WRITE and READ operations \n" +
                            "independantly by doing a cold reboot after the WRITE",
                    "Clear Disk Cache Now", JOptionPane.PLAIN_MESSAGE);
        }

        // Same as above, just for Read operations instead of Writes.
        if (App.readTest) {
            bmOptions.readBM();
        }

        //Insantiate observers
        dbObserver = new dbObserver(readBM.getRun());
        guiObserver = new guiObserver(readBM.getRun());
        slackObserver = new slackObserver(readBM.getMark().getCumAvg(), readBM.getMark().getCumMax());
        //Register Observers
        readBM.registerObserver(dbObserver);
        readBM.registerObserver(guiObserver);
        readBM.registerObserver(slackObserver);
        //Notify observers
        readBM.notifyObservers();

        App.nextMarkNumber += numOfMarks;
        return true;
    }
}
    /*
     * Process a list of 'chunks' that have been processed, ie that our thread has previously
     * published to Swing. For my info, watch Professor Cohen's video -
     * Module_6_RefactorBadBM Swing_DiskWorker_Tutorial.mp4
     * @param markList a list of DiskMark objects reflecting some completed benchmarks
  */