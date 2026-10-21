package com.example.spaceshooter.service;

import com.example.spaceshooter.model.EnemyDefinition;
import com.example.spaceshooter.model.EnemyKind;
import com.example.spaceshooter.model.GameStatus;
import com.example.spaceshooter.model.Wave;
import com.example.spaceshooter.model.WaveEntry;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class GameTest {

    private static Game newGame() {
        WaveManager waves = new WaveManager(
                List.of(
                        new Wave(1, List.of(new WaveEntry(EnemyKind.SCOUT, 3))),
                        new Wave(2, List.of(new WaveEntry(EnemyKind.TANK, 1)))),
                List.of(
                        new EnemyDefinition(EnemyKind.SCOUT, 1, 100, 10),
                        new EnemyDefinition(EnemyKind.TANK, 5, 45, 60)));
        return new Game(waves, new Random(1));
    }

    @Test
    void shouldSpawnTheFirstWaveOnCreation() {
        Game game = newGame();

        assertEquals(1, game.getCurrentWaveNumber());
        assertEquals(3, game.getEnemies().size());
        assertEquals(GameStatus.RUNNING, game.getStatus());
    }

    @Test
    void shouldRejectNonPositiveTimeStep() {
        Game game = newGame();

        assertThrows(IllegalArgumentException.class, () -> game.update(0));
    }

    @Test
    void shouldStartWithZeroScore() {
        assertEquals(0, newGame().getScore());
    }
}
