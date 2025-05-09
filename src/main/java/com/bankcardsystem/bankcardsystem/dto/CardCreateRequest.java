package com.bankcardsystem.bankcardsystem.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
public class CardCreateRequest {
    @NotBlank
    private String cardNumber;

    @NotNull
    private LocalDate expirationDate;

    @PositiveOrZero
    private BigDecimal initialBalance;
}
