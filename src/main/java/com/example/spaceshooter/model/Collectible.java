package com.example.spaceshooter.model;

/**
 * Capability interface: an object the player can pick up and whose effect is applied to the ship.
 */
public interface Collectible {

    void applyTo(PlayerShip ship);
}
