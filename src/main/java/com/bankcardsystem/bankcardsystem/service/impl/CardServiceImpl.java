package com.bankcardsystem.bankcardsystem.service.impl;

import com.bankcardsystem.bankcardsystem.dto.CardCreateRequest;
import com.bankcardsystem.bankcardsystem.dto.CardDto;
import com.bankcardsystem.bankcardsystem.dto.TransferRequest;
import com.bankcardsystem.bankcardsystem.entity.Card;
import com.bankcardsystem.bankcardsystem.entity.CardStatus;
import com.bankcardsystem.bankcardsystem.entity.User;
import com.bankcardsystem.bankcardsystem.exception.AccessDeniedException;
import com.bankcardsystem.bankcardsystem.exception.NotFoundException;
import com.bankcardsystem.bankcardsystem.mapper.CardMapper;
import com.bankcardsystem.bankcardsystem.repository.CardRepository;
import com.bankcardsystem.bankcardsystem.repository.UserRepository;
import com.bankcardsystem.bankcardsystem.service.CardService;
import com.bankcardsystem.bankcardsystem.util.EncryptionService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.bankcardsystem.bankcardsystem.entity.CardStatus.ACTIVE;
import static com.bankcardsystem.bankcardsystem.entity.CardStatus.BLOCKED;
import static java.util.stream.Collectors.toList;

// TODO: добавить логирование

@Service
@RequiredArgsConstructor
public class CardServiceImpl implements CardService {

    private final CardRepository cardRepository;
    private final UserRepository userRepository;
    private final CardMapper cardMapper;
    private final EncryptionService encryptionService;

    @Override
    public CardDto createCard(CardCreateRequest request, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("user not found"));

        Card card = Card.builder()
                .cardNumber(encryptionService.encrypt(request.getCardNumber()))
                .expirationDate(request.getExpirationDate())
                .balance(request.getInitialBalance())
                .status(ACTIVE)
                .user(user)
                .build();

        Card saved = cardRepository.save(card);

        return cardMapper.toDto(saved);
    }

    @Override
    public List<CardDto> getUserCards(Long userId) {
        return cardRepository.findAllByUserId(userId)
                .stream()
                .map(cardMapper::toDto)
                .collect(toList());
    }

    @Override
    public Page<CardDto> getUserCardsFiltered(Long userId, CardStatus status, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Card> cards = cardRepository.findByUserIdWithFilters(userId,status, pageable);
        return cards.map(cardMapper::toDto);
    }

    @Override
    public void blockCard(Long userId, Long cardId) {
        Card card = cardRepository.findById(cardId)
                .orElseThrow(() -> new NotFoundException("card not found"));

        if (!card.getUser().getId().equals(userId)) {
            throw new AccessDeniedException("you don`t own this card");
        }

        if (card.getStatus().equals(BLOCKED)) {
            throw new IllegalStateException("card is already blocked");
        }

        card.setStatus(BLOCKED);
        cardRepository.save(card);
    }

    @Override
    public void transferBetweenCards(Long userId, TransferRequest request) {
        Card fromCard = cardRepository.findById(request.getFromCardId())
                .orElseThrow(() -> new NotFoundException("card not found"));
        Card toCard = cardRepository.findById(request.getToCardId())
                .orElseThrow(() -> new NotFoundException("card not found"));

        if (!fromCard.getUser().getId().equals(userId) || !toCard.getUser().getId().equals(userId)) {
            throw new AccessDeniedException("you don`t own this card");
        }

        if (fromCard.getStatus().equals(BLOCKED) || toCard.getStatus().equals(BLOCKED)) {
            throw new IllegalStateException("card is already blocked");
        }

        if (fromCard.getBalance().compareTo(request.getAmount()) < 0) {
            throw new IllegalStateException("Недостаточно средств для перевода");
        }

        fromCard.setBalance(fromCard.getBalance().subtract(request.getAmount()));
        toCard.setBalance(toCard.getBalance().add(request.getAmount()));

        cardRepository.save(fromCard);
        cardRepository.save(toCard);
    }
}
