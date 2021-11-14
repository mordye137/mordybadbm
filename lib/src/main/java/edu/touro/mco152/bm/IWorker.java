package edu.touro.mco152.bm;

import java.util.List;

public interface IWorker {

    //doInBackground
    Boolean startExecution() throws Exception;

    //process
    void operation(List<DiskMark> markList);

    //done
    void complete();

    //isCancelled
    boolean gotCancelled();

    //setProgress
    void setProgressStatus(int percentComplete);

    //publish
    void post(DiskMark wMark);


}
