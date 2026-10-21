package com.example.spaceshooter.service;

import com.example.spaceshooter.exception.DataLoadException;
import com.example.spaceshooter.model.EnemyDefinition;
import com.example.spaceshooter.model.EnemyKind;
import com.example.spaceshooter.model.Wave;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class CsvGameDataSourceTest {

    private final GameDataSource source = new CsvGameDataSource();

    @Test
    void shouldLoadEnemyDefinitionsFromTheBundledFile() throws DataLoadException {
        List<EnemyDefinition> definitions = source.loadEnemyDefinitions();

        assertEquals(3, definitions.size());
        EnemyDefinition tank = definitions.stream()
                .filter(d -> d.kind() == EnemyKind.TANK)
                .findFirst()
                .orElseThrow();
        assertEquals(5, tank.health());
        assertEquals(60, tank.points());
    }

    @Test
    void shouldGroupWaveLinesByWaveNumber() throws DataLoadException {
        List<Wave> waves = source.loadWaves();

        assertEquals(3, waves.size());
        assertEquals(1, waves.get(0).number());
        assertEquals(2, waves.get(1).entries().size(), "wave 2 has two lines in the CSV");
        assertFalse(waves.get(2).entries().isEmpty());
    }

    @Test
    void bundledFilesExistOnTheClasspath() {
        assertNotNull(CsvGameDataSource.class.getResource(CsvGameDataSource.ENEMIES_FILE));
        assertNotNull(CsvGameDataSource.class.getResource(CsvGameDataSource.WAVES_FILE));
    }
}
