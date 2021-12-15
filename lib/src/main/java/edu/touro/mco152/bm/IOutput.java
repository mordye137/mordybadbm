package edu.touro.mco152.bm;

import java.util.List;

public interface IOutput {

    boolean gotCancelled();

    void setProgressStatus(int percentComplete);

    void post(DiskMark wMark);

    void abort(boolean b);

    void enact();
}
