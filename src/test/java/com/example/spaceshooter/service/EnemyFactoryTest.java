package com.example.spaceshooter.service;

import com.example.spaceshooter.model.Enemy;
import com.example.spaceshooter.model.EnemyDefinition;
import com.example.spaceshooter.model.EnemyKind;
import com.example.spaceshooter.model.FighterEnemy;
import com.example.spaceshooter.model.ScoutEnemy;
import com.example.spaceshooter.model.TankEnemy;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertTrue;

class EnemyFactoryTest {

    @Test
    void shouldCreateTheSubclassMatchingTheKind() {
        Enemy scout = EnemyFactory.create(new EnemyDefinition(EnemyKind.SCOUT, 1, 100, 10), 0, 0);
        Enemy fighter = EnemyFactory.create(new EnemyDefinition(EnemyKind.FIGHTER, 2, 90, 25), 0, 0);
        Enemy tank = EnemyFactory.create(new EnemyDefinition(EnemyKind.TANK, 5, 45, 60), 0, 0);

        assertInstanceOf(ScoutEnemy.class, scout);
        assertInstanceOf(FighterEnemy.class, fighter);
        assertInstanceOf(TankEnemy.class, tank);
    }

    @Test
    void shouldCopyStatsFromTheDefinition() {
        Enemy tank = EnemyFactory.create(new EnemyDefinition(EnemyKind.TANK, 5, 45, 60), 10, 20);

        assertEquals(5, tank.getHealth());
        assertEquals(60, tank.getPoints());
        assertEquals(10, tank.getX(), 1e-9);
        assertEquals(20, tank.getY(), 1e-9);
    }

    /**
     * Polymorphism: the loop only knows Enemy, yet each subclass moves with its own pattern.
     */
    @Test
    void shouldMoveEachEnemyWithItsOwnPatternThroughTheCommonType() {
        List<Enemy> enemies = List.of(
                EnemyFactory.create(new EnemyDefinition(EnemyKind.SCOUT, 1, 100, 10), 100, 0),
                EnemyFactory.create(new EnemyDefinition(EnemyKind.FIGHTER, 2, 90, 25), 100, 0),
                EnemyFactory.create(new EnemyDefinition(EnemyKind.TANK, 5, 45, 60), 100, 0));

        double[] startY = enemies.stream().mapToDouble(Enemy::getY).toArray();
        for (Enemy enemy : enemies) {
            enemy.update(0.5);
        }

        for (int i = 0; i < enemies.size(); i++) {
            assertTrue(enemies.get(i).getY() > startY[i], "every enemy descends");
        }
        // Scout falls straight: no horizontal drift at all.
        assertEquals(100, enemies.get(0).getX(), 1e-9);
    }
}
