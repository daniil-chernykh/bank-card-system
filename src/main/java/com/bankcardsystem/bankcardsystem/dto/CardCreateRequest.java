package com.bankcardsystem.bankcardsystem.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class CardCreateRequest {
    @NotBlank
    private String cardNumber;

    @NotNull
    private LocalDate expirationDate;

    @PositiveOrZero
    private BigDecimal initialBalance;
}
