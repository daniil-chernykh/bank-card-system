package com.bankcardsystem.bankcardsystem.repository;

import com.bankcardsystem.bankcardsystem.entity.Card;
import com.bankcardsystem.bankcardsystem.entity.CardStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CardRepository extends JpaRepository<Card, Long> {

    List<Card> findAllByUserId(Long userId);

    @Query("SELECT c FROM Card c WHERE c.user.id = :userId AND (:status IS NULL OR c.status = :status)")
    Page<Card> findByUserIdWithFilters(@Param("userId") Long userId,
                                       @Param("status") CardStatus status,
                                       Pageable pageable);
}
