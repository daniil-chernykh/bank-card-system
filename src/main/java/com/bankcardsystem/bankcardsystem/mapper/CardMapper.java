package com.bankcardsystem.bankcardsystem.mapper;

import com.bankcardsystem.bankcardsystem.dto.CardDto;
import com.bankcardsystem.bankcardsystem.entity.Card;
import com.bankcardsystem.bankcardsystem.util.EncryptionService;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.beans.factory.annotation.Autowired;

@Mapper(componentModel = "spring")
public abstract class CardMapper {

    @Autowired
    protected EncryptionService encryptionService;

    @Mapping(target = "maskedCardNumber", expression = "java(maskCardNumber(encryptionService.decrypt(card.getCardNumber())))")
    public abstract CardDto toDto(Card card);

    protected String maskCardNumber(String number) {
        if (number == null || number.length() < 4) return "****";
        return "**** **** **** " + number.substring(number.length() - 4);
    }
}
