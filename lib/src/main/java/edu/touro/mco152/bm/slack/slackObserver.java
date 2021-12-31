package edu.touro.mco152.bm.slack;

import edu.touro.mco152.bm.bmObserver;

/**
 * A Slack observer class that implements our bmObserver class
 */
public class slackObserver implements bmObserver {

    double runAvg;
    double runMax;

    /**
     * @param runAvg the DiskMark average run time
     * @param runMax the DiskMark max run time
     */
    public slackObserver(double runAvg, double runMax) {
        this.runAvg = runAvg;
        this.runMax = runMax;
    }

    /**
     * Overrides the update() method and checks to see if the
     * run max time is greater than 3% of the run average time.
     * If it is, slack manager sends a message to slack channel
     */
    @Override
    public void update() {
        if (runMax > (runAvg * 1.03)){
            SlackManager slackmgr = new SlackManager("BadBM");
            slackmgr.postMsg2OurChannel(":poop:The run max time was greater than 3% of the run average:poop:");
        }
    }
}
