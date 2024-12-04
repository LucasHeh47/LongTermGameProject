package com.lucasj.gamedev.misc;

import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintStream;
import java.io.PrintWriter;

public class Debug {
	
	public static boolean build = false;
    private static final String LOG_FILE_NAME = "debug.log";

    public static void initializeLogging() {
    	if(!build) return;
        try {
            // Create a FileOutputStream for the log file
            FileOutputStream fos = new FileOutputStream(LOG_FILE_NAME, true); // Append mode

            // Create a PrintStream for both System.out and System.err
            PrintStream logStream = new PrintStream(fos, true);

            // Redirect standard output and standard error to the log file
            System.setOut(logStream);
            System.setErr(logStream);

            // Optional: Write an initial log entry to indicate logging started
            System.out.println("[INFO] Logging initialized.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
	
	public static void log(String str, Object message) {
		String logMessage = "[" + str + "] " + message;

        if (build) {
            try (FileWriter fileWriter = new FileWriter(LOG_FILE_NAME, true);
                 PrintWriter printWriter = new PrintWriter(fileWriter)) {
                printWriter.println(logMessage);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            // Print to console for development/debugging
            System.out.println(logMessage);
        }
	}
	
	public static void log(Object obj, Object message) {
		String logMessage = "[" + obj.getClass().getSimpleName() + "] " + message;

	    if (build) {
	        // Write to the debug.log file
	        try (FileWriter fileWriter = new FileWriter(LOG_FILE_NAME, true);
	             PrintWriter printWriter = new PrintWriter(fileWriter)) {
	            printWriter.println(logMessage);
	        } catch (IOException e) {
	            e.printStackTrace();
	        }
	    } else {
	        // Print to console for development/debugging
	        System.out.println(logMessage);
		}
	}
	
	public static void err(Object obj, Object message) {
		String logMessage = "[" + obj.getClass().getSimpleName() + "] " + message;

	    if (build) {
	        // Write to the debug.log file
	        try (FileWriter fileWriter = new FileWriter(LOG_FILE_NAME, true);
	             PrintWriter printWriter = new PrintWriter(fileWriter)) {
	            printWriter.println("[ERROR] " + logMessage);
	        } catch (IOException e) {
	            e.printStackTrace();
	        }
	    } else {
	        // Print to console for development/debugging
	        System.err.println(logMessage);
		}
	}
	
}
