/*
 * Created on Jun 7, 2004
 * Shashikant
 */
package edu.iu.iv.common.util.perl;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;
import java.util.StringTokenizer;

import edu.iu.iv.common.util.SystemCommandRunner;

/**
 * Runs perl code.
 * 
 * @author Team IVC
 * @version 0.1
 *  
 */
public class PerlRunner extends SystemCommandRunner {

    public static final int PERL_INSTALLED_YES = 0;

    public static final int PERL_INSTALLED_NO = 1;

    public static final int PERL_INSTALLED_UNSURE = 2;

    private static int PERL_INSTALLED_STATUS = -1;

    private static final String DEFAULT_PERL_PATH = "perl";

    private static String PERL_PATH;

    private static String PERL_VERSION = "0.0";

    public PerlRunner() {
        super();
        if (PERL_INSTALLED_STATUS == -1)
            setPerlPath(getPerlPath());
    }

    public String getPerlVersion() {
        return PERL_VERSION;
    }

    private void confirmPerlInstallStatus() {
        BufferedReader reader;
        try {
            Process proc = Runtime.getRuntime().exec(PERL_PATH + " -version");
            reader = new BufferedReader(new InputStreamReader(proc
                    .getInputStream()));
            proc.waitFor();
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.toLowerCase().startsWith("this is perl")) {
                    StringTokenizer tokenizer = new StringTokenizer(line);
                    while (tokenizer.hasMoreTokens()) {
                        String ver = tokenizer.nextToken();
                        if (ver.matches("v\\d\\.\\d"))
                            PERL_VERSION = ver;
                    }
                    PERL_INSTALLED_STATUS = PERL_INSTALLED_YES;
                    reader.close();
                    break;
                } else
                    PERL_INSTALLED_STATUS = PERL_INSTALLED_NO;
            }
        } catch (IOException ioe) {
            PERL_INSTALLED_STATUS = PERL_INSTALLED_UNSURE;
        } catch (InterruptedException ie) {
            PERL_INSTALLED_STATUS = PERL_INSTALLED_UNSURE;
        }
    }

    public int getPerlInstallationStatus() {
        return PERL_INSTALLED_STATUS;
    }

    public String getPerlPath() {
        if (PERL_PATH == null) {
            PERL_PATH = DEFAULT_PERL_PATH;
        }
        return PERL_PATH;
    }

    public void setPerlPath(String path) {
        PERL_PATH = path;
        confirmPerlInstallStatus();
    }

    public void run(String perlCommand) throws IOException,
            NullPointerException, InterruptedException {
        perlCommand = PERL_PATH + " " + perlCommand;
        super.run(perlCommand);
    }

    public void run(String perlScript, List params) throws IOException,
            InterruptedException, NullPointerException {

        for (int i = 0; i < params.size(); ++i)
            perlScript += " " + (String) params.get(i);

        run(perlScript);
    }

    public void run(String perlScript, String[] params) throws IOException,
            InterruptedException, NullPointerException {

        for (int i = 0; i < params.length; ++i)
            perlScript += " " + params[i];

        run(perlScript);
    }
}