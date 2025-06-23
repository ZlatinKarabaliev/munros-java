package com.xdesign.munrotable.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.xdesign.munrotable.model.Hill;
import com.xdesign.munrotable.service.HillSearchService;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class HillControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private HillSearchService hillSearchService;

    private List<Hill> loadHillsFromJson(String jsonFile) throws IOException {
        String json = readJsonFile(jsonFile);
        return objectMapper.readValue(json, new TypeReference<>() {});
    }

    private String readJsonFile(String filename) throws IOException {
        Path path = new ClassPathResource(filename).getFile().toPath();
        return Files.readString(path, UTF_8);
    }

    private void performAndExpectJson(String uri, String jsonFile) throws Exception {
        List<Hill> hillsFromJson = loadHillsFromJson(jsonFile);
        when(hillSearchService.searchHills(any())).thenReturn(hillsFromJson);

        mockMvc.perform(get(uri)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(readJsonFile(jsonFile)));

        verify(hillSearchService, times(1)).searchHills(any());
    }

    @Test
    void should_return_all_munros_and_tops() throws Exception {
        performAndExpectJson("/hills", "all-munros-and-tops.json");
    }

    @Nested
    class FilteringTest {

        @Test
        void should_return_only_category_top() throws Exception {
            performAndExpectJson("/hills?category=Top", "tops-only.json");
        }

        @Test
        void should_return_category_top_1200_metres_high_or_more() throws Exception {
            performAndExpectJson("/hills?category=Top&minHeight=1200", "tops-greater-than-or-equal-to-1200-metres.json");
        }

        @Test
        void should_return_category_munro_920_metres_or_less() throws Exception {
            performAndExpectJson("/hills?category=Munro&maxHeight=920", "munros-920-metres-high-or-less.json");
        }

        @Test
        void should_return_category_none_exactly_1000_metres_high() throws Exception {
            performAndExpectJson("/hills?minHeight=1000&maxHeight=1000", "all-summits-exactly-1000-metres-high.json");
        }
    }

    @Nested
    class AllParamsTest {

        @Test
        void should_return_five_highest_munros_ordered_height_desc() throws Exception {
            performAndExpectJson("/hills?category=Munro&sort=height_desc&limit=5", "five-highest-munros-ordered-height-desc.json");
        }

        @Test
        void should_return_first_five_munros_in_alphabetical_order_with_identically_named_munros_ordered_height_desc() throws Exception {
            performAndExpectJson("/hills?category=Munro&sort=name_asc&sort=height_desc&limit=5", "first-five-munros-ordered-by-name-asc-and-height-desc.json");
        }

        @Test
        void should_return_category_top_1200_metres_high_or_more_ordered_height_desc() throws Exception {
            performAndExpectJson("/hills?category=Top&minHeight=1200&sort=height_desc", "tops-1200-metres-high-or-more-ordered-height-desc.json");
        }

        @Test
        void should_return_category_munro_920_metres_high_or_less_ordered_height_asc() throws Exception {
            performAndExpectJson("/hills?category=Munro&maxHeight=920&sort=height_asc", "munro-920-metres-high-or-less-ordered-height-asc.json");
        }
    }


    @Test
    void should_return_bad_request_for_invalid_category() throws Exception {
        mockMvc.perform(get("/hills")
                        .param("category", "INVALID")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Invalid category: INVALID"));
    }
}
