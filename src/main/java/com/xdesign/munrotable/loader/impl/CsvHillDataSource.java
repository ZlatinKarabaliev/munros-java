package com.xdesign.munrotable.loader.impl;

import com.xdesign.munrotable.model.Hill;
import com.xdesign.munrotable.util.CsvUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

@Component
@Profile("csv")
public class CsvHillDataSource extends AbstractHillDataSource {

    private final String fileName;
    private static final Logger log = LoggerFactory.getLogger(CsvHillDataSource.class);

    public CsvHillDataSource(@Value("${hill-search.munro.file.name}") String fileName) {
        this.fileName = fileName;
    }

    @Override
    public List<Hill> loadHills() {
        log.info("Loading hills from default CSV file: munros.csv");
        try (InputStream stream = getStreamFromClasspath(fileName)) {
            return CsvUtils.parseCsv(stream);
        } catch (IOException e) {
            throw new RuntimeException("Failed to load hills from CSV", e);
        }
    }
}



//
//@Component
//@Profile("csv")
//public class CsvHillDataSource implements HillDataSource {
//
//    private final String csvFileName;
//
//    public CsvHillDataSource(@Value("${hill-search.munro.file.name}") String csvFileName) {
//        this.csvFileName = csvFileName;
//    }
//
//    @Override
//    public List<Hill> loadHills() {
//        try (InputStream inputStream = new ClassPathResource(csvFileName).getInputStream();
//             BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
//
//            return reader.lines()
//                    .skip(1) // skip header
//                    .map(CsvUtils::parseLine)
//                    .filter(java.util.Objects::nonNull)
//                    .toList();
//        } catch (IOException e) {
//            throw new RuntimeException("Failed to load hills from CSV", e);
//        }
//    }
//
//
//    // Load from Spring Resource (main usage)
//    private List<Hill> loadFromFile(Resource resource) {
//        try {
//            return loadFromFile(resource.getFile());
//        } catch (IOException e) {
//            throw new RuntimeException("Failed to read CSV file", e);
//        }
//    }
//
//    // Core CSV loading logic
//    private List<Hill> loadFromFile(File file) {
//        try (var reader = new com.opencsv.CSVReader(new FileReader(file))) {
//            List<String[]> lines = reader.readAll();
//
//            if (lines.isEmpty()) {
//                throw new RuntimeException("CSV file is empty");
//            }
//
//            String[] headers = lines.get(0);
//            List<Hill> loadedHills = new ArrayList<>();
//
//            for (int i = 1; i < lines.size(); i++) {
//                Map<String, String> rowMap = mapHeadersToRow(headers, lines.get(i));
//                if (isValidHill(rowMap)) {
//                    loadedHills.add(createHill(rowMap));
//                }
//            }
//
//            return loadedHills;
//
//        } catch (Exception e) {
//            throw new RuntimeException("Failed to read CSV file", e);
//        }
//    }
//
//    private Map<String, String> mapHeadersToRow(String[] headers, String[] row) {
//        Map<String, String> map = new HashMap<>();
//        for (int i = 0; i < headers.length && i < row.length; i++) {
//            map.put(headers[i], row[i]);
//        }
//        return map;
//    }
//
//    private boolean isValidHill(Map<String, String> row) {
//        return row.get(NAME_FIELD) != null && !row.get(NAME_FIELD).isBlank()
//                && row.get(CATEGORY_FIELD) != null && !row.get(CATEGORY_FIELD).isBlank();
//    }
//
//    private Hill createHill(Map<String, String> row) {
//        String normalizedCategory = normalizeCategory(row.get(CATEGORY_FIELD));
//        return new Hill(
//                row.get(NAME_FIELD),
//                Double.parseDouble(row.get(HEIGHT_FIELD)),
//                row.get(GRID_REFERENCE_FIELD),
//                Hill.Category.valueOf(normalizedCategory)
//        );
//    }
//
//    private String normalizeCategory(String rawCategory) {
//        if (rawCategory == null || rawCategory.isBlank()) {
//            throw new IllegalArgumentException("Unsupported category: " + rawCategory);
//        }
//        return switch (rawCategory.trim().toUpperCase()) {
//            case "MUN" -> "MUNRO";
//            case "TOP" -> "TOP";
//            case "NONE" -> "NONE";
//            default -> throw new IllegalArgumentException("Unsupported category: " + rawCategory);
//        };
//    }
//}
