package com.xdesign.munrotable.factory;

import com.xdesign.munrotable.dto.HillSearchRequest;
import com.xdesign.munrotable.dto.Sort;
import com.xdesign.munrotable.dto.SortField;
import com.xdesign.munrotable.dto.SortOrder;
import com.xdesign.munrotable.model.Hill;
import org.apache.commons.lang3.Validate;

import java.util.List;
import java.util.stream.Collectors;

import static java.util.Collections.emptyList;

public final class HillSearchRequestFactory {

    private HillSearchRequestFactory() {}

    public static HillSearchRequest newRequest(
            String category,
            Double minHeight,
            Double maxHeight,
            List<String> sortCriteria,
            int limit) {

        if (category != null && !category.equalsIgnoreCase("Munro") && !category.equalsIgnoreCase("Top")) {
            throw new IllegalArgumentException("Invalid category: " + category);
        }

        Validate.isTrue(limit > 0, "Limit must be greater than zero");

        Hill.Category hillCategory = null;
        if (category != null && !category.isBlank()) {
            hillCategory = Hill.Category.valueOf(category.trim().toUpperCase());
        }

        List<Sort> sorts = emptyList();
        if (sortCriteria != null && !sortCriteria.isEmpty()) {
            sorts = sortCriteria.stream()
                    .map(HillSearchRequestFactory::parseSort)
                    .collect(Collectors.toList());
        }

        return new HillSearchRequestImpl(hillCategory, minHeight, maxHeight, sorts, limit);
    }

    private static Sort parseSort(String criterion) {
        String[] parts = criterion.split("_");
        if (parts.length != 2) {
            throw new IllegalArgumentException("Invalid sort format: " + criterion);
        }

        SortField field = SortField.valueOf(parts[0].toUpperCase());
        SortOrder order = SortOrder.valueOf(parts[1].toUpperCase());

        return new Sort(field, order);
    }

    private record HillSearchRequestImpl(Hill.Category category,
                                         Double minHeight,
                                         Double maxHeight,
                                         List<Sort> sorts,
                                         int limit) implements HillSearchRequest {
    }
}
