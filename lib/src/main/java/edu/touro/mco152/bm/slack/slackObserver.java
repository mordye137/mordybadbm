package edu.touro.mco152.bm.slack;

import edu.touro.mco152.bm.bmObserver;

public class slackObserver implements bmObserver {

    double runAvg;
    double runMax;

    public slackObserver(double runAvg, double runMax) {
        this.runAvg = runAvg;
        this.runMax = runMax;
    }

    @Override
    public void update() {
        if (runMax > (runAvg * 1.03)){
            SlackManager slackmgr = new SlackManager("BadBM");
            slackmgr.postMsg2OurChannel("ruh roh");
        }
    }
}
