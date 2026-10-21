package com.example.spaceshooter.service;

import com.example.spaceshooter.model.Enemy;
import com.example.spaceshooter.model.EnemyDefinition;
import com.example.spaceshooter.model.EnemyKind;
import com.example.spaceshooter.model.ScoutEnemy;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ScoreServiceTest {

    private static Enemy scoutWorth(int points) {
        return EnemyFactory.create(new EnemyDefinition(EnemyKind.SCOUT, 1, 100, points), 0, 0);
    }

    @Test
    void shouldAddPointsForASingleKill() {
        ScoreService service = new ScoreService();

        service.registerKill(scoutWorth(10));

        assertEquals(10, service.getScore());
    }

    @Test
    void shouldRaiseMultiplierAfterThreeKillsInARow() {
        ScoreService service = new ScoreService();
        for (int i = 0; i < 3; i++) {
            service.registerKill(scoutWorth(10));
        }

        assertEquals(2, service.currentMultiplier());
        service.registerKill(scoutWorth(10));
        // 3 kills at x1 (30) + 1 kill at x2 (20)
        assertEquals(50, service.getScore());
    }

    @Test
    void shouldResetStreakWhenPlayerIsHit() {
        ScoreService service = new ScoreService();
        for (int i = 0; i < 3; i++) {
            service.registerKill(scoutWorth(10));
        }

        service.registerPlayerHit();

        assertEquals(0, service.getStreak());
        assertEquals(1, service.currentMultiplier());
    }

    @Test
    void shouldCapMultiplier() {
        ScoreService service = new ScoreService();
        for (int i = 0; i < 100; i++) {
            service.registerKill(scoutWorth(0));
        }

        assertEquals(5, service.currentMultiplier());
    }

    @Test
    void shouldAddWaveBonusScaledByWaveNumber() {
        ScoreService service = new ScoreService();

        service.addWaveBonus(3);

        assertEquals(300, service.getScore());
    }

    @Test
    void shouldRejectInvalidWaveNumber() {
        ScoreService service = new ScoreService();

        assertThrows(IllegalArgumentException.class, () -> service.addWaveBonus(0));
    }

    @Test
    void shouldRejectNullEnemy() {
        ScoreService service = new ScoreService();

        assertThrows(NullPointerException.class, () -> service.registerKill(null));
    }
}
