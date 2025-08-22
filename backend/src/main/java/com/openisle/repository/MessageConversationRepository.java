package com.openisle.repository;

import com.openisle.model.MessageConversation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.openisle.model.User;
import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import com.openisle.model.User;
import java.util.List;

@Repository
public interface MessageConversationRepository extends JpaRepository<MessageConversation, Long> {
    @Query("SELECT c FROM MessageConversation c JOIN c.participants p1 JOIN c.participants p2 WHERE p1.user = :user1 AND p2.user = :user2")
    Optional<MessageConversation> findConversationByUsers(@Param("user1") User user1, @Param("user2") User user2);
    
    @Query("SELECT DISTINCT c FROM MessageConversation c " +
           "JOIN c.participants p " +
           "LEFT JOIN FETCH c.lastMessage lm " +
           "LEFT JOIN FETCH lm.sender " +
           "LEFT JOIN FETCH c.participants cp " +
           "LEFT JOIN FETCH cp.user " +
           "WHERE p.user.id = :userId " +
           "ORDER BY COALESCE(lm.createdAt, c.createdAt) DESC")
    List<MessageConversation> findConversationsByUserIdOrderByLastMessageDesc(@Param("userId") Long userId);

    List<MessageConversation> findByChannelTrue();

    long countByChannelTrue();
}