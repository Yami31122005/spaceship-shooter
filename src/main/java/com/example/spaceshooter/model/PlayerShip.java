package com.example.spaceshooter.model;

import java.util.Optional;

/**
 * The ship controlled by the player. Moves horizontally and fires with a cooldown.
 */
public class PlayerShip extends Ship implements Shooter {

    private static final int MAX_HEALTH = 3;
    private static final double SPEED = 300;            // pixels per second
    private static final double BASE_COOLDOWN = 0.25;   // seconds between two shots

    private final double fieldWidth;
    private double cooldownRemaining;
    private double fireRateMultiplier = 1.0;
    private double boostRemaining;
    private int direction;

    public PlayerShip(double fieldWidth, double fieldHeight) {
        super(fieldWidth / 2 - 20, fieldHeight - 60, 40, 30, MAX_HEALTH);
        this.fieldWidth = fieldWidth;
    }

    /** @param direction -1 (left), 0 (stop) or 1 (right) */
    public void setDirection(int direction) {
        if (direction < -1 || direction > 1) {
            throw new IllegalArgumentException("direction must be -1, 0 or 1");
        }
        this.direction = direction;
    }

    @Override
    public void update(double dt) {
        cooldownRemaining = Math.max(0, cooldownRemaining - dt);
        updateBoost(dt);

        double newX = getX() + direction * SPEED * dt;
        // Clamp instead of bouncing: the ship simply stops at the screen edge.
        setX(Math.max(0, Math.min(fieldWidth - getWidth(), newX)));
    }

    @Override
    public Optional<Bullet> shoot() {
        if (isDestroyed() || cooldownRemaining > 0) {
            return Optional.empty();
        }
        cooldownRemaining = BASE_COOLDOWN / fireRateMultiplier;
        return Optional.of(new Bullet(getX() + getWidth() / 2 - 2, getY() - 10));
    }

    /**
     * Temporarily speeds up firing. Used by the rapid-fire power-up.
     *
     * @param multiplier fire rate factor, at least 1
     * @param durationSeconds how long the boost lasts
     */
    public void boostFireRate(double multiplier, double durationSeconds) {
        if (multiplier < 1) {
            throw new IllegalArgumentException("multiplier must be at least 1");
        }
        if (durationSeconds <= 0) {
            throw new IllegalArgumentException("duration must be positive");
        }
        this.fireRateMultiplier = multiplier;
        this.boostRemaining = durationSeconds;
    }

    public double getFireRateMultiplier() {
        return fireRateMultiplier;
    }

    private void updateBoost(double dt) {
        if (boostRemaining <= 0) {
            return;
        }
        boostRemaining -= dt;
        if (boostRemaining <= 0) {
            boostRemaining = 0;
            fireRateMultiplier = 1.0;
        }
    }
}
