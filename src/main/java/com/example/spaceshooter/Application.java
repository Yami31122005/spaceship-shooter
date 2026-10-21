package com.example.spaceshooter;

import com.example.spaceshooter.exception.DataLoadException;
import com.example.spaceshooter.exception.InvalidGameDataException;
import com.example.spaceshooter.model.Entity;
import com.example.spaceshooter.model.Enemy;
import com.example.spaceshooter.model.GameStatus;
import com.example.spaceshooter.service.CsvGameDataSource;
import com.example.spaceshooter.service.Game;
import com.example.spaceshooter.service.GameDataSource;
import com.example.spaceshooter.service.WaveManager;

import java.util.Comparator;
import java.util.Optional;
import java.util.Random;

/**
 * Entry point. Loads the data, builds the game and plays it with a simple autopilot
 * so the run is visible in the console. Fixed seed => the same game every run.
 */
public class Application {

    private static final double TIME_STEP = 1.0 / 60;
    private static final int MAX_TICKS = 60 * 600; // safety stop: 10 simulated minutes
    private static final long RANDOM_SEED = 42L;

    public static void main(String[] args) {
        try {
            GameDataSource source = new CsvGameDataSource();
            WaveManager waves = new WaveManager(source.loadWaves(), source.loadEnemyDefinitions());
            Game game = new Game(waves, new Random(RANDOM_SEED));
            play(game);
        } catch (DataLoadException e) {
            System.err.println("Cannot load game data: " + e.getMessage());
            System.exit(1);
        } catch (InvalidGameDataException e) {
            System.err.println("Invalid game data: " + e.getMessage());
            System.exit(2);
        }
    }

    private static void play(Game game) {
        int ticks = 0;
        int announcedWave = -1;

        while (game.getStatus() == GameStatus.RUNNING && ticks < MAX_TICKS) {
            if (game.getCurrentWaveNumber() != announcedWave) {
                announcedWave = game.getCurrentWaveNumber();
                System.out.printf("Wave %d started (%d enemies alive)%n", announcedWave, game.getEnemies().size());
            }
            steerTowardNearestEnemy(game);
            game.fire();
            game.update(TIME_STEP);
            ticks++;
        }

        System.out.printf("Result: %s | score: %d | last wave: %d | health: %d/%d%n",
                game.getStatus(), game.getScore(), game.getCurrentWaveNumber(),
                game.getPlayer().getHealth(), game.getPlayer().getMaxHealth());
    }

    private static void steerTowardNearestEnemy(Game game) {
        double shipCenter = game.getPlayer().getX() + game.getPlayer().getWidth() / 2;
        Optional<Enemy> target = game.getEnemies().stream()
                .filter(Entity::isAlive)
                .filter(enemy -> enemy.getY() >= 0) // ignore enemies still above the screen
                .min(Comparator.comparingDouble(enemy -> Math.abs(enemyCenter(enemy) - shipCenter)));

        int direction = target
                .map(enemy -> Integer.signum((int) Math.round(enemyCenter(enemy) - shipCenter)))
                .orElse(0);
        game.moveShip(direction);
    }

    private static double enemyCenter(Enemy enemy) {
        return enemy.getX() + Enemy.SIZE / 2;
    }
}
