package com.example.spaceshooter.model;

/**
 * Anything that occupies a rectangle on the play field.
 * Position is protected so subclasses move themselves; outside code reads it through getters.
 */
public abstract class Entity {

    private double x;
    private double y;
    private final double width;
    private final double height;
    private boolean alive = true;

    protected Entity(double x, double y, double width, double height) {
        if (width <= 0 || height <= 0) {
            throw new IllegalArgumentException("Entity size must be positive");
        }
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    /** Advances this entity by {@code dt} seconds. */
    public abstract void update(double dt);

    /** Axis-aligned bounding box test. Dead entities never collide. */
    public boolean intersects(Entity other) {
        return alive && other.alive
                && x < other.x + other.width && x + width > other.x
                && y < other.y + other.height && y + height > other.y;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public double getWidth() {
        return width;
    }

    public double getHeight() {
        return height;
    }

    public boolean isAlive() {
        return alive;
    }

    /** Marks the entity for removal; the game loop discards it on the next cleanup. */
    public void destroy() {
        alive = false;
    }

    protected void setX(double x) {
        this.x = x;
    }

    protected void setY(double y) {
        this.y = y;
    }
}
