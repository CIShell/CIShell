/*
 * InfoVis CyberInfrastructure: A Data-Code-Compute Resource for Research
 * and Education in Information Visualization (http://iv.slis.indiana.edu/).
 *
 * Created on Mar 8, 2005 at Indiana University.
 */
package edu.iu.iv.internal;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Calendar;

import edu.iu.iv.core.IVC;
import edu.iu.iv.core.IVCPreferences;
import edu.iu.iv.core.Logger;


/**
 * Implementation of Logger interface, used by IVC to log
 * user events and errors to file. The location of the file used
 * is configurable through the preferences of IVC, and the specified
 * name will have a timestamp appended to it to produce a unique file
 * for each session.
 *
 * @author Team IVC
 */
public class BasicLogger implements Logger {
    private static final String ENTRY_TOKEN = "!ENTRY";
    private static final String INFO_TOKEN = "INFO:";
    private static final String WARNING_TOKEN = "WARNING:";
    private static final String ERROR_TOKEN = "ERROR:";
    private File file;
    private String fileLocation;
    private int maxSize;

    /**
     * Creates a new BasicLogger object.
     *
     * @param filename the name of the file for this Logger to use
     * @param maxSize the maximum size that the given File can grow to,
     *    in kilobytes.  When this size is met, the oldest items will
     *    be removed to make room for new ones.
     */
    public BasicLogger(String filename, int maxSize) {
        this.maxSize = maxSize * 1024; //keep it in bytes
        fileLocation = filename;
        generateUniqueFile(fileLocation);
    }
    
    /**
     * Return the File used by this Logger
     * 
     * @return the File used by this Logger
     */
    public File getFile(){
        return file;
    }

    /**
     * Sets the maximum size that the log file can grow to, in
     * kilobytes. When the limit is reached and new items are to be
     * added, the log will trim the oldest entries until enough
     * space is available.
     *
     * @param maxSize the maximum size that the log file can grow to
     */
    public void setMaxSize(int maxSize) {
        this.maxSize = maxSize * 1024;

        //trim file if needed
        long size = file.length();

        if (size > maxSize) {
            remove((int) (size - maxSize));
        }
    }

    /**
     * Returns the maximum allowed size of the log file, in kilobytes.
     *
     * @return the size, in kilobytes, that this Logger's file is
     * allowed to grow to
     */
    public int getMaxSize() {
        return maxSize / 1024;
    }

    /**
     * Log the given message with the tag of INFO.
     *
     * @param message the informational message to log
     */
    public void info(String message) {
        write(INFO_TOKEN, message);
    }

    /**
     * Log the given message with the tag of WARNING.
     *
     * @param message the warning message to log
     */
    public void warning(String message) {
        write(WARNING_TOKEN, message);
    }

    /**
     * Log the given message with the tag of ERROR.
     *
     * @param message the error message to log
     */
    public void error(String message) {
        write(ERROR_TOKEN, message);
    }

    /*
     * create log file with given name plus unique timestamp
     */
    private void generateUniqueFile(String filename) {
        int index = filename.lastIndexOf(File.separator);
        String path = filename.substring(0, index + 1);
        String tmpName = filename.substring(index + 1);

        index = tmpName.lastIndexOf(".");

        String preExtension = tmpName.substring(0, index);
        String extension = tmpName.substring(index);

        Calendar now = Calendar.getInstance();
        String month = (now.get(Calendar.MONTH) + 1) + ""; //zero based

        if (month.length() == 1) {
            month = "0" + month;
        }

        String day = now.get(Calendar.DAY_OF_MONTH) + "";

        if (day.length() == 1) {
            day = "0" + day;
        }

        String year = now.get(Calendar.YEAR) + "";
        String hour = now.get(Calendar.HOUR_OF_DAY) + "";

        if (hour.length() == 1) {
            hour = "0" + hour;
        }

        String minute = now.get(Calendar.MINUTE) + "";

        if (minute.length() == 1) {
            minute = "0" + minute;
        }

        String second = now.get(Calendar.SECOND) + "";

        if (second.length() == 1) {
            second = "0" + second;
        }

        String timestamp = "-" + month + "-" + day + "-" + year + "-" + hour +
            "-" + minute + "-" + second;

        fileLocation = path + preExtension + timestamp + extension;
        file = new File(fileLocation);

        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
            }
        }
    }

    /*
     * return a timestamp string in the form:
     * Fri, Mar 4 2005 10:14:26 PM 
     * for entries in the log
     */
    private String getTimeStamp() {
        Calendar now = Calendar.getInstance();
        int month = now.get(Calendar.MONTH);
        int day = now.get(Calendar.DAY_OF_MONTH);
        int year = now.get(Calendar.YEAR);

        String hour = now.get(Calendar.HOUR) + "";

        if (hour.length() == 1) {
            hour = "0" + hour;
        }

        String minute = now.get(Calendar.MINUTE) + "";

        if (minute.length() == 1) {
            minute = "0" + minute;
        }

        String second = now.get(Calendar.SECOND) + "";

        if (second.length() == 1) {
            second = "0" + second;
        }

        int dayValue = now.get(Calendar.DAY_OF_WEEK);

        String dayString = getDay(dayValue);
        String monthString = getMonth(month);

        String amPm;

        if (Calendar.AM_PM == Calendar.AM) {
            amPm = "AM";
        } else {
            amPm = "PM";
        }

        String timestamp = dayString + ", " + monthString + " " + day + " " +
            year + " " + hour + ":" + minute + ":" + second + " " + amPm;

        return timestamp;
    }

    /*
     * return the String representation of the month that the given int
     * represents in the constants given in java.util.Calendar
     */
    private String getMonth(int monthValue) {
        if (monthValue == Calendar.JANUARY) {
            return "Jan";
        }

        if (monthValue == Calendar.FEBRUARY) {
            return "Feb";
        }

        if (monthValue == Calendar.MARCH) {
            return "Mar";
        }

        if (monthValue == Calendar.APRIL) {
            return "Apr";
        }

        if (monthValue == Calendar.MAY) {
            return "May";
        }

        if (monthValue == Calendar.JUNE) {
            return "June";
        }

        if (monthValue == Calendar.JULY) {
            return "July";
        }

        if (monthValue == Calendar.AUGUST) {
            return "Aug";
        }

        if (monthValue == Calendar.SEPTEMBER) {
            return "Sept";
        }

        if (monthValue == Calendar.OCTOBER) {
            return "Oct";
        }

        if (monthValue == Calendar.NOVEMBER) {
            return "Nov";
        }

        return "Dec";
    }

    /*
     * return the String representation of the day of the week that the given int
     * represents in the constants given in java.util.Calendar
     */
    private String getDay(int dayValue) {
        if (dayValue == Calendar.SUNDAY) {
            return "Sun";
        }

        if (dayValue == Calendar.MONDAY) {
            return "Mon";
        }

        if (dayValue == Calendar.TUESDAY) {
            return "Tues";
        }

        if (dayValue == Calendar.WEDNESDAY) {
            return "Wed";
        }

        if (dayValue == Calendar.THURSDAY) {
            return "Thurs";
        }

        if (dayValue == Calendar.FRIDAY) {
            return "Fri";
        }

        return "Sat";
    }

    /*
     * write the given message to the log, with the given token
     * (one of the constants INFO, WARNING, ERROR at the top of this class)
     */
    private void write(String token, String message) {
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(file, true));
            writer.write(ENTRY_TOKEN + " " + token + " " + getTimeStamp());
            writer.newLine();
            writer.write(message);
            writer.newLine();
            writer.newLine();
            writer.close();
        } catch (IOException e) {
        }

        long size = file.length();

        if (size > maxSize) {
            remove((int) (size - maxSize));
        }
    }

    /*
     * removes items from the log until the given size is removed, this may
     * result (and most often does) in more than the given size removed, since items
     * are removed as a whole, not leaving partial items, so often more than the 
     * desired kb are removed to keep the log looking sane.
     */
    private void remove(int size) {
        try {
            //remove one item at a time until size is met
            String tmpFileLocation = IVC.getInstance().getConfiguration().getString(IVCPreferences.TEMPORARY_FILES_FOLDER_PREFERENCE);
            
            if (tmpFileLocation == null) {
                tmpFileLocation = System.getProperty("user.dir");
            }
            
            File trashFile = new File(tmpFileLocation + File.separator + "trash.log");
            int removedSize = 0;
            String line = null;
            BufferedReader reader = new BufferedReader(new FileReader(file));

            while (removedSize < size) {
                line = removeItem(reader, trashFile);
                removedSize = (int) trashFile.length();
            }

            //write the remainder to new output file
            trashFile.delete();

            File tmpFile = new File(tmpFileLocation + File.separator +
                    "tmp.log");
            BufferedWriter tmpWriter = new BufferedWriter(new FileWriter(
                        tmpFile, true));

            while (line != null) {
                tmpWriter.write(line);
                tmpWriter.newLine();
                line = reader.readLine();
            }

            tmpWriter.close();
            reader.close();

            //get rid of original and rename tmp
            file.delete();
            tmpFile.renameTo(new File(fileLocation));
            file = new File(fileLocation);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /*
     * removes a single item from the logs file and returns the next string that is read -
     * the next item that is kept.  This uses the given trash file to write out things
     * that were removed, which makes it possible to see exactly how much was removed by
     * checking the size of this trash file. This file is later discarded in the method above.
     */
    private String removeItem(BufferedReader reader, File trashFile) {
        String result = null;

        try {
            BufferedWriter tmpWriter = new BufferedWriter(new FileWriter(
                        trashFile));

            //read through the file until the first item is passed
            boolean done = false;
            boolean started = false;
            String line = reader.readLine();

            while (!done && (line != null)) {
                if (line.startsWith(ENTRY_TOKEN)) {
                    if (started) {
                        done = true;
                    } else {
                        started = true;
                        line = reader.readLine();
                    }
                } else {
                    tmpWriter.write(line);
                    tmpWriter.newLine();
                    line = reader.readLine();
                }
            }

            tmpWriter.close();
            result = line;
        } catch (IOException e) {
            e.printStackTrace();
        }

        return result;
    }
}
