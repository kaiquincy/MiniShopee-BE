package com.example.demo.repository;

import com.example.demo.model.ChatMessage;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {
    Page<ChatMessage> findByRoomIdOrderByCreatedAtDesc(Long roomId, Pageable pageable);
    long countByRoomIdAndReadAtIsNullAndSender_IdNot(Long roomId, Long viewerId);
    Optional<ChatMessage> findTopByRoomIdOrderByCreatedAtDesc(Long roomId);
}
