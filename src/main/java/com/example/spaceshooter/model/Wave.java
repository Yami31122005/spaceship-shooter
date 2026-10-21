package com.example.spaceshooter.model;

import com.example.spaceshooter.exception.InvalidGameDataException;

import java.util.List;

/**
 * A numbered group of enemies. The wave is cleared when all of its enemies are gone.
 */
public record Wave(int number, List<WaveEntry> entries) {

    public Wave {
        if (number <= 0) {
            throw new InvalidGameDataException("wave number must be > 0");
        }
        if (entries == null || entries.isEmpty()) {
            throw new InvalidGameDataException("wave " + number + " has no enemies");
        }
        entries = List.copyOf(entries);
    }
}
