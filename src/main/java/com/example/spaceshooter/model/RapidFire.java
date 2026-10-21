package com.example.spaceshooter.model;

/**
 * Doubles the fire rate for a limited time.
 */
public class RapidFire extends PowerUp {

    private static final double MULTIPLIER = 2.0;
    private static final double DURATION_SECONDS = 8.0;

    public RapidFire(double x, double y) {
        super(x, y);
    }

    @Override
    public void applyTo(PlayerShip ship) {
        ship.boostFireRate(MULTIPLIER, DURATION_SECONDS);
    }
}
