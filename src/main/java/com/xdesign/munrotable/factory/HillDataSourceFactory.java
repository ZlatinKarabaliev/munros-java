package com.xdesign.munrotable.factory;

import com.xdesign.munrotable.loader.HillDataSource;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class HillDataSourceFactory {

    private final HillDataSource csv;
    private final HillDataSource json;
    private final String preferredSource;

    public HillDataSourceFactory(
            @Qualifier("csvHillDataSource") HillDataSource csv,
            @Qualifier("jsonHillDataSource") HillDataSource json,
            @Value("${hill.source:json}") String preferredSource
    ) {
        this.csv = csv;
        this.json = json;
        this.preferredSource = preferredSource.toLowerCase();
    }

    public HillDataSource getActiveDataSource() {
        return switch (preferredSource) {
            case "csv" -> csv;
            case "json" -> json;
            default -> throw new IllegalStateException("Unsupported hill.source: " + preferredSource);
        };
    }
}
