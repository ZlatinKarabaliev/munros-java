package com.xdesign.munrotable.util;

import com.xdesign.munrotable.dto.SortField;
import com.xdesign.munrotable.model.Hill;

import java.util.Comparator;
import java.util.EnumMap;
import java.util.Map;

public final class HillComparators {

    private static final Map<SortField, Comparator<Hill>> COMPARATORS = new EnumMap<>(SortField.class);

    static {
        COMPARATORS.put(SortField.HEIGHT, Comparator.comparing(Hill::height, Comparator.nullsLast(Double::compareTo)));
        COMPARATORS.put(SortField.NAME, Comparator.comparing(Hill::name, String.CASE_INSENSITIVE_ORDER));
    }

    private HillComparators() { }

    public static Comparator<Hill> getComparator(SortField field) {
        return COMPARATORS.getOrDefault(field, (h1, h2) -> 0);
    }
}
