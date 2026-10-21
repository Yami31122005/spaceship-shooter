package com.example.spaceshooter.model;

/**
 * A collectible that drifts downward. Subclasses decide what picking it up does.
 */
public abstract class PowerUp extends Entity implements Collectible {

    private static final double FALL_SPEED = 120; // pixels per second

    protected PowerUp(double x, double y) {
        super(x, y, 24, 24);
    }

    @Override
    public void update(double dt) {
        setY(getY() + FALL_SPEED * dt);
    }
}
