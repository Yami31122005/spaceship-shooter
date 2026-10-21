package com.example.spaceshooter.model;

/**
 * Abstract enemy. Health and points come from the data file; the movement pattern is
 * the part each subclass must define. Template method: update() is fixed, move() varies.
 */
public abstract class Enemy extends Ship {

    public static final double SIZE = 36;

    private final double speed;
    private final int points;

    protected Enemy(double x, double y, int health, double speed, int points) {
        super(x, y, SIZE, SIZE, health);
        if (speed <= 0) {
            throw new IllegalArgumentException("speed must be positive");
        }
        if (points < 0) {
            throw new IllegalArgumentException("points must not be negative");
        }
        this.speed = speed;
        this.points = points;
    }

    @Override
    public final void update(double dt) {
        move(dt);
    }

    /** Movement pattern specific to each enemy type. */
    protected abstract void move(double dt);

    protected double getSpeed() {
        return speed;
    }

    public int getPoints() {
        return points;
    }
}
