package com.openisle.repository;

import com.openisle.model.InviteToken;
import com.openisle.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.Optional;

public interface InviteTokenRepository extends JpaRepository<InviteToken, String> {
    Optional<InviteToken> findByInviterAndCreatedDate(User inviter, LocalDate createdDate);
}
