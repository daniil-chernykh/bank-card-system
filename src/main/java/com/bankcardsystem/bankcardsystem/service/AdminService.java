package com.bankcardsystem.bankcardsystem.service;

import com.bankcardsystem.bankcardsystem.dto.CardCreateRequest;
import com.bankcardsystem.bankcardsystem.dto.CardDto;
import com.bankcardsystem.bankcardsystem.dto.UserDto;

import java.util.List;

public interface AdminService {
    List<UserDto> getAllUsers();
    List<CardDto> getAllCards();
    List<CardDto> getUsersCards(Long userId);
    void blockCard(Long cardId);
    void unblockCard(Long cardId);
    void deleteUser(Long userId);
    void deleteCard(Long cardId);
    CardDto createCardForUser(Long userId, CardCreateRequest request);
}
