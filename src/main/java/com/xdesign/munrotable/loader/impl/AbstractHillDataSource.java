package com.xdesign.munrotable.loader.impl;

import com.xdesign.munrotable.loader.HillDataSource;
import org.springframework.core.io.ClassPathResource;

import java.io.InputStream;

public abstract class AbstractHillDataSource implements HillDataSource {
    protected InputStream getStreamFromClasspath(String path) {
        try {
            return new ClassPathResource(path).getInputStream();
        } catch (Exception e) {
            throw new RuntimeException("Could not load resource: " + path, e);
        }
    }
}