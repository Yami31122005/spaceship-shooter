package com.example.spaceshooter.model;

import com.example.spaceshooter.exception.InvalidGameDataException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;

class EnemyDefinitionTest {

    @Test
    void shouldRejectZeroHealth() {
        assertThrows(InvalidGameDataException.class,
                () -> new EnemyDefinition(EnemyKind.SCOUT, 0, 100, 10));
    }

    @Test
    void shouldRejectNonPositiveSpeed() {
        assertThrows(InvalidGameDataException.class,
                () -> new EnemyDefinition(EnemyKind.SCOUT, 1, 0, 10));
    }

    @Test
    void shouldRejectNegativePoints() {
        assertThrows(InvalidGameDataException.class,
                () -> new EnemyDefinition(EnemyKind.SCOUT, 1, 100, -5));
    }

    @Test
    void shouldRejectWaveEntryWithoutEnemies() {
        assertThrows(InvalidGameDataException.class, () -> new WaveEntry(EnemyKind.TANK, 0));
    }
}
