package com.bankcardsystem.bankcardsystem.service;

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
import com.bankcardsystem.bankcardsystem.service.impl.CardServiceImpl;
import com.bankcardsystem.bankcardsystem.util.EncryptionService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
public class CardServiceImplTest {
    @Mock
    private CardRepository cardRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private CardMapper cardMapper;

    @Mock
    private EncryptionService encryptionService;

    @InjectMocks
    private CardServiceImpl cardService;

    // ---------- createCard ----------

    @Test
    void shouldCreateCardSuccessfully() {
        // given
        Long userId = 1L;
        var user = new User();
        user.setId(userId);

        var request = CardCreateRequest.builder()
                .cardNumber("1234 5678 9012 3456")
                .expirationDate(LocalDate.now().plusYears(1))
                .initialBalance(new BigDecimal(1000))
                .build();

        var savedCard = new Card();
        savedCard.setId(1L);

        var expectedDto = CardDto.builder().build();
        expectedDto.setId(1L);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(cardRepository.save(any(Card.class))).thenReturn(savedCard);
        when(cardMapper.toDto(savedCard)).thenReturn(expectedDto);
        when(encryptionService.encrypt("1234 5678 9012 3456")).thenReturn("**** **** **** 3456");

        // when
        CardDto result = cardService.createCard(request, userId);

        // then
        assertEquals(expectedDto, result);
        verify(cardRepository).save(any(Card.class));
    }

    @Test
    void shouldThrowIfUserNotFoundOnCreateCard() {
        // given
        Long userId = 99L;
        CardCreateRequest request = CardCreateRequest.builder()
                .cardNumber("1234")
                .expirationDate(LocalDate.now())
                .initialBalance(BigDecimal.TEN)
                .build();

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // when & then
        assertThrows(NotFoundException.class, () -> cardService.createCard(request, userId));
        verify(cardRepository, never()).save(any());
    }


    // ---------- blockCard ----------

    @Test
    void shouldBlockCardSuccessfully() {
        // given
        Long userId = 1L;
        Long cardId = 10L;

        User user = new User(); user.setId(userId);
        Card card = new Card();
        card.setId(cardId);
        card.setUser(user);
        card.setStatus(CardStatus.ACTIVE);

        when(cardRepository.findById(cardId)).thenReturn(Optional.of(card));

        // when
        cardService.blockCard(userId, cardId);

        // then
        assertEquals(CardStatus.BLOCKED, card.getStatus());
        verify(cardRepository).save(card);
    }

    @Test
    void shouldThrowIfUserNotOwnerWhenBlockingCard() {
        // given
        Long userId = 1L;
        Long cardId = 10L;

        User otherUser = new User(); otherUser.setId(2L);
        Card card = new Card();
        card.setId(cardId);
        card.setUser(otherUser);
        card.setStatus(CardStatus.ACTIVE);

        when(cardRepository.findById(cardId)).thenReturn(Optional.of(card));

        // when & then
        assertThrows(AccessDeniedException.class, () -> cardService.blockCard(userId, cardId));
        verify(cardRepository, never()).save(any());
    }


    @Test
    void shouldThrowIfCardAlreadyBlocked() {
        // given
        Long userId = 1L;
        Long cardId = 10L;

        User user = new User(); user.setId(userId);
        Card card = new Card();
        card.setId(cardId);
        card.setUser(user);
        card.setStatus(CardStatus.BLOCKED);

        when(cardRepository.findById(cardId)).thenReturn(Optional.of(card));

        // when & then
        assertThrows(IllegalStateException.class, () -> cardService.blockCard(userId, cardId));
        verify(cardRepository, never()).save(any());
    }


    // ---------- transferBetweenCards ----------

    @Test
    void shouldTransferMoneySuccessfully() {
        // given
        Long userId = 1L;
        TransferRequest request = TransferRequest.builder()
                .fromCardId(1L)
                .toCardId(2L)
                .amount(BigDecimal.valueOf(500))
                .build();

        User user = new User(); user.setId(userId);

        Card fromCard = new Card();
        fromCard.setId(1L);
        fromCard.setUser(user);
        fromCard.setBalance(BigDecimal.valueOf(1000));
        fromCard.setStatus(CardStatus.ACTIVE);

        Card toCard = new Card();
        toCard.setId(2L);
        toCard.setUser(user);
        toCard.setBalance(BigDecimal.valueOf(200));
        toCard.setStatus(CardStatus.ACTIVE);

        when(cardRepository.findById(1L)).thenReturn(Optional.of(fromCard));
        when(cardRepository.findById(2L)).thenReturn(Optional.of(toCard));

        // when
        cardService.transferBetweenCards(userId, request);

        // then
        assertEquals(BigDecimal.valueOf(500), fromCard.getBalance());
        assertEquals(BigDecimal.valueOf(700), toCard.getBalance());

        verify(cardRepository).save(fromCard);
        verify(cardRepository).save(toCard);
    }

    @Test
    void shouldThrowIfUserNotOwnerOfAnyCard() {
        // given
        Long userId = 1L;
        Long otherUserId = 2L;

        TransferRequest request = TransferRequest.builder()
                .fromCardId(1L)
                .toCardId(2L)
                .amount(BigDecimal.TEN)
                .build();

        User otherUser = new User(); otherUser.setId(otherUserId);

        Card fromCard = new Card();
        fromCard.setId(1L);
        fromCard.setUser(otherUser);

        Card toCard = new Card();
        toCard.setId(2L);
        toCard.setUser(otherUser);

        when(cardRepository.findById(1L)).thenReturn(Optional.of(fromCard));
        when(cardRepository.findById(2L)).thenReturn(Optional.of(toCard));

        // when & then
        assertThrows(AccessDeniedException.class, () -> cardService.transferBetweenCards(userId, request));
    }

    @Test
    void shouldThrowIfInsufficientFunds() {
        // given
        Long userId = 1L;
        TransferRequest request = TransferRequest.builder()
                .fromCardId(1L)
                .toCardId(2L)
                .amount(BigDecimal.valueOf(1000))
                .build();

        User user = new User(); user.setId(userId);

        Card fromCard = new Card();
        fromCard.setId(1L);
        fromCard.setUser(user);
        fromCard.setBalance(BigDecimal.valueOf(500));
        fromCard.setStatus(CardStatus.ACTIVE);

        Card toCard = new Card();
        toCard.setId(2L);
        toCard.setUser(user);
        toCard.setBalance(BigDecimal.ZERO);
        toCard.setStatus(CardStatus.ACTIVE);

        when(cardRepository.findById(1L)).thenReturn(Optional.of(fromCard));
        when(cardRepository.findById(2L)).thenReturn(Optional.of(toCard));

        // when & then
        assertThrows(IllegalStateException.class, () -> cardService.transferBetweenCards(userId, request));
    }

    @Test
    void shouldThrowIfAnyCardBlocked() {
        // given
        Long userId = 1L;
        TransferRequest request = TransferRequest.builder()
                .fromCardId(1L)
                .toCardId(2L)
                .amount(BigDecimal.TEN)
                .build();

        User user = new User(); user.setId(userId);

        Card fromCard = new Card();
        fromCard.setId(1L);
        fromCard.setUser(user);
        fromCard.setStatus(CardStatus.BLOCKED);
        fromCard.setBalance(BigDecimal.valueOf(100));

        Card toCard = new Card();
        toCard.setId(2L);
        toCard.setUser(user);
        toCard.setStatus(CardStatus.ACTIVE);
        toCard.setBalance(BigDecimal.ZERO);

        when(cardRepository.findById(1L)).thenReturn(Optional.of(fromCard));
        when(cardRepository.findById(2L)).thenReturn(Optional.of(toCard));

        // when & then
        assertThrows(IllegalStateException.class, () -> cardService.transferBetweenCards(userId, request));
    }



}
