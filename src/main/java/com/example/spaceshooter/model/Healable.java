package com.example.spaceshooter.model;

/**
 * Capability interface: anything that can recover health.
 * Kept separate from Ship so that power-ups depend on the capability, not on a concrete class.
 */
public interface Healable {

    void heal(int amount);
}
