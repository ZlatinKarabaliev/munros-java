package com.xdesign.munrotable.dto;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThatThrownBy;


class HillSearchRequestFactoryTest {

    @Test
    void testNewRequest_withZeroLimit_throwsException() {
        assertThatThrownBy(() -> HillSearchRequestFactory.newRequest(
                "MUNRO",
                100.0,
                200.0,
                List.of(),
                0
        )).isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Limit must be greater than zero");
    }

    @Test
    void testNewRequest_withInvalidCategory_throwsException() {
        assertThatThrownBy(() -> HillSearchRequestFactory.newRequest(
                "INVALID_CATEGORY",
                null,
                null,
                List.of(),
                10
        )).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void testNewRequest_withInvalidSortFormat_throwsException() {
        assertThatThrownBy(() -> HillSearchRequestFactory.newRequest(
                null,
                null,
                null,
                List.of("invalidsort"),
                10
        )).isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Invalid sort format");
    }

    @Test
    void testNewRequest_withInvalidSortField_throwsException() {
        assertThatThrownBy(() -> HillSearchRequestFactory.newRequest(
                null,
                null,
                null,
                List.of("unknown_asc"),
                10
        )).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void testNewRequest_withInvalidSortOrder_throwsException() {
        assertThatThrownBy(() -> HillSearchRequestFactory.newRequest(
                null,
                null,
                null,
                List.of("name_unknown"),
                10
        )).isInstanceOf(IllegalArgumentException.class);
    }
}