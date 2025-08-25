package com.openisle.repository;

import com.openisle.model.MessageParticipant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MessageParticipantRepository extends JpaRepository<MessageParticipant, Long> {
    Optional<MessageParticipant> findByConversationIdAndUserId(Long conversationId, Long userId);
    List<MessageParticipant> findByUserId(Long userId);
}