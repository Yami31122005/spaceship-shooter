package com.example.spaceshooter.service;

import com.example.spaceshooter.exception.InvalidGameDataException;
import com.example.spaceshooter.model.EnemyDefinition;
import com.example.spaceshooter.model.EnemyKind;
import com.example.spaceshooter.model.Wave;
import com.example.spaceshooter.model.WaveEntry;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

/**
 * Hands out waves in order and knows the stats of each enemy kind.
 * The data is validated once here, so the game loop can trust it afterwards.
 */
public class WaveManager {

    private final List<Wave> waves;
    private final Map<EnemyKind, EnemyDefinition> definitions;
    private int nextWaveIndex;

    public WaveManager(List<Wave> waves, List<EnemyDefinition> definitions) {
        Objects.requireNonNull(waves, "waves");
        Objects.requireNonNull(definitions, "definitions");
        if (waves.isEmpty()) {
            throw new InvalidGameDataException("at least one wave is required");
        }

        Map<EnemyKind, EnemyDefinition> byKind = new EnumMap<>(EnemyKind.class);
        for (EnemyDefinition definition : definitions) {
            if (byKind.put(definition.kind(), definition) != null) {
                throw new InvalidGameDataException("duplicate enemy definition: " + definition.kind());
            }
        }

        for (Wave wave : waves) {
            for (WaveEntry entry : wave.entries()) {
                if (!byKind.containsKey(entry.kind())) {
                    throw new InvalidGameDataException(
                            "wave " + wave.number() + " uses undefined enemy " + entry.kind());
                }
            }
        }

        this.waves = List.copyOf(waves);
        this.definitions = byKind;
    }

    public boolean hasMoreWaves() {
        return nextWaveIndex < waves.size();
    }

    public Optional<Wave> nextWave() {
        if (!hasMoreWaves()) {
            return Optional.empty();
        }
        return Optional.of(waves.get(nextWaveIndex++));
    }

    public EnemyDefinition definitionFor(EnemyKind kind) {
        return definitions.get(kind);
    }
}
