package com.example.spaceshooter.service;

import com.example.spaceshooter.model.Bullet;
import com.example.spaceshooter.model.Enemy;
import com.example.spaceshooter.model.EnemyDefinition;
import com.example.spaceshooter.model.GameStatus;
import com.example.spaceshooter.model.PlayerShip;
import com.example.spaceshooter.model.PowerUp;
import com.example.spaceshooter.model.RapidFire;
import com.example.spaceshooter.model.RepairKit;
import com.example.spaceshooter.model.Wave;
import com.example.spaceshooter.model.WaveEntry;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;

/**
 * Orchestrates one game: moves entities, resolves combat, spawns waves and decides
 * win/loss. It coordinates the other services; it does not do their work itself.
 */
public class Game {

    public static final double FIELD_WIDTH = 800;
    public static final double FIELD_HEIGHT = 600;

    private static final double POWER_UP_DROP_CHANCE = 0.15;
    private static final int COLLISION_DAMAGE = 1;
    private static final double SPAWN_SPACING = 60;
    private static final double SPAWN_TOP_OFFSET = 40;

    private final WaveManager waves;
    private final Random random;
    private final PlayerShip player = new PlayerShip(FIELD_WIDTH, FIELD_HEIGHT);
    private final List<Enemy> enemies = new ArrayList<>();
    private final List<Bullet> bullets = new ArrayList<>();
    private final List<PowerUp> powerUps = new ArrayList<>();
    private final ScoreService scoreService = new ScoreService();
    private final CollisionService collisionService = new CollisionService();

    private GameStatus status = GameStatus.RUNNING;
    private int currentWaveNumber;

    public Game(WaveManager waves, Random random) {
        this.waves = Objects.requireNonNull(waves, "waves");
        this.random = Objects.requireNonNull(random, "random");
        spawnNextWave();
    }

    /** @param direction -1 left, 0 stop, 1 right */
    public void moveShip(int direction) {
        player.setDirection(direction);
    }

    public void fire() {
        player.shoot().ifPresent(bullets::add);
    }

    /** Advances the game by {@code dt} seconds. Does nothing once the game is over. */
    public void update(double dt) {
        if (dt <= 0) {
            throw new IllegalArgumentException("time step must be positive");
        }
        if (status != GameStatus.RUNNING) {
            return;
        }

        player.update(dt);
        enemies.forEach(enemy -> enemy.update(dt));
        bullets.forEach(bullet -> bullet.update(dt));
        powerUps.forEach(powerUp -> powerUp.update(dt));

        resolveCombat();
        removeInactive();

        if (player.isDestroyed()) {
            status = GameStatus.LOST;
            return;
        }
        advanceWaveIfCleared();
    }

    public PlayerShip getPlayer() {
        return player;
    }

    public List<Enemy> getEnemies() {
        return List.copyOf(enemies);
    }

    public List<PowerUp> getPowerUps() {
        return List.copyOf(powerUps);
    }

    public int getScore() {
        return scoreService.getScore();
    }

    public GameStatus getStatus() {
        return status;
    }

    public int getCurrentWaveNumber() {
        return currentWaveNumber;
    }

    private void resolveCombat() {
        for (Enemy killed : collisionService.resolveBulletHits(bullets, enemies)) {
            scoreService.registerKill(killed);
            maybeDropPowerUp(killed);
        }

        collisionService.resolveRamming(player, enemies).forEach(rammed -> damagePlayer());

        for (Enemy enemy : enemies) {
            if (enemy.isAlive() && enemy.getY() > FIELD_HEIGHT) {
                enemy.destroy();
                damagePlayer(); // an enemy that gets past the ship hurts the player
            }
        }

        collisionService.collectPowerUps(player, powerUps);
    }

    private void damagePlayer() {
        player.takeDamage(COLLISION_DAMAGE);
        scoreService.registerPlayerHit();
    }

    private void maybeDropPowerUp(Enemy enemy) {
        if (random.nextDouble() >= POWER_UP_DROP_CHANCE) {
            return;
        }
        PowerUp drop = random.nextBoolean()
                ? new RepairKit(enemy.getX(), enemy.getY())
                : new RapidFire(enemy.getX(), enemy.getY());
        powerUps.add(drop);
    }

    private void removeInactive() {
        bullets.removeIf(bullet -> !bullet.isAlive());
        enemies.removeIf(enemy -> !enemy.isAlive());
        powerUps.removeIf(powerUp -> !powerUp.isAlive() || powerUp.getY() > FIELD_HEIGHT);
    }

    private void advanceWaveIfCleared() {
        if (!enemies.isEmpty()) {
            return;
        }
        scoreService.addWaveBonus(currentWaveNumber);
        if (waves.hasMoreWaves()) {
            spawnNextWave();
        } else {
            status = GameStatus.WON;
        }
    }

    private void spawnNextWave() {
        Wave wave = waves.nextWave()
                .orElseThrow(() -> new IllegalStateException("no wave left to spawn"));
        currentWaveNumber = wave.number();

        int slot = 0;
        for (WaveEntry entry : wave.entries()) {
            EnemyDefinition definition = waves.definitionFor(entry.kind());
            for (int i = 0; i < entry.count(); i++) {
                double x = random.nextDouble() * (FIELD_WIDTH - Enemy.SIZE);
                // Enemies enter one after another from above the screen, not stacked on one spot.
                double y = -SPAWN_TOP_OFFSET - slot * SPAWN_SPACING;
                enemies.add(EnemyFactory.create(definition, x, y));
                slot++;
            }
        }
    }
}
