package com.example.spaceshooter.model;

/**
 * A projectile fired by the player, travelling upward.
 */
public class Bullet extends Entity {

    private static final double SPEED = 500; // pixels per second

    public Bullet(double x, double y) {
        super(x, y, 4, 10);
    }

    @Override
    public void update(double dt) {
        setY(getY() - SPEED * dt);
        if (getY() + getHeight() < 0) {
            destroy();
        }
    }
}
