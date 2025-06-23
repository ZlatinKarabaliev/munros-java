package com.xdesign.munrotable.controller;

import com.xdesign.munrotable.dto.HillSearchRequest;
import com.xdesign.munrotable.factory.HillSearchRequestFactory;
import com.xdesign.munrotable.model.Hill;
import com.xdesign.munrotable.service.HillSearchService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(produces = MediaType.APPLICATION_JSON_VALUE)
public class HillController {

    private static final Logger log = LoggerFactory.getLogger(HillController.class);

    private final HillSearchService hillSearchService;

    public HillController(HillSearchService hillSearchService) {
        this.hillSearchService = hillSearchService;
    }

    /**
     * GET /hills endpoint that supports filtering, sorting and limiting the results.
     *
     * @param category  optional filter by hill category (e.g. "MUNRO", "TOP")
     * @param minHeight optional minimum height filter
     * @param maxHeight optional maximum height filter
     * @param sort      optional list of sorting criteria in format "field_order", e.g. "height_desc"
     * @param limit     optional limit on number of results, default 1000
     * @return filtered, sorted and limited list of hills
     */
    @GetMapping("/hills")
    public List<Hill> getHills(
            @RequestParam(required = false) String category,
            @RequestParam(required = false) Double minHeight,
            @RequestParam(required = false) Double maxHeight,
            @RequestParam(required = false) List<String> sort,
            @RequestParam(required = false, defaultValue = "1000") int limit) {

        HillSearchRequest request = HillSearchRequestFactory.newRequest(category, minHeight, maxHeight, sort, limit);
        return hillSearchService.searchHills(request);
    }

    /**
     * Handles known expected exceptions like invalid arguments.
     */
    @ExceptionHandler({IllegalArgumentException.class, IllegalStateException.class})
    public ResponseEntity<String> handleExpectedErrors(Exception ex) {
        return ResponseEntity
                .badRequest()
                .body(ex.getMessage());
    }

    /**
     * Handles any unexpected exceptions by logging and returning 500 error.
     */
    @ExceptionHandler(Throwable.class)
    public ResponseEntity<String> handleUnexpectedErrors(Throwable t) {
        log.error("Unexpected error", t);
        return ResponseEntity
                .internalServerError()
                .body("An unexpected error occurred");
    }
}
