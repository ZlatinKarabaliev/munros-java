package com.xdesign.munrotable.controller;

import com.xdesign.munrotable.dto.HillSearchRequest;
import com.xdesign.munrotable.dto.HillSearchRequestFactory;
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

    @GetMapping(value = "/hills", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<Hill> getHills(
            @RequestParam(required = false) String category,
            @RequestParam(required = false) Double minHeight,
            @RequestParam(required = false) Double maxHeight,
            @RequestParam(required = false) List<String> sort,
            @RequestParam(required = false, defaultValue = "1000") int limit) {

        HillSearchRequest request = HillSearchRequestFactory.newRequest(category, minHeight, maxHeight, sort, limit);
        return hillSearchService.searchHills(request);
    }

    @ExceptionHandler({IllegalArgumentException.class, IllegalStateException.class})
    public ResponseEntity<String> handleExpectedErrors(Exception ex) {
        return ResponseEntity
            .badRequest()
            .body(ex.getMessage());
    }

    @ExceptionHandler(Throwable.class)
    public ResponseEntity<String> handleUnexpectedErrors(Throwable t) {
        log.error("Unexpected error", t);

        return ResponseEntity
            .internalServerError()
            .body("");
    }
}
