package com.example.spaceshooter.service;

import com.example.spaceshooter.model.Enemy;

import java.util.Objects;

/**
 * Score rules: kills give points multiplied by a streak bonus, taking damage breaks the streak,
 * clearing a wave gives a bonus.
 */
public class ScoreService {

    private static final int MAX_MULTIPLIER = 5;
    private static final int KILLS_PER_MULTIPLIER_STEP = 3;
    private static final int WAVE_BONUS_PER_NUMBER = 100;

    private int score;
    private int streak;

    public void registerKill(Enemy enemy) {
        Objects.requireNonNull(enemy, "enemy");
        score += enemy.getPoints() * currentMultiplier();
        streak++;
    }

    /** Taking damage resets the streak: a clean run is what the multiplier rewards. */
    public void registerPlayerHit() {
        streak = 0;
    }

    public void addWaveBonus(int waveNumber) {
        if (waveNumber < 1) {
            throw new IllegalArgumentException("wave number must be at least 1");
        }
        score += WAVE_BONUS_PER_NUMBER * waveNumber;
    }

    public int currentMultiplier() {
        return Math.min(MAX_MULTIPLIER, 1 + streak / KILLS_PER_MULTIPLIER_STEP);
    }

    public int getScore() {
        return score;
    }

    public int getStreak() {
        return streak;
    }
}
