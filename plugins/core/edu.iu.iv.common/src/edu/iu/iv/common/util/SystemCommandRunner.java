/*
 * Created on Aug 5, 2004
 */
package edu.iu.iv.common.util;

import java.io.IOException;

/**
 * @author Shashikant
 */
public class SystemCommandRunner {

    private StreamRedirecter outStreamRedirecter;

    private StreamRedirecter errStreamRedirecter;

    private boolean printError;

    private boolean printOutput;

    public SystemCommandRunner() {
        outStreamRedirecter = null;
        errStreamRedirecter = null;
        printError = printOutput = true;
    }

    public void setPrintErrors(boolean printErr) {
        this.printError = printErr;
    }

    public void setPrintOutput(boolean printOut) {
        this.printOutput = printOut;
    }

    public void run(String command) throws IOException, InterruptedException,
            NullPointerException {

        if (command == null)
            throw new NullPointerException("Command cannot be null");

        System.out.println("Executing:\n" +command) ;
        Process proc = Runtime.getRuntime().exec(command);

        if (printOutput) {
            outStreamRedirecter = new StreamRedirecter(proc.getInputStream(),
                    System.out);
            outStreamRedirecter.setMessagePrefix("Out> ");
            outStreamRedirecter.start();
        }
        if (printError) {
            errStreamRedirecter = new StreamRedirecter(proc.getErrorStream(),
                    System.err);
            errStreamRedirecter.setMessagePrefix("Error> ");
            errStreamRedirecter.start();
        }
        proc.waitFor();
    }
}