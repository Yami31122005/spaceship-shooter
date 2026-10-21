package com.example.spaceshooter.service;

import com.example.spaceshooter.model.Enemy;
import com.example.spaceshooter.model.EnemyDefinition;
import com.example.spaceshooter.model.FighterEnemy;
import com.example.spaceshooter.model.ScoutEnemy;
import com.example.spaceshooter.model.TankEnemy;

import java.util.Objects;

/**
 * Builds the right Enemy subclass for a definition. Callers only see the Enemy type,
 * so spawning code never needs to know which movement pattern a kind uses.
 */
public final class EnemyFactory {

    private EnemyFactory() {
    }

    public static Enemy create(EnemyDefinition definition, double x, double y) {
        Objects.requireNonNull(definition, "definition");
        return switch (definition.kind()) {
            case SCOUT -> new ScoutEnemy(x, y, definition.health(), definition.speed(), definition.points());
            case FIGHTER -> new FighterEnemy(x, y, definition.health(), definition.speed(), definition.points());
            case TANK -> new TankEnemy(x, y, definition.health(), definition.speed(), definition.points());
        };
    }
}
