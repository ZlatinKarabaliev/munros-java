package com.xdesign.munrotable.loader;

import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvException;
import com.xdesign.munrotable.model.Hill;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.xdesign.munrotable.util.CsvColumnHeading.*;

@Component
public class CsvHillDataLoader implements HillDataLoader {

    private final List<Hill> hills;

    public CsvHillDataLoader(@Value("${hill-search.munro.file.name}") String fileName) {
        this.hills = loadFromFile(fileName);
    }

    @Override
    public List<Hill> loadHills() {
        return hills;
    }

    private List<Hill> loadFromFile(String fileName) {
        try (var reader = new CSVReader(new FileReader(new ClassPathResource(fileName).getFile()))) {
            List<String[]> lines = reader.readAll();
            if (lines.isEmpty()) {
                throw new RuntimeException("CSV file is empty");
            }
            String[] headers = lines.get(0);

            List<Hill> loadedHills = new ArrayList<>();
            for (int i = 1; i < lines.size(); i++) {
                Map<String, String> rowMap = mapHeadersToRow(headers, lines.get(i));
                if (isValidHill(rowMap)) {
                    loadedHills.add(createHill(rowMap));
                }
            }
            return loadedHills;
        } catch (IOException | CsvException e) {
            throw new RuntimeException("Failed to read CSV file", e);
        }
    }

    private Map<String, String> mapHeadersToRow(String[] headers, String[] row) {
        var map = new java.util.HashMap<String, String>();
        for (int i = 0; i < headers.length && i < row.length; i++) {
            map.put(headers[i], row[i]);
        }
        return map;
    }

    private boolean isValidHill(Map<String, String> row) {
        return row.get(NAME_FIELD) != null && !row.get(NAME_FIELD).isBlank()
                && row.get(CATEGORY_FIELD) != null && !row.get(CATEGORY_FIELD).isBlank();
    }

    private String normalizeCategory(String rawCategory) {
        if (rawCategory == null || rawCategory.isBlank()) {
            throw new IllegalArgumentException("Unsupported category: " + rawCategory);
        }
        return switch (rawCategory.trim().toUpperCase()) {
            case "MUN" -> "MUNRO";
            case "TOP" -> "TOP";
            case "NONE" -> "NONE";
            default -> throw new IllegalArgumentException("Unsupported category: " + rawCategory);
        };
    }

    private Hill createHill(Map<String, String> row) {
        String normalizedCategory = normalizeCategory(row.get(CATEGORY_FIELD));
        return new Hill(
                row.get(NAME_FIELD),
                Double.parseDouble(row.get(HEIGHT_FIELD)),
                row.get(GRID_REFERENCE_FIELD),
                Hill.Category.valueOf(normalizedCategory)
        );
    }
}
