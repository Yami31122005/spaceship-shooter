package com.example.spaceshooter.service;

import com.example.spaceshooter.model.Bullet;
import com.example.spaceshooter.model.Enemy;
import com.example.spaceshooter.model.EnemyDefinition;
import com.example.spaceshooter.model.EnemyKind;
import com.example.spaceshooter.model.PlayerShip;
import com.example.spaceshooter.model.PowerUp;
import com.example.spaceshooter.model.RepairKit;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CollisionServiceTest {

    private final CollisionService service = new CollisionService();

    private static Enemy enemyAt(double x, double y, int health) {
        return EnemyFactory.create(new EnemyDefinition(EnemyKind.SCOUT, health, 100, 10), x, y);
    }

    @Test
    void bulletInsideEnemyDamagesItAndIsConsumed() {
        Enemy enemy = enemyAt(100, 100, 2);
        Bullet bullet = new Bullet(110, 110);

        List<Enemy> killed = service.resolveBulletHits(List.of(bullet), List.of(enemy));

        assertTrue(killed.isEmpty(), "two-hit enemy survives one bullet");
        assertEquals(1, enemy.getHealth());
        assertFalse(bullet.isAlive());
    }

    @Test
    void lethalBulletReportsTheKill() {
        Enemy enemy = enemyAt(100, 100, 1);
        Bullet bullet = new Bullet(110, 110);

        List<Enemy> killed = service.resolveBulletHits(List.of(bullet), List.of(enemy));

        assertEquals(List.of(enemy), killed);
        assertFalse(enemy.isAlive());
    }

    @Test
    void bulletMissingEnemyChangesNothing() {
        Enemy enemy = enemyAt(100, 100, 1);
        Bullet bullet = new Bullet(500, 500);

        List<Enemy> killed = service.resolveBulletHits(List.of(bullet), List.of(enemy));

        assertTrue(killed.isEmpty());
        assertTrue(enemy.isAlive());
        assertTrue(bullet.isAlive());
    }

    @Test
    void oneBulletCannotHitTwoEnemies() {
        Enemy first = enemyAt(100, 100, 1);
        Enemy second = enemyAt(100, 100, 1);

        List<Enemy> killed = service.resolveBulletHits(
                List.of(new Bullet(110, 110)), List.of(first, second));

        assertEquals(1, killed.size());
        assertTrue(second.isAlive());
    }

    @Test
    void enemyRammingTheShipIsDestroyed() {
        PlayerShip ship = new PlayerShip(800, 600);
        Enemy enemy = enemyAt(ship.getX(), ship.getY(), 3);

        List<Enemy> rammed = service.resolveRamming(ship, List.of(enemy));

        assertEquals(List.of(enemy), rammed);
        assertFalse(enemy.isAlive());
    }

    @Test
    void powerUpTouchingTheShipIsAppliedAndRemoved() {
        PlayerShip ship = new PlayerShip(800, 600);
        ship.takeDamage(1);
        PowerUp kit = new RepairKit(ship.getX(), ship.getY());

        service.collectPowerUps(ship, List.of(kit));

        assertEquals(ship.getMaxHealth(), ship.getHealth());
        assertFalse(kit.isAlive());
    }
}
