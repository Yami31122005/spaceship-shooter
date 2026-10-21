package com.example.spaceshooter.model;

import com.example.spaceshooter.exception.InvalidGameDataException;

import java.util.Objects;

/**
 * Stats of one enemy type, loaded from enemies.csv. Immutable value object.
 * Validation happens here so that bad data is rejected as early as possible.
 */
public record EnemyDefinition(EnemyKind kind, int health, double speed, int points) {

    public EnemyDefinition {
        Objects.requireNonNull(kind, "kind");
        if (health <= 0) {
            throw new InvalidGameDataException("health must be > 0 for " + kind);
        }
        if (speed <= 0) {
            throw new InvalidGameDataException("speed must be > 0 for " + kind);
        }
        if (points < 0) {
            throw new InvalidGameDataException("points must be >= 0 for " + kind);
        }
    }
}
