package edu.touro.mco152.bm.commands;

/**
 * An invoker class that constructs the invoker with 2 commands, read and write
 */
public class simpleInvoker {
    private ICommand writeBm;
    private ICommand readBm;

    public simpleInvoker(ICommand writeBm, ICommand readBm){
        this.writeBm = writeBm;
        this.readBm = readBm;
    }

    public void writeBm() {
        writeBm.execute();
    }

    public void readBM() {
        readBm.execute();
    }
}
