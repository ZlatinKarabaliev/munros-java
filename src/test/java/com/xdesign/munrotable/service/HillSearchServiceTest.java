package com.xdesign.munrotable.service;

import com.xdesign.munrotable.dto.HillSearchRequest;
import com.xdesign.munrotable.dto.HillSearchRequestFactory;
import com.xdesign.munrotable.loader.HillDataLoader;
import com.xdesign.munrotable.model.Hill;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class HillSearchServiceUnitTest {

    @Mock
    HillDataLoader hillDataLoader;

    HillSearchService hillSearchService;

    @BeforeEach
    void setUp() {
        hillSearchService = new HillSearchService(hillDataLoader);
    }

    @Test
    void testSearchWithCategoryFilter() {
        Hill hill1 = new Hill("Hill A", 1000.0, "GR1", Hill.Category.MUNRO);
        Hill hill2 = new Hill("Hill B", 800.0, "GR2", Hill.Category.TOP);
        Hill hill3 = new Hill("Hill C", 1200.0, "GR3", Hill.Category.MUNRO);

        when(hillDataLoader.loadHills()).thenReturn(List.of(hill1, hill2, hill3));

        HillSearchRequest request = HillSearchRequestFactory.newRequest(
                "MUNRO",
                null,
                null,
                List.of(),
                10
        );

        List<Hill> results = hillSearchService.searchHills(request);

        assertThat(results).containsExactlyInAnyOrder(hill1, hill3);
        assertThat(results).allMatch(h -> h.category() == Hill.Category.MUNRO);
    }

    @Test
    void testSearchWithHeightRangeFilter() {
        Hill hill1 = new Hill("Hill A", 1000.0, "GR1", Hill.Category.MUNRO);
        Hill hill2 = new Hill("Hill B", 800.0, "GR2", Hill.Category.TOP);
        Hill hill3 = new Hill("Hill C", 1200.0, "GR3", Hill.Category.MUNRO);

        when(hillDataLoader.loadHills()).thenReturn(List.of(hill1, hill2, hill3));

        HillSearchRequest request = HillSearchRequestFactory.newRequest(
                null,
                850.0,
                1100.0,
                List.of(),
                10
        );

        List<Hill> results = hillSearchService.searchHills(request);

        assertThat(results).containsExactly(hill1);
        assertThat(results).allMatch(h -> h.height() >= 850 && h.height() <= 1100);
    }

    @Test
    void testSearchWithSorting() {
        Hill hill1 = new Hill("Alpha", 1000.0, "GR1", Hill.Category.MUNRO);
        Hill hill2 = new Hill("Beta", 800.0, "GR2", Hill.Category.TOP);
        Hill hill3 = new Hill("Gamma", 1200.0, "GR3", Hill.Category.MUNRO);

        when(hillDataLoader.loadHills()).thenReturn(List.of(hill1, hill2, hill3));

        HillSearchRequest request = HillSearchRequestFactory.newRequest(
                null,
                null,
                null,
                List.of("name_desc"),
                10
        );

        List<Hill> results = hillSearchService.searchHills(request);

        assertThat(results).containsExactly(hill3, hill2, hill1);
    }

    @Test
    void testSearchWithLimit() {
        Hill hill1 = new Hill("Hill A", 1000.0, "GR1", Hill.Category.MUNRO);
        Hill hill2 = new Hill("Hill B", 800.0, "GR2", Hill.Category.TOP);
        Hill hill3 = new Hill("Hill C", 1200.0, "GR3", Hill.Category.MUNRO);

        when(hillDataLoader.loadHills()).thenReturn(List.of(hill1, hill2, hill3));

        HillSearchRequest request = HillSearchRequestFactory.newRequest(
                null,
                null,
                null,
                List.of("height_desc"),
                2
        );

        List<Hill> results = hillSearchService.searchHills(request);

        assertThat(results).hasSize(2);
        assertThat(results).containsExactly(hill3, hill1);
    }
}
