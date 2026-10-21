package com.example.spaceshooter.model;

/**
 * Base class for everything with health: the player ship and the enemies.
 * Owns the health rules so that damage and healing behave identically for both.
 */
public abstract class Ship extends Entity implements Healable {

    private final int maxHealth;
    private int health;

    protected Ship(double x, double y, double width, double height, int maxHealth) {
        super(x, y, width, height);
        if (maxHealth <= 0) {
            throw new IllegalArgumentException("maxHealth must be positive");
        }
        this.maxHealth = maxHealth;
        this.health = maxHealth;
    }

    public void takeDamage(int amount) {
        if (amount < 0) {
            throw new IllegalArgumentException("damage must not be negative");
        }
        health = Math.max(0, health - amount);
        if (health == 0) {
            destroy();
        }
    }

    @Override
    public void heal(int amount) {
        if (amount < 0) {
            throw new IllegalArgumentException("heal amount must not be negative");
        }
        // A destroyed ship cannot be repaired: the power-up is wasted, not an error.
        if (isDestroyed()) {
            return;
        }
        health = Math.min(maxHealth, health + amount);
    }

    public int getHealth() {
        return health;
    }

    public int getMaxHealth() {
        return maxHealth;
    }

    public boolean isDestroyed() {
        return health == 0;
    }
}
