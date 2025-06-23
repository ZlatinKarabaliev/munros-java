package com.xdesign.munrotable.util;

import com.xdesign.munrotable.model.Hill;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class CsvUtils {

    private static final Logger log = LoggerFactory.getLogger(CsvUtils.class);

    public static List<Hill> parseCsv(InputStream stream) {
        List<Hill> hills = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(stream))) {
            String line = reader.readLine(); // header
            if (line == null || !line.toLowerCase().contains("name")) {
                throw new RuntimeException("CSV file is empty or invalid header");
            }

            while ((line = reader.readLine()) != null) {
                String[] tokens = line.split(",", -1); // keep empty tokens
                if (tokens.length < 4) continue;

                String name = tokens[0].trim();
                String heightStr = tokens[1].trim();
                String gridRef = tokens[2].trim();
                String categoryStr = tokens[3].trim();

                if (name.isEmpty() || categoryStr.isEmpty()) continue;

                double height;
                try {
                    height = Double.parseDouble(heightStr);
                } catch (NumberFormatException e) {
                    continue;
                }

                Hill.Category category;
                try {
                    category = Hill.Category.valueOf(categoryStr.toUpperCase());
                } catch (IllegalArgumentException e) {
                    log.warn("Unsupported category found in CSV: {}", categoryStr);
                    throw new RuntimeException("Unsupported category: " + categoryStr);
                }

                hills.add(new Hill(name, height, gridRef, category));
            }

            return hills;
        } catch (Exception e) {
            log.warn("Failed to read CSV file", e);
            throw new RuntimeException("Failed to read CSV file", e);
        }
    }
}
