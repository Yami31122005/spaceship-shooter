package com.example.spaceshooter.exception;

/**
 * Unchecked exception for data that is readable but breaks a game rule
 * (unknown enemy kind, negative health, wave referencing an undefined enemy...).
 */
public class InvalidGameDataException extends RuntimeException {

    public InvalidGameDataException(String message) {
        super(message);
    }
}
