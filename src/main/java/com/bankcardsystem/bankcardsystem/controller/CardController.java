package com.bankcardsystem.bankcardsystem.controller;

import com.bankcardsystem.bankcardsystem.dto.CardCreateRequest;
import com.bankcardsystem.bankcardsystem.dto.CardDto;
import com.bankcardsystem.bankcardsystem.dto.TransferRequest;
import com.bankcardsystem.bankcardsystem.entity.CardStatus;
import com.bankcardsystem.bankcardsystem.security.JwtUserDetails;
import com.bankcardsystem.bankcardsystem.service.CardService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import static org.springframework.http.HttpStatus.CREATED;

// TODO: добавить логирование

@RestController
@RequestMapping("/api/cards")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
public class CardController {

    private final CardService cardService;

    @GetMapping
    @PreAuthorize("hasRole('USER')")
    public Page<CardDto> getUserCards(
            @RequestParam(required = false)CardStatus status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @AuthenticationPrincipal JwtUserDetails user
    ) {
        return cardService.getUserCardsFiltered(user.getId(), status, page, size);
    }

    @PatchMapping("/{cardId}/block")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<Void> blockCard(
            @PathVariable Long cardId,
            @AuthenticationPrincipal JwtUserDetails user
    ) {
        cardService.blockCard(user.getId(), cardId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/transfer")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<Void> transferBetweenCards(
            @RequestBody @Valid TransferRequest request,
            @AuthenticationPrincipal JwtUserDetails user
    ) {
        cardService.transferBetweenCards(user.getId(), request);
        return ResponseEntity.ok().build();
    }

    @PostMapping
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<CardDto> createCard(
            @RequestBody @Valid CardCreateRequest request,
            @AuthenticationPrincipal JwtUserDetails user
    ) {
        CardDto created = cardService.createCard(request, user.getId());
        return ResponseEntity.status(CREATED).body(created);
    }

}
