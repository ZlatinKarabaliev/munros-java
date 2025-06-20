package com.xdesign.munrotable.service;

import com.xdesign.munrotable.dto.HillSearchRequest;
import com.xdesign.munrotable.dto.Sort;
import com.xdesign.munrotable.dto.SortField;
import com.xdesign.munrotable.dto.SortOrder;
import com.xdesign.munrotable.loader.HillDataLoader;
import com.xdesign.munrotable.model.Hill;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;

@Service
public class HillSearchService {

    private final HillDataLoader loader;

    public HillSearchService(HillDataLoader loader) {
        this.loader = loader;
    }

    public List<Hill> searchHills(HillSearchRequest request) {
        return loader.loadHills().stream()
                .filter(hill -> request.category() == null || hill.category() == request.category())
                .filter(hill -> request.minHeight() == null || hill.height() >= request.minHeight())
                .filter(hill -> request.maxHeight() == null || hill.height() <= request.maxHeight())
                .sorted(buildComparator(request))
                .limit(request.limit() > 0 ? request.limit() : Long.MAX_VALUE)
                .toList();
    }

    private Comparator<Hill> buildComparator(HillSearchRequest request) {
        Comparator<Hill> comparator = Comparator.comparingInt(h -> 0); // неутрален comparator

        for (Sort sort : request.sorts()) {
            Comparator<Hill> fieldComparator;
            if (sort.field() == SortField.HEIGHT) {
                fieldComparator = Comparator.comparing(Hill::height);
            } else if (sort.field() == SortField.NAME) {
                fieldComparator = Comparator.comparing(Hill::name, String.CASE_INSENSITIVE_ORDER);
            } else {
                throw new IllegalArgumentException("Unsupported sort field: " + sort.field());
            }

            if (sort.order() == SortOrder.DESC) {
                fieldComparator = fieldComparator.reversed();
            }
            comparator = comparator.thenComparing(fieldComparator);
        }
        return comparator;
    }
}
