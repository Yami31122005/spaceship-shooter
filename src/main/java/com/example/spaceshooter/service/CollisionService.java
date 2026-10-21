package com.example.spaceshooter.service;

import com.example.spaceshooter.model.Bullet;
import com.example.spaceshooter.model.Enemy;
import com.example.spaceshooter.model.PlayerShip;
import com.example.spaceshooter.model.PowerUp;

import java.util.ArrayList;
import java.util.List;

/**
 * Resolves what happens when entities touch. Only decides outcomes (damage, destruction);
 * scoring and game-over rules live in other classes.
 */
public class CollisionService {

    static final int BULLET_DAMAGE = 1;

    /**
     * Each bullet hits at most one enemy, and is consumed by that hit.
     *
     * @return enemies killed by this call (they were alive before and dead after)
     */
    public List<Enemy> resolveBulletHits(List<Bullet> bullets, List<Enemy> enemies) {
        List<Enemy> killed = new ArrayList<>();
        for (Bullet bullet : bullets) {
            if (!bullet.isAlive()) {
                continue;
            }
            for (Enemy enemy : enemies) {
                if (enemy.isAlive() && bullet.intersects(enemy)) {
                    bullet.destroy();
                    enemy.takeDamage(BULLET_DAMAGE);
                    if (!enemy.isAlive()) {
                        killed.add(enemy);
                    }
                    break;
                }
            }
        }
        return killed;
    }

    /**
     * An enemy ramming the ship is destroyed by the impact; the caller applies the damage to the ship.
     *
     * @return enemies that rammed the ship this call
     */
    public List<Enemy> resolveRamming(PlayerShip ship, List<Enemy> enemies) {
        List<Enemy> rammed = new ArrayList<>();
        for (Enemy enemy : enemies) {
            if (enemy.isAlive() && enemy.intersects(ship)) {
                enemy.destroy();
                rammed.add(enemy);
            }
        }
        return rammed;
    }

    /** Applies and removes every power-up touching the ship. */
    public void collectPowerUps(PlayerShip ship, List<PowerUp> powerUps) {
        for (PowerUp powerUp : powerUps) {
            if (powerUp.isAlive() && powerUp.intersects(ship)) {
                powerUp.applyTo(ship);
                powerUp.destroy();
            }
        }
    }
}
