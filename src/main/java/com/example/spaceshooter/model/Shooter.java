package com.example.spaceshooter.model;

import java.util.Optional;

/**
 * Capability interface: anything that can emit a bullet.
 * Returns an empty Optional when it cannot fire yet (cooldown, destroyed...).
 */
public interface Shooter {

    Optional<Bullet> shoot();
}
