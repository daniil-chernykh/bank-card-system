package com.bankcardsystem.bankcardsystem.service;

import com.bankcardsystem.bankcardsystem.dto.CardCreateRequest;
import com.bankcardsystem.bankcardsystem.dto.CardDto;
import com.bankcardsystem.bankcardsystem.dto.UserDto;
import com.bankcardsystem.bankcardsystem.entity.Card;
import com.bankcardsystem.bankcardsystem.entity.CardStatus;
import com.bankcardsystem.bankcardsystem.entity.Role;
import com.bankcardsystem.bankcardsystem.entity.User;
import com.bankcardsystem.bankcardsystem.exception.NotFoundException;
import com.bankcardsystem.bankcardsystem.mapper.CardMapper;
import com.bankcardsystem.bankcardsystem.repository.CardRepository;
import com.bankcardsystem.bankcardsystem.repository.UserRepository;
import com.bankcardsystem.bankcardsystem.service.impl.AdminServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AdminServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private CardRepository cardRepository;

    @Mock
    private CardMapper cardMapper;

    @InjectMocks
    private AdminServiceImpl adminService;

    @Test
    void shouldReturnAllUsersAsUserDtoList() {
        // given
        List<User> users = List.of(
                new User(1L, "u1@mail.com", "p1", Role.USER, List.of()),
                new User(2L, "u2@mail.com", "p2", Role.ADMIN, List.of())
        );

        when(userRepository.findAll()).thenReturn(users);

        // when
        List<UserDto> result = adminService.getAllUsers();

        // then
        assertEquals(2, result.size());
        assertEquals("u1@mail.com", result.get(0).getEmail());
        assertEquals(Role.USER, result.get(0).getRole());
    }

    @Test
    void shouldReturnAllCardsMappedToDto() {
        // given
        Card card1 = new Card();
        card1.setId(1L);

        Card card2 = new Card();
        card2.setId(2L);

        List<Card> cards = List.of(card1, card2);

        CardDto dto1 = CardDto.builder()
                .id(1L)
                .build();

        CardDto dto2 = CardDto.builder()
                .id(2L)
                .build();

        when(cardRepository.findAll()).thenReturn(cards);
        when(cardMapper.toDto(card1)).thenReturn(dto1);
        when(cardMapper.toDto(card2)).thenReturn(dto2);

        // when
        List<CardDto> result = adminService.getAllCards();

        // then
        assertEquals(2, result.size());
        assertEquals(1L, result.get(0).getId());
        assertEquals(2L, result.get(1).getId());
    }

    @Test
    void shouldReturnAllCardsOfUserByIdMappedToDto() {
        // given
        Long userId = 42L;

        Card card1 = new Card();
        card1.setId(1L);

        Card card2 = new Card();
        card2.setId(2L);

        when(cardRepository.findAllByUserId(userId)).thenReturn(List.of(card1, card2));

        CardDto dto1 = CardDto.builder()
                .id(1L)
                .build();

        CardDto dto2 = CardDto.builder()
                .id(2L)
                .build();

        when(cardMapper.toDto(card1)).thenReturn(dto1);
        when(cardMapper.toDto(card2)).thenReturn(dto2);

        // when
        List<CardDto> result = adminService.getUsersCards(userId);

        // then
        assertEquals(2, result.size());
        assertEquals(1L, result.get(0).getId());
        assertEquals(2L, result.get(1).getId());
    }

    @Test
    void shouldBlockCardSuccessfully() {
        // given
        Card card = new Card();
        card.setId(1L);
        card.setStatus(CardStatus.ACTIVE);

        when(cardRepository.findById(1L)).thenReturn(Optional.of(card));

        // when
        adminService.blockCard(1L);

        // then
        assertEquals(CardStatus.BLOCKED, card.getStatus());
        verify(cardRepository).save(card);
    }

    @Test
    void shouldThrowIfCardAlreadyBlocked() {
        // given
        Card card = new Card();
        card.setId(1L);
        card.setStatus(CardStatus.BLOCKED);

        when(cardRepository.findById(1L)).thenReturn(Optional.of(card));

        // when & then
        assertThrows(IllegalStateException.class, () -> adminService.blockCard(1L));
        verify(cardRepository, never()).save(any());
    }

    @Test
    void shouldUnblockCardSuccessfully() {
        // given
        Card card = new Card();
        card.setId(1L);
        card.setStatus(CardStatus.BLOCKED);

        when(cardRepository.findById(1L)).thenReturn(Optional.of(card));

        // when
        adminService.unblockCard(1L);

        // then
        assertEquals(CardStatus.ACTIVE, card.getStatus());
        verify(cardRepository).save(card);
    }

    @Test
    void shouldThrowIfCardAlreadyUnblocked() {
        // given
        Card card = new Card();
        card.setId(1L);
        card.setStatus(CardStatus.ACTIVE);

        when(cardRepository.findById(1L)).thenReturn(Optional.of(card));

        // when & then
        assertThrows(IllegalStateException.class, () -> adminService.unblockCard(1L));
        verify(cardRepository, never()).save(any());
    }

    @Test
    void shouldDeleteUserIfExists() {
        // given
        when(userRepository.existsById(1L)).thenReturn(true);

        // when
        adminService.deleteUser(1L);

        // then
        verify(userRepository).deleteById(1L);
    }

    @Test
    void shouldThrowIfUserNotFoundOnDelete() {
        // given
        when(userRepository.existsById(99L)).thenReturn(false);

        // when & then
        assertThrows(NotFoundException.class, () -> adminService.deleteUser(99L));
        verify(userRepository, never()).deleteById(any());
    }

    @Test
    void shouldCreateCardForUserSuccessfully() {
        // given
        Long userId = 1L;
        User user = new User();
        user.setId(userId);

        CardCreateRequest request = CardCreateRequest.builder()
                .cardNumber("1234 5678 9012 3456")
                .initialBalance(BigDecimal.valueOf(1000))
                .expirationDate(LocalDate.of(2026, 12, 31))
                .build();

        Card cardToSave = new Card();
        cardToSave.setCardNumber("1234 5678 9012 3456");
        cardToSave.setExpirationDate(request.getExpirationDate());
        cardToSave.setBalance(request.getInitialBalance());
        cardToSave.setStatus(CardStatus.ACTIVE);
        cardToSave.setUser(user);

        Card savedCard = new Card();
        savedCard.setId(100L);

        CardDto expectedDto = CardDto.builder()
                .id(100L)
                .build();

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(cardRepository.save(any(Card.class))).thenReturn(savedCard);
        when(cardMapper.toDto(savedCard)).thenReturn(expectedDto);

        // when
        CardDto result = adminService.createCardForUser(userId, request);

        // then
        assertEquals(expectedDto, result);
        verify(cardRepository).save(any(Card.class));
    }

}
