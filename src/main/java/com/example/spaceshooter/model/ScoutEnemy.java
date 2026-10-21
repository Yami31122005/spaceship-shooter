package com.example.spaceshooter.model;

/**
 * Fast enemy flying straight down.
 */
public class ScoutEnemy extends Enemy {

    public ScoutEnemy(double x, double y, int health, double speed, int points) {
        super(x, y, health, speed, points);
    }

    @Override
    protected void move(double dt) {
        setY(getY() + getSpeed() * dt);
    }
}
