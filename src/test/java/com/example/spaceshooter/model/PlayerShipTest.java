package com.example.spaceshooter.model;

import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PlayerShipTest {

    private static final double FIELD_WIDTH = 800;
    private static final double FIELD_HEIGHT = 600;

    @Test
    void shouldMoveRightWhenDirectionIsOne() {
        PlayerShip ship = new PlayerShip(FIELD_WIDTH, FIELD_HEIGHT);
        double startX = ship.getX();

        ship.setDirection(1);
        ship.update(0.1);

        assertTrue(ship.getX() > startX);
    }

    @Test
    void shouldStayInsideTheFieldWhenMovingPastTheEdge() {
        PlayerShip ship = new PlayerShip(FIELD_WIDTH, FIELD_HEIGHT);

        ship.setDirection(-1);
        ship.update(60); // far longer than needed to reach the left edge

        assertEquals(0, ship.getX(), 1e-9);
    }

    @Test
    void shouldRejectDirectionOutsideRange() {
        PlayerShip ship = new PlayerShip(FIELD_WIDTH, FIELD_HEIGHT);

        assertThrows(IllegalArgumentException.class, () -> ship.setDirection(2));
    }

    @Test
    void shouldRespectFireCooldown() {
        PlayerShip ship = new PlayerShip(FIELD_WIDTH, FIELD_HEIGHT);

        assertTrue(ship.shoot().isPresent());
        assertEquals(Optional.empty(), ship.shoot());

        ship.update(0.3); // longer than the 0.25 s cooldown
        assertTrue(ship.shoot().isPresent());
    }

    @Test
    void shouldFireFasterWhileRapidFireIsActive() {
        PlayerShip ship = new PlayerShip(FIELD_WIDTH, FIELD_HEIGHT);
        ship.boostFireRate(2.0, 5.0);
        ship.shoot();

        ship.update(0.15); // base cooldown is 0.25 s, boosted cooldown is 0.125 s

        assertTrue(ship.shoot().isPresent());
    }

    @Test
    void shouldRestoreNormalFireRateWhenBoostExpires() {
        PlayerShip ship = new PlayerShip(FIELD_WIDTH, FIELD_HEIGHT);
        ship.boostFireRate(2.0, 1.0);

        ship.update(1.5);

        assertEquals(1.0, ship.getFireRateMultiplier(), 1e-9);
    }

    @Test
    void shouldRejectInvalidBoost() {
        PlayerShip ship = new PlayerShip(FIELD_WIDTH, FIELD_HEIGHT);

        assertThrows(IllegalArgumentException.class, () -> ship.boostFireRate(0.5, 1));
        assertThrows(IllegalArgumentException.class, () -> ship.boostFireRate(2, 0));
    }

    @Test
    void shouldBeDestroyedWhenHealthReachesZero() {
        PlayerShip ship = new PlayerShip(FIELD_WIDTH, FIELD_HEIGHT);

        ship.takeDamage(ship.getMaxHealth());

        assertTrue(ship.isDestroyed());
        assertFalse(ship.isAlive());
        assertTrue(ship.shoot().isEmpty(), "a destroyed ship cannot fire");
    }

    @Test
    void shouldRejectNegativeDamage() {
        PlayerShip ship = new PlayerShip(FIELD_WIDTH, FIELD_HEIGHT);

        assertThrows(IllegalArgumentException.class, () -> ship.takeDamage(-1));
    }

    @Test
    void shouldNotHealAboveMaxHealth() {
        PlayerShip ship = new PlayerShip(FIELD_WIDTH, FIELD_HEIGHT);

        ship.heal(10);

        assertEquals(ship.getMaxHealth(), ship.getHealth());
    }

    @Test
    void shouldIgnoreHealingWhenDestroyed() {
        PlayerShip ship = new PlayerShip(FIELD_WIDTH, FIELD_HEIGHT);
        ship.takeDamage(ship.getMaxHealth());

        ship.heal(1);

        assertEquals(0, ship.getHealth());
    }
}
