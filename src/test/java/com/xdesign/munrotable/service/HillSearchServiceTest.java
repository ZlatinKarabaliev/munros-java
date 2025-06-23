package com.xdesign.munrotable.service;

import com.xdesign.munrotable.dto.HillSearchRequest;
import com.xdesign.munrotable.factory.HillDataSourceFactory;
import com.xdesign.munrotable.factory.HillSearchRequestFactory;
import com.xdesign.munrotable.loader.HillDataSource;
import com.xdesign.munrotable.model.Hill;
import com.xdesign.munrotable.model.Hill.Category;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class HillSearchServiceTest {

    private HillSearchService service;
    private HillDataSourceFactory factory;
    private HillDataSource dataSource;

    @BeforeEach
    void setUp() {
        factory = mock(HillDataSourceFactory.class);
        dataSource = mock(HillDataSource.class);
        when(factory.getActiveDataSource()).thenReturn(dataSource);
        service = new HillSearchService(factory);
    }

    private void setMockedHills(List<Hill> hills) {
        when(dataSource.loadHills()).thenReturn(hills);
    }

    private HillSearchRequest request(String category, Double minHeight, Double maxHeight, List<String> sorts, int limit) {
        return HillSearchRequestFactory.newRequest(category, minHeight, maxHeight, sorts, limit);
    }

    @Test
    void shouldReturnAllHillsWhenNoFiltersApplied() {
        setMockedHills(List.of(
                new Hill("A", 100.0, "NN12", Category.MUNRO),
                new Hill("B", 200.0, "NN13", Category.TOP)
        ));

        List<Hill> result = service.searchHills(request(null, null, null, List.of(), 10));

        assertThat(result).hasSize(2);
    }

    @Test
    void shouldFilterByCategory() {
        setMockedHills(List.of(
                new Hill("A", 100.0, "NN12", Category.MUNRO),
                new Hill("B", 200.0, "NN13", Category.TOP)
        ));

        List<Hill> result = service.searchHills(request("MUNRO", null, null, List.of(), 10));

        assertThat(result).hasSize(1);
        assertEquals("A", result.get(0).name());
    }

    @Test
    void shouldFilterByHeightRange() {
        setMockedHills(List.of(
                new Hill("Low", 50.0, "NN12", Category.MUNRO),
                new Hill("Mid", 150.0, "NN13", Category.MUNRO),
                new Hill("High", 300.0, "NN14", Category.MUNRO)
        ));

        List<Hill> result = service.searchHills(request("MUNRO", 100.0, 200.0, List.of(), 10));

        assertThat(result).hasSize(1);
        assertEquals("Mid", result.get(0).name());
    }

    @Test
    void shouldApplyLimit() {
        setMockedHills(List.of(
                new Hill("H1", 100.0, "NN11", Category.MUNRO),
                new Hill("H2", 110.0, "NN12", Category.MUNRO),
                new Hill("H3", 120.0, "NN13", Category.MUNRO)
        ));

        List<Hill> result = service.searchHills(request("MUNRO", null, null, List.of(), 2));

        assertThat(result).hasSize(2);
    }

    @Test
    void shouldSortByNameDescending() {
        setMockedHills(List.of(
                new Hill("Alpha", 1000.0, "GR1", Category.MUNRO),
                new Hill("Beta", 800.0, "GR2", Category.TOP),
                new Hill("Gamma", 1200.0, "GR3", Category.MUNRO)
        ));

        List<Hill> result = service.searchHills(request(null, null, null, List.of("name_desc"), 10));

        assertThat(result)
                .hasSize(3)
                .extracting(Hill::name)
                .containsExactly("Gamma", "Beta", "Alpha");
    }

    @Test
    void shouldSortByNameThenHeight() {
        setMockedHills(List.of(
                new Hill("A", 100.0, "NN11", Category.MUNRO),
                new Hill("A", 80.0, "NN12", Category.MUNRO),
                new Hill("B", 200.0, "NN13", Category.MUNRO)
        ));

        List<Hill> result = service.searchHills(request("MUNRO", null, null, List.of("name_asc", "height_asc"), 10));

        assertThat(result).hasSize(3);
        assertEquals(80.0, result.get(0).height());
        assertEquals(100.0, result.get(1).height());
        assertEquals("B", result.get(2).name());
    }
}