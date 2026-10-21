package com.example.spaceshooter.service;

import com.example.spaceshooter.exception.DataLoadException;
import com.example.spaceshooter.model.EnemyDefinition;
import com.example.spaceshooter.model.Wave;

import java.util.List;

/**
 * Where the game's static data comes from. The game depends on this abstraction,
 * not on CSV, so another source (JSON, database, in-memory test data) can replace it.
 */
public interface GameDataSource {

    List<EnemyDefinition> loadEnemyDefinitions() throws DataLoadException;

    List<Wave> loadWaves() throws DataLoadException;
}
