package com.xdesign.munrotable.config;

import com.xdesign.munrotable.loader.HillDataSource;
import com.xdesign.munrotable.loader.impl.CsvHillDataSource;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DataSourceConfiguration {

    @Bean
    @Qualifier("csvHillDataSource")
    public HillDataSource csvHillDataSource() {
        return new CsvHillDataSource("munros.csv");
    }
}

