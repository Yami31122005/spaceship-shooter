package com.example.spaceshooter.model;

/**
 * Restores one point of health.
 */
public class RepairKit extends PowerUp {

    private static final int HEAL_AMOUNT = 1;

    public RepairKit(double x, double y) {
        super(x, y);
    }

    @Override
    public void applyTo(PlayerShip ship) {
        ship.heal(HEAL_AMOUNT);
    }
}
