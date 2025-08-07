package com.openisle.repository;

import com.openisle.model.PointLog;
import com.openisle.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.Optional;

public interface PointLogRepository extends JpaRepository<PointLog, Long> {
    Optional<PointLog> findByUserAndLogDate(User user, LocalDate logDate);
}
