package com.xdesign.munrotable.service;

import com.xdesign.munrotable.dto.HillSearchRequest;
import com.xdesign.munrotable.dto.Sort;
import com.xdesign.munrotable.dto.SortOrder;
import com.xdesign.munrotable.factory.HillDataSourceFactory;
import com.xdesign.munrotable.loader.HillDataSource;
import com.xdesign.munrotable.model.Hill;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

@Service
public class HillSearchService {

    private static final Logger log = LoggerFactory.getLogger(HillSearchService.class);
    private final HillDataSourceFactory factory;

    public HillSearchService(HillDataSourceFactory factory) {
        this.factory = factory;
    }

    public List<Hill> searchHills(HillSearchRequest request) {
        HillDataSource dataSource = factory.getActiveDataSource();
        Stream<Hill> stream = dataSource.loadHills().stream();

        stream = applyFilters(stream, request);

        Comparator<Hill> comparator = buildComparator(request);
        if (comparator != null) {
            stream = stream.sorted(comparator);
        }

        long limit = request.limit() > 0 ? request.limit() : Long.MAX_VALUE;
        List<Hill> result = stream.limit(limit).toList();

        log.debug("Result list contains {} hills", result.size());
        return result;
    }

    private Stream<Hill> applyFilters(Stream<Hill> stream, HillSearchRequest request) {
        return stream
                .filter(h -> request.category() == null || h.category() == request.category())
                .filter(h -> request.minHeight() == null || (h.height() != null && h.height() >= request.minHeight()))
                .filter(h -> request.maxHeight() == null || (h.height() != null && h.height() <= request.maxHeight()));
    }

    private Comparator<Hill> buildComparator(HillSearchRequest request) {
        List<Sort> sorts = Optional.ofNullable(request.sorts()).orElse(List.of());
        if (sorts.isEmpty()) return null;

        Comparator<Hill> comparator = null;
        for (Sort sort : sorts) {
            Comparator<Hill> fieldComparator = switch (sort.field()) {
                case HEIGHT -> Comparator.comparing(Hill::height, Comparator.nullsLast(Double::compareTo));
                case NAME -> Comparator.comparing(Hill::name, Comparator.nullsLast(String.CASE_INSENSITIVE_ORDER));
            };

            if (sort.order() == SortOrder.DESC) {
                fieldComparator = fieldComparator.reversed();
            }

            comparator = comparator == null ? fieldComparator : comparator.thenComparing(fieldComparator);
        }

        return comparator;
    }
}
