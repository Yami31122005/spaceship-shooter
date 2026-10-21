package com.example.spaceshooter.exception;

/**
 * Checked exception raised when a data file cannot be found or read.
 * Checked on purpose: the caller must decide whether the game can start without its data.
 */
public class DataLoadException extends Exception {

    public DataLoadException(String message) {
        super(message);
    }

    public DataLoadException(String message, Throwable cause) {
        super(message, cause);
    }
}
