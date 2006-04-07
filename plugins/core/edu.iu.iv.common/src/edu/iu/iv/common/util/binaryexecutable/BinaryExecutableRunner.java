/*
 * Created on Aug 5, 2004
 */
package edu.iu.iv.common.util.binaryexecutable;

import java.io.IOException;
import java.util.List;

import edu.iu.iv.common.util.SystemCommandRunner;

/**
 * @author Shashikant
 */
public class BinaryExecutableRunner extends SystemCommandRunner {

    public BinaryExecutableRunner() {
        super();
    }

    public void run(String cmd) throws IOException,
            NullPointerException, InterruptedException {
        super.run(cmd);
    }

    public void run(String cmd, List params) throws IOException,
            InterruptedException, NullPointerException {

        for (int i = 0; i < params.size(); ++i)
            cmd += " " + (String) params.get(i);

        run(cmd);
    }

    public void run(String cmd, String[] params) throws IOException,
            InterruptedException, NullPointerException {

        for (int i = 0; i < params.length; ++i)
            cmd += " " + params[i];

        run(cmd);
    }

}