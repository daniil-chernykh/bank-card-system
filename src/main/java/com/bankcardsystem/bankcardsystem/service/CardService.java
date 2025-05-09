package com.bankcardsystem.bankcardsystem.service;

import com.bankcardsystem.bankcardsystem.dto.CardCreateRequest;
import com.bankcardsystem.bankcardsystem.dto.CardDto;
import com.bankcardsystem.bankcardsystem.dto.TransferRequest;
import com.bankcardsystem.bankcardsystem.entity.CardStatus;
import org.springframework.data.domain.Page;

import java.util.List;

public interface CardService {
    CardDto createCard(CardCreateRequest request, Long userId);
    List<CardDto> getUserCards(Long userId);
    Page<CardDto> getUserCardsFiltered(Long userId, CardStatus status, int page, int size);
    void blockCard(Long userId, Long cardId);
    void transferBetweenCards(Long userId, TransferRequest request);
}
