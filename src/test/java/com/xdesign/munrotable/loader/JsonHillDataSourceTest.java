package com.xdesign.munrotable.loader;

import com.xdesign.munrotable.loader.impl.JsonHillDataSource;
import com.xdesign.munrotable.model.Hill;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;


class JsonHillDataSourceTest {
    private final JsonHillDataSource dataSource = new JsonHillDataSource();

    @Test
    void shouldLoadHillsFromJson() {
        List<Hill> hills = dataSource.loadHills();
        assertNotNull(hills);
        assertEquals(2, hills.size());
    }
}
