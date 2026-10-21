package com.example.spaceshooter.service;

import com.example.spaceshooter.exception.InvalidGameDataException;
import com.example.spaceshooter.model.EnemyDefinition;
import com.example.spaceshooter.model.EnemyKind;
import com.example.spaceshooter.model.Wave;
import com.example.spaceshooter.model.WaveEntry;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class WaveManagerTest {

    private static final List<EnemyDefinition> DEFINITIONS = List.of(
            new EnemyDefinition(EnemyKind.SCOUT, 1, 100, 10),
            new EnemyDefinition(EnemyKind.TANK, 5, 45, 60));

    @Test
    void shouldHandOutWavesInOrder() {
        WaveManager manager = new WaveManager(List.of(
                new Wave(1, List.of(new WaveEntry(EnemyKind.SCOUT, 2))),
                new Wave(2, List.of(new WaveEntry(EnemyKind.TANK, 1)))), DEFINITIONS);

        assertEquals(1, manager.nextWave().orElseThrow().number());
        assertEquals(2, manager.nextWave().orElseThrow().number());
        assertTrue(manager.nextWave().isEmpty());
        assertFalse(manager.hasMoreWaves());
    }

    @Test
    void shouldRejectWaveUsingAnUndefinedEnemy() {
        List<Wave> waves = List.of(new Wave(1, List.of(new WaveEntry(EnemyKind.FIGHTER, 1))));

        assertThrows(InvalidGameDataException.class, () -> new WaveManager(waves, DEFINITIONS));
    }

    @Test
    void shouldRejectDuplicateEnemyDefinitions() {
        List<EnemyDefinition> duplicated = List.of(
                new EnemyDefinition(EnemyKind.SCOUT, 1, 100, 10),
                new EnemyDefinition(EnemyKind.SCOUT, 2, 80, 20));
        List<Wave> waves = List.of(new Wave(1, List.of(new WaveEntry(EnemyKind.SCOUT, 1))));

        assertThrows(InvalidGameDataException.class, () -> new WaveManager(waves, duplicated));
    }

    @Test
    void shouldRejectEmptyWaveList() {
        assertThrows(InvalidGameDataException.class, () -> new WaveManager(List.of(), DEFINITIONS));
    }
}
