package io.autoinvestor.ui;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.util.Date;

public record PriceDTO(
        @JsonFormat(shape = JsonFormat.Shape.STRING)
        Date date,
        int price) { }
