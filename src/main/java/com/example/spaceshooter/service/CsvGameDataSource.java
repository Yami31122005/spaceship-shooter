package com.example.spaceshooter.service;

import com.example.spaceshooter.exception.DataLoadException;
import com.example.spaceshooter.exception.InvalidGameDataException;
import com.example.spaceshooter.model.EnemyDefinition;
import com.example.spaceshooter.model.EnemyKind;
import com.example.spaceshooter.model.Wave;
import com.example.spaceshooter.model.WaveEntry;
import com.example.spaceshooter.utils.CsvParser;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * Loads enemy stats and waves from the CSV files bundled in src/main/resources.
 */
public class CsvGameDataSource implements GameDataSource {

    static final String ENEMIES_FILE = "/enemies.csv";
    static final String WAVES_FILE = "/waves.csv";

    @Override
    public List<EnemyDefinition> loadEnemyDefinitions() throws DataLoadException {
        List<EnemyDefinition> definitions = new ArrayList<>();
        for (String[] cells : readRows(ENEMIES_FILE, 4)) {
            definitions.add(new EnemyDefinition(
                    parseKind(cells[0]),
                    parseInt(cells[1], "health"),
                    parseDouble(cells[2], "speed"),
                    parseInt(cells[3], "points")));
        }
        return definitions;
    }

    @Override
    public List<Wave> loadWaves() throws DataLoadException {
        // Several CSV lines share a wave number; group them before building each Wave.
        Map<Integer, List<WaveEntry>> entriesByWave = new TreeMap<>();
        for (String[] cells : readRows(WAVES_FILE, 3)) {
            int waveNumber = parseInt(cells[0], "wave");
            WaveEntry entry = new WaveEntry(parseKind(cells[1]), parseInt(cells[2], "count"));
            entriesByWave.computeIfAbsent(waveNumber, k -> new ArrayList<>()).add(entry);
        }

        List<Wave> waves = new ArrayList<>();
        entriesByWave.forEach((number, entries) -> waves.add(new Wave(number, entries)));
        return waves;
    }

    private static List<String[]> readRows(String resource, int columns) throws DataLoadException {
        try (InputStream in = CsvGameDataSource.class.getResourceAsStream(resource)) {
            if (in == null) {
                throw new DataLoadException("Missing data file on classpath: " + resource);
            }
            BufferedReader reader = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8));
            return CsvParser.parse(reader, columns);
        } catch (IOException e) {
            throw new DataLoadException("Cannot read " + resource, e);
        }
    }

    private static EnemyKind parseKind(String value) {
        try {
            return EnemyKind.valueOf(value.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new InvalidGameDataException("Unknown enemy kind: '" + value + "'");
        }
    }

    private static int parseInt(String value, String field) {
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            throw new InvalidGameDataException("Invalid integer for " + field + ": '" + value + "'");
        }
    }

    private static double parseDouble(String value, String field) {
        try {
            return Double.parseDouble(value);
        } catch (NumberFormatException e) {
            throw new InvalidGameDataException("Invalid number for " + field + ": '" + value + "'");
        }
    }
}
