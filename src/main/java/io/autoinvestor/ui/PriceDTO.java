package io.autoinvestor.ui;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;

public record PriceDTO(@JsonFormat(shape = JsonFormat.Shape.STRING) Date date, int price) {}
