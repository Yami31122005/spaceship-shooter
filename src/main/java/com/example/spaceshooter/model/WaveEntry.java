package com.example.spaceshooter.model;

import com.example.spaceshooter.exception.InvalidGameDataException;

import java.util.Objects;

/**
 * "Spawn {count} enemies of {kind}" as one line of a wave.
 */
public record WaveEntry(EnemyKind kind, int count) {

    public WaveEntry {
        Objects.requireNonNull(kind, "kind");
        if (count <= 0) {
            throw new InvalidGameDataException("count must be > 0 for " + kind);
        }
    }
}
