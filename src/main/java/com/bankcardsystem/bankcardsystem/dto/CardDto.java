package com.bankcardsystem.bankcardsystem.dto;

import com.bankcardsystem.bankcardsystem.entity.CardStatus;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class CardDto {
    private Long id;
    private String maskedCardNumber;
    private LocalDate expirationDate;
    private CardStatus status;
    private BigDecimal balance;
}
