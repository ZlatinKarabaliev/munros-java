package com.xdesign.munrotable.loader.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.xdesign.munrotable.loader.HillDataSource;
import com.xdesign.munrotable.model.Hill;
import org.springframework.context.annotation.Profile;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

@Component
@Profile("json")
public class JsonHillDataSource implements HillDataSource {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public List<Hill> loadHills() {
        try {
            InputStream inputStream = new ClassPathResource("hills.json").getInputStream();
            return objectMapper.readValue(inputStream, new TypeReference<List<Hill>>() {});
        } catch (IOException e) {
            throw new RuntimeException("Failed to load hills from JSON", e);
        }
    }
}
