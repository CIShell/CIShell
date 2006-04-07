/*
 * Created on Aug 5, 2004
 */
package edu.iu.iv.common.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;

/**
 * Copies an input stream as data comes in
 * to an output stream.
 * 
 * @author Team IVC
 */
public class StreamRedirecter extends Thread {
    private InputStream inputStream;

    private OutputStream outputStream;

    private String messagePrefix;

    public StreamRedirecter(InputStream inputStream, OutputStream outputStream) {
        this.inputStream = inputStream;
        this.outputStream = outputStream;
    }

    public StreamRedirecter(InputStream inputStream, OutputStream outputStream, String messagePrefix) {
        this.inputStream = inputStream;
        this.outputStream = outputStream;
        this.messagePrefix = messagePrefix ;
    }
    
    public void setMessagePrefix(String messagePrefix) {
        this.messagePrefix = messagePrefix;
    }

    public void run() {
        try {
            if (messagePrefix == null)
                messagePrefix = "" ;
            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(inputStream));
            BufferedWriter writer = new BufferedWriter(
                    new OutputStreamWriter(outputStream));
            String line = null;

            while (reader != null) {
                line = reader.readLine() ;
                if (line == null)
                    break ;
                writer.write(messagePrefix + line);
                writer.newLine();
                writer.flush();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
