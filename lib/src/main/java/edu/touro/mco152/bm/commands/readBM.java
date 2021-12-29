package edu.touro.mco152.bm.commands;

import edu.touro.mco152.bm.*;
import edu.touro.mco152.bm.commands.ICommand;
import edu.touro.mco152.bm.persist.DiskRun;
import edu.touro.mco152.bm.persist.EM;
import edu.touro.mco152.bm.persist.dbObserver;
import edu.touro.mco152.bm.slack.slackObserver;
import edu.touro.mco152.bm.ui.Gui;
import edu.touro.mco152.bm.ui.guiObserver;
import jakarta.persistence.EntityManager;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.Date;
import java.util.Observable;
import java.util.Observer;
import java.util.logging.Level;
import java.util.logging.Logger;

import static edu.touro.mco152.bm.App.*;
import static edu.touro.mco152.bm.DiskMark.MarkType.READ;

/**
 * A new class that separates the read operations from DiskWorker
 */
public class readBM implements ICommand{

    IOutput worker;
    int numOfMarks;
    int numOfBlocks;
    int blockSizeKb;
    DiskRun.BlockSequence blockSequence;
    DiskRun run;
    DiskMark rMark;
    //List of Observers
    ArrayList<bmObserver> observers = new ArrayList<bmObserver>();


    /**
     * A constructor for our command
     * @param worker the type of Output worker to be used
     * @param numOfMarks
     * @param numOfBlocks
     * @param blockSizeKb
     * @param blockSequence
     */
    public readBM(IOutput worker, int numOfMarks, int numOfBlocks, int blockSizeKb, DiskRun.BlockSequence blockSequence){
        this.worker = worker;
        this.numOfMarks = numOfMarks;
        this.numOfBlocks = numOfBlocks;
        this.blockSizeKb = blockSizeKb;
        this.blockSequence = blockSequence;
    }


    /**
     * runs the read benchmark
     */
    @Override
    public void execute()
    {
        // declare local vars formerly in DiskWorker

        int wUnitsComplete = 0,
                rUnitsComplete = 0,
                unitsComplete;

        int wUnitsTotal = App.writeTest ? numOfBlocks * numOfMarks : 0;
        int rUnitsTotal = App.readTest ? numOfBlocks * numOfMarks : 0;
        int unitsTotal = wUnitsTotal + rUnitsTotal;
        float percentComplete;

        int blockSize = blockSizeKb*KILOBYTE;
        byte [] blockArr = new byte [blockSize];
        for (int b=0; b<blockArr.length; b++) {
            if (b%2==0) {
                blockArr[b]=(byte)0xFF;
            }
        }

        int startFileNum = App.nextMarkNumber;

        run = new DiskRun(DiskRun.IOMode.READ, blockSequence);

        /*//Instantaite observers
        bmObserver dbObserver = new dbObserver(run);
        bmObserver guiObserver = new guiObserver(run);

        //Register observers
        registerObserver(dbObserver);
        registerObserver(guiObserver);*/

        run.setNumMarks(numOfMarks);
        run.setNumBlocks(numOfBlocks);
        run.setBlockSize(blockSizeKb);
        run.setTxSize(App.targetTxSizeKb());
        run.setDiskInfo(Util.getDiskInfo(dataDir));

        msg("disk info: (" + run.getDiskInfo() + ")");

        Gui.chartPanel.getChart().getTitle().setVisible(true);
        Gui.chartPanel.getChart().getTitle().setText(run.getDiskInfo());

        for (int m = startFileNum; m < startFileNum + numOfMarks && !worker.gotCancelled(); m++) {

            if (App.multiFile) {
                testFile = new File(dataDir.getAbsolutePath()
                        + File.separator + "testdata" + m + ".jdm");
            }
            rMark = new DiskMark(READ);
            rMark.setMarkNum(m);
            long startTime = System.nanoTime();
            long totalBytesReadInMark = 0;

            try {
                try (RandomAccessFile rAccFile = new RandomAccessFile(testFile, "r")) {
                    for (int b = 0; b < numOfBlocks; b++) {
                        if (blockSequence == DiskRun.BlockSequence.RANDOM) {
                            int rLoc = Util.randInt(0, numOfBlocks - 1);
                            rAccFile.seek((long) rLoc * blockSize);
                        } else {
                            rAccFile.seek((long) b * blockSize);
                        }
                        rAccFile.readFully(blockArr, 0, blockSize);
                        totalBytesReadInMark += blockSize;
                        rUnitsComplete++;
                        unitsComplete = rUnitsComplete + wUnitsComplete;
                        percentComplete = (float) unitsComplete / (float) unitsTotal * 100f;
                        worker.setProgressStatus((int) percentComplete);
                    }
                }
            } catch (IOException ex) {
                Logger.getLogger(App.class.getName()).log(Level.SEVERE, null, ex);
            }
            long endTime = System.nanoTime();
            long elapsedTimeNs = endTime - startTime;
            double sec = (double) elapsedTimeNs / (double) 1000000000;
            double mbRead = (double) totalBytesReadInMark / (double) MEGABYTE;
            rMark.setBwMbSec(mbRead / sec);
            msg("m:" + m + " READ IO is " + rMark.getBwMbSec() + " MB/s    "
                    + "(MBread " + mbRead + " in " + sec + " sec)");
            App.updateMetrics(rMark);
            worker.post(rMark);

            run.setRunMax(rMark.getCumMax());
            run.setRunMin(rMark.getCumMin());
            run.setRunAvg(rMark.getCumAvg());
            run.setEndTime(new Date());

        }

        //Register Slack Observer
        /*bmObserver slackObserver = new slackObserver(run.getRunAvg(), run.getRunMax());
        registerObserver(slackObserver);*/

        //Notify Observers
        //notifyObservers();
    }

    //Observer methods

    /**
     * Register method to add new observer to list of observers
     * @param o observer to add
     */
    public void registerObserver(bmObserver o){
        observers.add(o);
    }


    /**
     * Notify method that loops through the observer list and calls each update() method
     */
    public void notifyObservers(){
        for (bmObserver o: observers) {
            o.update();
        }
    }

    public DiskRun getRun() {return run;}

    public DiskMark getMark() {return rMark;}
}
