package com.bankcardsystem.bankcardsystem.service.impl;

import com.bankcardsystem.bankcardsystem.dto.CardCreateRequest;
import com.bankcardsystem.bankcardsystem.dto.CardDto;
import com.bankcardsystem.bankcardsystem.dto.UserDto;
import com.bankcardsystem.bankcardsystem.entity.Card;
import com.bankcardsystem.bankcardsystem.entity.User;
import com.bankcardsystem.bankcardsystem.exception.NotFoundException;
import com.bankcardsystem.bankcardsystem.mapper.CardMapper;
import com.bankcardsystem.bankcardsystem.repository.CardRepository;
import com.bankcardsystem.bankcardsystem.repository.UserRepository;
import com.bankcardsystem.bankcardsystem.service.AdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.bankcardsystem.bankcardsystem.entity.CardStatus.ACTIVE;
import static com.bankcardsystem.bankcardsystem.entity.CardStatus.BLOCKED;

//TODO: добавить логирование

@Service
@RequiredArgsConstructor
public class AdminServiceImpl implements AdminService {

    private final UserRepository userRepository;
    private final CardRepository cardRepository;
    private final CardMapper cardMapper;

    @Override
    public List<UserDto> getAllUsers() {
        return userRepository.findAll()
                .stream()
                .map(user -> new UserDto(user.getId(), user.getEmail(), user.getRole()))
                .toList();
    }

    @Override
    public List<CardDto> getAllCards() {
        return cardRepository.findAll()
                .stream()
                .map(cardMapper::toDto)
                .toList();
    }

    @Override
    public List<CardDto> getUsersCards(Long userId) {
        return cardRepository.findAllByUserId(userId)
                .stream()
                .map(cardMapper::toDto)
                .toList();
    }

    @Override
    public void blockCard(Long cardId) {
        Card card = cardRepository.findById(cardId)
                .orElseThrow(() -> new NotFoundException("card not found"));

        if (card.getStatus() == BLOCKED) {
            throw new IllegalStateException("card already blocked");
        }

        card.setStatus(BLOCKED);
        cardRepository.save(card);
    }

    @Override
    public void unblockCard(Long cardId) {
        Card card = cardRepository.findById(cardId)
                .orElseThrow(() -> new NotFoundException("card not found"));

        if (card.getStatus() == ACTIVE) {
            throw new IllegalStateException("card already is not blocked");
        }

        card.setStatus(ACTIVE);
        cardRepository.save(card);
    }

    @Override
    public void deleteUser(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException("user not found");
        }

        userRepository.deleteById(userId);
    }

    @Override
    public void deleteCard(Long cardId) {
        if (!cardRepository.existsById(cardId)) {
            throw new NotFoundException("user not found");
        }

        cardRepository.deleteById(cardId);
    }

    @Override
    public CardDto createCardForUser(Long userId, CardCreateRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("user not found"));

        Card card = Card.builder()
                .cardNumber(request.getCardNumber())
                .balance(request.getInitialBalance())
                .expirationDate(request.getExpirationDate())
                .status(ACTIVE)
                .user(user)
                .build();

        Card savedCard = cardRepository.save(card);
        return cardMapper.toDto(savedCard);
    }
}
