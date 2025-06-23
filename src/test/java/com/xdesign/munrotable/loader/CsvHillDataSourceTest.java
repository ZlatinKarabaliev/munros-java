package com.xdesign.munrotable.loader;

import com.xdesign.munrotable.loader.impl.CsvHillDataSource;
import com.xdesign.munrotable.model.Hill;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class CsvHillDataSourceTest {

    @Test
    void shouldLoadMunrosFromRealCsvFile() {
        CsvHillDataSource dataSource = new CsvHillDataSource("munros.csv");
        List<Hill> hills = dataSource.loadHills();

        assertNotNull(hills);
        assertFalse(hills.isEmpty());
        assertTrue(hills.stream().anyMatch(h -> h.name().equalsIgnoreCase("Ben Nevis")));
        assertTrue(hills.stream().anyMatch(h -> h.category() == Hill.Category.MUNRO));
    }

    @Test
    void shouldContainAtLeastOneTop() {
        CsvHillDataSource dataSource = new CsvHillDataSource("munros.csv");
        List<Hill> hills = dataSource.loadHills();

        assertTrue(hills.stream().anyMatch(h -> h.category() == Hill.Category.TOP));
    }

    @Test
    void shouldHaveCorrectColumnHeaders() {
        CsvHillDataSource dataSource = new CsvHillDataSource("munros.csv");
        List<Hill> hills = dataSource.loadHills();

        Hill sample = hills.get(0);
        assertNotNull(sample.name());
        assertNotNull(sample.height());
        assertNotNull(sample.gridReference());
        assertNotNull(sample.category());
    }
}
