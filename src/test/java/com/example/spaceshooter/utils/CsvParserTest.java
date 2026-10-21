package com.example.spaceshooter.utils;

import com.example.spaceshooter.exception.InvalidGameDataException;
import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class CsvParserTest {

    private static BufferedReader reader(String content) {
        return new BufferedReader(new StringReader(content));
    }

    @Test
    void shouldSkipHeaderBlankLinesAndComments() throws IOException {
        String csv = "a,b\n# comment\n\n1, 2\n3,4\n";

        List<String[]> rows = CsvParser.parse(reader(csv), 2);

        assertEquals(2, rows.size());
        assertEquals("1", rows.get(0)[0]);
        assertEquals("2", rows.get(0)[1], "cells are trimmed");
    }

    @Test
    void shouldRejectRowWithWrongColumnCount() {
        String csv = "a,b\n1,2,3\n";

        assertThrows(InvalidGameDataException.class, () -> CsvParser.parse(reader(csv), 2));
    }

    @Test
    void shouldRejectEmptyFile() {
        assertThrows(InvalidGameDataException.class, () -> CsvParser.parse(reader(""), 2));
    }
}
