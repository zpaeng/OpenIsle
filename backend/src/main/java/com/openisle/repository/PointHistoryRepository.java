package com.openisle.repository;

import com.openisle.model.PointHistory;
import com.openisle.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PointHistoryRepository extends JpaRepository<PointHistory, Long> {
    List<PointHistory> findByUserOrderByIdDesc(User user);
    long countByUser(User user);
}
