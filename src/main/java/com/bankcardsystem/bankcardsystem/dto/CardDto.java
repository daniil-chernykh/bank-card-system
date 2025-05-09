package com.bankcardsystem.bankcardsystem.dto;

import com.bankcardsystem.bankcardsystem.entity.CardStatus;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
public class CardDto {
    private Long id;
    private String maskedCardNumber;
    private LocalDate expirationDate;
    private CardStatus status;
    private BigDecimal balance;
}
