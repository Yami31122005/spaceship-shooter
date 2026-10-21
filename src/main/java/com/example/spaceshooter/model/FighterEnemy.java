package com.example.spaceshooter.model;

/**
 * Medium enemy descending in a sine-wave pattern.
 */
public class FighterEnemy extends Enemy {

    private static final double VERTICAL_FACTOR = 0.6;
    private static final double SWAY_AMPLITUDE = 120; // pixels per second
    private static final double SWAY_FREQUENCY = 3;   // radians per second

    private double elapsed;

    public FighterEnemy(double x, double y, int health, double speed, int points) {
        super(x, y, health, speed, points);
    }

    @Override
    protected void move(double dt) {
        elapsed += dt;
        setY(getY() + getSpeed() * VERTICAL_FACTOR * dt);
        setX(getX() + Math.sin(elapsed * SWAY_FREQUENCY) * SWAY_AMPLITUDE * dt);
    }
}
