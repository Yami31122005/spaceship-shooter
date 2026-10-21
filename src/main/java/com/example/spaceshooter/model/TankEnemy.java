package com.example.spaceshooter.model;

/**
 * Slow, tough enemy that sways left and right, reversing direction every few seconds.
 */
public class TankEnemy extends Enemy {

    private static final double TURN_INTERVAL = 2.0; // seconds between direction changes
    private static final double SWAY_SPEED = 40;     // pixels per second

    private double timeSinceTurn;
    private int horizontalDirection = 1;

    public TankEnemy(double x, double y, int health, double speed, int points) {
        super(x, y, health, speed, points);
    }

    @Override
    protected void move(double dt) {
        setY(getY() + getSpeed() * dt);

        timeSinceTurn += dt;
        if (timeSinceTurn >= TURN_INTERVAL) {
            horizontalDirection = -horizontalDirection;
            timeSinceTurn = 0;
        }
        setX(getX() + horizontalDirection * SWAY_SPEED * dt);
    }
}
