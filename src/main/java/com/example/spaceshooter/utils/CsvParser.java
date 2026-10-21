package com.example.spaceshooter.utils;

import com.example.spaceshooter.exception.InvalidGameDataException;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Minimal CSV reader: comma-separated, first line is a header, blank lines and
 * lines starting with '#' are ignored. No quoting: the game data never needs it.
 */
public final class CsvParser {

    private CsvParser() {
    }

    /**
     * @param reader source positioned at the start of the file (header included)
     * @param expectedColumns number of cells every data row must have
     * @return trimmed cells of each data row, header excluded
     */
    public static List<String[]> parse(BufferedReader reader, int expectedColumns) throws IOException {
        String header = reader.readLine();
        if (header == null) {
            throw new InvalidGameDataException("CSV file is empty");
        }

        List<String[]> rows = new ArrayList<>();
        String line;
        int lineNumber = 1;
        while ((line = reader.readLine()) != null) {
            lineNumber++;
            if (line.isBlank() || line.startsWith("#")) {
                continue;
            }
            String[] cells = line.split(",", -1); // -1 keeps trailing empty cells
            if (cells.length != expectedColumns) {
                throw new InvalidGameDataException("Line " + lineNumber + ": expected "
                        + expectedColumns + " columns but found " + cells.length);
            }
            for (int i = 0; i < cells.length; i++) {
                cells[i] = cells[i].trim();
            }
            rows.add(cells);
        }
        return rows;
    }
}
